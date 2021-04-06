package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class VisitedLocationTest {
  @Test
  public void testSetUserId() {
    VisitedLocation visitedLocation = new VisitedLocation();
    UUID randomUUIDResult = UUID.randomUUID();
    visitedLocation.setUserId(randomUUIDResult);
    assertSame(randomUUIDResult, visitedLocation.getUserId());
  }

  @Test
  public void testSetLocation() {
    VisitedLocation visitedLocation = new VisitedLocation();
    Location location = new Location(10.0, 10.0);
    visitedLocation.setLocation(location);
    assertSame(location, visitedLocation.getLocation());
  }

  @Test
  public void testSetTimeVisited() {
    VisitedLocation visitedLocation = new VisitedLocation();
    Date date = new Date(1L);
    visitedLocation.setTimeVisited(date);
    assertSame(date, visitedLocation.getTimeVisited());
  }
}

