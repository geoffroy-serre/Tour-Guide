package com.gpsUtil.service;

import java.util.Locale;
import java.util.UUID;
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

  @Override
  public Mono<VisitedLocation> getUserLocation(UUID userId) {
    Locale.setDefault(Locale.US);
    return Mono.just(gpsUtil.getUserLocation(userId));
  }

  @Override
  public Flux<Attraction> getAttractions() {
    return Flux.fromIterable(gpsUtil.getAttractions());
  }


}
