package com.tourGuide.model;


import java.util.Date;
import java.util.UUID;
import reactor.core.Disposable;

public class VisitedLocation {
  private UUID userId;
  private Location location;
  private Date timeVisited;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public void setTimeVisited(Date timeVisited) {
    this.timeVisited = timeVisited;
  }

  public Location getLocation() {
    return location;
  }

  public Date getTimeVisited() {
    return timeVisited;
  }

  public VisitedLocation(){
    timeVisited = null;
    location = null;
    userId = null;
  }

  public VisitedLocation(UUID userId, Location location, Date timeVisited) {
    this.userId = userId;
    this.location = location;
    this.timeVisited = timeVisited;
  }
}
