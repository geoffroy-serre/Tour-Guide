package com.tourGuide;

import com.jayway.jsonpath.JsonPath;
import com.jsoniter.output.JsonStream;
import com.tourGuide.exception.UserNameNotFound;
import com.tourGuide.helper.InternalTestHelper;
import com.tourGuide.model.*;
import com.tourGuide.service.RewardsService;
import com.tourGuide.service.TourGuideService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Attr;

import reactor.core.publisher.Flux;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestTourGuideController {

  @Autowired
  private MockMvc mvc;

  @Autowired
  TourGuideService tourGuideService;

  @Autowired
  RewardsService rewardsService;

  WebClient webClientGps = WebClient.create("http://localhost:8081");

  Flux<Attraction> attractions =
          webClientGps.get().uri("/getAttractions").retrieve().bodyToFlux(Attraction .class);
  List<Attraction> attractionList = attractions.collectList().block();

  @BeforeEach
  public void setup(){
    InternalTestHelper.setInternalUserNumber(1);
  }


  @Test
  public void getLocationNoPAram() throws Exception {
    mvc.perform(get("/getLocation")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getLocationUserUnknown() throws Exception {
    mvc.perform(get("/getLocation")
            .param("userName", "IamUnknow")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void getLocation() throws Exception {
    MvcResult result = mvc.perform(get("/getLocation")
            .param("userName", "internalUser0")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
    System.out.println(result.getResponse().getContentAsString());
    Assertions.assertTrue(result.getResponse().getContentAsString().contains("latitude"));
    Assertions.assertTrue(result.getResponse().getContentAsString().contains("longitude"));
    Assertions.assertEquals(200, result.getResponse().getStatus());
  }

@Test
  public void getNearbyAttractions() throws Exception {
    MvcResult result =mvc.perform(get("/getNearbyAttractions")
            .param("userName", "internalUser0")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
  System.out.println(result.getResponse().getContentAsString());
    Assertions.assertTrue(result.getResponse().getContentAsString().contains("userLocation"));
  Assertions.assertTrue(result.getResponse().getContentAsString().contains("suggestedAttraction"));

  }

  @Test
  public void getNearbyAttractionsUserUnknown() throws Exception {
    mvc.perform(get("/getNearbyAttractions")
            .param("userName", "ImUnknown")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void getNearbyAttractionsNoPAram() throws Exception {
    mvc.perform(get("/getNearbyAttractions")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getRewards() throws Exception {
    rewardsService.setProximityBuffer(10);
    Attraction attractionVisited = attractionList.get(0);
    User user = tourGuideService.getUser("internalUser0");
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
            attractionVisited,
            new Date()));
    rewardsService.calculateRewards(user, attractionList);
    TimeUnit.SECONDS.sleep(1);
    MvcResult result = mvc.perform(get("/getRewards")
            .param("userName", "internalUser0")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
    Assertions.assertFalse(result.getResponse().getContentAsString().isEmpty());
    Assertions.assertTrue( result.getResponse().getContentAsString().length()>2);
  }
  @Test
  public void getRewardsUserHaveNoReward() throws Exception {
    MvcResult result = mvc.perform(get("/getRewards")
            .param("userName", "internalUser0")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
    System.out.println("REsult:--->"+result.getResponse().getContentAsString());
    System.out.println(result.getResponse().getContentAsString().length());
    Assertions.assertEquals(2, result.getResponse().getContentAsString().length());
  }

  @Test
  public void getRewardsUserUnknown() throws Exception {
    mvc.perform(get("/getRewards")
            .param("userName", "ImUnknown")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void getRewardsNoPAram() throws Exception {
    mvc.perform(get("/getRewards")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getAllCurrentLocations() throws Exception {
    MvcResult result = mvc.perform(get("/getAllCurrentLocations")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
    System.out.println(result.getResponse().getContentAsString());
    Assertions.assertTrue(result.getResponse().getContentAsString().contains("latitude"));
    Assertions.assertTrue(result.getResponse().getContentAsString().contains("longitude"));

  }

  @Test
  public void getTripDeals() throws Exception {
    MvcResult result = mvc.perform(get("/getTripDeals")
            .param("userName","internalUser0")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

    Assertions.assertTrue(result.getResponse().getContentAsString().contains("name"));
    Assertions.assertTrue(result.getResponse().getContentAsString().contains("price"));
    Assertions.assertTrue(result.getResponse().getContentAsString().contains("tripId"));
  }

  @Test
  public void getTripDealsNoParam() throws Exception {
    MvcResult result = mvc.perform(get("/getTripDeals")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
    Assertions.assertEquals(400, result.getResponse().getStatus());
  }

  @Test
  public void getTripDealsUSerUnknow() throws Exception {
    MvcResult result = mvc.perform(get("/getTripDeals")
            .param("userName","ImUnknown")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
    Assertions.assertEquals(404, result.getResponse().getStatus());
  }



}



