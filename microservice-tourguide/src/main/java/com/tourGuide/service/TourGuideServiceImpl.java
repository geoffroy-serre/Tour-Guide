package com.tourGuide.service;

import com.tourGuide.helper.InternalTestHelper;
import com.tourGuide.model.*;
import com.tourGuide.tracker.Tracker;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class TourGuideServiceImpl implements TourGuideService {

  WebClient webClientGps;
  WebClient webClientTripPricer;
  private List<Attraction> attractions;
  private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
  private final RewardsService rewardsService;
  public final Tracker tracker;
  boolean testMode = true;
  int SIZE_OF_NEARBY_ATTRACTIONS_LIST = 5;

  public TourGuideServiceImpl(@Qualifier("getWebClientGps") final WebClient pWebClientGps,
                              @Qualifier("getWebClientTripPricer") final WebClient pWebClientTripPricer,
                              RewardsService pRewardService) {
    this.webClientGps = pWebClientGps;
    rewardsService = pRewardService;
    this.webClientTripPricer = pWebClientTripPricer;

    Locale.setDefault(Locale.US);
    if (testMode) {
      logger.info("TestMode enabled");
      logger.debug("Initializing users");
      initializeInternalUsers();
      logger.debug("Finished initializing users");
    }
    tracker = new Tracker(this);

    addShutDownHook();
  }

  /**
   * @inheritDoc
   */
  private Retry retry() {
    return Retry.backoff(60, Duration.ofSeconds(5));
  }

  /**
   * @inheritDoc
   */
  @Override
  public AttractionsSuggestion getAttractionsSuggestion(final User user) {
    logger.debug("getAttractionsSuggestion launched");
    AttractionsSuggestion suggestion = new AttractionsSuggestion();
    suggestion.setUserLocation(new Location(
            user.getLastVisitedLocation().getLocation().getLatitude(),
            user.getLastVisitedLocation().getLocation().getLongitude()));

    TreeMap<String, NearbyAttraction> suggestedAttractions = new TreeMap<>();
    List<Attraction> attractionsList = getNearByAttractions(
            user.getLastVisitedLocation());
    final AtomicInteger indexHolder = new AtomicInteger(1);
    attractionsList.stream()
            .sorted(Comparator.comparingDouble(a -> rewardsService.getDistance(a,
                    user.getLastVisitedLocation().getLocation())))
            .forEach(a -> {
              final int index = indexHolder.getAndIncrement();
              suggestedAttractions.put(a.attractionName,
                      new NearbyAttraction(new Location(a.getLatitude(), a.getLongitude()),
                              rewardsService.getDistance(a,
                                      user.getLastVisitedLocation().getLocation()),
                              rewardsService.getRewardPoints(user.getLastVisitedLocation(), a,
                                      user).block()));
            });
    suggestion.setSuggestedAttractions(suggestedAttractions);
    logger.debug("getAttractionsSuggestion returned suggestion: "+suggestion);
    return suggestion;
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<UserReward> getUserRewards(User user) {
    logger.debug("getUserRewards launched for user id: "+user.getUserId());
    return user.getUserRewards();
  }

  /**
   * @inheritDoc
   */
  @Override
  public Mono<VisitedLocation> getUserLocation(User user) {
    logger.debug("getUserLocation launched for user id: "+user.getUserId());
    Mono<VisitedLocation> visitedLocation = (user.getVisitedLocations().size() > 0) ?
            Mono.just(user.getLastVisitedLocation()) :
            trackUserLocation(user);
    logger.debug("getUserLocation finished for user id: "+user.getUserId());
    return visitedLocation;
  }

  /**
   * @inheritDoc
   */
  @Override
  public User getUser(String userName) {
    logger.debug("getUser launched for user: "+userName);
    return internalUserMap.get(userName);
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<User> getAllUsers() {
    logger.debug("getAllUser launched ");
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  /**
   * @inheritDoc
   */
  @Override
  public void addUser(User user) {
    logger.debug("addUser launched for user id: "+user);
    if (!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<Provider> getTripDeals(User user) {
    logger.debug("getTripDeaks launched for user id: "+user.getUserId());
    int cumulativeRewardPoints =
            user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

    try {
      Flux<Provider> providers = webClientTripPricer.get()
              .uri("/getPrice?tripPricerApiKey=" + tripPricerApiKey
                      + "&userId=" + user.getUserId()
                      + "&nbrOfAdult=" + user.getUserPreferences().getNumberOfAdults()
                      + "&nbrOfChildren=" + user.getUserPreferences().getNumberOfChildren()
                      + "&tripDuration=" + user.getUserPreferences().getTripDuration()
                      + "&cumulativeRewardPoints=" + cumulativeRewardPoints)
              .retrieve()
              .bodyToFlux(Provider.class);
      List<Provider> result = providers.collectList().block();
      user.setTripDeals(result);
      return result;
    } catch (WebClientException e) {
      logger.error(e.toString());
    }
    return new ArrayList<>();
  }

  /**
   * @inheritDoc
   */
  @Override
  public Mono<VisitedLocation> trackUserLocation(User user) {
    logger.debug("trackUserLocation launched for user id: "+user.getUserId());
    final String getLocationUri = "/getUserLocation?userId=" + user.getUserId();
    return webClientGps.get()
            .uri(getLocationUri)
            .retrieve()
            .bodyToMono(VisitedLocation.class)
            .map(visitedLocation -> {
              saveNewVisitedLocation(user, visitedLocation);
              return visitedLocation;
            })
            .retryWhen(retry());
  }

  /**
   * Use to simplify save of a New VisitedLocation of a user.
   *
   * @param user            User
   * @param visitedLocation VisitedLocation
   */
  private void saveNewVisitedLocation(User user,
                                      VisitedLocation visitedLocation) {
    logger.debug("saveNewVisitedLocation launched for user id: "+user.getUserId());
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
            new Location(visitedLocation.getLocation().getLatitude(),
                    visitedLocation.getLocation().getLongitude()),
            visitedLocation.getTimeVisited()));
    rewardsService.calculateRewards(user, getAttractions());

  }

  /**
   * @inheritDoc
   */
  @Override
  public List<Attraction> getAllAttractionsFromGpsMicroService() {
    logger.debug("getAllAttractionsFromGpsMicroService launched");
    final String attractionUri = "/getAttractions";
    Flux<Attraction> attractionsFlux = webClientGps.get()
            .uri(attractionUri)
            .retrieve()
            .bodyToFlux(Attraction.class);
    List<Attraction> listOfAttraction = attractionsFlux.collectList()
            .block();
    setAttractions(listOfAttraction);
    return listOfAttraction;
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<Attraction> getAttractions() {
    return attractions;
  }

  /**
   * @inheritDoc
   */
  @Override
  public List<Attraction> getNearByAttractions(
          final VisitedLocation visitedLocation) {
    logger.debug("getNearByAttractions launched");
    List<Attraction> listOfAttraction = getAttractions();

    List<Attraction> nearbyFiveAttractions = listOfAttraction.stream()
            .sorted(Comparator.comparingDouble(a -> rewardsService
                    .getDistance(a, visitedLocation.getLocation())))
            .limit(SIZE_OF_NEARBY_ATTRACTIONS_LIST)
            .collect(Collectors.toList());
    nearbyFiveAttractions.forEach(a -> logger
            .debug("getNearByAttractions:" + a.attractionName));

    return nearbyFiveAttractions;
  }

  /**
   * Use to stop the Tracker started by the TourGuideService constructor.
   */
  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        tracker.stopTracking();
      }
    });
  }


  /**
   * @inheritDoc
   */
  @Override
  public List<VisitedLocation> getAllUsersLocations() {
    logger.debug("getAllUserlocations launched");
    List<VisitedLocation> allVisitedLocations = new ArrayList<>();
    for (User user : getAllUsers()) {
      allVisitedLocations.addAll(user.getVisitedLocations());
    }
    return allVisitedLocations;

  }

  /**
   * @inheritDoc
   */
  @Override
  public Map<UUID, Location> getAllCurrentLocation() {
    logger.debug("getAllCurrentLocation launched");
    Map<UUID, Location> locations = new HashMap<>();
    for (User user : getAllUsers()) {
      Location loc = user.getLastVisitedLocation() != null ?
              user.getLastVisitedLocation().getLocation() : null;
      locations.put(user.getUserId(), loc);
    }
    return locations;
  }

  /**********************************************************************************
   *
   * Methods Below: For Internal Testing
   *
   **********************************************************************************/
  private static final String tripPricerApiKey = "test-server-api-key";
  // Database connection will be used for external users, but for testing purposes internal users
  // are provided and stored in memory
  private final Map<String, User> internalUserMap = new HashMap<>();

  private void initializeInternalUsers() {
    getAllAttractionsFromGpsMicroService();
    IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
      String userName = "internalUser" + i;
      String phone = "000";
      String email = userName + "@tourGuide.com";
      User user = new User(UUID.randomUUID(), userName, phone, email);
      generateUserLocationHistory(user);

      internalUserMap.put(userName, user);
    });
    logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
  }

  private void generateUserLocationHistory(User user) {
    IntStream.range(0, 3).forEach(i -> {
      user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
              new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
    });
  }

  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }

  public void setAttractions(final List<Attraction> pAttractions) {
    attractions = pAttractions;
  }

}
