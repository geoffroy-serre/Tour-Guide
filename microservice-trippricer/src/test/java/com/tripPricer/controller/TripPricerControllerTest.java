package com.tripPricer.controller;

import com.tripPricer.service.TripPricerServiceImpl;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import tripPricer.Provider;
import tripPricer.TripPricer;

@WebFluxTest(controllers = TripPricerController.class)
@ExtendWith(SpringExtension.class)
public class TripPricerControllerTest {

  @MockBean
  private TripPricerServiceImpl tripPricerServiceImpl;

  @MockBean
  private TripPricer tripPricer;

  @Autowired
  private WebTestClient webClient;

  @Test
  public void testGetPrice() {
    UUID uuid = UUID.randomUUID();
    webClient.get().uri(
            uriBuilder ->
                    uriBuilder
                            .path("/getPrice")
                            .queryParam("tripPricerApiKey", "ApiKey")
                            .queryParam("userId", uuid)
                            .queryParam("nbrOfAdult", 1)
                            .queryParam("nbrOfChildren", 1)
                            .queryParam("tripDuration", 1)
                            .queryParam("cumulativeRewardPoints", "1")
                            .build())

            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Provider.class);

  }

  @Test
  public void testGetPriceMissOnePAram() {
    UUID uuid = UUID.randomUUID();
    webClient.get().uri(
            uriBuilder ->
                    uriBuilder
                            .path("/getPrice")
                            .queryParam("userId", uuid)
                            .queryParam("nbrOfAdult", 1)
                            .queryParam("nbrOfChildren", 1)
                            .queryParam("tripDuration", 1)
                            .queryParam("cumulativeRewardPoints", "1")
                            .build())

            .exchange()
            .expectStatus().isBadRequest();

  }

  @Test
  public void testGetPriceNoParam() {
    webClient.get().uri(
            uriBuilder ->
                    uriBuilder
                            .path("/getPrice")
                            .build())

            .exchange()
            .expectStatus().isBadRequest();

  }
}

