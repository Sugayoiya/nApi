package kono.ene.napi.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    /**
     *  错误码
     */
    protected Integer code;

    /**
     *  错误信息
     */
    protected String msg;

    public BusinessException() {
        super();
    }

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(Integer code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(ErrorEnum error) {
        super(error.getErrorMsg());
        this.code = error.getErrorCode();
        this.msg = error.getErrorMsg();
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
