package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.dto.CancelDstributionOrderDTO;

import java.util.List;
import java.util.Map;

/**
 * 配货取消单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface CancelDstributionOrderDao {

    /**
     * 新增配货取消单数据
     * @param cancelDstributionOrderDTO
     */
    void insertCancelDstributionOrder(CancelDstributionOrderDTO cancelDstributionOrderDTO);

    /**
     * 获取配货取消单关联订单号
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getOrderValue();

    /**
     * 批量修改配货取消单
     * @param dstributionOrderValue
     */
    void updateOrder(@Param("dstributionOrderValue") List<Map<String,String>> dstributionOrderValue);

    String getOrderDetailFlag(String documentNo);
}
