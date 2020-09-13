package ru.drsdgdbye.basic_authentication.config;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.drsdgdbye.basic_authentication.security.exceptions.UserAlreadyExistsException;
import ru.drsdgdbye.basic_authentication.security.exceptions.UserNotFoundException;
import ru.drsdgdbye.basic_authentication.service.dto.SuccessDto;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new SuccessDto(false, details));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<UserAlreadyExistsException> handleUserAlreadyExistsException() {
        return new ResponseEntity(new UserAlreadyExistsException("User already exists").getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<UserNotFoundException> handleUserNotFoundException() {
        return new ResponseEntity(new UserAlreadyExistsException("User not found").getMessage(), HttpStatus.NOT_FOUND);
    }
}
