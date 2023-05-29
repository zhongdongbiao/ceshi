package utry.data.modular.baseConfig.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 10:24
 * description 用户投诉关联DTO
 */
@Data
public class UserComplaintDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 旧的用户id
     */
    @ApiModelProperty("旧的用户id")
    private String oldAccountId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String accountId;

    /**
     * 真实姓名
     */
    @ApiModelProperty("真实姓名")
    private String realName;

    /**
     * 账户
     */
    @ApiModelProperty("账户")
    private String account;

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
