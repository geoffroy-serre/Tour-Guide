package com.tourGuide.service;


import com.tourGuide.model.*;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;


@Service
public class RewardsServiceImpl implements RewardsService {
  private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

  // proximity in miles
  private static final int defaultProximityBuffer = 10;
  private int proximityBuffer = defaultProximityBuffer;
  private static final int attractionProximityRange = 200;

  WebClient webClientRewardCenter;


  public RewardsServiceImpl(@Qualifier("getWebClientRewardCenter") WebClient pWebClientRewardCenter) {
    this.webClientRewardCenter = pWebClientRewardCenter;
  }

  @Override
  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
  }

  @Override
  public void setDefaultProximityBuffer() {
    proximityBuffer = defaultProximityBuffer;
  }

  /**
   * points for visiting each Attraction.
   *
   * @param user User
   */
  @Override
  public void calculateRewards(final User user,
                               final List<Attraction> attractions) {
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
  }

  ;

  @Override
  public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    return !(getDistance(attraction, location) > attractionProximityRange);
  }

  @Override
  public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    assert visitedLocation.getLocation() != null;
    return !(getDistance(attraction, visitedLocation.getLocation()) > proximityBuffer);
  }

  @Override
  public Mono<Integer> getRewardPoints(VisitedLocation visitedLocation, Attraction attraction,
                                       User user) {
    return webClientRewardCenter.get().uri("/getAttractionRewardsPoints?attractionId=" + attraction.attractionId + "&userId=" + user.getUserId())
            .retrieve()
            .bodyToMono(Integer.class)
            .map(reward -> {
              user.addUserReward(new UserReward(visitedLocation, attraction, reward));
              return reward;
            })
            .retryWhen(retry());
  }

  @Override
  public double getDistance(Location loc1, Location loc2) {
    double lat1 = Math.toRadians(loc1.getLatitude());
    double lon1 = Math.toRadians(loc1.getLongitude());
    double lat2 = Math.toRadians(loc2.getLatitude());
    double lon2 = Math.toRadians(loc2.getLongitude());
    double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    double nauticalMiles = 60 * Math.toDegrees(angle);
    return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
  }

  @Override
  public Retry retry() {
    return Retry
            .backoff(60,
                    Duration.ofSeconds(5));
  }

}
