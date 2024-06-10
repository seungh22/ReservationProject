package com.example.reservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

import static com.example.reservation.exception.ErrorCode.ACCESS_DENIED;
import static com.example.reservation.exception.ErrorCode.INVALID_REQUEST;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ReservationException.class)
    public ResponseError handleReservationException(ReservationException e) {
        log.error("{} 예외 발생", e.getErrorCode());

        return new ResponseError(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException 예외 발생", e);

        return new ResponseError(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseError handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException 예외 발생", e);

        return new ResponseError(ACCESS_DENIED, ACCESS_DENIED.getDescription());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException 예외 발생", e);

        return new ResponseError(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }
}
