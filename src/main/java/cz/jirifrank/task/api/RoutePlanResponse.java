package cz.jirifrank.task.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoutePlanResponse {
	private List<String> route;
}
