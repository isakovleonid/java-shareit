package ru.practicum.shareit;


import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.handler.NotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
class ControllerAdvice {
    @Getter
    class ErrorDescription {
        private String error;
        private String message;

        public ErrorDescription(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler()
    public ErrorDescription handleBindException(Exception e) {
        return new ErrorDescription("Ошибка сервера", e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler()
    public ErrorDescription handleNotFoundException(NotFoundException e) {
        return new ErrorDescription("Данные не найдены", e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler()
    public ErrorDescription handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ErrorDescription("Ошибка при сохранении из-за некорректных данных", e.getCause().getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler()
    public ErrorDescription handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorDescription("Ошибка в параметрах запроса", e.getBindingResult().getFieldErrors().stream()
                .map(a -> a.getDefaultMessage())
                .collect(Collectors.toSet())
                .toString());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler()
    public ErrorDescription handleMethodArgumentNotValidException(ConstraintViolationException e) {
        return new ErrorDescription("Ошибка в параметрах запроса", e.getConstraintViolations().stream()
                .map(violation -> {
                    String field = violation.getPropertyPath().toString();
                    String message = violation.getMessage();
                    return field + ":" + message;
                })
                .collect(Collectors.toSet())
                .toString());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler()
    public ErrorDescription handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return new ErrorDescription("Ошибка в заголовке запроса запроса", e.getMessage());
    }
}