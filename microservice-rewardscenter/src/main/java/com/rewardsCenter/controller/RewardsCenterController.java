package com.rewardsCenter.controller;


import com.rewardsCenter.service.RewardsService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RewardsCenterController {

  private static final Logger logger = LoggerFactory.getLogger(RewardsCenterController.class);

  @Autowired
  RewardsService rewardsService;

  @GetMapping("/getAttractionRewardsPoints")
  public Mono<Integer> getAttractionRewardsPoint(@RequestParam UUID attractionId,
                                             @RequestParam UUID userId) {
    logger.debug("Call to getRewards with attraction: " + attractionId + " and userId: " + userId);
    return rewardsService.getRewardPoints(attractionId, userId);
  }


}
