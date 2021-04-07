package com.tourGuide.service;





import com.tourGuide.model.*;
import java.time.Duration;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class RewardsServiceImpl implements RewardsService {
  private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
  private static final int defaultProximityBuffer = 10; // in Miles.
  private int proximityBuffer = defaultProximityBuffer;
  private static final int attractionProximityRange = 200;
  WebClient webClientRewardCenter;
  Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);

  public RewardsServiceImpl(@Qualifier("getWebClientRewardCenter") WebClient pWebClientRewardCenter) {
    this.webClientRewardCenter = pWebClientRewardCenter;
    logger.debug("RewardsServiceImpl constructor initialized");
  }

  /**
   * @inheritDoc
   */
  @Override
  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
    logger.debug("RewardsServiceImpl proximity buffer setted");
  }

  /**
   * @inheritDoc
   */
  @Override
  public void setDefaultProximityBuffer() {
    proximityBuffer = defaultProximityBuffer;
    logger.debug("RewardsServiceImpl default proximity buffer setted");
  }

  /**
   * @inheritDoc
   */
  @Override
  public void calculateRewards(final User user,
                               final List<Attraction> attractions) {
    logger.debug("Calculating user rewards");
    user.getVisitedLocations().forEach(visitedLocation -> {
      attractions.stream()
              .filter(attraction -> nearAttraction(visitedLocation, attraction))
              .forEach(attraction -> {
                if (user.getUserRewards().stream().noneMatch(
                        r -> r.attraction.attractionName
                                .equals(attraction.attractionName))) {
                  getRewardPoints(visitedLocation, attraction, user).subscribe();
                }
              });
    });
    logger.debug("user reward calculated");
  }

  /**
   * @inheritDoc
   */
  @Override
  public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    logger.debug("isWithinAttractionProximity launched");
    return !(getDistance(attraction, location) > attractionProximityRange);
  }

  /**
   * @inheritDoc
   */
  @Override
  public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    logger.debug("nearAttraction launched");
    assert visitedLocation.getLocation() != null;
    return !(getDistance(attraction, visitedLocation.getLocation()) > proximityBuffer);
  }

  /**
   * @inheritDoc
   */
  @Override
  public Mono<Integer> getRewardPoints(VisitedLocation visitedLocation, Attraction attraction,
                                       User user) {
    logger.debug("getRewardsPoints launched");
    return webClientRewardCenter.get().uri("/getAttractionRewardsPoints?attractionId=" + attraction.attractionId + "&userId=" + user.getUserId())
            .retrieve()
            .bodyToMono(Integer.class)
            .map(reward -> {
              user.addUserReward(new UserReward(visitedLocation, attraction, reward));
              return reward;
            })
            .retryWhen(retry());
  }

  /**
   * @inheritDoc
   */
  @Override
  public double getDistance(Location loc1, Location loc2) {
    logger.debug("getDistance launched");
    double lat1 = Math.toRadians(loc1.getLatitude());
    double lon1 = Math.toRadians(loc1.getLongitude());
    double lat2 = Math.toRadians(loc2.getLatitude());
    double lon2 = Math.toRadians(loc2.getLongitude());
    double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    double nauticalMiles = 60 * Math.toDegrees(angle);
    logger.debug("getDistance finished distance = " + STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles);
    return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
  }

  /**
   * @inheritDoc
   */
  @Override
  public Retry retry() {
    return Retry
            .backoff(60,
                    Duration.ofSeconds(5));
  }
}
