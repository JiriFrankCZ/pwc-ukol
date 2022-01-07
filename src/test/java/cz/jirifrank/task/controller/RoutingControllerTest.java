package cz.jirifrank.task.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoutingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getRoute_givenValidShortPath_shouldReturnCountyList() throws Exception {
		mockMvc.perform(get("/routing/{origin}/{destination}", "CZE", "ITA"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.route", Matchers.contains("CZE", "AUT", "ITA")));
	}

	@Test
	void getRoute_givenValidLongPath_shouldReturnCountyList() throws Exception {
		mockMvc.perform(get("/routing/{origin}/{destination}", "PRT", "VNM"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.route", Matchers.contains("PRT", "ESP", "FRA", "DEU", "POL", "RUS", "CHN", "VNM")));
	}

	@Test
	void getRoute_givenInvalidCountryNames_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/routing/{origin}/{destination}", "CE", "USA"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getRoute_givenNotPossibleRoute_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/routing/{origin}/{destination}", "CZE", "USA"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void getRoute_givenUnknownCountries_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/routing/{origin}/{destination}", "CZK", "USA"))
				.andExpect(status().isBadRequest());
	}
}
