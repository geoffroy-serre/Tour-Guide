package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class AttractionsSuggestionTest {
  @Test
  public void testSetUserLocation() {
    AttractionsSuggestion attractionsSuggestion = new AttractionsSuggestion();
    Location location = new Location(10.0, 10.0);
    attractionsSuggestion.setUserLocation(location);
    assertSame(location, attractionsSuggestion.getUserLocation());
  }

  @Test
  public void testSetSuggestedAttractions() {
    AttractionsSuggestion attractionsSuggestion = new AttractionsSuggestion();
    HashMap<String, NearbyAttraction> pSuggestedAttractions = new HashMap<String,
            NearbyAttraction>(1);
    attractionsSuggestion.setSuggestedAttractions(pSuggestedAttractions);
    assertEquals("AttractionsSuggestion[userLocation=null, suggestedAttractions={}]",
            attractionsSuggestion.toString());
  }

  @Test
  public void testToString() {
    AttractionsSuggestion attractionsSuggestion = new AttractionsSuggestion();
    String actualToStringResult = attractionsSuggestion.toString();
    assertEquals("AttractionsSuggestion[userLocation=null, suggestedAttractions=null]",
            actualToStringResult);
  }
}

