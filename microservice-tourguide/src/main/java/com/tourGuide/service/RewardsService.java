package com.tourGuide.service;


import com.tourGuide.model.Attraction;
import com.tourGuide.model.Location;
import com.tourGuide.model.User;
import com.tourGuide.model.VisitedLocation;
import java.util.List;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public interface RewardsService {


  void setProximityBuffer(int proximityBuffer);

  void setDefaultProximityBuffer();

  void calculateRewards(final User user, final List<Attraction> attractions);

  boolean isWithinAttractionProximity(Attraction attraction, Location location);

  boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

  Mono<Integer> getRewardPoints(VisitedLocation visitedLocation, Attraction attraction,
                                User user);

  double getDistance(Location loc1, Location loc2);

  Retry retry();
}


