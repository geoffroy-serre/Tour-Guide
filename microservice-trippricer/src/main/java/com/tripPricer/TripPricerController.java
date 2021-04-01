package com.tripPricer;

import com.tripPricer.service.TripPricerServiceImpl;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tripPricer.Provider;

@RestController
public class TripPricerController {
  Logger logger = LoggerFactory.getLogger(TripPricerController.class);
  @Autowired
  TripPricerServiceImpl tripPricerService;

  @GetMapping("/getPrice")
  Flux<Provider> getPrice(@RequestParam String tripPricerApiKey, @RequestParam UUID userId,
                          @RequestParam int nbrOfAdult,
                          @RequestParam int nbrOfChildren, @RequestParam int tripDuration,
                          @RequestParam int cumulativeRewardPoints) {
    logger.debug("Call of getPrice with tripPricerApiKey: " + tripPricerApiKey + " and userId: " + userId + " nbrOfAdult: " + nbrOfAdult + " nbrOfChildren: " + nbrOfChildren + "and tripDuration: " + tripDuration + " cumulativeRewardsPpoints: " + cumulativeRewardPoints);
    return tripPricerService.getPrice(tripPricerApiKey, userId, nbrOfAdult, nbrOfChildren,
            tripDuration, cumulativeRewardPoints);
  }


}
