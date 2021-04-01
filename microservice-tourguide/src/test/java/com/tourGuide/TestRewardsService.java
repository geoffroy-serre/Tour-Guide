package com.tourGuide;

import com.tourGuide.helper.InternalTestHelper;
import com.tourGuide.model.Attraction;
import com.tourGuide.model.User;
import com.tourGuide.model.UserReward;
import com.tourGuide.model.VisitedLocation;
import com.tourGuide.service.RewardsService;
import com.tourGuide.service.RewardsServiceImpl;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRewardsService {

  WebClient webClientGps = WebClient.create("http://localhost:8081");

  @Autowired
  RewardsService rewardsService;
 Flux<Attraction> attractions =
          webClientGps.get().uri("/getAttractions").retrieve().bodyToFlux(Attraction.class);
 List<Attraction> att = attractions.collectList().block();

  @BeforeEach
  public void setUp() {
  }

  int testUsersCount = 1;

  @Test
  public void userGetRewards() {
    InternalTestHelper.setInternalUserNumber(testUsersCount);
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
            att.get(0),
            new Date()));
    rewardsService.calculateRewards(user, attractions.collectList().block());
    List<UserReward> userRewards = user.getUserRewards();
    Assertions.assertEquals(1, userRewards.size());
  }

  @Test
  public void isWithinAttractionProximity() {
    Assertions.assertTrue(rewardsService.isWithinAttractionProximity(att.get(0),
            att.get(0)));
  }

  @Test
  public void nearAllAttractions() {
    User user = new User(UUID.randomUUID(), "john", "000",
            "john@tourGuide.com");
    InternalTestHelper.setInternalUserNumber(1);
    rewardsService.setProximityBuffer(Integer.MAX_VALUE);
    rewardsService.calculateRewards(user,
            att);
    List<UserReward> userRewards = user.getUserRewards();
    Assertions.assertEquals(att.size(), userRewards.size());
  }

}
