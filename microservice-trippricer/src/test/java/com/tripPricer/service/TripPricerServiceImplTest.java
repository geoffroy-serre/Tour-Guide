package com.tripPricer.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tripPricer.Provider;
import tripPricer.TripPricer;


@ContextConfiguration(classes = {TripPricerServiceImpl.class, TripPricer.class})
@ExtendWith(SpringExtension.class)
public class TripPricerServiceImplTest {

  @MockBean
  private TripPricer tripPricer;

  @Autowired
  private TripPricerServiceImpl tripPricerServiceImpl;

  @Test
  public void testGetPrice() {
    UUID uuid = UUID.randomUUID();
    List<Provider> providers = new ArrayList<>();
    when(tripPricer.getPrice("ApiKey",uuid,1,1,1,1)).thenReturn(providers);
    tripPricerServiceImpl.getPrice("ApiKey",uuid,1,1,1,1);
    Mockito.verify(tripPricer,Mockito.times(1)).getPrice("ApiKey",uuid,1,1,1,1);
  }
}

