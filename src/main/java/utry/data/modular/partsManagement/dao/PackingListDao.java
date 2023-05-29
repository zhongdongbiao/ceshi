package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.dto.PackingListDTO;
import utry.data.modular.partsManagement.dto.ProcessedOrderDTO;
import utry.data.modular.partsManagement.model.PackingList;
import utry.data.modular.partsManagement.model.PackingListDetail;
import utry.data.modular.partsManagement.model.ReceiptDetail;

import java.util.List;
import java.util.Map;

/**
 * 装箱单数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface PackingListDao{

    /**
     *装箱单数据添加
     * @param packingListDTO
     */
    void insertPackingList (PackingListDTO packingListDTO);

    /**
     * 根据单据数据获取装箱单详情数据
     * @param documentNumber
     * @return
     */
    @DS("git_adb")
    PackingListDetail getPackingListDetailByOrder(String documentNumber);

    /**
     * 根据单据数据获取装箱单详情数据
     * @param packingListNo
     * @return
     */
    @DS("git_adb")
    PackingList getPackingListByPackingListDetail(String packingListNo);

    /**
     * 根据单据号数据获取装箱单详情行数
     * @param documentNumber
     * @return
     */
    @DS("git_adb")
    int getPackingListCountByOrder(String documentNumber);

    /**
     * 获取装箱单DTO数据
     * @param associatedOrderNumber
     * @return
     */
    List<PackingListDTO> getPackingListDTO(String associatedOrderNumber);

    /**
     * 根据装箱单号获取装箱单详情
     * @param packingListNo
     * @return
     */
    List<PackingListDetail> getPackingListDetail(String packingListNo);

    /**
     * 获取实时的装箱单数量
     * @return
     */
    @DS("git_adb")
    int getPackingListCount();

    /**
     * 根据时间段获取没有生成了缺件处理单的装箱单的总数量
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getNoMisCount(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据时间段获取生成了缺件处理单的装箱单的总数量
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAllMisCount(@Param("startDate") String startDate, @Param("endDate") String endDate);


    /**
     * 根据时间段获取未走缺件处理单的配货单到装箱单的总时长
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAllTime(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据时间段获取生成了缺件处理单的配货单到装箱单的总时长
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAllMisTime(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取装箱单的关联订单号
     */
    @DS("git_adb")
    List<Map<String,String>> getPackingValue();

    /**
     * 批量修改装箱单
     * @param packingValue
     */
    void updatePacking(@Param("packingValue") List<Map<String,String>> packingValue);

    String getOrderDetailFlag(String packingListNo);
}
