package utry.data.modular.baseConfig.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : ldk
 * @date : 13:38 2022/11/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CCSeatInfo {


    /**
     * 用户id
     */
    private String id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 坐席分机号码（工号）
     */
    private String extension;
    /**
     * 状态（启用/禁用）
     */
    private String status;
    /**
     * 坐席所属队列号码
     */
    private String agentFromQueue;
    /**
     * 坐席所属队列名称
     */
    private String agentFromQueueName;
    /**
     * 坐席所属部门ID
     */
    private String departmentId;
    /**
     * 坐席所属部门名称
     */
    private String departmentName;

    /**
     * 创建时间
     */
    private String creationTime;

    /**
     * 修改时间
     */
    private String updateTime;

}
