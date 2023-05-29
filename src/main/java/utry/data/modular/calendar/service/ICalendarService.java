package utry.data.modular.calendar.service;


import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import utry.data.modular.calendar.model.CalendarDto;
import utry.data.modular.calendar.vo.AttendanceCalendarVo ;

import java.util.List;
import java.util.Map;

/**
 *  @Description:
 *  @author: lvlb
 *  @Date: 2020/9/23 13:25
 */
public interface ICalendarService {

    /***
     * 获取日历数据
     * @param calendarDto
     * @return
     */
    List<CalendarDto> getCalendarList(CalendarDto calendarDto) throws Exception;

    /***
     * 更新假期类型
     * @param calendarDto
     * @return
     */
    int updateCalendarType(CalendarDto calendarDto);

    /***
     * 批量更新假期类型
     * @param calendarList
     */
    void batchUpdateCalendarType(List<CalendarDto> calendarList);

    /**
     * 获取该日期的属性
     * @param fullDate
     * @return
     */
    CalendarDto selectTypeByYear(String fullDate);

    /**
     *
     * @param calendarDto
     * @return
     */
    List<AttendanceCalendarVo> getCalendarVoList(CalendarDto calendarDto);

    /**
     * 导入excel设置节假日以及调班日期
     * @param file
     */
    void importCalendarTemplate(MultipartFile file);

    /**
     * 根据日期批量设置节假日或者调班
     * @param calendarList
     */
    void batchUpdateCalendarTypeByDate(List<CalendarDto> calendarList);

    /**
     * 导出Excel
     * @param response
     */
    void exportCalendarTemplate(HttpServletResponse response);

    /**
     * 判断该时间是否是工作时间
     * @param time
     * @return
     */
    String getWhetherWorkTime(String time);

    /**
     * 获取冬令时夏令时时间
     * @return
     */
    Map<String,Object> getDayLightTimeData();


    /**
     * 设置冬令时夏令时时间生效
     * @param type
     * @param name
     */
    void setDayLightTimeData(String type,String name);

}
