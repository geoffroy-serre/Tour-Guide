package com.rewardsCenter.service;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface RewardsService {
  Mono<Integer> getRewardPoints(UUID attractionId, UUID userId);
}

