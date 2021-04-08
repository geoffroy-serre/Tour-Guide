package com.rewardsCenter.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.rewardsCenter.service.RewardsService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {RewardsCenterController.class})
@ExtendWith(SpringExtension.class)
public class RewardsCenterControllerTest {
  @Autowired
  private RewardsCenterController rewardsCenterController;

  @MockBean
  private RewardsService rewardsService;

  @Test
  public void testGetAttractionRewardsPoint() throws Exception {
    when(this.rewardsService.getRewardPoints((UUID) any(), (UUID) any())).thenReturn(null);
    MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/getAttractionRewardsPoints");
    MockHttpServletRequestBuilder paramResult = getResult.param("attractionId", String.valueOf(UUID.randomUUID()));
    MockHttpServletRequestBuilder requestBuilder = paramResult.param("userId", String.valueOf(UUID.randomUUID()));
    MockMvc buildResult = MockMvcBuilders.standaloneSetup(this.rewardsCenterController).build();
    ResultActions actualPerformResult = buildResult.perform(requestBuilder);
    actualPerformResult.andExpect(MockMvcResultMatchers.status().isOk());
  }
  @Test
  public void testGetAttractionRewardsPointBadRequestNoPAram() throws Exception {
Mono<Integer> result = Mono.just(1);
    when(rewardsService.getRewardPoints((UUID) any(), (UUID) any())).thenReturn(result);
    MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/getAttractionRewardsPoints");

    MockMvc buildResult = MockMvcBuilders.standaloneSetup(this.rewardsCenterController).build();
    ResultActions actualPerformResult = buildResult.perform(getResult);
    actualPerformResult.andExpect(MockMvcResultMatchers.status().isBadRequest());
  }


}

