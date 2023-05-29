package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class UserData implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 账号
	 */
	private String account;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 手机号码
	 */
	private String mobilePhone;
	/**
	 * 电话号码
	 */
	private String phone;
	/**
	 * 邮箱地址
	 */
	private String email;
	/**
	 * 部门
	 */
	private String dept;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 状态
	 */
	private String state;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
