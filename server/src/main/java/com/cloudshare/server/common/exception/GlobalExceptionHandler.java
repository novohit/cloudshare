package com.cloudshare.server.common.exception;

import com.cloudshare.server.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.novo.limit.exception.RateLimitException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author novo
 * @since 2023-02-21
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理未知异常
     * 都是无意义的 不需要提示 message 返回模糊信息或记录日志
     *
     * @param request request
     * @param e       exception
     * @return Response<Void>
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response<Void>> exceptionHandler(HttpServletRequest request, Exception e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error("[业务异常] url:[{}],msg:[{}]", requestUrl, e.getMessage());
        if (e instanceof HttpRequestMethodNotSupportedException exception) {
            log.error("[接口请求方式错误] url:[{}] message:[{}]", requestUrl, exception.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        if (e instanceof RateLimitException exception) {
            httpStatus = HttpStatus.BAD_REQUEST;
            log.error("[系统限流] url:[{}]", requestUrl, e);
        }

        Response<Void> response = Response.build(500, null, e.getMessage());
        return new ResponseEntity<>(response, headers, httpStatus);
    }

    /**
     * 处理已知的业务异常
     * 灵活地处理响应的 http状态码 不能写死 500 而是返回e里的httpStatus
     * 所以不用 @ResponseBody注解 自己设置返回的响应体，所以要设置比较多的类型
     *
     * @param request request
     * @param e       exception
     * @return ResponseEntity<Response < Void>>
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Response<Void>> bizExceptionHandler(HttpServletRequest request, BizException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        Response<Void> response = Response.build(e.getCode(), null, e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus httpStatus = e.getHttpStatus();
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        log.error("[业务异常] url:[{}],msg:[{}]", requestUrl, e.getMessage());
        return new ResponseEntity<>(response, headers, httpStatus);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Response<Void> methodArgumentNotValidExceptionHandler(HttpServletRequest request, HttpMediaTypeNotSupportedException e) {
        // content type 不对
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        log.error("[参数异常] url:[{}],msg:[{}]", requestUrl, e.getMessage());
        return Response.error(e.getMessage());
    }


    /**
     * RequestBody注解、Java bean中参数错误产生的异常、表单(Content-Type: application/json、Content-Type: application/xml)
     *
     * @param request request
     * @param e       exception
     * @return Response<Void>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Response<Void> methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String errorMsg = this.formatAllErrorsMessages(errors);
        log.error("[参数异常] url:[{}],msg:[{}]", requestUrl, errorMsg);
        return Response.error(errorMsg);
    }

    /**
     * 非Java bean(如路径参数等)参数错误产生的异常
     * 只作用于参数已传但是不符合要求的情况
     *
     * @param request request
     * @param e       exception
     * @return Response<Void>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Response<Void> constraintViolationExceptionHandler(HttpServletRequest request, ConstraintViolationException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();

        // ConstraintViolationException自带的getMessage()也是可以用的，如果对错误信息没有严格的格式要求可以不用通过这种循环来自定义拼接
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream().map(constraintViolation -> {
            // register.username
            String paramPath = constraintViolation.getPropertyPath().toString();
            String param = paramPath.substring(paramPath.indexOf('.') + 1);
            return "%s %s".formatted(param, constraintViolation.getMessage());
        }).collect(Collectors.joining(";"));
        log.error("[参数异常] url:[{}]", requestUrl, e);
        return Response.error(message);
    }

    /**
     * 作用于参数未传
     *
     * @param request request
     * @param e       exception
     * @return Response<Void>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Response<Void> missingServletRequestParameterExceptionHandler(HttpServletRequest request, MissingServletRequestParameterException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        String message = "Required request parameter '%s'".formatted(e.getParameterName());
        log.error("[参数异常] url:[{}]", requestUrl, e);
        return Response.error(message);
    }

    /**
     * Java bean 、表单(Content-Type: multipart/form-data)参数错误产生的异常
     *
     * @param request request
     * @param e       exception
     * @return Response<Void>
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Response<Void> bindExceptionExceptionHandler(HttpServletRequest request, BindException e) {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String errorMsg = this.formatAllErrorsMessages(errors);
        log.error("[参数异常] url:[{}]", requestUrl, e);
        return Response.error(errorMsg);
    }


    /**
     * 拼接所有参数错误信息
     *
     * @param errors errors
     * @return errorMsg
     */
    private String formatAllErrorsMessages(List<ObjectError> errors) {
        String message = errors.stream()
                .map(objectError -> {
                    if (objectError instanceof FieldError) {
                        FieldError fieldError = (FieldError) objectError;
                        return "%s %s".formatted(fieldError.getField(), fieldError.getDefaultMessage());
                    } else {
                        return objectError.getDefaultMessage();
                    }
                })
                .collect(Collectors.joining(";"));
        return message;
    }
}
