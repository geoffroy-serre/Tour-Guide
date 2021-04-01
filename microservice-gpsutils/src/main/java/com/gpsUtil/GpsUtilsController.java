package com.gpsUtil;

import com.gpsUtil.service.GpsUtilService;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
public class GpsUtilsController {
  Logger logger = LoggerFactory.getLogger(GpsUtilsController.class);

  @Autowired
  GpsUtilService gpsUtilsService;

  @GetMapping("/getUserLocation")
  public Mono<VisitedLocation> getUserLocation(@RequestParam UUID userId) {
    logger.debug("Call of getUserLocation with "+userId);
    return gpsUtilsService.getUserLocation(userId);
  }

  @GetMapping("/getAttractions")
  public Flux<Attraction> getAttractions() {
    logger.debug("Call of getAttractions");
    return gpsUtilsService.getAttractions();
  }


  

}
