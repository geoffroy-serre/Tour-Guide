package com.tourGuide;

import com.tourGuide.helper.InternalTestHelper;
import com.tourGuide.model.*;
import com.tourGuide.service.RewardsService;
import com.tourGuide.service.RewardsServiceImpl;
import com.tourGuide.service.TourGuideService;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TestTourGuideService {

  WebClient webClientGps = WebClient.create("http://localhost:8081");
  WebClient webClientTripPricer= WebClient.create("http://localhost:8083");

  @Autowired
  RewardsService rewardsService;

  @BeforeEach
  public void setUp() {
  }

  @Test
  public void getUserLocation()  {
    InternalTestHelper.setInternalUserNumber(1);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    tourGuideService.tracker.stopTracking();

    User user = tourGuideService.getAllUsers().get(0);
    VisitedLocation visitedLocation =
            tourGuideService.trackUserLocation(user).single().block();

    Assertions.assertEquals(user.getUserId(), visitedLocation.getUserId());
  }

  @Test
  public void addUser() {
    InternalTestHelper.setInternalUserNumber(0);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
    tourGuideService.addUser(user);
    tourGuideService.addUser(user2);
    User retrivedUser = tourGuideService.getUser(user.getUserName());
    User retrivedUser2 = tourGuideService.getUser(user2.getUserName());
    tourGuideService.tracker.stopTracking();
    assertEquals(user, retrivedUser);
    assertEquals(user2, retrivedUser2);
  }

  @Test
  public void getAllUsers() {
    InternalTestHelper.setInternalUserNumber(0);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
    tourGuideService.addUser(user);
    tourGuideService.addUser(user2);
    List<User> allUsers = tourGuideService.getAllUsers();
    tourGuideService.tracker.stopTracking();
    assertTrue(allUsers.contains(user));
    assertTrue(allUsers.contains(user2));
  }

  @Test
  public void trackUser()  {
    InternalTestHelper.setInternalUserNumber(0);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).block();
    tourGuideService.tracker.stopTracking();
    assertEquals(user.getUserId(), visitedLocation.getUserId());
  }

  @Test
  public void getNearbyAttractions()  {
    InternalTestHelper.setInternalUserNumber(1);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).block();
    List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
    tourGuideService.tracker.stopTracking();
    Assertions.assertEquals(5, attractions.size());
  }

  @Test
  public void getTripDeals() {
    InternalTestHelper.setInternalUserNumber(0);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    List<Provider> providers = tourGuideService.getTripDeals(user);
    tourGuideService.tracker.stopTracking();
    assertEquals(5, providers.size());
  }

  @Test
  public void getAllVisitedAttraction() {
    InternalTestHelper.setInternalUserNumber(1);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    List<VisitedLocation> allUsers = tourGuideService.getAllUsersLocations();
    tourGuideService.tracker.stopTracking();
    //List<VisitedLocation> result = allUsers.join();
    assertEquals(3, allUsers.size());
  }

  @Test
  public void getAllCurrentVisitedAttraction() {
    InternalTestHelper.setInternalUserNumber(10);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    tourGuideService.tracker.stopTracking();
    Assertions.assertEquals(10, tourGuideService.getAllCurrentLocation().size());
  }

  @Test
  public void getAttractionsSuggestion(){
    InternalTestHelper.setInternalUserNumber(1);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripPricer,
            rewardsService);
    tourGuideService.tracker.stopTracking();
    User user =tourGuideService.getAllUsers().get(0);
    AttractionsSuggestion suggestion = tourGuideService
            .getAttractionsSuggestion(user);

    // THEN
    assertThat(suggestion.getUserLocation().getLatitude())
            .isEqualTo(user.getLastVisitedLocation().getLocation()
                    .getLatitude());
    assertThat(suggestion.getUserLocation().getLongitude())
            .isEqualTo(user.getLastVisitedLocation().getLocation()
                    .getLongitude());

  }



}
