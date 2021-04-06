package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class NearbyAttractionTest {
  @Test
  public void testConstructor() {
    Location location = new Location(10.0, 10.0);
    double pDistance = 10.0;
    int pUserReward = 1;
    NearbyAttraction actualNearbyAttraction = new NearbyAttraction(location, pDistance, pUserReward);
    assertSame(location, actualNearbyAttraction.getAttractionLocation());
    assertEquals(1, actualNearbyAttraction.getUserReward());
    assertEquals(10.0, actualNearbyAttraction.getDistance());
  }
}

