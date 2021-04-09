package com.tourGuide;

import com.tourGuide.model.Attraction;
import com.tourGuide.model.User;
import com.tourGuide.model.UserReward;
import com.tourGuide.model.VisitedLocation;
import com.tourGuide.service.RewardsService;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestRewardsService {

  WebClient webClientGps = WebClient.create("http://localhost:8081");

  @Autowired
  RewardsService rewardsService;
  Flux<Attraction> attractions;
  List<Attraction> attractionList;
  int countCheck;
  int testUsersCount;

  @BeforeEach
  public void setUp() {
    countCheck = 0;
    testUsersCount = 0;
    attractions = webClientGps.get().uri("/getAttractions").retrieve().bodyToFlux(Attraction.class);
    attractionList = attractions.collectList().block();
    //InternalTestHelper.setInternalUserNumber(testUsersCount);
  }

  @Test
  public void userGetRewards() {
    rewardsService.setProximityBuffer(10);
    Attraction attractionVisited = attractionList.get(0);
    User user = new User(UUID.randomUUID(), "john", "000",
            "john@tourGuide.com");
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
            attractionVisited,
            new Date()));
    rewardsService.calculateRewards(user, attractionList);
    List<UserReward> userRewards = user.getUserRewards();
    while (countCheck < 1) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      countCheck = 0;
      if (userRewards.size() > 0) {
        countCheck++;
      }
    }

    Assertions.assertEquals(1, userRewards.size());
  }

  @Test
  public void isWithinAttractionProximity() {
    Assertions.assertTrue(rewardsService.isWithinAttractionProximity(attractionList.get(0),
            attractionList.get(0)));
  }

  @Test
  public void nearAllAttractions() {
    rewardsService.setProximityBuffer(Integer.MAX_VALUE);
    Attraction attractionVisited = attractionList.get(0);
    User user = new User(UUID.randomUUID(), "john", "000",
            "john@tourGuide.com");
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
            attractionVisited,
            new Date()));
    rewardsService.calculateRewards(user, attractionList);
    List<UserReward> userRewards = user.getUserRewards();
    while (countCheck == 0) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      countCheck = 0;
      if (userRewards.size() > 0) {
        countCheck++;
      }
    }

    Assertions.assertEquals(attractionList.size(), userRewards.size());
  }

  @Test
  public void setDefaultProximityBuffer() {
    int defaultProximityBuffer = rewardsService.getDefaultProximityBuffer();
    rewardsService.setDefaultProximityBuffer();
    Assertions.assertEquals(defaultProximityBuffer, rewardsService.getDefaultProximityBuffer());
  }
}
