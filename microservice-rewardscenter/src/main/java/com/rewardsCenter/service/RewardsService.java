package com.rewardsCenter.service;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface RewardsService {
  /**
   * Retrieve rewards point for a given user
   * @param attractionId UUID
   * @param userId UUID
   * @return Mono<Integer>
   */
  Mono<Integer> getRewardPoints(UUID attractionId, UUID userId);
}

