package cz.jirifrank.task.service;

import java.util.List;

public interface RouteService {
	List<String> getShortestRoute(String source, String destination);
}
