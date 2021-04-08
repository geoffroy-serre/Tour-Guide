package com.tourGuide.webClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("docker")
public class WebClientUriConfigDocker {

    /**
     * TripPricer microservice url : http://trippricer:8083.
     * @return WebClient
     */
    @Bean
    public WebClient getWebClientTripPricer() {
      return WebClient.create("http://trippricer:8083");
    }

    /**
     * GpsUtil microservice url : http://gpsutils:8081.
     * @return WebClient
     */
    @Bean
    public WebClient getWebClientGps() {
      return WebClient.create("http://gpsutils:8081");
    }

    /**
     * RewardCenter microservice url : http://rewardscenter:8082.
     * @return WebClient
     */
    @Bean
    public WebClient getWebClientRewardCenter() {
      return WebClient.create("http://rewardscenter:8082");
    }
  }


