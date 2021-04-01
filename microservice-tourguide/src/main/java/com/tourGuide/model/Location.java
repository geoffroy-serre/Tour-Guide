package com.tourGuide.model;

public class Location {
  private  double longitude;
  private  double latitude;

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public Location(){
    longitude = 0.0;
    latitude = 0.0;
  }

  public Location(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }
}
