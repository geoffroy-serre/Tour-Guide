package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UserRewardTest {
  @Test
  public void testSetRewardPoints() {
    VisitedLocation visitedLocation = new VisitedLocation();
    UserReward userReward = new UserReward(visitedLocation, new Attraction());
    int rewardPoints = 1;
    userReward.setRewardPoints(rewardPoints);
    assertEquals(1, userReward.getRewardPoints());
  }
}

