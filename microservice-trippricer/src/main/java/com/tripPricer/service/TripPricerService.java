package com.tripPricer.service;

import java.util.UUID;
import reactor.core.publisher.Flux;
import tripPricer.Provider;

public interface TripPricerService {

  /**
   * Return a List of Provider with pertinents offers depending of user preferences.
   *
   * @param tripPricerApiKey       String
   * @param userId                 UUID
   * @param nbrOfAdult             int
   * @param nbrOfChildren          int
   * @param tripDuration           int
   * @param cumulativeRewardPoints int
   * @return Flux<Provider>
   */
  Flux<Provider> getPrice(String tripPricerApiKey, UUID userId, int nbrOfAdult,
                          int nbrOfChildren, int tripDuration, int cumulativeRewardPoints);
}

