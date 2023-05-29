package utry.data.modular.calendar.dao;

import utry.data.modular.calendar.model.CalendarDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.core.base.dao.IBaseDao;
import utry.data.modular.calendar.vo.AttendanceCalendarVo;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: lvlb
 * @Date: 2020/9/23 13:32
 */
@SuppressWarnings("all")
@Mapper
public interface CalendarMapper extends IBaseDao {

    /***
     * 获取日历数据
     * @param calendarDto
     * @return
     */
    List<CalendarDto> getCalendarList(@Param("dto") CalendarDto calendarDto);

    /***
     * 更新假期类型
     * @param calendarDto
     * @return
     */
    int updateCalendarType(@Param("dto") CalendarDto calendarDto);

    /***
     * 查询当前年月初始化数据
     * @param calendarDto
     * @return
     */
    int selectCountByYear(@Param("dto") CalendarDto calendarDto);

    /***
     * 清空当年数据
     * @param calendarDto
     */
    void deleteCountByYear(@Param("dto") CalendarDto calendarDto);

    /***
     * 保存日历数据
     * @param calendarDto
     */
    void insertCalendarData(@Param("dto") CalendarDto calendarDto);

    /**
     * 查询类型
     *
     * @param fullDate
     * @return
     */
    CalendarDto selectTypeByYear(@Param("fullDate") String fullDate);

    List<AttendanceCalendarVo> getCalendarVoList(@Param("dto") CalendarDto calendarDto);

    void updateCalendarTypeByDate(@Param("dto") CalendarDto calendarDto);

    /**
     * 获取当前是属于夏令时还是冬令时
     * @return
     */
    Map<String,Object> getDayLightTimeData();

    /**
     *
     * @param type
     * @param name
     * @return 设置夏令时时间
     */
    Integer setDayLightTimeData(@Param("type") String type, @Param("name") String name);
}
