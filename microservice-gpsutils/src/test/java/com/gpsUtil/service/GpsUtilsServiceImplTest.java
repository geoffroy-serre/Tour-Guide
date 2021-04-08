package com.gpsUtil.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {GpsUtilsServiceImpl.class, GpsUtil.class})
@ExtendWith(SpringExtension.class)
class GpsUtilsServiceImplTest {

  @MockBean
  private GpsUtil gpsUtil;

  @Autowired
  GpsUtilsServiceImpl gpsUtilsService;

  @Test
  void getUserLocation() {
    UUID uuid = UUID.randomUUID();
    Location location = new Location(10.,10.1);
    Date date = new Date();
    VisitedLocation visitedLocation = new VisitedLocation(uuid,location,date);
    when(gpsUtil.getUserLocation(uuid)).thenReturn(visitedLocation);
    gpsUtilsService.getUserLocation(uuid);
    Mockito.verify(gpsUtil,Mockito.times(1)).getUserLocation(uuid);
  }

  @Test
  void getAttractions() {
    List<Attraction> attractions = new ArrayList<>();
    when(gpsUtil.getAttractions()).thenReturn(attractions);
    gpsUtilsService.getAttractions();
    Mockito.verify(gpsUtil,Mockito.times(1)).getAttractions();

  }
}
