package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 部门对应队列id集合业务类
 * @author: WangXinhao
 * @create: 2022-11-14 12:11
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeptQueueIdBo {

    /**
     * 父部门id
     */
    private String deptId;

    /**
     * 队列id
     */
    private List<String> queueId;
}
