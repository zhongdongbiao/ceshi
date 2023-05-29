package utry.data.enums;

/**
 * @program: data
 * @description: 返回code、message枚举类
 * @author: WangXinhao
 * @create: 2022-06-10 14:58
 **/

public enum  BizCodeEnum {

    NO_DATA(500, "暂无数据"),
    PARAMETER_EMPTY(400, "参数为空"),
    PARAMETER_ERROR(400, "参数错误"),
    ;

    private Integer code;

    private String message;

    BizCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
