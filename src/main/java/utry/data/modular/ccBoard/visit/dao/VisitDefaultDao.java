package utry.data.modular.ccBoard.visit.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.visit.bo.Complaint;
import utry.data.modular.ccBoard.visit.bo.DefaultRate;
import utry.data.modular.ccBoard.visit.dto.CompleteProjectDto;
import utry.data.modular.ccBoard.visit.dto.VisitDefaultDto;
import utry.data.modular.ccBoard.visit.model.VisitDefault;
import utry.data.modular.ccBoard.visit.vo.VisitDefaultVo;

import java.util.List;

/**
 * @author zhongdongbiao
 * @date 2022/10/25 16:09
 */
@Mapper
public interface VisitDefaultDao {

    /**
     * 根据服务单号查询是否有数据
     * @param serviceNumber
     * @return
     */
    VisitDefault getFlag(String serviceNumber);

    /**
     *添加回访违约单数据
     * @param insertDefault
     */
    void createVisitAudit(VisitDefault insertDefault);

    /**
     * 根据服务单号修改回访违约单数据
     * @param updateDefault
     */
    void updateVisitDefault(VisitDefault updateDefault);

    /**
     * 当月违约量
     * @param dateDurationQueueIdDto 查询条件
     * @param accountingCenter 队列id
     * @param result 审核结果 1 违约
     * @return
     */
    @DS("git_adb")
    int monthDefaultCount(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter, @Param("result") String result);

    /**
     * 获取所有队列每日违约量
     * @param date
     * @param accountingCenter 队列id
     * @param result 审核结果 1 违约
     * @return
     */
    @DS("git_adb")
    int getEveryDayDefaultCount(@Param("date") String date, @Param("accountingCenter") List<String> accountingCenter, @Param("result") String result);

    /**
     * 查询回访违约单数量
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @param systemState 1 已申诉 0 待申诉
     * @return
     */
    @DS("git_adb")
    Integer getNoComplaint(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter, @Param("systemState") String systemState);

    /**
     * 获取申诉统计-任务分类维度
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<Complaint> getComplaintByTaskType(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取申诉统计-大区维度
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<Complaint> getComplaintByRegion(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取申诉统计-产品品类维度
     * @param dateDurationQueueIdDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<Complaint> getComplaintByProductCategory(@Param("dateDurationQueueIdDto") CompleteProjectDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取回访违约列表
     * @param visitDefaultDto
     * @param accountingCenter 队列id
     * @return
     */
    @DS("git_adb")
    List<VisitDefaultVo> getVisitDefault(@Param("visitDefaultDto") VisitDefaultDto visitDefaultDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取违约率Bo
     * @param dateDurationQueueIdDto
     * @param accountingCenter
     * @return
     */
    @DS("git_adb")
    List<DefaultRate> getDefaultRate(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取回访违约记录
     * @param serviceNumber
     * @return
     */
    @DS("git_adb")
    VisitDefault getVisitDefaultDetail(@Param("serviceNumber") String serviceNumber);

}

