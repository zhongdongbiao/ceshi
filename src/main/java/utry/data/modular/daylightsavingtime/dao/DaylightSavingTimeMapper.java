package utry.data.modular.daylightsavingtime.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.daylightsavingtime.dto.DaylightSavingTimeDto;
import utry.data.modular.daylightsavingtime.model.DaylightSavingTime;

import java.util.List;

/**
 * @author zd
 * @version 1.0.0
 * @ClassName AttendanceLocationMapper.java
 * @Description TODO
 * @createTime 2022年02月08日 15:56:00
 */
@SuppressWarnings("all")
@Mapper
public interface DaylightSavingTimeMapper {
    /**
     * 分页查询位置单列表
     * @param pageData 分页查询条件
     * @return 黑白红名单列表
     */
    List<DaylightSavingTime> selectDaylightSavingTimeByPage(DaylightSavingTimeDto pageData);

    /**
     * 分页查询位置总数
     * @param pageData 分页查询条件
     * @return 黑白红名单总数
     */
    Integer selectDaylightSavingTimeTotalByPage(DaylightSavingTimeDto pageData);

    /**
     * 保存日光节约时制信息
     * @param daylightSavingTime
     * @return
     */
    Integer saveDaylightSavingTime(DaylightSavingTime daylightSavingTime);

    /**
     * 修改日光节约时制信息
     * @param daylightSavingTime
     * @return
     */
    Integer updateDaylightSavingTime(DaylightSavingTime daylightSavingTime);

    /**
     * 删除日光节约时制信息
     * @param ids
     */
    void deleteDaylightSavingTime(List<String> ids);
    /**
     * 获取指定状态的日光节约时制
     * @param isEnable
     * @return
     */
    DaylightSavingTime getDaylightSavingTimeInfo(@Param("isEnable") String isEnable);
}
