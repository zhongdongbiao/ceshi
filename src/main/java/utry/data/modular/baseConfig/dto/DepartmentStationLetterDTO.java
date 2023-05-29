package utry.data.modular.baseConfig.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 16:20
 * description 部门站内信DTO
 */
@Data
public class DepartmentStationLetterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 部门编号
     */
    @ApiModelProperty("部门编号")
    private String departmentNumber;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String departmentName;

    /**
     * 关联项目
     */
    @ApiModelProperty("关联项目")
    private String relationProject;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private String updateTime;

}
