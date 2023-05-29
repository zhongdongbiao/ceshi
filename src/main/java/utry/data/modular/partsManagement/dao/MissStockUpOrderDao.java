package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.dto.ProcessedOrderDTO;
import utry.data.modular.partsManagement.model.MissStockUpOrder;
import utry.data.modular.partsManagement.model.PackingListDetail;

import java.util.List;

/**
 * 缺件备货单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface MissStockUpOrderDao{

    /**
     * 缺件备货单添加
     * @param missStockUpOrder
     */
    void insertMissStockUpOrder(MissStockUpOrder missStockUpOrder);

    /**
     * 缺件备货单详情数据修改
     * @param missStockUpOrder
     */
    void updateMissStockUpOrder(MissStockUpOrder missStockUpOrder);

    /**
     * 根据单据数据缺件备货单数据
     * @return
     */
    List<MissStockUpOrder> getMissStockUpOrderByOrder(@Param("associatedOrderNumber")String associatedOrderNumber);

    /**
     * 根据时间段获取缺件备货单数量
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getMissStockUpOrderCount(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取实时的缺件单数
     * @return
     */
    @DS("git_adb")
    int realMissOrder();

    /**
     * 根据单据号获取是否重复
     * @param documentNo
     * @return
     */
    String getFlag(String documentNo);
}
