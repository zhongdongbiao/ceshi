package utry.data.modular.partsManagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @program: data
 * @description:
 * @author: WangXinhao
 * @create: 2022-06-17 19:39
 **/
@Data
@Builder
public class LibraryCountVO {

    @ApiModelProperty(value = "日期yyyy-MM-dd")
    private List<String> shijian;

    @ApiModelProperty(value = "在库数量曲线")
    private List<Long> zaikuquxian;

    @ApiModelProperty(value = "需求数量曲线")
    private List<Long> zxuqiuquxian;

    @ApiModelProperty(value = "安全在库曲线")
    private List<Long> anquanquxian;

    @ApiModelProperty(value = "3日均线")
    private List<Long> treeday;

    @ApiModelProperty(value = "7日均线")
    private List<Long> sivenday;
}
