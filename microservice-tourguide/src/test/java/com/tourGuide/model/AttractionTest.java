package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class AttractionTest {
  @Test
  public void testConstructor() {
    Attraction actualAttraction = new Attraction();
    assertEquals(0.0, actualAttraction.getLatitude());
    assertNull(actualAttraction.state);
    assertNull(actualAttraction.city);
    assertNull(actualAttraction.attractionName);
    assertNull(actualAttraction.attractionId);
    assertEquals(0.0, actualAttraction.getLongitude());
  }

  @Test
  public void testConstructor2() {
    String attractionName = "Attraction Name";
    String city = "Oxford";
    String state = "MD";
    double latitude = 10.0;
    double longitude = 10.0;
    Attraction actualAttraction = new Attraction(attractionName, city, state, latitude, longitude);
    assertEquals(10.0, actualAttraction.getLatitude());
    assertEquals("MD", actualAttraction.state);
    assertEquals("Oxford", actualAttraction.city);
    assertEquals("Attraction Name", actualAttraction.attractionName);
    assertEquals(10.0, actualAttraction.getLongitude());
  }
}

