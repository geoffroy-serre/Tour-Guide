package com.gpsUtil.controller;

import com.gpsUtil.service.GpsUtilService;
import com.gpsUtil.service.GpsUtilsServiceImpl;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@WebFluxTest(controllers = GpsUtilsController.class)
@ExtendWith(SpringExtension.class)
public class GpsUtilsControllerTest {
  @MockBean
  private GpsUtilsServiceImpl gpsUtilsService;

  @MockBean
  private GpsUtil gpsUtil;

  @Autowired
  private WebTestClient webClient;

  @Test
  public void getAttractions() {
    webClient.get().uri(
            uriBuilder ->
                    uriBuilder
                            .path("/getAttractions")
                            .build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Attraction.class);

  }


  @Test
  public void getUserLocation() {
    UUID uuid = UUID.randomUUID();
    webClient.get().uri(
            uriBuilder ->
                    uriBuilder
                            .path("/getUserLocation")
                            .queryParam("userId", uuid)
                            .build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(VisitedLocation.class);

  }

  @Test
  public void getUserLocationNoParam() {
    UUID uuid = UUID.randomUUID();
    webClient.get().uri("/getUserLocation")
            .exchange()
            .expectStatus().isBadRequest();
  }

  @Test
  public void getUserLocationWrongParam() {
    UUID uuid = UUID.randomUUID();
    webClient.get().uri(
            uriBuilder ->
                    uriBuilder
                            .path("/getUserLocation")
                            .queryParam("wrongParam", uuid)
                            .build())
            .exchange()
            .expectStatus().isBadRequest();

  }
}

