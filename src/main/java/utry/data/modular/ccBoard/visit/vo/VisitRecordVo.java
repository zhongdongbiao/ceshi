package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;
import utry.data.modular.ccBoard.visit.bo.VisitAuditBo;
import utry.data.modular.ccBoard.visit.model.VisitDefault;

import java.io.Serializable;

/**
 * 服务回访记录Vo
 * @author zhongdongbiao
 * @date 2022/11/4 14:34
 */
@Data
public class VisitRecordVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 服务回访记录
     */
    private VisitAuditBo visitAudit;

    /**
     * 回访违约单
     */
    private VisitDefault visitDefault;
}
