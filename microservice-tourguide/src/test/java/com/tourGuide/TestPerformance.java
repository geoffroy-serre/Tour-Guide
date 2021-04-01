package com.tourGuide;

import com.tourGuide.helper.InternalTestHelper;
import com.tourGuide.model.Attraction;
import com.tourGuide.model.User;
import com.tourGuide.model.VisitedLocation;
import com.tourGuide.service.RewardsServiceImpl;
import com.tourGuide.service.TourGuideService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
public class TestPerformance {


  /*
   * A note on performance improvements:
   *
   *     The number of users generated for the high volume tests can be easily adjusted via this
   * method:
   *
   *     		InternalTestHelper.setInternalUserNumber(100000);
   *
   *
   *     These tests can be modified to suit new solutions, just as long as the performance metrics
   *     at the end of the tests remains consistent.
   *
   *     These are performance metrics that we are trying to hit:
   *
   *     highVolumeTrackLocation: 100,000 users within 15 minutes:
   *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch
   * .getTime()));
   *
   *     highVolumeGetRewards: 100,000 users within 20 minutes:
   *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds
   * (stopWatch.getTime()));
   */

  RewardsServiceImpl rewardsService = new RewardsServiceImpl(
          WebClient.create("http://localhost:8082"));

  WebClient webClientTripDeals = WebClient.create("http://localhost:8083");

  WebClient webClientGps = WebClient.create("http://localhost:8081");;
  int testUsersCount = 1;
  int vln = 0;

  @Test
  public void highVolumeTrackLocation() throws ExecutionException, InterruptedException {
// Users should be incremented up to 100,000, and test finishes within 15 minutes
    InternalTestHelper.setInternalUserNumber(testUsersCount);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripDeals,
            rewardsService);
    tourGuideService.tracker.stopTracking();

    List<User> allUsers = tourGuideService.getAllUsers();
    allUsers.forEach(user -> user.setVisitedLocations(new ArrayList<>()));
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    for (User user : allUsers) {
      tourGuideService.trackUserLocation(user).subscribe();
    }


    while (vln < allUsers.size()) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      vln = 0;
      allUsers.forEach(u -> {
        if (u.getVisitedLocations().size() > 0) {
          vln++;
        };
      });
    }
    stopWatch.stop();

    System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    Assertions.assertEquals( vln, testUsersCount);
    Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

  @Test
  public void highVolumeGetRewards() throws ExecutionException, InterruptedException {
    InternalTestHelper.setInternalUserNumber(testUsersCount);
    TourGuideService tourGuideService = new TourGuideService(webClientGps,webClientTripDeals,
            rewardsService);
    tourGuideService.tracker.stopTracking();

    // Users should be incremented up to 100,000, and test finishes within 20 minutes
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    System.out.println("Start StopWatch");
    Attraction attractionTourGuide =
            webClientGps.get().uri("/getAttractions").retrieve().bodyToFlux(Attraction.class).collectList().block().get(0);
    List<Attraction> attractions =
            webClientGps.get().uri("/getAttractions").retrieve().bodyToFlux(Attraction.class).collectList().block();
    List<User> allUsers = tourGuideService.getAllUsers();

    allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(),
            attractionTourGuide, new Date())));

    allUsers.forEach(user -> rewardsService.calculateRewards(user, attractions));
    stopWatch.stop();

    System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds for " + allUsers.size() + " users.");
    Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }
}
