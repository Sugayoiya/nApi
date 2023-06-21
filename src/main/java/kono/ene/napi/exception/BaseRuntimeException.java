package kono.ene.napi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseRuntimeException extends RuntimeException {
    private Integer code;
    private Integer status;
    private String msg;

    public BaseRuntimeException(Integer code, String msg, Object... params) {
        super();
        this.code = code;
        this.msg = String.format(msg, params);
    }

    public BaseRuntimeException(Integer code, Integer status, String msg, Object... params) {
        super();
        this.code = code;
        this.status = status;
        this.msg = String.format(msg, params);
    }

    @Override
    public String getMessage() {
        return code + ":" + msg;
    }
}
