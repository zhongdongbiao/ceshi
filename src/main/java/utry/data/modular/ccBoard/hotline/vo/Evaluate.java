package utry.data.modular.ccBoard.hotline.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 客户评价实体类
 * @Author zh
 * @Date 2022/12/20 13:13
 */
@Data
public class Evaluate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 录音文件
     */
    private String recordFileName;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 服务明细
     */
    private String serviceDetails;

    /**
     * 满意度评价
     */
    private String comment;


    private String agentId;

}
