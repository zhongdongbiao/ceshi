package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户工厂关联DTO
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class UserCategoryConfigDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * accountID
	 */
	private String accountId;
	/**
	 * 账号
	 */
	private String account;
	/**
	 * 角色状态
	 */
	private String status;
	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 性别：0未知1男2女
	 */
	private String sexuality;
	/**
	 * 删除状态0正常1已删除
	 */
	private String deleted;
	/**
	 * 修改时间
	 */
	private String updateTime;
	/**
	 * 锁定标记，0正常，1锁定
	 */
	private String isLocked;
	/**
	 * 邮箱地址
	 */
	private String email;
	/**
	 * 产品类型
	 */
	private String productType;
	/**
	 * 创建时间
	 */
	private String createTime;
}
