package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.calendar.model.CalendarDto;
import utry.data.modular.partsManagement.bo.*;
import utry.data.modular.partsManagement.dto.*;
import utry.data.modular.partsManagement.model.PartOrder;
import utry.data.modular.partsManagement.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 订单详情
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@SuppressWarnings("all")
@Mapper
public interface OrderDetailDao {

    /**
     * 订单详情添加
     *
     * @param orderDetailDTO
     */
    void insertOrderDetail(OrderDetailDTO orderDetailDTO);

    /**
     * 根据订单日期时间段获取订单行数量
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    @DS("git_adb")
    int getCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,
                             @Param("userId") String userId,@Param("inventoryDate") String inventoryDate);


    /**
     * 根据订单日期时间段获取订单行数据
     * @return
     */
    List<PartOrderDTO> getPartOrderDTOByDate();


    /**
     * 根据订单号获取订单详情行数
     *
     * @param documentNumber
     * @return
     */
    @DS("git_adb")
    int getOrderDetailVoCount(String documentNumber);

    /**
     * 根据日期时间段获取考勤日历
     * @param startDate
     * @param endDate
     * @return
     */
    List<CalendarDto> getWorkDayCount(@Param("startDate") String startDate,@Param("endDate") String endDate);

    /**
     * 根据时间获取未更新的订单详情
     * @param startDate
     * @param endDate
     * @return
     */
    List<PartOrderDTO> getUpdatePartOrder(@Param("startDate") String startDate, @Param("endDate") String endDate);


    /**
     * 批量修改订单的预计完成时间预计采购时间
     * @param orderDetailVos
     */
    void updateOrder(@Param("orderDetailVos") List<OrderDetailVo> orderDetailVos);

    /**
     *根据条件查询获取待处理订单详细数据
     * @param orderDetailConditionDto
     * @return
     */
    @DS("git_adb")
    List<OrderDetailVo> getprocessedOrder(OrderDetailConditionDTO orderDetailConditionDto);

    /**
     * 根据条件查询获取缺件处理订单详细数据
     * @param orderDetailConditionDto
     * @return
     */
    @DS("git_adb")
    List<OrderDetailVo> getMissStockUpOrder(OrderDetailConditionDTO orderDetailConditionDto);

    /**
     * 根据条件查询获取已装箱订单详细数据
     * @param orderDetailConditionDto
     * @return
     */
    @DS("git_adb")
    List<OrderDetailVo> getVoteOrder(OrderDetailConditionDTO orderDetailConditionDto);

    /**
     * 根据条件查询获取已妥投订单详细数据
     * @param orderDetailConditionDto
     * @return
     */
    @DS("git_adb")
    List<OrderDetailVo> getPackingListDetail(OrderDetailConditionDTO orderDetailConditionDto);

    /**
     * 根据作业订单号获取作业订单DTO
     * @param documentNumber
     * @return
     */

    OrderDetailDTO getOrderDetailDTO(String documentNumber);

