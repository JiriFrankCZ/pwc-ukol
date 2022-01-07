package cz.jirifrank.task.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GenericApiError {
	private String message;
	private List<String> errors;
}
