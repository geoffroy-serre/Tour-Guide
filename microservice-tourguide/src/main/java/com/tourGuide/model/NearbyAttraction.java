package com.tourGuide.model;

/**
 * Model is used as answer to getNearbyAttractions request.
 *
 */
public class NearbyAttraction {


    private Location attractionLocation;
    private double distance;
    private int userReward;
    private NearbyAttraction() {
    }


    public NearbyAttraction(final Location pAttractionLocation,
                            final double pDistance, final int pUserReward) {
        this();
        attractionLocation = pAttractionLocation;
        distance = pDistance;
        userReward = pUserReward;
    }


    public Location getAttractionLocation() {
        return attractionLocation;
    }
    public double getDistance() {
        return distance;
    }
    public int getUserReward() {
        return userReward;
    }

}
