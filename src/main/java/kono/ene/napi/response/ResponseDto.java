package kono.ene.napi.response;


import brave.Tracer;
import kono.ene.napi.exception.BaseRuntimeException;
import kono.ene.napi.util.SpringContextUtil;
import lombok.Data;


import java.util.UUID;

@Data
public class ResponseDto<T> {
    private Integer code;
    private String msg;
    private T data;

    private ResponseDto() {
    }

    private ResponseDto(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    static public ResponseDto<?> success() {
        return new ResponseDto<>(0, null, null);
    }

    static public <R> ResponseDto<R> success(R data) {
        return new ResponseDto<>(0, null, data);
    }

    static public ResponseDto<?> failed(Integer code, String msg) {
        return new ResponseDto<>(code, msg, null);
    }

    public T parseDataSafely() {
        if (code != 0) {
            throw new BaseRuntimeException(code, msg);
        }
        return data;
    }
}
