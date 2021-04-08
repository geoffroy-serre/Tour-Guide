package com.tripPricer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tripPricer.TripPricer;


@Configuration
public class TripPricerModule {

  @Bean
  public TripPricer getTripPricer() {
    return new TripPricer();
  }


}
