package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目服务类型-服务类型、服务明细视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:54
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceAndNumberVo {

    /**
     * 服务类型/服务明细名称
     */
    private String name;

    /**
     * 总数量
     */
    private Integer totalNumber;

    /**
     * 已受理数量
     */
    private Integer acceptNumber;
}
