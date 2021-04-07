package com.tourGuide.service;

import com.tourGuide.model.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface TourGuideService {

  /**
   * Return an AttractionSuggestion object. It contains the five closest tourists attractions
   * Disregard the distance containing:
   * - Name of the Attraction.
   * - Attraction location lat/long.
   * - User location lat/long.
   * - The distance between the user and the location.
   * - The rewards points for each attractions.
   * The first attraction is the closest one, the last the further one.
   *
   * @param user User
   * @return AttractionSuggestion AttractionSuggestion
   */
  AttractionsSuggestion getAttractionsSuggestion(final User user);

  /**
   * Return the rewards list for given user.
   *
   * @param user User
   * @return List<Reward>
   */
  List<UserReward> getUserRewards(User user);

  /**
   * Gateway to call GpsUtils MicroService to get the Location of the given user.
   *
   * @param user User
   * @return Mono<VisitedLocation>
   */
  Mono<VisitedLocation> getUserLocation(User user);

  /**
   * Retrieves the User with the given username. Is case sensitive.
   *
   * @param userName String
   * @return User
   */
  User getUser(String userName);

  /**
   * Retrieves the Users.
   *
   * @return List<User>
   */
  List<User> getAllUsers();

  /**
   * Add the given User to existing datas.
   *
   * @param user User
   */
  void addUser(User user);

  /**
   * Return a list of Provider concordings to User preferences.
   *
   * @param user User
   * @return List<Provider>
   */
  List<Provider> getTripDeals(User user);

  /**
   * Update the given user's location by calling microService GpsUtil.
   *
   * @param user User
   * @return Mono<VisitedLocation>
   */
  Mono<VisitedLocation> trackUserLocation(User user);

  /**
   * Return All the Attraction known by GpsUtils MicroService
   *
   * @return List<Attraction>
   */
  List<Attraction> getAllAttractionsFromGpsMicroService();

  /**
   * Use to get Attractions List, provided for internal class use.
   *
   * @return List<Attraction>
   */
  List<Attraction> getAttractions();

  /**
   * Get the list of the n closest attractions. The number n is defined by the
   * SIZE_OF_NEARBY_ATTRACTIONS_LIST constant.
   *
   * @param visitedLocation VisitedLocation
   * @return List<Attraction>
   */
  List<Attraction> getNearByAttractions(
          final VisitedLocation visitedLocation);

  /**
   * Return All the users location, past and present.
   * WARNING: It can result in a large List size.
   *
   * @return List<VisitedLocation>
   */
  List<VisitedLocation> getAllUsersLocations();

  /**
   * Return all users last Location.
   *
   * @return Map<UUID, Location>
   */
  Map<UUID, Location> getAllCurrentLocation();

}

