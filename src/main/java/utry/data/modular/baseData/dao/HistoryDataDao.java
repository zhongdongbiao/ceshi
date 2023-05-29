package utry.data.modular.baseData.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseData.dto.HistoriDataDto;
import utry.data.modular.partsManagement.dto.CancelDstributionOrderDTO;

import java.util.List;

/**
 * 配货取消单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface HistoryDataDao {


    List<HistoriDataDto> getHistoryDisposable();

    int insertHistoryDisposable(@Param("list") List<HistoriDataDto> historiDataDtos);

    List<HistoriDataDto> getHistoryBybatch(@Param("start") Integer start, @Param("end") Integer end);
}
