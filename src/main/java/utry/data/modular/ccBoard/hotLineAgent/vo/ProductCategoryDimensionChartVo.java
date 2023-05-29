package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 热线项目服务类型-产品品类维度图表视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:50
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryDimensionChartVo {

    /**
     * 产品品类代码
     */
    private String productCategoryCode;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 总数量
     */
    private Integer totalNumber;

    /**
     * 服务类型
     */
    private List<ServiceAndNumberVo> serviceType;
}
