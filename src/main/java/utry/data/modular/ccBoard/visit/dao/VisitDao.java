package utry.data.modular.ccBoard.visit.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.ccBoard.common.dto.DateDurationQueueIdDto;
import utry.data.modular.ccBoard.visit.model.VisitTask;
import utry.data.modular.ccBoard.visit.bo.NotComplete;

import java.util.List;

/**
 * 回访任务
 * 
 * @author zhongdongbiao
 * @date 2022-10-25 14:14:25
 */
@Mapper
public interface VisitDao {

    /**
     * 根据服务单号查询是否有数据
     * @param serviceNumber
     * @return
     */
    VisitTask getFlag(String serviceNumber);

    /**
     * 添加回访任务数据
     * @param insertTask
     */
    void create(VisitTask insertTask);

    /**
     * 根据服务单号修改回访任务
     * @param updateTask
     */
    void updateVisitTask(VisitTask updateTask);

    /**
     * 获取未完成回访任务的数量
     *
     * @param dateDurationQueueIdDto
     * @param accountingCenter
     * @return
     */
    @DS("git_adb")
    List<NotComplete> getNotCompete(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("accountingCenter") List<String> accountingCenter);

    /**
     * 获取回访任务数量
     * @param dateDurationQueueIdDto
     * @param type 完成状态 1 完成 0 未完成
     * @return
     */
    @DS("git_adb")
    Integer getVisitTask(@Param("dateDurationQueueIdDto") DateDurationQueueIdDto dateDurationQueueIdDto, @Param("type") String type,@Param("accountingCenter") List<String> accountingCenter);

}
