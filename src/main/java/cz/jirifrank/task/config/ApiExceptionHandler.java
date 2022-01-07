package cz.jirifrank.task.config;

import cz.jirifrank.task.api.GenericApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String ERROR_FORMAT = "%s %s: %s";

	@ResponseBody
	@ExceptionHandler({ConstraintViolationException.class})
	public ResponseEntity<GenericApiError> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
		log.warn("Validation error of incoming request [{}] => {}", request, ex.toString());

		List<String> errors = ex.getConstraintViolations().stream()
				.map(violation -> ERROR_FORMAT.formatted(
						violation.getRootBeanClass().getName(),
						violation.getPropertyPath(),
						violation.getMessage()
				)).collect(Collectors.toList());

		return new ResponseEntity<>(
				new GenericApiError(ex.getLocalizedMessage(), errors),
				new HttpHeaders(),
				HttpStatus.BAD_REQUEST
		);
	}
}


