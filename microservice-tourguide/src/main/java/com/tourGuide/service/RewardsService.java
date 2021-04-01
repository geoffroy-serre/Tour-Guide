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
public class RewardsService {
  private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

  // proximity in miles
  private final int defaultProximityBuffer = 10;
  private int proximityBuffer = defaultProximityBuffer;
  private final int attractionProximityRange = 200;

  WebClient webClientGps;
  WebClient webClientTripPricer;
  WebClient webClientRewardCenter;


  public RewardsService(@Qualifier("getWebClientRewardCenter") WebClient pWebClientRewardCenter) {
    this.webClientRewardCenter = pWebClientRewardCenter;
  }

  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
  }

  public void setDefaultProximityBuffer() {
    proximityBuffer = defaultProximityBuffer;
  }

  /**
   * points for visiting each Attraction.
   *
   * @param user User
   */
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

  public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
    return !(getDistance(attraction, location) > attractionProximityRange);
  }

  private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    assert visitedLocation.getLocation() != null;
    return !(getDistance(attraction, visitedLocation.getLocation()) > proximityBuffer);
  }

  public Mono<Integer> getRewardPoints(VisitedLocation visitedLocation, Attraction attraction,
                                       User user) {
    return webClientRewardCenter.get().uri("/getAttractionRewardsPoints&attractionId=" + attraction.attractionId + "&userId=" + user.getUserId())
            .retrieve()
            .bodyToMono(Integer.class)
            .map(reward -> {
              user.addUserReward(new UserReward(visitedLocation, attraction, reward));
              return reward;
            })
            .retryWhen(retry());
  }

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

  private Retry retry() {
    return Retry
            .backoff(100,
                    Duration.ofSeconds(10));
  }

}
