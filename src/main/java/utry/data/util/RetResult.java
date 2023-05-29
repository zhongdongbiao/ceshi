package utry.data.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import utry.data.enums.RetCode;

public class RetResult<T> {
    public int code;



    @JsonIgnore
    private String respCode;

    private String msg;

    private T data;

    private String type;

    public RetResult<T> setCode(RetCode retCode) {
        this.code = retCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public RetResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RetResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public RetResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getType() {
        return type;
    }

    public RetResult<T> setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data + '\'' +
                ", type=" + type;
    }

    public String getRespCode() {
        return respCode;
    }

    public RetResult<T> setRespCode(String respCode) {
        this.respCode = respCode;
        return this;
    }
}
