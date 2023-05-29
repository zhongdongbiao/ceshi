package utry.data.modular.baseData.service.impl;

import org.springframework.stereotype.Service;
import utry.data.modular.baseData.dao.HistoryDataDao;
import utry.data.modular.baseData.dto.HistoriDataDto;
import utry.data.modular.baseData.service.IBaseDataService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 工厂管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class BaseDataServiceImpl implements IBaseDataService {


    @Resource
    private HistoryDataDao historyDataDao;

    @Override
    public List<HistoriDataDto> getHistoryDisposable() {
        return historyDataDao.getHistoryDisposable();
    }

    @Override
    public int insertHistoryDisposable(List<HistoriDataDto> historiDataDtos) {
        return this.historyDataDao.insertHistoryDisposable(historiDataDtos);
    }

    @Override
    public List<HistoriDataDto>  getHistoryBybatch(Integer start, Integer end) {
        return historyDataDao.getHistoryBybatch(start,end);
    }
}

