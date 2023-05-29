package utry.data.modular.indicatorWarning.vo;

import lombok.Data;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/18 10:43
 * description 指标异常预警的Vo参数类
 */
@Data
public class IndicatorAnomalyWarningVo {

    /**
     * 指标预警值
     */
    private String warningValue;

    /**
     * 预警文案
     */
    private String warningCopyWriting;

    /**
     * 收货单单号/作业订单号
     */
    private String documentNumber;


}
