package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.dto.DistributionSingleDTO;
import utry.data.modular.partsManagement.model.DistributionSubsidiary;

import java.util.List;
import java.util.Map;

/**
 * 配货单详情数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface DistributionSingleDao{

    /**
     * 配货单详情数据添加
     * @param distributionSingleDTO
     */
    void insertDistributionSingle(DistributionSingleDTO distributionSingleDTO);

    /**
     * 根据订单号获取配货单数据
     * @param associatedOrderNumber
     * @return
     */
    List<DistributionSingleDTO> getDistributionSingleDTO(String associatedOrderNumber);

    /**
     * 根据配货单号获取配货明细
     * @param distributionSingleNo
     * @return
     */
    List<DistributionSubsidiary> getDistributionSubsidiary(@Param("distributionSingleNo")String distributionSingleNo,@Param("associatedOrderNumber")String associatedOrderNumber);

    /**
     * 根据时间段获取配货单到作业订单的总时长
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAllTime(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据时间段获取配货单总数量
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAllCount(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取实时的配货订单数
     * @return
     */
    @DS("git_adb")
    int distributionOrderCount();

    /**
     * 获取附表中关联的订单号
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getOrderValue();

    /**
     * 批量修改主表数
     * @param orderValue
     */
    void updateOrder(@Param("orderValue") List<Map<String,String>> orderValue);

    String getOrderDetailFlag(@Param("singleNo")String singleNo,@Param("serviceStoreNumber") String serviceStoreNumber);
}
