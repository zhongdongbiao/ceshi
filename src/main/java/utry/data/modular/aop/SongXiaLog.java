package utry.data.modular.aop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 日志实体类
 */
@ApiModel
@Data
public class SongXiaLog {

    @ApiModelProperty(name = "id", value = "唯一id")
    private String id;

    @ApiModelProperty(name = "path", value = "接口路径")
    private String path;


    @ApiModelProperty(name = "parameter", value = "入参")
    private String parameter;

    @ApiModelProperty(name = "returnValue", value = "出参")
    private String returnValue;

    @ApiModelProperty(name = "operationDescribe", value = "操作描述")
    private String operationDescribe;

    @ApiModelProperty(name = "createTime", value = "日志创建日期（年月日）")
    private String createTime;

    @ApiModelProperty(name = "createDay", value = "日志创建日期（年月日时分秒）")
    private String createDay;

    @ApiModelProperty(name = "statusCode", value = "状态码")
    private String statusCode;


    public SongXiaLog(String id, String path, String parameter, String returnValue, String describe, String createTime, String createDay, String statusCode) {
        this.id = id;
        this.path = path;
        this.parameter = parameter;
        this.returnValue = returnValue;
        this.operationDescribe = describe;
        this.createTime = createTime;
        this.createDay = createDay;
        this.statusCode = statusCode;
    }
}
