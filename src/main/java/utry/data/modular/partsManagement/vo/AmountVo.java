package utry.data.modular.partsManagement.vo;

import lombok.Data;

/**
 * 移动端nds2
 * @author zhongdongbiao
 * @date 2023/3/6 9:58
 */
@Data
public class AmountVo {
    /**
     * 日期
     */
    private String date;
    /**
     * 在库金额
     */
    private Double amount;
}
