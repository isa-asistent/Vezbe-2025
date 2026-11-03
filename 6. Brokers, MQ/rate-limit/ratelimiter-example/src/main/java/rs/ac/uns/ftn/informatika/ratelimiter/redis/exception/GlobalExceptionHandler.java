package rs.ac.uns.ftn.informatika.ratelimiter.redis.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RequestNotPermitted.class)
	public ResponseEntity<Map<String, Object>> handleRequestNotPermitted(RequestNotPermitted ex) {
		LOG.warn("Rate limit exceeded: {}", ex.getMessage());

		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("timestamp", LocalDateTime.now());
		errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
		errorResponse.put("error", "Too Many Requests");
		errorResponse.put("message", "Rate limit exceeded. Please try again later.");
		errorResponse.put("details", ex.getMessage());

		return ResponseEntity
				.status(HttpStatus.TOO_MANY_REQUESTS)
				.body(errorResponse);
	}
}

