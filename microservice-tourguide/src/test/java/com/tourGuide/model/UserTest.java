package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.money.MonetaryContext;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public class UserTest {
  @Test
  public void testConstructor() {
    UUID userId = UUID.randomUUID();
    String userName = "janedoe";
    String phoneNumber = "4105551212";
    String emailAddress = "42 Main St";
    User actualUser = new User(userId, userName, phoneNumber, emailAddress);
    assertEquals("42 Main St", actualUser.getEmailAddress());
    assertEquals("janedoe", actualUser.getUserName());
    assertEquals("4105551212", actualUser.getPhoneNumber());
    UserPreferences userPreferences = actualUser.getUserPreferences();
    assertEquals(1, userPreferences.getNumberOfAdults());
    assertEquals(1, userPreferences.getTicketQuantity());
    assertEquals(1, userPreferences.getTripDuration());
    assertEquals(0, userPreferences.getNumberOfChildren());
    assertEquals(Integer.MAX_VALUE, userPreferences.getAttractionProximity());
    Money highPricePoint = userPreferences.getHighPricePoint();
    assertFalse(highPricePoint.isNegative());
    assertTrue(highPricePoint.getFactory() instanceof org.javamoney.moneta.internal.MoneyAmountFactory);
    assertTrue(highPricePoint.getCurrency() instanceof org.javamoney.moneta.internal.JDKCurrencyAdapter);
    Money lowerPricePoint = userPreferences.getLowerPricePoint();
    assertEquals(0, lowerPricePoint.signum());
    MonetaryContext expectedContext = highPricePoint.getContext();
    assertSame(expectedContext, lowerPricePoint.getContext());
    assertEquals("USD 0", lowerPricePoint.toString());
  }

  @Test
  public void testSetPhoneNumber() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    String phoneNumber = "4105551212";
    user.setPhoneNumber(phoneNumber);
    assertEquals("4105551212", user.getPhoneNumber());
  }

  @Test
  public void testSetEmailAddress() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    String emailAddress = "42 Main St";
    user.setEmailAddress(emailAddress);
    assertEquals("42 Main St", user.getEmailAddress());
  }

  @Test
  public void testSetLatestLocationTimestamp() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    Date date = new Date(1L);
    user.setLatestLocationTimestamp(date);
    assertSame(date, user.getLatestLocationTimestamp());
  }

  @Test
  public void testAddToVisitedLocations() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    VisitedLocation visitedLocation = new VisitedLocation();
    user.addToVisitedLocations(visitedLocation);
    assertEquals(1, user.getVisitedLocations().size());
  }

  @Test
  public void testClearVisitedLocations() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    user.clearVisitedLocations();
    assertTrue(user.getVisitedLocations().isEmpty());
  }

  @Test
  public void testAddUserReward() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    VisitedLocation visitedLocation = new VisitedLocation();
    UserReward userReward = new UserReward(visitedLocation, new Attraction());
    user.addUserReward(userReward);
    assertEquals(1, user.getUserRewards().size());
  }

  @Test
  public void testSetUserRewards() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    ArrayList<UserReward> userRewardList = new ArrayList<UserReward>();
    user.setUserRewards(userRewardList);
    assertSame(userRewardList, user.getUserRewards());
  }

  @Test
  public void testGetLastVisitedLocation2() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    VisitedLocation visitedLocation = new VisitedLocation();
    user.addToVisitedLocations(visitedLocation);
    VisitedLocation actualLastVisitedLocation = user.getLastVisitedLocation();
    assertSame(visitedLocation, actualLastVisitedLocation);
  }

  @Test
  public void testSetTripDeals() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    ArrayList<Provider> providerList = new ArrayList<Provider>();
    user.setTripDeals(providerList);
    assertSame(providerList, user.getTripDeals());
  }

  @Test
  public void testSetVisitedLocations() {
    User user = new User(UUID.randomUUID(), "janedoe", "4105551212", "42 Main St");
    ArrayList<VisitedLocation> visitedLocationList = new ArrayList<VisitedLocation>();
    user.setVisitedLocations(visitedLocationList);
    assertSame(visitedLocationList, user.getVisitedLocations());
  }
}

