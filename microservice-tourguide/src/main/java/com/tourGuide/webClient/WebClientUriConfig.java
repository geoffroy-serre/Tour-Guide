package com.tourGuide.webClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientUriConfig {

  /**
   * TripPricer microservice url : http://localhost:8083.
   * @return WebClient
   */
  @Bean
  public WebClient getWebClientTripPricer() {
    return WebClient.create("http://localhost:8083");
  }

  /**
   * GpsUtil microservice url : http://localhost:8081.
   * @return WebClient
   */
  @Bean
  public WebClient getWebClientGps() {
    return WebClient.create("http://localhost:8081");
  }

  /**
   * RewardCenter microservice url : http://localhost:8082.
   * @return WebClient
   */
  @Bean
  public WebClient getWebClientRewardCenter() {
    return WebClient.create("http://localhost:8082");
  }
}
