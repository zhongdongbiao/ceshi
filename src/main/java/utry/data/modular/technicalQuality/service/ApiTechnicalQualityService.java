package utry.data.modular.technicalQuality.service;

import utry.data.modular.technicalQuality.model.PartInformation;
import utry.data.modular.technicalQuality.model.QualityFeedback;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/8 9:47
 */
public interface ApiTechnicalQualityService {

    /**
     * 品质反馈单详情查询
     * @param qualityFeedback
     */
    List<PartInformation> queryQualityFeedbackDetail(QualityFeedback qualityFeedback);
}
