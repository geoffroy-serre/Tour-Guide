package com.gpsUtil.service;

import java.util.UUID;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GpsUtilService {
  Mono<VisitedLocation> getUserLocation(UUID userId);
  Flux<Attraction> getAttractions();
}
