package com.tourGuide.model;

import java.util.UUID;

public class Provider {
  public final String name;
  public final double price;
  public final UUID tripId;

  public Provider(){
    name = null;
    price = 0.0;
    tripId=null;
  }

  public Provider(UUID tripId, String name, double price) {
    this.name = name;
    this.tripId = tripId;
    this.price = price;
  }
}
