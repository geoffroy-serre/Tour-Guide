package com.tripPricer.service;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public interface TripPricerService {

  @Autowired
  TripPricer tripPricer = new TripPricer();

  Flux<Provider> getPrice(String tripPricerApiKey, UUID userId, int nbrOfAdult,
                                 int nbrOfChildren, int tripDuration, int cumulativeRewardPoints);
}

