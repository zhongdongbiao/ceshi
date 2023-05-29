package utry.data.modular.ccBoard.visit.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 违约率
 * @author zhongdongbiao
 * @date 2022/11/3 15:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultRate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 回访完成违约率
     */
    private String completeDefaultRate;

    /**
     * 回访审核违约率
     */
    private String auditDefaultRate;
}
