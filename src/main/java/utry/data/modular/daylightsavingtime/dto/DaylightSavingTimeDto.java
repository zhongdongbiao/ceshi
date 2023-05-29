package utry.data.modular.daylightsavingtime.dto;/**
 * @ClassName DaylightSavingTime.java
 * @author zd
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022年02月10日 09:20:00
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * TODO * @version 1.0 * @author zhangdi * @date 2022/2/10 9:20
 */
@Data
@ApiModel(value = "日光节约时制信息")
public class DaylightSavingTimeDto implements Serializable {
    @ApiModelProperty(name = "id", value = "唯一标识")
    private String id;
    @ApiModelProperty(name = "daylightSavingTimeName", value = "日光节约时制名称")
    private String daylightSavingTimeName;
    @ApiModelProperty(name = "daylightSavingTimeMorning", value = "日光节约时制上午时间")
    private String daylightSavingTimeMorning;
    @ApiModelProperty(name = "daylightSavingTimeAfternoon", value = "日光节约时制下午时间")
    private String daylightSavingTimeAfternoon;
    @ApiModelProperty(name = "isEnable", value = "是否启用 1.是 0 否")
    private String isEnable;

}
