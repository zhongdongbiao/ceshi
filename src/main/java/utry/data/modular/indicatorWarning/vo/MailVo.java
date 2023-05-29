package utry.data.modular.indicatorWarning.vo;

import lombok.Data;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/17 16:41
 * description 返回的站内信的内容
 */
@Data
public class MailVo {

    /**
     * 投诉单号
     */
    private String complaintNumber;

    /**
     * 站内信的Tag 当前只有“投诉处理”
     */
    private String mailTag;

    /**
     * 系统状态
     */
    private String systemState;

    /**
     * 投诉开始时间
     */
    private String complaintStartTime;

    /**
     * 该行信息是否以点击过
     */
    private Boolean onClick;

}
