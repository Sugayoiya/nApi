package kono.ene.napi.exception;

import lombok.Data;

@Data
public class BaseException extends Exception {
    private Integer code;
    private Integer status;
    private String msg;

    public BaseException(Integer code, String msg, Object... params) {
        super();
        this.code = code;
        this.msg = params.length == 0 ? msg : String.format(msg, params);
    }

    public BaseException(Integer code, Integer status, String msg, Object... params) {
        super();
        this.code = code;
        this.status = status;
        this.msg = params.length == 0 ? msg : String.format(msg, params);
    }

    @Override
    public String getMessage() {
        return code + ":" + msg;
    }

}
