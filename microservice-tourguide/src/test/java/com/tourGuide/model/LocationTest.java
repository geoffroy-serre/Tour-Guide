package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LocationTest {
  @Test
  public void testSetLongitude() {
    Location location = new Location(10.0, 10.0);
    double longitude = 10.0;
    location.setLongitude(longitude);
    assertEquals(10.0, location.getLongitude());
  }

  @Test
  public void testSetLatitude() {
    Location location = new Location(10.0, 10.0);
    double latitude = 10.0;
    location.setLatitude(latitude);
    assertEquals(10.0, location.getLatitude());
  }

  @Test
  public void testConstructor() {
    double latitude = 10.0;
    double longitude = 10.0;
    Location actualLocation = new Location(latitude, longitude);
    assertEquals(10.0, actualLocation.getLatitude());
    assertEquals(10.0, actualLocation.getLongitude());
  }
}

