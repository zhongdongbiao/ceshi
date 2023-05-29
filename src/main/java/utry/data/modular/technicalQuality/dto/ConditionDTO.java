package utry.data.modular.technicalQuality.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhongdongbiao
 * @date 2022/5/5 10:06
 */
@Data
public class ConditionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文字类型 0 等于 1 相似 2 不等于
     * 数字类型 = 等于 >= 大于等于 > 大于 <>不等于 <= 小于等于 < 小于
     */
    @ApiModelProperty("文字类型 0 等于 1 相似 2 不等于")
    private String textType;

    @ApiModelProperty("数字类型 = 等于 >= 大于等于 > 大于 <>不等于 <= 小于等于 < 小于")
    private String numberType;

    /**
     * 值 ASC升序 DESC降序
     */

    @ApiModelProperty("值 ASC升序 DESC降序")
    private String sort;

    /**
     * 值
     */
    @ApiModelProperty("值")
    private String value;
}
