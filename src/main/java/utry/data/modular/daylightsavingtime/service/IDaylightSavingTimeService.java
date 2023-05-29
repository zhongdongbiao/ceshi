package utry.data.modular.daylightsavingtime.service;

import com.github.pagehelper.PageInfo;
import utry.data.base.Page;
import utry.data.modular.daylightsavingtime.dto.DaylightSavingTimeDto;
import utry.data.modular.daylightsavingtime.model.DaylightSavingTime;

import java.util.List;

/**
 * @author zd
 * @version 1.0.0
 * @ClassName IDaylightSavingTimeService.java
 * @Description TODO
 * @createTime 2022年02月09日 17:18:00
 */
public interface IDaylightSavingTimeService {
    /**
     * 分页获取日光节约时制列表
     * @param page 分页查询条件
     * @return 考勤位置列表
     */
    PageInfo selectDaylightSavingTimeByPage(Page<DaylightSavingTimeDto> page);

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
    DaylightSavingTime getDaylightSavingTimeInfo(String isEnable);
}
