package com.rewardsCenter.service;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rewardCentral.RewardCentral;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RewardCentral.class, RewardsServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class RewardsServiceImplTest {

  @MockBean
  private RewardCentral rewardCentral;

  @Autowired
  private RewardsServiceImpl rewardsServiceImpl;

  @Test
  public void testGetRewardPoints() {
    UUID attractionId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    when(rewardCentral.getAttractionRewardPoints(attractionId, userId)).thenReturn(5);
    rewardsServiceImpl.getRewardPoints(attractionId, userId);
    Mockito.verify(rewardCentral, Mockito.times(1)).getAttractionRewardPoints(attractionId, userId);
  }

}

