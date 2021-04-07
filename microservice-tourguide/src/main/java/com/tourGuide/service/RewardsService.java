package com.tourGuide.service;


import com.tourGuide.model.Attraction;
import com.tourGuide.model.Location;
import com.tourGuide.model.User;
import com.tourGuide.model.VisitedLocation;
import java.util.List;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public interface RewardsService {

  /**
   * Set the proximity buffer.
   * It determines the area of proximity  between two locations allowed.
   *
   * @param proximityBuffer int
   */
  void setProximityBuffer(int proximityBuffer);

  /**
   * User to change the proximity buffer.
   * It determines the area of proximity  between two locations allowed.
   */
  void setDefaultProximityBuffer();

  /**
   * Calculate rewards for given User.
   * If the user have a new rewards available, it is save directly after the calculation.
   *
   * @param user        User
   * @param attractions List<Attraction>
   */
  void calculateRewards(final User user, final List<Attraction> attractions);

  /**
   * Determine if a location is near the attraction.
   * It use the proximity buffer .
   *
   * @param attraction Attraction.
   * @param location   Location.
   * @return boolean True if it's near.
   */
  boolean isWithinAttractionProximity(Attraction attraction, Location location);

  /**
   * Use to know if a visitedLocation of a user is near of an attraction.
   * This method is use to calculate rewards.
   *
   * @param visitedLocation VisitedLocation
   * @param attraction      Attraction
   * @return boolean True if it's near.
   */
  boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

  /**
   * Use to get the right amount of point for a VisitedAttraction.
   * Only get points. It assume you have previously verify the proximity of the visited Location
   * It add the rewards in the user rewards list.
   *
   * @param visitedLocation VisitedLocation
   * @param attraction      Attraction
   * @param user            User
   * @return int rewards point
   */
  Mono<Integer> getRewardPoints(VisitedLocation visitedLocation, Attraction attraction,
                                User user);

  /**
   * Get the distance between two location. (latitude & longitude)
   *
   * @param loc1
   * @param loc2
   * @return
   */
  double getDistance(Location loc1, Location loc2);

  /**
   * Method used to test that proximity buffer cant be set.
   * @return int DEFAULT_PROXIMITY_BUFFER
   */
  public int getDefaultProximityBuffer();

  /**
   * use to define the Retry condition of the web client in case of exception.
   * default is 60 attempts max and 5 seconds pause between retry.
   * Retry time is exponential.
   *
   * @return Retry
   */
  Retry retry();
}


