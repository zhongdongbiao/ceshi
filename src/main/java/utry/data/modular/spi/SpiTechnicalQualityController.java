package utry.data.modular.spi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.technicalQuality.dto.SpiQualityFeedbackEditDto;
import utry.data.modular.technicalQuality.model.QualityFeedback;
import utry.data.modular.technicalQuality.service.SpiTechnicalQualityService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 技术品质管理
 *
 * @author WJ
 */
@RestController
@RequestMapping("subApi/spiTechnicalQuality")
@Api(tags = "技术品质管理SPI")
public class SpiTechnicalQualityController extends CommonController {

    @Resource
    private SpiTechnicalQualityService spiTechnicalQualityService;

    @ApiOperation(value = "品质反馈单数据创建", notes = "品质反馈单数据创建")
    @PostMapping("/addQualityFeedback")
    public RetResult addQualityFeedback(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        QualityFeedback qualityFeedback = new QualityFeedback();
        qualityFeedback = (QualityFeedback)TimeUtil.requestToObject(request,qualityFeedback);
        if(StringUtils.isEmpty(qualityFeedback.getManageNumber())){
            return RetResponse.makeErrRsp("管理编号为空，添加失败！");
        }
        try {
            spiTechnicalQualityService.addQualityFeedback(qualityFeedback);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return RetResponse.makeRsp(401,"该品质反馈单已存在，无需重复推送");
        }
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "品质反馈单数据更新", notes = "品质反馈单数据更新")
    @PostMapping("/editQualityFeedback")
    public RetResult editQualityFeedback(HttpServletRequest request ) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        SpiQualityFeedbackEditDto spiQualityFeedbackEditDto = new SpiQualityFeedbackEditDto();
        spiQualityFeedbackEditDto  = (SpiQualityFeedbackEditDto)TimeUtil.requestToObject(request,spiQualityFeedbackEditDto);
        if(StringUtils.isEmpty(spiQualityFeedbackEditDto.getManageNumber())){
            return RetResponse.makeErrRsp("管理编号为空，添加失败！");
        }
        spiTechnicalQualityService.updateQualityFeedback(spiQualityFeedbackEditDto);
        return RetResponse.makeOKRsp();
    }
}
