package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.dto.CancelPurchaseOrderDTO;

/**
 * 采购订单取消
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface CancelPurchaseOrderDao{

    /**
     *采购订单取消添加
     * @param cancelPurchaseOrderDTO
     */
    void insertCancelPurchaseOrder(CancelPurchaseOrderDTO cancelPurchaseOrderDTO);

    String getFlag(String receiptNumber);
}
