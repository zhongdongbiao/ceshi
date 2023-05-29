package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.LogisticsInformation;

import java.util.List;

/**
 * 物流信息数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface LogisticsInformationDao{

    /**
     * 物流信息数据添加
     * @param logisticsInformation
     */
    void insertLogisticsInformation(LogisticsInformation logisticsInformation);

    /**
     * 根据物流单号获取物流信息
     * @param logisticsSingleNumber
     */
    List<LogisticsInformation> getLogisticsInformation(String logisticsSingleNumber);

    Integer select(LogisticsInformation requestToObject);
}
