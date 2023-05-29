package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户工厂关联DTO
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class UserDistrictDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	/**
	 * accountID
	 */
	private String accountId;
	/**
	 * accountIDs
	 */
	private List<UserDistrictDTO> list;
	/**
	 * MD5密码
	 */
	private String password;
	/**
	 * 企业ID
	 */
	private String companyID;
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
	 * 人员ID
	 */
	private String staffId;
	/**
	 * 原密码
	 */
	private String Password1;
	/**
	 * 创建时间
	 */
	private String foundTime;
	/**
	 * 删除状态0正常1已删除
	 */
	private String deleted;
	/**
	 * 删除时间
	 */
	private String deleteTime;
	/**
	 * 上次提醒修改密码的时间
	 */
	private String remindTime;
	/**
	 *
	 */
	private String loginDate;
	/**
	 * 锁定标记，0正常，1锁定
	 */
	private String isLocked;
	/**
	 * 尝试登录次数
	 */
	private String attempts;
	/**
	 * 岗位
	 */
	private String job;
	/**
	 * 联系方式
	 */
	private String contactNumber;
	/**
	 * 备注
	 */
	private String description;
	/**
	 * 邮箱地址
	 */
	private String email;
	/**
	 * 系统状态
	 */
	private String systemStatus;
	/**
	 * 核算片区
	 */
	private String regional;
	/**
	 * 区管姓名
	 */
	private String adminName;
	/**
	 * 所辖区域
	 */
	private String area;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 更新时间
	 */
	private String updateTime;
	/**
	 * 所辖区域
	 */
	private String reduceArea;
	/**
	 * 核算片区id
	 */
	private String districtId;
}
