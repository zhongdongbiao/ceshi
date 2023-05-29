package utry.data.base;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "统一返回对象")
public class ResultManage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "code", value = "状态值：200为正常，500和其他均为异常")
    private int code;

    @ApiModelProperty(name = "message", value = "错误信息：状态为非200时的错误信息")
    private String message;

    @ApiModelProperty(name = "data", value = "各业务数据")
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
