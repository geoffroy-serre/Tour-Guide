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
public class TourGuideService {


  WebClient webClientGps;
  WebClient webClientTripPricer;

  private List<Attraction> attractions;

  private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
  private final RewardsService rewardsService;
  public final Tracker tracker;
  boolean testMode = true;
  int SIZE_OF_NEARBY_ATTRACTIONS_LIST = 5;


  public TourGuideService(@Qualifier("getWebClientGps") final WebClient pWebClientGps,
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

  public AttractionsSuggestion getAttractionsSuggestion(final User user) {

    AttractionsSuggestion suggestion = new AttractionsSuggestion();
    suggestion.setUserLocation(new Location(
            user.getLastVisitedLocation().getLocation().getLatitude(),
            user.getLastVisitedLocation().getLocation().getLongitude()));

    TreeMap<String, NearbyAttraction> suggestedAttractions =
            new TreeMap<>();
    List<Attraction> attractionsList = getNearByAttractions(
            user.getLastVisitedLocation());
    final AtomicInteger indexHolder = new AtomicInteger(1);
    attractionsList.stream()
            .sorted(Comparator.comparingDouble(a -> rewardsService
                    .getDistance(a,
                            user.getLastVisitedLocation().getLocation())))
            .forEach(a -> {
              final int index = indexHolder.getAndIncrement();
              suggestedAttractions.put(
                      index + ". " + a.attractionName,
                      new NearbyAttraction(
                              new Location(a.getLatitude(),
                                      a.getLongitude()),
                              rewardsService.getDistance(a,
                                      user.getLastVisitedLocation()
                                              .getLocation()),
                              rewardsService.getRewardPoints(
                                      user.getLastVisitedLocation(), a,
                                      user).block()));
            });

    suggestion.setSuggestedAttractions(suggestedAttractions);

    return suggestion;
  }

  public List<UserReward> getUserRewards(User user) {
    return user.getUserRewards();
  }

  public Mono<VisitedLocation> getUserLocation(User user) {
    Mono<VisitedLocation> visitedLocation = (user.getVisitedLocations().size() > 0) ?
            Mono.just(user.getLastVisitedLocation()) :
            trackUserLocation(user);
    return visitedLocation;
  }

  public User getUser(String userName) {
    return internalUserMap.get(userName);
  }

  public List<User> getAllUsers() {
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  public void addUser(User user) {
    if (!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
  }

  public List<Provider> getTripDeals(User user) {
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

  private Retry retry() {
    return Retry
            .backoff(100,
                    Duration.ofSeconds(10));
  }

  /**
   * @param user User
   * @return CompletableFuture<VisitedLocation>
   */
  public Mono<VisitedLocation> trackUserLocation(User user) {

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

  private void saveNewVisitedLocation(final User user,
                                      final VisitedLocation visitedLocationDTO) {
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
            new Location(visitedLocationDTO.getLocation().getLatitude(),
                    visitedLocationDTO.getLocation().getLongitude()),
            visitedLocationDTO.getTimeVisited()));
    rewardsService.calculateRewards(user, getAttractions());

  }

  public List<Attraction> getAttractions() {
    return attractions;
  }

  /**
   * Return the five closest attractions from the last position.
   *
   * @param visitedLocation VisitedLocation
   * @return List<Attraction>
   */
  public List<Attraction> getNearByAttractions(
          final VisitedLocation visitedLocation) {
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

  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        tracker.stopTracking();
      }
    });
  }

  /**
   * Async
   * Use to get all the visited locations of all users.
   *
   * @return CompletableFuture<List < VisitedLocation>>
   */

  public List<VisitedLocation> getAllUsersLocations() {

    List<VisitedLocation> allVisitedLocations = new ArrayList<>();
    for (User user : getAllUsers()) {
      allVisitedLocations.addAll(user.getVisitedLocations());
    }
    return allVisitedLocations;

  }

  public Map<UUID, Location> getAllCurrentLocation() {
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

}
