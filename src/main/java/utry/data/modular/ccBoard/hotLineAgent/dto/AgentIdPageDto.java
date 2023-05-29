package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.Data;

/**
 * @program: data
 * @description: 坐席id、分页查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-24 17:10
 **/

@Data
public class AgentIdPageDto {

    /**
     * 工号
     */
    private String agentId;

    /**
     * 分页页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
