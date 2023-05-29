package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.dto.DistributionListCancelDTO;

import java.util.List;
import java.util.Map;

/**
 * 配货明细取消单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface DistributionListCancelDao {

    /**
     *配货明细取消单添加
     * @param distributionListCancelDTO
     */
    @DS("git_adb")
    void insertDistributionListCancell(DistributionListCancelDTO distributionListCancelDTO);

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
    void updateOrder(@Param("orderValue")List<Map<String,String>> orderValue);

    String getOrderDetailFlag(String documentNo);
}
