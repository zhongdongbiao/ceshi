package utry.data.modular.indicatorWarning.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/18 11:19
 * description
 */
@Data
public class IndicatorAnomalyWarningDto {

    @ApiModelProperty(name = "documentNumber", value = "收货单号/作业订单号")
    private String documentNumber;

    @ApiModelProperty(name = "transitDays", value = "在途天数")
    private Double transitDays;

    @ApiModelProperty(name = "circulationDays", value = "流转天数")
    private Integer circulationDays;

    @ApiModelProperty(name = "partDrawingNumber", value = "部件图号")
    private String partDrawingNumber;

    @ApiModelProperty(name = "describedDrawingNo", value = "图号描述")
    private String describedDrawingNo;

    @ApiModelProperty(name = "currentInventory",  value = "当前库存")
    private String currentInventory;

    @ApiModelProperty(name = "arrivalDays", value = "还有多少天到货")
    private Integer arrivalDays;

}
