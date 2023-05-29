package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.dto.ProcessedOrderDTO;
import utry.data.modular.partsManagement.dto.ReceiptConditionDTO;
import utry.data.modular.partsManagement.dto.ReceiptDTO;
import utry.data.modular.partsManagement.model.Receipt;
import utry.data.modular.partsManagement.model.ReceiptDetail;
import utry.data.modular.partsManagement.vo.ReceiptVo;

import java.util.List;
import java.util.Map;

/**
 * 收货单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface ReceiptDao{

    /**
     * 收货单添加
     * @param receiptDTO
     */
    void insertReceipt(ReceiptDTO receiptDTO);

    /**
     * 收货单详情数据修改
     * @param receipt
     */
    void updateReceipt(Receipt receipt);

    /**
     *收货单详情数据删除
     * @param documentNumber
     */
    void deleteReceipt(String documentNumber);

    /**
     * 根据时间段获取收货订单数
     * @param startDate
     * @param endDate
     */
    @DS("git_adb")
    int getCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("userId") String userId,@Param("inventoryDate") String inventoryDate);

    /**
     * 根据时间段获取符合NDS2的收货订单数
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getCountByNDS2(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("userId") String userId,@Param("inventoryDate") String inventoryDate);

    /**
     * 根据单据数据获取收货单详情数据
     * @param associatedOrderNumber
     * @return
     */
    @DS("git_adb")
    ReceiptDetail getReceiptDetailByOrder(String associatedOrderNumber);

    /**
     * 根据服务店收货单详情数据查询收货单数据
     * @param documentNumber
     * @return
     */
    @DS("git_adb")
    Receipt getReceiptByReceiptDetail(String documentNumber);

    /**
     * 根据服务店收货单详情数据查询收货单数据
     * @param documentNumber
     * @return
     */
    @DS("git_adb")
    int getReceiptDetailCountByOrderNumer(String documentNumber);

    /**
     * 根据省份查询货品在途时间
     * @param startDate
     * @param endDate
     * @param sort
     * @return
     */
    @DS("git_adb")
    List<Map<Object,Object>> getGoodTimeBySort(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("sort") String sort);

    /**
     * 根据来源单号获取收货单DTO
     * @param associatedOrderNumber
     * @return
     */
    List<ReceiptDTO> getReceiptDTO(String associatedOrderNumber);

    /**
     * 根据收货单号获取收货单详情
     * @param documentNumber
     * @return
     */
    @DS("git_adb")
    List<ReceiptDetail> getReceiptDetail(String documentNumber);

    /**
     * 获取服务店收货单列表
     * @param receiptConditionDTO
     * @return
     */
    @DS("git_adb")
    List<ReceiptVo> getReceiptList(ReceiptConditionDTO receiptConditionDTO);
    /**
     * 根据时间段获取收货订单数
     * @param startDate
     * @param endDate
     */
    @DS("git_adb")
    int getReceiptListCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
    /**
     * 根据时间段获取妥投订单数
     * @param startDate
     * @param endDate
     */
    @DS("git_adb")
    int getVoteCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取超时的妥投订单数
     * @return
     */
    @DS("git_adb")
    int getTimeOutReceipt();

    /**
     * 装箱单到收货单的总时长
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAllTime(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 收货单到妥投订单的总时长
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAllVoteTime(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取实时的收货单数量
     * @return
     */
    @DS("git_adb")
    int realReceiptOrder();

    /**
     * 获取实时的妥投单数量
     * @return
     */
    @DS("git_adb")
    int realVoteNumber();

    /**
     *根据收货单号获取收货单
     * @param documentNumber
     */
    String getReceiptFlag(String documentNumber);

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

    /**
     * 获取担当的符合nds3的服务点收货订单行数
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getCountByNDS3(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @DS("git_adb")
    int getCountByScreenDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @DS("git_adb")
    int getCountByNds2ScreenDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取超时的妥投订单行数
     * @return
     */
    int getVoteTimeOutLine();
}
