package utry.data.modular.partsManagement.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 日期金额业务类
 * @author: WangXinhao
 * @create: 2022-06-21 09:51
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "日期金额业务类")
public class DateAmountBO {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
}
