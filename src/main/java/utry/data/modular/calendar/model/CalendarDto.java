package utry.data.modular.calendar.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 考勤日历（才可作为排班日历 基本日历使用）
 * @author: lvlb
 * @Date: 2020/9/24 16:48
 */
@Data
public class CalendarDto {
    @ApiModelProperty(value = "主键")
    private int id;
    @ApiModelProperty(value = "年份")
    private String year;
    @ApiModelProperty(value = "月份")
    private String month;
    @ApiModelProperty(value = "日")
    private String day;
    @ApiModelProperty(value = "年月日")
    private String fullDate;
    @ApiModelProperty(value = "周几")
    private String dayOfWeek;
    @ApiModelProperty(value = "日期类型:0工作日1非工作日")
    private int type;
    @ApiModelProperty(value = "假期类型：0补班 1法定假日放假")
    private String typeStatus;
    @ApiModelProperty(value = "描述")
    private String depict;
}
