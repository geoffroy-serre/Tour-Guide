package com.tourGuide.model;

import java.util.Map;

public class AttractionsSuggestion {


    private Location userLocation;
    private Map<String, NearbyAttraction> suggestedAttractions;


    public AttractionsSuggestion() {
    }


    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(final Location pUserLocation) {
        userLocation = pUserLocation;
    }


    public Map<String, NearbyAttraction> getSuggestedAttraction() {
        return suggestedAttractions;
    }


    public void setSuggestedAttractions(
            final Map<String, NearbyAttraction> pSuggestedAttractions) {
        suggestedAttractions = pSuggestedAttractions;
    }


    @Override
    public String toString() {
        return "AttractionsSuggestionDTO [userLocation=" + userLocation
                + ", suggestedAttractions=" + suggestedAttractions + "]";
    }

}
