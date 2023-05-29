package utry.data.modular.technicalQuality.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.technicalQuality.dao.QualityFeedbackDao;
import utry.data.modular.technicalQuality.dto.SpiQualityFeedbackEditDto;
import utry.data.modular.technicalQuality.model.PartInformation;
import utry.data.modular.technicalQuality.model.QualityFeedback;
import utry.data.modular.technicalQuality.service.SpiTechnicalQualityService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 技术品质SPI实现类
 *
 * @author zhongdongbiao
 * @date 2022/4/8 9:47
 */
@Service
public class SpiTechnicalQualityServiceImpl implements SpiTechnicalQualityService {

    @Resource
    private QualityFeedbackDao qualityFeedbackDao;

    /**
     * 品质反馈单创建
     * @param qualityFeedback
     */
    @Override
    public void addQualityFeedback(QualityFeedback qualityFeedback) {
        //插入品质反馈单
        qualityFeedbackDao.insertQualityFeedback(qualityFeedback);
    }

    /**
     * 品质反馈单更新
     * @param spiQualityFeedbackEditDto
     */
    @Override
    public void updateQualityFeedback(SpiQualityFeedbackEditDto spiQualityFeedbackEditDto) {
        //查询品质反馈单状态
        String status = qualityFeedbackDao.selectQualityFeedbackStatus(spiQualityFeedbackEditDto.getManageNumber());
        String businessTime = spiQualityFeedbackEditDto.getBusinessTime();
        String systemState = spiQualityFeedbackEditDto.getSystemState();
        if("已提交".equals(status)&&!systemState.equals(status)){
            spiQualityFeedbackEditDto.setAuditFinishTime(businessTime);
        }
        if ("已提交".equals(spiQualityFeedbackEditDto.getSystemState())){
            spiQualityFeedbackEditDto.setSubmitTime(businessTime);
        }
        if ("已审阅".equals(spiQualityFeedbackEditDto.getSystemState())){
            spiQualityFeedbackEditDto.setReviewTime(businessTime);
        }
        if ("已审核".equals(spiQualityFeedbackEditDto.getSystemState())){
            spiQualityFeedbackEditDto.setAuditTime(businessTime);
        }
        if ("已关单".equals(spiQualityFeedbackEditDto.getSystemState())){
            spiQualityFeedbackEditDto.setCloseOrderTime(businessTime);
        }
        //更新品质反馈单
        qualityFeedbackDao.updateQualityFeedback(spiQualityFeedbackEditDto);
    }
}
