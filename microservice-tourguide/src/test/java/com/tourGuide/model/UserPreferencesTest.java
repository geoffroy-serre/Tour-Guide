package com.tourGuide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.money.MonetaryContext;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public class UserPreferencesTest {
  @Test
  public void testConstructor() {
    UserPreferences actualUserPreferences = new UserPreferences();
    assertEquals(Integer.MAX_VALUE, actualUserPreferences.getAttractionProximity());
    assertEquals(1, actualUserPreferences.getTripDuration());
    assertEquals(1, actualUserPreferences.getTicketQuantity());
    assertEquals(0, actualUserPreferences.getNumberOfChildren());
    assertEquals(1, actualUserPreferences.getNumberOfAdults());
    Money lowerPricePoint = actualUserPreferences.getLowerPricePoint();
    assertEquals("USD 0", lowerPricePoint.toString());
    Money highPricePoint = actualUserPreferences.getHighPricePoint();
    assertEquals("USD 2147483647", highPricePoint.toString());
    assertEquals(1, highPricePoint.signum());
    assertTrue(lowerPricePoint.getFactory() instanceof org.javamoney.moneta.internal.MoneyAmountFactory);
    assertTrue(lowerPricePoint.isNegativeOrZero());
    MonetaryContext expectedContext = lowerPricePoint.getContext();
    assertSame(expectedContext, highPricePoint.getContext());
    assertEquals("2147483647", highPricePoint.getNumberStripped().toString());
  }

  @Test
  public void testSetAttractionProximity() {
    UserPreferences userPreferences = new UserPreferences();
    int attractionProximity = 1;
    userPreferences.setAttractionProximity(attractionProximity);
    assertEquals(1, userPreferences.getAttractionProximity());
  }

  @Test
  public void testSetLowerPricePoint() {
    UserPreferences userPreferences = new UserPreferences();
    Money lowerPricePoint = null;
    userPreferences.setLowerPricePoint(lowerPricePoint);
    assertNull(userPreferences.getLowerPricePoint());
  }

  @Test
  public void testSetHighPricePoint() {
    UserPreferences userPreferences = new UserPreferences();
    Money highPricePoint = null;
    userPreferences.setHighPricePoint(highPricePoint);
    assertNull(userPreferences.getHighPricePoint());
  }

  @Test
  public void testSetTripDuration() {
    UserPreferences userPreferences = new UserPreferences();
    int tripDuration = 1;
    userPreferences.setTripDuration(tripDuration);
    assertEquals(1, userPreferences.getTripDuration());
  }

  @Test
  public void testSetTicketQuantity() {
    UserPreferences userPreferences = new UserPreferences();
    int ticketQuantity = 1;
    userPreferences.setTicketQuantity(ticketQuantity);
    assertEquals(1, userPreferences.getTicketQuantity());
  }

  @Test
  public void testSetNumberOfAdults() {
    UserPreferences userPreferences = new UserPreferences();
    int numberOfAdults = 10;
    userPreferences.setNumberOfAdults(numberOfAdults);
    assertEquals(10, userPreferences.getNumberOfAdults());
  }

  @Test
  public void testSetNumberOfChildren() {
    UserPreferences userPreferences = new UserPreferences();
    int numberOfChildren = 10;
    userPreferences.setNumberOfChildren(numberOfChildren);
    assertEquals(10, userPreferences.getNumberOfChildren());
  }
}

