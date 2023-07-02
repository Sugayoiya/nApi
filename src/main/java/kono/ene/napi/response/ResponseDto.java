package kono.ene.napi.response;


import kono.ene.napi.exception.BusinessException;
import kono.ene.napi.exception.ErrorEnum;
import lombok.Data;

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

    public static <T> ResponseDto<T> failed(ErrorEnum errorEnum) {
        return new ResponseDto<>(errorEnum.getErrorCode(), errorEnum.getErrorMsg(), null);
    }

    public T parseDataSafely() {
        if (code != 0) {
            throw new BusinessException(code, msg);
        }
        return data;
    }
}
