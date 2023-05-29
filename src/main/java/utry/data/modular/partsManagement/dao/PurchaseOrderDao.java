package utry.data.modular.partsManagement.dao;


import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.bo.PurchaseOrderBO;
import utry.data.modular.partsManagement.bo.PurchaseOrderCountBO;
import utry.data.modular.partsManagement.dto.PurchaseDTO;
import utry.data.modular.partsManagement.dto.PurchaseOrderConditionDTO;
import utry.data.modular.partsManagement.dto.PurchaseOrderDTO;
import utry.data.modular.partsManagement.model.PurchaseOrder;
import utry.data.modular.partsManagement.model.PurchaseOrderDetail;
import utry.data.modular.partsManagement.vo.PurchaseOrderListVo;
import utry.data.modular.partsManagement.vo.PurchaseOrderVo;

import java.util.List;
import java.util.Map;

/**
 * 采购订单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface PurchaseOrderDao{

    /**
     * 采购订单添加
     * @param purchaseOrderDTO
     */
    void insertPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);

    /**
     * 采购订单详情数据修改
     * @param purchaseOrder
     */
    void updatePurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 根据部件图号获取未完成采购订单数量
     * @param partDrawingNo
     * @return
     */
    @DS("git_adb")
    int getPurchaseOrderCountByPartDrawingNo(String partDrawingNo);

    /**
     * 根据部件图号获取最近的采购订单
     * @return
     */
    List<Map<String,String>> getPurchaseOrderByPartDrawingNo();

    /**
     * 根据部件图号获取采购订单Vo
     * @param documentNo
     * @return
     */
   PurchaseOrderVo getPurchaseOrderVoByDocumentNo(String documentNo);

    /**
     * 根据部件图号获取采购订单DTO
     * @param partDrawingNo
     * @return
     */

    List<PurchaseDTO> getPurchaseDTOByPartDrawingNo(String partDrawingNo);

    /**
     * 根据部件图号获取最新的采购订单Vo
     * @param partDrawingNo
     * @return
     */
    PurchaseDTO getPurchaseOrderVo(String partDrawingNo);

    /**
     * 根据单据号获取采购订单详情
     * @param documentNo
     * @return
     */
    List<PurchaseOrderDetail> getPurchaseOrderDetailByNo(@Param("documentNo") String documentNo, @Param("partDrawingNo") String partDrawingNo);

    /**
     * 获取超时的采购订单数量
     * @return
     */
    @DS("git_adb")
    int getTimeOutOrder();

    String getFlag(String documentNo);

    /**
     * 获取采购订单列表
     * @param purchaseOrderConditionDTO
     * @return
     */
    @DS("git_adb")
    List<PurchaseOrderListVo> getPurchaseOrder(PurchaseOrderConditionDTO purchaseOrderConditionDTO);

    /**
     * 获取采购订单详情
     * @param documentNumber
     * @return
     */
    @DS("git_adb")
    PurchaseOrder getPurchaseOrderDetail(String documentNumber);

    /**
     * 根据日期查询采购在途订单数量
     * @param date 日期
     * @return PurchaseOrderCountBO
     */
    @DS("git_adb")
    List<PurchaseOrderCountBO> selectPurchaseOrderCount(@Param("date") String date);

    /**
     * 根据采购单号获取部件图号
     * @param documentNo
     * @return
     */
    @DS("git_adb")
    List<String> getPartDrawingNo(String documentNo);

    /**
     * 根据日期查询采购在途订单量
     * @param date
     * @return
     */
    @DS("git_adb")
    List<PurchaseOrderBO> getPurchaseOrderBO(@Param("date") String date);
}
