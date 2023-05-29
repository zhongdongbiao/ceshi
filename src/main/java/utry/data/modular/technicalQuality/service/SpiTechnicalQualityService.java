package utry.data.modular.technicalQuality.service;

import utry.data.modular.technicalQuality.dto.SpiQualityFeedbackEditDto;
import utry.data.modular.technicalQuality.model.PartInformation;
import utry.data.modular.technicalQuality.model.QualityFeedback;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/8 9:47
 */
public interface SpiTechnicalQualityService {

    /**
     * 品质反馈单创建
     * @param qualityFeedback
     */
    void addQualityFeedback(QualityFeedback qualityFeedback);
    /**
     * 品质反馈单更新
     * @param spiQualityFeedbackEditDto
     */
    void updateQualityFeedback(SpiQualityFeedbackEditDto spiQualityFeedbackEditDto);
}
