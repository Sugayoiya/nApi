package kono.ene.napi.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kono.ene.napi.response.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@AllArgsConstructor
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private HttpServletResponse httpServletResponse;
    private HttpServletRequest httpServletRequest;

    /**
     * validation参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseDto<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException e) {
        log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        log.info("validation parameters error！The reason is:{}", message);
        httpServletResponse.setStatus(400);
        return ResponseDto.failed(CommonErrorEnum.PARAM_VALID.getErrorCode(), message.substring(0, message.length() - 1));
    }

    /**
     * validation参数校验异常
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseDto<?> bindException(BindException e) {
        log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        log.info("validation parameters error！The reason is:{}", message);
        httpServletResponse.setStatus(400);
        return ResponseDto.failed(CommonErrorEnum.PARAM_VALID.getErrorCode(), message.substring(0, message.length() - 1));
    }

    /**
     * 处理空指针的异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseDto<?> exceptionHandler(NullPointerException e) {
        log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);
        log.error("null point exception！The reason is:{}", e.getMessage(), e);
        httpServletResponse.setStatus(400);
        return ResponseDto.failed(CommonErrorEnum.SYSTEM_ERROR);
    }

    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseDto<?> systemExceptionHandler(Exception e) {
        log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);
        log.error("system exception！The reason is：{}", e.getMessage(), e);
        httpServletResponse.setStatus(400);
        return ResponseDto.failed(CommonErrorEnum.SYSTEM_ERROR);
    }

    /**
     * 自定义校验异常（如参数校验等）
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseDto<?> businessExceptionHandler(BusinessException e) {
        log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);
        log.info("business exception！The reason is：{}", e.getMessage(), e);
        httpServletResponse.setStatus(422);
        return ResponseDto.failed(e.getCode(), e.getMessage());
    }

    /**
     * http请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseDto<?> handleException(HttpRequestMethodNotSupportedException e) {
        log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);
        log.error(e.getMessage(), e);
        httpServletResponse.setStatus(400);
        return ResponseDto.failed(-1, String.format("不支持'%s'请求", e.getMethod()));
    }
}
