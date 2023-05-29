package utry.data.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author zhongdongbiao
 * @date 2022/5/5 10:06
 */
@Data
public class DateConditionUtil implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类型 0 在此之间 1 早于 2 晚于 3 当日
     */
    @ApiModelProperty("类型 0 在此之间 1 早于 2 晚于 3 当日")
    private String type;

    /**
     * 值 asc升序 desc降序
     */
    @ApiModelProperty("值 asc升序 desc降序")
    private String sort;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startDate;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String endDate;
}
