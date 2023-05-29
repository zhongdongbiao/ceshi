package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.dto.CancelServiceOrderDTO;

import java.util.List;
import java.util.Map;

/**
 * 服务店备货取消单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface CancelServiceOrderDao {

    /**
     * 服务店备货取消单添加
     * @param cancelServiceOrderDTO
     */
    void insertCancelServiceOrder(CancelServiceOrderDTO cancelServiceOrderDTO);

    /**
     * 获取服务店备货取消单关联订单号
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getOrderValue();

    /**批量修改服务店备货取消单
     *
     * @param cancelServiceOrderValue
     */
    void updateOrder(@Param("cancelServiceOrderValue") List<Map<String,String>> cancelServiceOrderValue);

    String getFlag(String receiptNumber);
}
