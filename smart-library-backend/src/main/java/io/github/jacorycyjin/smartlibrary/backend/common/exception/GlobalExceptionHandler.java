package io.github.jacorycyjin.smartlibrary.backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author Jacory
 * @date 2025/12/11
 */
@RestController
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements ResponseBodyAdvice<Result<?>> {

    @ExceptionHandler(value = BusinessException.class)
    public Result<Object> bizErrorHandler(BusinessException e, HttpServletRequest request) {
        log.warn("requestURI: {} BusinessException: ", request.getRequestURI(), e);
        return Result.fail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("requestURI: {} MethodArgumentNotValidException: ", request.getRequestURI(), e);
        return Result.fail(ApiCode.PARAM_INVALID.getCode(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public Result<?> constraintViolationExceptionHandler(ConstraintViolationException e, HttpServletRequest request) {
        log.error("requestURI: {} ConstraintViolationException: ", request.getRequestURI(), e);
        return Result.fail(ApiCode.PARAM_INVALID.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = BindException.class)
    public Result<?> methodArgumentNotValidHandler(BindException e, HttpServletRequest request) {
        log.warn("The request [{}] BindException: ", request.getRequestURI(), e);
        return Result.fail(ApiCode.PARAM_INVALID.getCode(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result<?> defaultErrorHandler(Exception e, HttpServletRequest request) {
        log.error("requestURI: {} Exception:", request.getRequestURI(), e);
        return Result.fail(ApiCode.SERVER_ERROR.getCode(), ApiCode.SERVER_ERROR.getMessage());
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Result<?> beforeBodyWrite(Result<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body != null && !ApiCode.SUCCESS.getCode().equals(body.getCode())) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
        }
        return body;
    }
}
