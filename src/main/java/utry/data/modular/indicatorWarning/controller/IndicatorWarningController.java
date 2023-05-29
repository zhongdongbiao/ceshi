package utry.data.modular.indicatorWarning.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utry.core.common.LoginInfoParams;
import utry.data.modular.indicatorWarning.service.IndicatorWarningService;
import utry.data.modular.indicatorWarning.vo.IndicatorAnomalyWarningVo;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/17 16:19
 * description 指标预警
 */
@Api(tags = "指标预警")
@Controller
@RequestMapping("/IndicatorWarning")
public class IndicatorWarningController {

    @Resource
    private IndicatorWarningService indicatorWarningService;

    @ApiOperation(value = "站内信", notes = "站内信")
    @PostMapping("/mail")
    public RetResult mail(@RequestBody List<Map<String, String>> paramMap) {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        indicatorWarningService.stationLetter(paramMap);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "一天预警一次指标异常预警-零件管理", notes = "一天预警一次指标异常预警-零件管理")
    @PostMapping("/oneDayOneWarningPartsManagement")
    public RetResult oneDayOneWarningPartsManagement() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        indicatorWarningService.oneDayOneWarningPartsManagement();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "一天预警一次指标异常预警-大区服务", notes = "一天预警一次指标异常预警-大区服务")
    @PostMapping("/oneDayOneWarningDistrict")
    public RetResult oneDayOneWarningDistrict() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        indicatorWarningService.oneDayOneWarningDistrict();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "一天预警一次指标异常预警-投诉处理", notes = "一天预警一次指标异常预警-投诉处理")
    @PostMapping("/oneDayOneWarningComplaint")
    public RetResult oneDayOneWarningComplaint() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        indicatorWarningService.oneDayOneWarningComplaint();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "一天预警一次指标异常预警-技术品质", notes = "一天预警一次指标异常预警-技术品质")
    @PostMapping("/oneDayOneWarningCategory")
    public RetResult oneDayOneWarningCategory() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        indicatorWarningService.oneDayOneWarningCategory();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "3小时一次质保异常预警", notes = "3小时一次指标异常预警")
    @PostMapping("/threeHoursOneWarning")
    public RetResult threeHoursOneWarning() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        indicatorWarningService.threeHoursOneWarning();
        return RetResponse.makeOKRsp();
    }

}
