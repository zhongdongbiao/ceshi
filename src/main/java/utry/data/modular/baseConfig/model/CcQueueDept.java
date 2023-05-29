package utry.data.modular.baseConfig.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 队列基本信息
 * @author: WangXinhao
 * @create: 2022-10-31 11:05
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CcQueueDept {

    /**
     * 队列id
     */
    private String queueId;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 创建时间
     */
    private String creationTime;

    /**
     * 修改时间
     */
    private String updateTime;
}
