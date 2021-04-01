package com.tourGuide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
public class TourGuideApp {

  public static void main(String[] args) {
    SpringApplication.run(TourGuideApp.class, args);
  }

}
