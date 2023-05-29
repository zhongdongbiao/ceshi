package utry.data.modular.baseData.service;

import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseData.dto.HistoriDataDto;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.util.RetResult;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface IBaseDataService {

    List<HistoriDataDto> getHistoryDisposable();

    int insertHistoryDisposable(@Param("list") List<HistoriDataDto> historiDataDtos);

    List<HistoriDataDto> getHistoryBybatch(Integer start, Integer end);
}
