package kono.ene.napi.exception;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.DecodeException;
import kono.ene.napi.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private HttpServletRequest httpServletRequest;


    @ExceptionHandler(Exception.class)
    @ConditionalOnMissingBean
    public ResponseDto<?> baseExceptionHandler(Exception e) throws Exception {
        if (e instanceof BaseException) {
            log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);

            if (((BaseException) e).getStatus() != null) {
                httpServletResponse.setStatus(((BaseException) e).getStatus());
            } else {
                httpServletResponse.setStatus(422);
            }

            return ResponseDto.failed(((BaseException) e).getCode(), ((BaseException) e).getMsg());
        } else if (e instanceof BaseRuntimeException) {
            log.warn("url={}, msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);

            if (((BaseRuntimeException) e).getStatus() != null) {
                httpServletResponse.setStatus(((BaseRuntimeException) e).getStatus());
            } else {
                httpServletResponse.setStatus(422);
            }

            return ResponseDto.failed(((BaseRuntimeException) e).getCode(), ((BaseRuntimeException) e).getMsg());
        } else if (e instanceof BindException || e instanceof ServletRequestBindingException || e instanceof HttpMessageNotReadableException) {
            log.warn("url={},  msg={}", httpServletRequest.getRequestURI(), e.getMessage(), e);
            httpServletResponse.setStatus(400);
            return ResponseDto.failed(10400, e.getMessage());
        } else if (e instanceof DecodeException && !(e.getCause() instanceof DecodeException) && e.getCause() != null) {
            return baseExceptionHandler((Exception) e.getCause());
        }

        log.error("unknown error for url:{}", httpServletRequest.getRequestURI(), e);
        httpServletRequest.setAttribute("cat-state", e);
        throw e;
    }
}
