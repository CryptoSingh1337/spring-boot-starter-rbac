package pro.saransh.examples.rbac.basic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pro.saransh.springboot.rbac.exception.AccessDeniedException;

import java.util.Map;

@RestControllerAdvice
public class ExampleExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
        return Map.of(
                "error", "forbidden",
                "message", ex.getMessage()
        );
    }
}
