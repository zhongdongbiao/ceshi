package utry.data.modular.indicatorWarning.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.core.websocket.bo.UserInfo;
import utry.data.modular.baseConfig.dto.IndicatorDTO;
import utry.data.modular.indicatorWarning.dto.AssumeUserDto;
import utry.data.modular.indicatorWarning.dto.IndicatorAnomalyWarningDto;
import utry.data.modular.indicatorWarning.dto.MailDto;

import java.util.List;
import java.util.Map;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/17 17:09
 * description
 */
@Mapper
public interface IndicatorWarningDao {

    /**
     * 获取站内信列表
     * @return
     */
    MailDto getStationLetter(@Param("complaintNumber") String complaintNumber);

    /**
     * 查询指定指标信息
     * @param businessCode 业务类型编码
     * @param month 月份
     * @param indicatorCode
     * @return
     */
    IndicatorDTO selectTargetByIndicatorCode(@Param("businessCode") String businessCode, @Param("month") String month,@Param("indicatorCode") String indicatorCode);

    /**
     * 获取零件管理的所有的用户担当
     * @return
     */
    List<AssumeUserDto> getPartsManagementAllAssume();

    /**
     * 获取技术品质的所有的用户担当
     * @return
     */
    List<AssumeUserDto> getTechnicalQualityAllAssume();

    /**
     * 获取大区服务管理的所有用户担当
     * @return
     */
    List<AssumeUserDto> getDistrictAllAssume();

    /**
     * 获取投诉直辖的所有用户担当
     * @return
     */
    List<AssumeUserDto> getComplaintAllAssume();

    /**
     * 获取零件管理的责任担当
     * @param map
     * @return
     */
    List<AssumeUserDto> getResponsibilityAssume(Map<String, String> map);

    /**
     * 获取在途天数超过三天的收货单
     * @param userId 用户id
     * @param inventoryDate
     * @return
     */
    List<IndicatorAnomalyWarningDto> getTransitPassThreeDays(@Param("userId") String userId, @Param("inventoryDate") String inventoryDate);

    /**
     * 获取流转天数超过三天的作业订单
     * @param orderType 订单类型
     * @return
     */
    List<IndicatorAnomalyWarningDto> getCirculationPassThreeDays(@Param("orderType") String orderType);

    /**
     * 获取部品低于最小安全在库的
     * @return
     */
    List<IndicatorAnomalyWarningDto> getInventoryWarning(@Param("userId") String userId);

    /**
     * 30分钟预约及时率
     * @param map
     * @return
     */
    Map<String, Object> timely(Map<String, String> map);


    /**
     * 首次预约准时上门率
     * @param map
     * @return
     */
    Map<String, Object> firstPunctuality(Map<String, String> map);

    /**
     * 非首次预约准时上门率
     * @param map
     * @return
     */
    Map<String, Object> punctuality(Map<String, String> map);

    /**
     * TAT平均服务完成时长
     * @param map
     * @return
     */
    Map<String, Object> average(Map<String, String> map);

    /**
     * 投诉7天解决率
     * @param map
     * @return
     */
    Map<String, Object> solve(Map<String, String> map);

    /**
     * 一次修复率
     * @param map
     * @return
     */
    Map<String, Object> repair(Map<String, String> map);


    /**
     * N+1投诉解决方案提交率
     * @param map
     * @return
     */
    Map<String, Object> scheme(Map<String, String> map);

    /**
     * 投诉六天未结案
     * @return
     */
    List<Map<String, Object>> sixDayNoOverCase();

    /**
     * 投诉大于1天未提交方案
     * @return
     */
    List<Map<String, Object>> oneDayNoSolution();

    /**
     * 技术品质 - 一次性修复率
     * @param map
     * @return
     */
    Map<String, Object> calculateRepairRate(Map<String, Object> map);

    /**
     * 技术品质 - 核心品质，品质单审核作业时长
     * @param map
     * @return
     */
    Map<String, Object> calculateApprovalDuration(Map<String, Object> map);

    /**
     * 技术品质 - 技术品质，品质单审核作业时长
     * @param map
     * @return
     */
    List<Map<String, Object>> calculateAssumeApprovalDuration(Map<String, Object> map);

    /**
     * 新品上市资料七天完备率查询
     * @param map
     * @return
     */
    Map<String, Object> calculateCompletionRate(Map<String, Object> map);

    /**
     * 新品上市六天未上传资料
     * @param map
     * @return
     */
    List<Map<String, Object>> sixDaysDataNotUpload(Map<String, Object> map);

    /**
     * 根据用户账户id获取用户的信息
     * @param accountIdList
     * @return
     */
    List<UserInfo> getUserInfoByAccountId(@Param("accountIdList") List<String> accountIdList);

    /**
     * 通过部门名称获取关联项目
     * @param departmentNumber 部门名称
     * @return
     */
    String selectRelationProject(String departmentNumber);

    /**
     * 通过用户Id获取产品类型编码，如果用户Id为空则获取全部的
     * @param userId 用户Id
     * @return
     */
    List<String> getProductTypeCodeByUserId(String userId);
}
