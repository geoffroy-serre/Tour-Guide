package com.tripPricer.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TripPricerServiceImpl implements TripPricerService {

  @Autowired
  TripPricer tripPricer = new TripPricer();

  @Override
  public Flux<Provider> getPrice(String tripPricerApiKey, UUID userId, int nbrOfAdult,
                                 int nbrOfChildren, int tripDuration, int cumulativeRewardPoints) {
    return Flux.fromIterable(tripPricer.getPrice(tripPricerApiKey, userId,
            nbrOfAdult,
            nbrOfChildren,
            tripDuration, cumulativeRewardPoints));
  }
}
