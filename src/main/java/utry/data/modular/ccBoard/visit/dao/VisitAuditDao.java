package utry.data.modular.ccBoard.visit.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.visit.bo.CompleteProject;
import utry.data.modular.ccBoard.visit.bo.GrayProject;
import utry.data.modular.ccBoard.visit.bo.VisitAuditBo;
import utry.data.modular.ccBoard.visit.bo.VisitCompleteNumber;
import utry.data.modular.ccBoard.visit.dto.CompleteProjectDto;
import utry.data.modular.ccBoard.visit.dto.VisitResultDto;
import utry.data.modular.ccBoard.visit.dto.VisitTableDto;
import utry.data.modular.ccBoard.visit.model.VisitAudit;
import utry.data.modular.ccBoard.visit.vo.CallDetailVo;
import utry.data.modular.ccBoard.visit.vo.QueueVo;
import utry.data.modular.ccBoard.visit.vo.VisitResultVo;

import java.util.List;
import java.util.Map;

/**
 * 服务回访记录审核数据
 * @author zhongdongbiao
 * @date 2022/10/25 16:08
 */
@Mapper
public interface VisitAuditDao {

    /**
     * 根据服务单号查询是否存在服务回访记录审核数据
     * @param serviceNumber
     * @return
     */
    VisitAudit getFlag(String serviceNumber);

    /**
     * 创建服务回访记录审核数据
     * @param insertAudit
     */
    void createVisitAudit(VisitAudit insertAudit);

    /**
     * 服务回访记录审核数据修改
     * @param updateAudit
     */
    void updateVisitAudit(VisitAudit updateAudit);

    /**
     * 获取当日回访总量
     *
     * @param dateDurationQueueIdDto 查询条件
     * @param accountingCenter 核算中心
     * @return
     */
    @DS("git_adb")
    int getDayCompleteCount(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取每个坐席当日回访总量
     *
     * @param dateDurationQueueIdDto 查询条件
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<Map> getDayCompleteCountByVisitTable(@Param("dateDurationQueueIdDto") VisitTableDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 查询当月完成量
     * @param dateDurationQueueIdDto 查询条件
     * @param accountingCenter 队列id
     * @param completeNote 回访结果描述 1 完成
     * @return
     */
    @DS("git_adb")
    int monthCompleteCount(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter, @Param("completeNote") String completeNote);

    /**
     * 获取所有队列每日单日完成量
     * @param date
     * @param accountingCenter 队列id
     * @param completeNote 回访结果描述 1 完成
     * @return
     */
    @DS("git_adb")
    int getEveryDayCount(@Param("date") String date,@Param("accountingCenter") List<String> accountingCenter, @Param("completeNote") String completeNote);

    /**
     * 获取话务明细数据
     * @param accountCode
     * @return
     */
    @DS("git_adb")
    List<CallDetailVo> getCallDetail(@Param("accountCode") List<String> accountCode);

    /**
     * 获取回访记录
     * @param serviceNumber
     * @return
     */
    @DS("git_adb")
    VisitAuditBo getVisitRecord(@Param("serviceNumber") String serviceNumber);

    /**
     *获取回访记录审核数量
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @param systemState 1 已审核 0 待审核
     * @return
     */
    @DS("git_adb")
    Integer getComplete(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter, @Param("systemState") String systemState);

    /**
     * 获取已完成回访项目-任务分类维度
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<CompleteProject> getCompleteByTaskType(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取已完成回访项目-任务分类维度-灰色
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<GrayProject> getCompleteByTaskTypeByGray(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取已完成回访项目-大区维度
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<CompleteProject> getCompleteByRegion(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取已完成回访项目-大区维度-灰色
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<GrayProject> getCompleteByRegionByGray(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取已完成回访项目-产品品类维度
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<CompleteProject> getCompleteByProductCategory(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取已完成回访项目-产品品类维度-灰色
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<GrayProject> getCompleteByProductCategoryByGray(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取已完成回访项目-坐席维度
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<CompleteProject> getCompleteByTable(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取已完成回访项目-坐席维度-灰色
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<GrayProject> getCompleteByTableByGray(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取回访结果列表
     * @param visitResultDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<VisitResultVo> getVisitResult(@Param("visitResultDto") VisitResultDto visitResultDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取一级部门数据
     * @param businessType 1 回访 2 热线
     * @return
     */
    @DS("git_adb")
    List<QueueVo> getDeptList(@Param("businessType") String businessType);

    /**
     * 获取队列数据
     * @param businessType 1 回访 2 热线
     * @return
     */
    @DS("git_adb")
    List<QueueVo> getQueueList(@Param("businessType") String businessType);

    /**
     * 根据队列获取部门名称
     * @param deptId
     * @return
     */
    @DS("git_adb")
    List<String> getQueueDeptList(@Param("deptId") List<String> deptId);

    /**
     * 获取回访队列完成量
     * @param dateDurationQueueIdDto
     * @param accountingCenter
     * @return
     */
    List<VisitCompleteNumber> getVisitCompleteNumber(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 创建回访记录审核历史表
     * @param insertAudit
     */
    void createVisitAuditHistory(VisitAudit insertAudit);
}
