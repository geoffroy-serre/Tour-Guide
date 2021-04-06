package com.tourGuide;

import com.jsoniter.output.JsonStream;
import com.tourGuide.exception.UserNameNotFound;
import com.tourGuide.model.*;
import com.tourGuide.service.TourGuideService;
import com.tourGuide.service.TourGuideServiceImpl;
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
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName)).block();
		return JsonStream.serialize(visitedLocation != null ? visitedLocation.getLocation() : null);
    }

    @RequestMapping("/getNearbyAttractions") 
    public AttractionsSuggestion getNearbyAttractions(@RequestParam String userName) {
      if(getUser(userName)==null){
        throw new UserNameNotFound();
      }
      logger.info("New HTTP Request on /getNearbyAttractions for {}",
              userName);
      AttractionsSuggestion suggestion = tourGuideService
              .getAttractionsSuggestion(getUser(userName));
      logger.info(suggestion.toString() + "\n");
      return suggestion;
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public Map<UUID, Location> getAllCurrentLocations() {
    	// TODO: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }
    	
    	return tourGuideService.getAllCurrentLocation();
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
      if(getUser(userName)==null){
        throw new UserNameNotFound();
      }
      List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}