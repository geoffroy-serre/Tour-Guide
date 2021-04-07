package com.tourGuide;

import com.jsoniter.output.JsonStream;
import com.tourGuide.exception.UserNameNotFound;
import com.tourGuide.model.*;
import com.tourGuide.service.TourGuideService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TourGuideController {

  @Autowired
  TourGuideService tourGuideService;

  Logger logger = LoggerFactory.getLogger(TourGuideController.class);

  @RequestMapping("/")
  public String index() {
    return "Greetings from TourGuide!";
  }

  @RequestMapping("/getLocation")
  public String getLocation(@RequestParam String userName) throws ExecutionException,
          InterruptedException {
    logger.debug("getLocation for user: "+userName);
    VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName)).block();
    return JsonStream.serialize(visitedLocation != null ? visitedLocation.getLocation() : null);
  }

  @RequestMapping("/getNearbyAttractions")
  public AttractionsSuggestion getNearbyAttractions(@RequestParam String userName) {
    if (getUser(userName) == null) {
      logger.debug("userName not found for user: "+userName);
      throw new UserNameNotFound();
    }
    logger.debug("getNearbyAttractions for user: "+userName);
    AttractionsSuggestion suggestion = tourGuideService
            .getAttractionsSuggestion(getUser(userName));
    logger.info(suggestion.toString() + "\n");
    return suggestion;
  }

  @RequestMapping("/getRewards")
  public String getRewards(@RequestParam String userName) {
    logger.debug("getRewards for user: "+userName);
    return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
  }

  @RequestMapping("/getAllCurrentLocations")
  public Map<UUID, Location> getAllCurrentLocations() {
    logger.debug("getAllCurrentLocation for users");
    return tourGuideService.getAllCurrentLocation();
  }

  @RequestMapping("/getTripDeals")
  public String getTripDeals(@RequestParam String userName) {
    logger.debug("getTripDeals for user: "+userName);
    if (getUser(userName) == null) {
      logger.debug("User not found for user: "+userName);
      throw new UserNameNotFound();
    }
    List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    return JsonStream.serialize(providers);
  }

  private User getUser(String userName) {
    logger.debug("getUser for user: "+userName);
    return tourGuideService.getUser(userName);
  }


}
