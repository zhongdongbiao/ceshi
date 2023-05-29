package utry.data.modular.complaints.dao;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.complaints.dto.ComplaintDto;

import java.util.List;
import java.util.Map;

@Mapper
public interface ComplaintsDao {

    //新增投诉单信息
    Integer complaintDetailAdd(Map map);

    //修改投诉单信息
    Integer complaintDetailEdit(Map map);

    //通过投诉单号查询单个投诉单信息
    Map complaintById(String complaintNumber);

    //通过热线编号查询单个热线服务单
    Map hotLineById(String hotlineNumber);

    //热线服务单新增
    Integer hotLineAdd(Map map);

    //热线服务单修改
    Integer hotLineEdit(Map map);

    //批量插入投诉留言
    Integer complaintMessageAdd(List<Map> list);

    //删除投诉单号关联的投诉留言
    Integer complaintMessageDel(String complaintNumber);

    //查询投诉单号关联的投诉留言
    List<Map> complaintMessageListById(String complaintNumber);

    //批量插入投诉处理明细
    Integer complaintProcessDetailAdd(List<Map> list);

    //删除投诉单号关联的投诉处理明细
    Integer complaintProcessDetailDel(String complaintNumber);

    //查询投诉单号关联的投诉处理明细
    List<Map> complaintProcessDetailListById(String complaintNumber);

    //查询未完成投诉服务单号
    String[] notFinishIds();

    /**
     * 新增履历信息业务
     * @param list
     */
    void resumeDetail(List<Map<String, Object>> list);

    /**
     * 根据热线单号查询投诉单号
     * @param hotlineNumber
     * @return
     */
    String selectComplainNumberByHotlineNumber(@Param("hotlineNumber") String hotlineNumber);

    /**
     * 查询投诉7天解决率
     * @param complaintDto
     * @return
     */
    Map<String, Object> sevenDaySolveRate(@Param("dto") ComplaintDto complaintDto);

    /**
     * 分组查询每天的解决率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> daysSolveRate(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询投诉率
     * @param complaintDto
     * @return
     */
    Map<String, Object> complaintRate(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询每天的投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> daysComplaintRate(@Param("dto") ComplaintDto complaintDto);
    
    /**
     * 查询每月的投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> monthComplaintRate(@Param("dto") ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之投诉原因
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintRateReason(@Param("dto") ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之产品品类投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintRateProductCategory(@Param("dto") ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之大区投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintRateAccountingCenter(@Param("dto") ComplaintDto complaintDto);
    
    /**
     * 投诉率 - 下钻页面之地域(省份)投诉率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintRateProvinces(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询未结案数据
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> notOverCase(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询未结案总数
     * @param complaintDto
     * @return
     */
    Map<String, Object> notOverCaseNumber(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询投诉量占比
     * @return
     */
    Map<String, Object> selectComplainNumberProportion(@Param("dto") ComplaintDto complaintDto);

    /**
     * 按省份查询投诉量
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> selectComplainNumberByProvince(@Param("dto") ComplaintDto complaintDto);

    /**
     * 分组查询投诉量排名
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> selectComplainNumberRank(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询投诉7天解决率排名
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> selectComplainSolveRank(@Param("dto") ComplaintDto complaintDto);

    /**
     * 按省份查询投诉7天解决率占比
     * @param complaintDto
     * @return
     */
    Map<String, Object> selectComplainSolveProportion(@Param("dto") ComplaintDto complaintDto);

    /**
     * 按市查询投诉7天解决率占比
     * @param complaintDto
     * @return
     */
    Map<String, Object> selectComplainSolveProportionByCity(@Param("dto") ComplaintDto complaintDto);

    /**
     * 投诉异常监控
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintAbnormalMonitor(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询单据列表
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> selectDocumentsList(@Param("dto") ComplaintDto complaintDto);

    /**
     * 投诉原因分析
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintReasonAnalysis(@Param("dto") ComplaintDto complaintDto);

    /**
     * 分组查询每月或每天的解决率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> averageSolveRate(@Param("dto") ComplaintDto complaintDto);

    /**
     * 按月或按日查询N+1解决方案及时提交率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> daysSubmissionRate(@Param("dto") ComplaintDto complaintDto);

    /**
     * 查询N+1解决方案及时提交率
     * @param complaintDto
     * @return
     */
    Map<String, Object> complaintSolveSubmissionRate(@Param("dto") ComplaintDto complaintDto);

    /**
     * 投诉来源分析
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> complaintSourceAnalysis(@Param("dto") ComplaintDto complaintDto);

    /**
     * 品类投诉分析
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> categoryComplaintAnalysis(@Param("dto") ComplaintDto complaintDto);

    /**
     * 将派工单的7天解决率标签清空
     * @param dispatchingOrder
     * @return
     */
    Integer solveEligibleToNull(String dispatchingOrder);

    /**
     * 全部大区
     * @return
     */
    List<Map<String, Object>> selectAllRegion();

    /**
     * 根据投诉单号查询投诉详情
     * @param map
     * @return
     */
    Map<String, Object> selectComplaintDetailByNumber(Map<String, Object> map);

    /**
     * 根据投诉单号查询投诉留言信息
     * @param map
     * @return
     */
    List<Map<String, Object>> selectComplaintMessageByNumber(Map<String, Object> map);

    /**
     * 根据投诉单号查询投诉处理明细
     * @param map
     * @return
     */
    List<Map<String, Object>> selectComplaintProcessDetailByNumber(Map<String, Object> map);

    /**
     * 按市查询投诉量或投诉7天解决率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> selectComplainNumberByCity(@Param("dto") ComplaintDto complaintDto);

    /**
     * 门店排名
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> selectComplainByStore(@Param("dto") ComplaintDto complaintDto);

    /**
     * 通过核算中心查询部门信息
     * @param accountingCenterCode
     * @return
     */
    List<Map<String, Object>> selectDeptInfoByCenterCode(@Param("accountingCenterCode") String accountingCenterCode);

    /**
     * 通过投诉单号查询投诉升级信息
     * @param complaintNumber
     * @return
     */
    List<Map> selectComplaintUpdateByNumber(@Param("complaintNumber") String complaintNumber);

    /**
     * 批量插入投诉升级记录
     * @param list
     * @return
     */
    Integer complaintRecordAdd(List<Map> list);

    /**
     * 更新升级记录通知标志
     * @param updateNoticeList
     */
    void updateUpdateNotice(@Param("list") List<Map<String, String>> updateNoticeList);

    /**
     * N+1解决方案提交率排名
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> solveSubmitRank(@Param("dto") ComplaintDto complaintDto);

    /**
     * 结算费用及台量
     * @param complaintDto
     * @return
     */
    Map<String, Object> apiSettleData(@Param("dto") ComplaintDto complaintDto);
}
