package utry.data.modular.technicalQuality.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.dto.OrderDetailDTO;
import utry.data.modular.technicalQuality.dto.SpiQualityFeedbackEditDto;
import utry.data.modular.technicalQuality.model.PartInformation;
import utry.data.modular.technicalQuality.model.QualityFeedback;

import java.util.List;
import java.util.Map;

/**
 * 品质反馈单详情
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface QualityFeedbackDao {

    /**
     * 品质反馈单添加
     * @param qualityFeedback
     */
    void insertQualityFeedback(QualityFeedback qualityFeedback);

    /**
     * 品质反馈单更新
     * @param spiQualityFeedbackEditDto
     */
    void updateQualityFeedback(SpiQualityFeedbackEditDto spiQualityFeedbackEditDto);

    /**
     * 品质反馈单状态查询
     * @param manageNumber
     */
    @DS("git_adb")
    String selectQualityFeedbackStatus(String manageNumber);
}
