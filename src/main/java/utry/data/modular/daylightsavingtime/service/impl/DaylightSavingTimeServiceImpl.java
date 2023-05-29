package utry.data.modular.daylightsavingtime.service.impl;/**
 * @ClassName DaylightSavingTimeServiceImpl.java
 * @author zd
 * @version 1.0.0
 * @Description TODO
 * @createTime 2022年02月09日 17:17:00
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import utry.data.base.Page;
import utry.data.modular.daylightsavingtime.dao.DaylightSavingTimeMapper;
import utry.data.modular.daylightsavingtime.dto.DaylightSavingTimeDto;
import utry.data.modular.daylightsavingtime.model.DaylightSavingTime;
import utry.data.modular.daylightsavingtime.service.IDaylightSavingTimeService;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO * @version 1.0 * @author zhangdi * @date 2022/2/9 17:17
 */
@Service
public class DaylightSavingTimeServiceImpl implements IDaylightSavingTimeService {
    @Resource
    private DaylightSavingTimeMapper daylightSavingTimeMapper;
    @Override
    public PageInfo selectDaylightSavingTimeByPage(Page<DaylightSavingTimeDto> page) {
        PageHelper.startPage(page.getPage(), page.getSize(),false);
        List<DaylightSavingTime> result = daylightSavingTimeMapper.selectDaylightSavingTimeByPage(page.getPageData());
        Integer total = daylightSavingTimeMapper.selectDaylightSavingTimeTotalByPage(page.getPageData());
        PageInfo<DaylightSavingTime> pageInfo = new PageInfo<>(result);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public Integer saveDaylightSavingTime(DaylightSavingTime daylightSavingTime) {
        return daylightSavingTimeMapper.saveDaylightSavingTime(daylightSavingTime);
    }

    @Override
    public Integer updateDaylightSavingTime(DaylightSavingTime daylightSavingTime) {
        return daylightSavingTimeMapper.updateDaylightSavingTime(daylightSavingTime);
    }

    @Override
    public void deleteDaylightSavingTime(List<String> ids) {
        daylightSavingTimeMapper.deleteDaylightSavingTime(ids);
    }

    @Override
    public DaylightSavingTime getDaylightSavingTimeInfo(String isEnable) {
        return daylightSavingTimeMapper.getDaylightSavingTimeInfo(isEnable);
    }
}
