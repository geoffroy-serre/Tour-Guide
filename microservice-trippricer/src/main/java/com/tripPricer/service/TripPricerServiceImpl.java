package com.tripPricer.service;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TripPricerServiceImpl implements TripPricerService {

  @Autowired
  TripPricer tripPricer = new TripPricer();

  Logger logger = LoggerFactory.getLogger(TripPricerServiceImpl.class);

  /**
   * @inheritDoc
   */
  @Override
  public Flux<Provider> getPrice(String tripPricerApiKey, UUID userId, int nbrOfAdult,
                                 int nbrOfChildren, int tripDuration, int cumulativeRewardPoints) {
    logger.debug("Entering getPrice with those following data: " + tripPricerApiKey + " " + userId +
            " " + nbrOfAdult + " " + nbrOfChildren + " " + tripDuration + " " + cumulativeRewardPoints);
    return Flux.fromIterable(tripPricer.getPrice(tripPricerApiKey, userId,
            nbrOfAdult,
            nbrOfChildren,
            tripDuration, cumulativeRewardPoints));
  }
}
