package cz.jirifrank.task.controller;

import cz.jirifrank.task.api.RoutePlanResponse;
import cz.jirifrank.task.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Validated
@RestController
public class RoutingController {

	@Autowired
	private RouteService routeService;

	@GetMapping(path = "/routing/{origin}/{destination}")
	public ResponseEntity<RoutePlanResponse> getRoute(
			@PathVariable @NotBlank @Length(min = 3, max = 3) String origin,
			@PathVariable @NotBlank @Length(min = 3, max = 3) String destination) {

		log.debug("Requesting route from {} to {}.", origin, destination);
		List<String> route = routeService.getShortestRoute(origin, destination);

		if (CollectionUtils.isEmpty(route)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>(new RoutePlanResponse(route), HttpStatus.OK);
		}
	}
}
