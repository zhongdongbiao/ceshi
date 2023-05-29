package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 品类类型树
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
public class TreeDTO extends PTreeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	//技能层级
	private String label;
	//子类集合
	private List<TreeDTO> children;

	public TreeDTO(String label, List<TreeDTO> children) {
		this.label = label;
		this.children = children;
	}

	public TreeDTO() {
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<TreeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<TreeDTO> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "TreeDTO{" +
				"label='" + label + '\'' +
				", children=" + children +
				'}';
	}
}
