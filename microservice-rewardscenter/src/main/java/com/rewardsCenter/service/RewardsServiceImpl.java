package com.rewardsCenter.service;


import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import rewardCentral.RewardCentral;

@Service
public class RewardsServiceImpl implements RewardsService{

	@Autowired
	RewardCentral rewardsCentral;

	/**
	 *
	 * @inheritDoc
	 */
	@Override
	public Mono<Integer> getRewardPoints(UUID attractionId, UUID userId) {
		return Mono.just(rewardsCentral.getAttractionRewardPoints(attractionId, userId));
	}
}
