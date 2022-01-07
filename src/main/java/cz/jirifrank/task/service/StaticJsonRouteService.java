package cz.jirifrank.task.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import cz.jirifrank.task.model.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StaticJsonRouteService implements RouteService {

	@Value("${countries.url}")
	private String url;

	private Map<String, List<String>> worldMap;

	@PostConstruct
	public void init() {
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		CollectionType typeReference = TypeFactory
				.defaultInstance()
				.constructCollectionType(List.class, Country.class);

		List<Country> countries = Collections.emptyList();
		try {
			countries = objectMapper.readValue(new URL(url), typeReference);
		} catch (FileNotFoundException | MalformedURLException e) {
			log.warn("Fallback to initialization from static file.", e);

			InputStream inputStream = StaticJsonRouteService.class.getResourceAsStream("/countries.json");
			try {
				countries = objectMapper.readValue(inputStream, typeReference);
			} catch (Exception ex) {
				log.error("Exception during fallback initialization of countries.json.", e);
			}
		} catch (Exception e) {
			log.error("Exception during initialization of countries.json.", e);
		}

		if (!CollectionUtils.isEmpty(countries)) {
			worldMap = countries.stream()
					.collect(Collectors.toMap(Country::getCca3, Country::getBorders));

			log.info("Initialization of world finished. {} countries loaded.", worldMap.size());
		}
	}

	@Override
	public List<String> getShortestRoute(String origin, String destination) {
		if (!worldMap.containsKey(origin) || !worldMap.containsKey(destination)) {
			log.warn("Either origin or destination country is not in our world database.");

			return Collections.emptyList();
		}

		return getRoute(origin, destination);
	}


	private List<String> getRoute(String origin, String destination) {
		Map<String, String> pathMap = new HashMap<>();
		Deque<String> queue = new ArrayDeque<>();

		queue.add(origin);

		while (!queue.isEmpty()) {
			final String actualCountry = queue.remove();

			if (actualCountry.equals(destination)) {
				return getChainPath(actualCountry, pathMap);
			} else {
				worldMap.get(actualCountry).stream()
						.filter(node -> !pathMap.containsKey(node) && !pathMap.containsValue(node))
						.forEach(node -> {
							queue.add(node);
							pathMap.put(node, actualCountry);
						});
			}
		}

		return Collections.emptyList();
	}

	private List<String> getChainPath(String origin, Map<String, String> path) {
		List<String> route = new ArrayList<>();

		for (String node = origin; node != null; node = path.get(node)) {
			route.add(node);
		}

		Collections.reverse(route);
		return route;
	}
}