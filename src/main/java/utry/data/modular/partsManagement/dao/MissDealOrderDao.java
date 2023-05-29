package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.bo.MissDealOrderBO;
import utry.data.modular.partsManagement.dto.MissDealOrderDTO;
import utry.data.modular.partsManagement.model.MissDealOrderDetail;

import java.util.List;

/**
 * 缺件处理单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface MissDealOrderDao{

    /**
     *缺件处理单添加
     * @param missDealOrderDTO
     */
    void insertMissDealOrder(MissDealOrderDTO missDealOrderDTO);

    /**
     * 根据处理单ID获取缺件处理单数据
     * @param missDealOrderId
     * @return
     */
    MissDealOrderDTO getMissDealOrderDTO(String missDealOrderId);

    /**
     * 根据处理单单号获取处理单详情
     * @return
     */
    List<MissDealOrderDetail> getMissDealOrderDetail(@Param("documentNo") String documentNo,@Param("partDrawingNo") String partDrawingNo);

    String getOrderDetailFlag(String documentNo);

    /**
     * 根据时间、工厂查缺件订单量
     * @param date 日期
     * @param factoryCode 工厂代码
     * @return MissDealOrderBO
     */
    @DS("git_adb")
    List<MissDealOrderBO> selectMissDealOrderCount(@Param("date") String date, @Param("factoryCode") String factoryCode);
}
