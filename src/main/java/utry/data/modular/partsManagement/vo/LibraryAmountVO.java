package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @program: data
 * @description: 部品在库金额曲线视图
 * @author: WangXinhao
 * @create: 2022-06-16 17:00
 **/

@Data
@Builder
@ApiModel(value = "部品在库金额曲线视图")
public class LibraryAmountVO {

    @ApiModelProperty(value = "日期yyyy-MM-dd")
    private List<String> shijian;

    @ApiModelProperty(value = "在库金额曲线（元）")
    private List<Double> zaikuquxian;

    @ApiModelProperty(value = "需求金额曲线（元）")
    private List<Double> zxuqiuquxian;

    @ApiModelProperty(value = "安全在库曲线（元）")
    private List<Double> anquanquxian;

    @ApiModelProperty(value = "3日均线（元）")
    private List<Double> treeday;

    @ApiModelProperty(value = "7日均线（元）")
    private List<Double> sivenday;


}
