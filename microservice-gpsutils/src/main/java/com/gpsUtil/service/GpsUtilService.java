package com.gpsUtil.service;

import java.util.UUID;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GpsUtilService {
  /**
   * Return a visitedLocation. Locale is set to US to avoir Decimal issue with , insted of .
   * @param userId UUID
   * @return Mono<VisitedLocation>
   */
  Mono<VisitedLocation> getUserLocation(UUID userId);
  /**
   * Get All attractions
   * @return Flux<Attraction>
   */
  Flux<Attraction> getAttractions();
}
