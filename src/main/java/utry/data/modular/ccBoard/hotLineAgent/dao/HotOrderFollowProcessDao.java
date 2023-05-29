package utry.data.modular.ccBoard.hotLineAgent.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.ccBoard.hotLineAgent.model.HotOrderFollowProcess;

/**
 * @program: data
 * @description: 热线服务单跟进流程信息持久层
 * @author: WangXinhao
 * @create: 2022-10-24 14:58
 **/

@Mapper
public interface HotOrderFollowProcessDao {

    /**
     * 插入热线服务单跟进流程信息
     *
     * @param hotOrderFollowProcess 热线服务单跟进流程信息
     * @return 数量
     */
    int insert(HotOrderFollowProcess hotOrderFollowProcess);
}
