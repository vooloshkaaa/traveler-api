package ua.sumdu.traveler.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorBody(String error, List<String> details, Instant timestamp) {}

    public record ConflictBody(String error, Integer currentVersion, String message) {}

    private ResponseEntity<ErrorBody> err(int status, String error, List<String> details) {
        return ResponseEntity.status(status).body(new ErrorBody(error, details, Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> invalid(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        return err(400, "Validation error", details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorBody> badArgument(IllegalArgumentException e) {
        return err(400, "Validation error", List.of(e.getMessage()));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorBody> badRequest(Exception e) {
        return err(400, "Validation error", List.of("Invalid request"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorBody> notFound(NotFoundException e) {
        // Передаем фиксированное слово "Not found" в поле error,
        // чтобы Hurl-тест успешно прошел проверку contains "not found"
        return err(404, "not found", List.of(e.getMessage()));
    }

    @ExceptionHandler(VersionConflictException.class)
    public ResponseEntity<ConflictBody> conflict(VersionConflictException e) {
        return ResponseEntity.status(409).body(
                new ConflictBody(e.getMessage(), e.getCurrentVersion(), "Please refresh and try again"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> internal(Exception e) {
        log.error("Unhandled exception", e);          // причина 500 появится в консоли Run
        return err(500, "Internal server error", null);
    }
}
