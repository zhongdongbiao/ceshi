package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 安全在库金额业务对象
 * @author: WangXinhao
 * @create: 2022-06-09 18:23
 **/
@Data
@ApiModel(value = "安全在库金额业务对象")
public class SecurityBO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode ;

    @ApiModelProperty(value = "库存日期")
    private String inventoryDate ;

    @ApiModelProperty(value = "库存图号")
    private String partDrawingNumber;

    @ApiModelProperty(value = "部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "安全在库数量")
    private Long count;
}