    /**
     * 获取作业订单详情
     * @param documentNumber
     * @return
     */
    List<PartOrder> getPartOrder(@Param("documentNumber") String documentNumber, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取某个时间段的所有作业订单数
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getOrderCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取实时的作业订单数
     * @return
     */
    @DS("git_adb")
    int getRealOrderCount();

    /**
     * 获取超时的作业订单数
     * @return
     */
    @DS("git_adb")
    int getTimeOutOrderCountByDate();

    /**
     * 获取所有作业订单
     * @param orderDetailConditionDto
     * @return
     */
    @DS("git_adb")
    List<OrderDetailVo> selectAllDetail(OrderDetailConditionDTO orderDetailConditionDto);

    /**
     * 根据关联订单号查询是否存在于订单详情列表
     * @param documentNumber
     */
    String getOrderDetailFlag(String documentNumber);

    /**
     * 获取已经完成的订单详情
     */
    @DS("git_adb")
    List<Map<String,String>> selectNoOrderDetail();

    /**
     * 获取已经取消的订单详情
     */
    @DS("git_adb")
    List<Map<String,String>> selectCancelOrderDetail();

    /**
     * 修改已经完成的订单详情
     */
    void updateOrderDatil();

    /**
     * 修改已经取消的订单详情
     */
    void updateCancelOrderDatil();

    /**
     * 获取已经完成的订单
     */
    @DS("git_adb")
    List<Map<String,String>> selectNoOrder();

    /**
     * 修改已经完成的订单
     */
    void updateNotOrder();

    /**
     * 查询出货时间
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> selectTimeOrderDetail();

    /**
     * 添加出货时间
     * @param maps
     */
    void addTimeOrder(@Param("orderValue")List<Map<String,String>> maps);

    /**
     * 获取发货进度
     * @param documentNumber
     */
    String getDeliverySchedule(@Param("documentNumber")String documentNumber);

    /**
    * 根据订单日期时间段获取符合出货即纳率订单行数量
     * @param startDate
     * @param endDate
     * @param userId
     * @return
             */
    @DS("git_adb")
    int countByOrder(@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("userId") String userId,@Param("inventoryDate") String inventoryDate);

    /**
     * 根据订单日期时间段获取出货时间
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    List<Map<Object,Object>> getShipmentTime(@Param("startDate")String startDate,@Param("endDate") String endDate);

    /**
     * 根据品类获取所有的订单行数
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    List<Map<Object,Object>> getCountByDateByProductCategory(@Param("startDate") String startDate, @Param("endDate")String endDate);


    /**
     * 根据品类获取所有符合订单行数
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    List<Map<Object,Object>> countByOrderByProductCategory(@Param("startDate") String startDate,@Param("endDate") String endDate);

    /**
     * 获取时间段内的备货订单
     *
     * @param chartConditionDto
     * @param inventoryDate
     */
    @DS("git_adb")
    Integer getGoodOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取时间段内的作业订单
     *
     * @param chartConditionDto
     * @param inventoryDate
     */
    @DS("git_adb")
    Integer getWorkOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取时间段内的缺件采购中订单
     * @param chartConditionDto
     */
    @DS("git_adb")
    Integer getMissOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    @DS("git_adb")
    Integer getMissOrderDetail(@Param("date") String date);

    /**
     * 获取时间段内的已装箱订单
     *
     * @param chartConditionDto
     * @param inventoryDate
     */
    @DS("git_adb")
    Integer getPakageOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取时间段内的已妥投订单
     *
     * @param chartConditiVonDto
     * @param inventoryDate
     */
    @DS("git_adb")
    Integer getAlreadyVoteOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取时间段内的待处理订单
     *
     * @param chartConditionDto
     * @param inventoryDate
     */
    @DS("git_adb")
    Integer getProcesseOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取时间段内的服务店备货订单缺件采购中订单
     *
     * @param chartConditionDto
     * @param inventoryDate
     */
    @DS("git_adb")
    Integer getGoodMissOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取时间段内的服务店作业订单缺件采购中订单
     *
     * @param chartConditionDto
     * @param inventoryDate
     */
    @DS("git_adb")
    Integer getWorkMissOrder(@Param("chartConditionDto") ChartConditionDto chartConditionDto, @Param("inventoryDate") String inventoryDate);

    /**
     * 根据出货历时天数获取订单行数
     *
     * @param chartConditionDto
     * @param days
     * @param inventoryDate
     * @return
     */
    @DS("git_adb")
    Integer getOrderShipmentTime(@Param("chartConditionDto")ChartConditionDto chartConditionDto, @Param("days") String days, @Param("inventoryDate") String inventoryDate);

    /**
     * 根据出货历时天数获取缺货订单行数
     *
     * @param chartConditionDto
     * @param days
     * @param inventoryDate
     * @return
     */
    @DS("git_adb")
    Integer getMisOrderShipmentTime(@Param("chartConditionDto")ChartConditionDto chartConditionDto, @Param("days") String days, @Param("inventoryDate") String inventoryDate);

    /**
     * 切换为品类视角
     * @param productCategoryConditionDto
     * @return
     */
    @DS("git_adb")
    List<ProductCategoryVo> getProductCategory(ProductCategoryConditionDto productCategoryConditionDto);

    /**
     * 以天为单位查询部件图号需要数量
     * @param date 日期
     * @return PartDrawingNoNeedNumberBO
     */
    List<PartDrawingNoNeedNumberBO> selectPartDrawingNoNeedNumberByDate(@Param("date") String date);

    /**
     * 获取出货异常的单数
     * @param date
     * @return
     */
    @DS("git_adb")
    Integer getNotShipment(@Param("date") String date);

    /**
     * 获取N+1出货的单数
     * @param date
     * @return
     */
    @DS("git_adb")
    Integer getIsShipment(@Param("date") String date);

    /**
     * 根据时间查询服务店订单量
     * @param date
     * @return ServiceStoreOrderCountBO
     */
    @DS("git_adb")
    List<ServiceStoreOrderCountBO> selectServiceStoreOrderCount(@Param("date") String date);

    /**
     * 根据时间查询服务店异常订单量
     * @param date
     * @return ServiceStoreOrderAbnormalCountBO
     */
    @DS("git_adb")
    List<ServiceStoreOrderAbnormalCountBO> selectServiceStoreOrderAbnormalCount(@Param("date") String date);

    /**
     * 根据时间查仓库-部件图号-数量-时间
     * @param date
     * @return
     */
    List<PartDrawingNoNeedNumberOrderStartTimeBO> selectPartDrawingNoNeedNumberOrderStartTimeByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据条件查询获取已完成订单详细数据
     * @param orderDetailConditionDto
     * @return
     */
    List<OrderDetailVo> getCompleteListDetail(OrderDetailConditionDTO orderDetailConditionDto);

    /**
     * 根据条件获取大屏待处理订单
     * @param startDate
     * @param endDate
     * @return
     */
    List<JobOrderVo> getScreenProcess(@Param("startDate") String startDate, @Param("endDate")String endDate);
    /**
     * 根据条件获取大屏缺件采购中订单
     * @param startDate
     * @param endDate
     * @return
     */

    List<JobOrderVo> getScreenMis(@Param("startDate") String startDate, @Param("endDate")String endDate);
    /**
     * 根据条件获取大屏已装箱订单
     * @param startDate
     * @param endDate
     * @return
     */

    List<JobOrderVo> getScreenPackage(@Param("startDate") String startDate, @Param("endDate")String endDate);
    /**
     * 根据条件获取大屏已妥投订单
     * @param startDate
     * @param endDate
     * @return
     */

    List<JobOrderVo> getScreenApprove(@Param("startDate") String startDate, @Param("endDate")String endDate);
    /**
     * 根据条件获取大屏已收货订单
     * @param startDate
     * @param endDate
     * @return
     */
    List<JobOrderVo> getScreenGood(@Param("startDate") String startDate, @Param("endDate")String endDate);

    /**
     * 获取未添加考核截至日期的数据
     * @return
     */
    List<NoDeadlineOrderBo> getNoDeadlineOrderBo();

    /**
     * 获取非工作日日期
     * @return
     */
    List<CalendarDto> getAllWorkDayCount();

    /**
     * 订单添加考核时间以及考核截至时间
     * @param noDeadlineOrderBo
     */
    void updateDeadLine(@Param("noDeadlineOrderBo") List<NoDeadlineOrderBo> noDeadlineOrderBo);

    /**
     * 获取超时的作业订单行数
     * @return
     */
    int getWorkOrderTimeOutLine();

    /**
     * 根据品类查询工厂别部品出货即纳率
     * @param startDate
     * @param endDate
     * @param inventoryDate
     * @param sort
     * @return
     */
    @DS("git_adb")
    List<FactoryShipmentVo> getFactoryShipmentBySort(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("inventoryDate") String inventoryDate, @Param("sort") String sort);

    /**
     * 部品出货即纳率曲线
     * @param startDate
     * @param endDate
     * @param inventoryDate
     * @return
     */
    @DS("git_adb")
    List<PhoneShipmentVo> shipmentCurve(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("inventoryDate") String inventoryDate);

    /**
     * nds2曲线
     * @param startDate
     * @param endDate
     * @param inventoryDate
     * @return
     */
    @DS("git_adb")
    List<Nds2CurveVo> nds2Curve(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("inventoryDate") String inventoryDate);
}

