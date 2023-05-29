package utry.data.modular.baseConfig.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/16 15:33
 * description 部门信息类
 */
@Data
public class DepartmentData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统状态
     */
    @ApiModelProperty("系统状态")
    private String systemStatus;

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
     * 核算中心代码
     */
    @ApiModelProperty("核算中心代码")
    private String accountingCenterCode;

    /**
     * 核算中心
     */
    @ApiModelProperty("核算中心")
    private String accountingCenter;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建用户
     */
    @ApiModelProperty("创建用户")
    private String createUser;

    /**
     * 更改时间
     */
    @ApiModelProperty("更改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 更改用户
     */
    @ApiModelProperty("更改用户")
    private String updateUser;

}
