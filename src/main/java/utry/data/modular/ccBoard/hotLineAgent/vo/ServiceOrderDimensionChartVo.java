package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 热线项目服务类型-热线服务单维度图表视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:59
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrderDimensionChartVo {

    /**
     * 服务类型名称
     */
    private String serviceTypeName;

    /**
     * 总数量
     */
    private Integer totalNumber;

    /**
     * 服务明细
     */
    private List<ServiceAndNumberVo> serviceDetail;
}
