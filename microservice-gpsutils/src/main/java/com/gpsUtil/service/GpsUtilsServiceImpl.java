package com.gpsUtil.service;

import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class GpsUtilsServiceImpl implements GpsUtilService {

  @Autowired
  private GpsUtil gpsUtil;

  Logger logger = LoggerFactory.getLogger(GpsUtilsServiceImpl.class);

  /**
   * @inheritDoc
   */
  @Override
  public Mono<VisitedLocation> getUserLocation(UUID userId) {
    logger.debug("getUserLocation launched with id: " + userId);
    Locale.setDefault(Locale.US);
    return Mono.just(gpsUtil.getUserLocation(userId));
  }

  /**
   * @inheritDoc
   */
  @Override
  public Flux<Attraction> getAttractions() {
    logger.debug("getAttractions launched");
    return Flux.fromIterable(gpsUtil.getAttractions());
  }
}
