package utry.data.modular.technicalQuality.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 品类类型树
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class PTreeDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	//唯一标识
	private String id;
	//技能点类别名称
	private String name;
	//所属父技能类别id
	private String pid;
}
