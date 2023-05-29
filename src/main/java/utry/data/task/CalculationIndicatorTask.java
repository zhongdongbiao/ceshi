package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.base.BaseController;
import utry.data.modular.indicatorWarning.controller.IndicatorWarningController;
import utry.data.util.RetResult;

import javax.annotation.Resource;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/5/18 10:35
 * description
 */
@Api(tags = "计算指标预警的值")
@ServiceApi
@RestController
public class CalculationIndicatorTask extends BaseController {

    @Resource
    private IndicatorWarningController indicatorWarningController;

    @ApiOperation(value = "零件管理一天一次的指标预警", notes = "零件管理一天一次的指标预警")
    @RequestMapping(value = "/api/task/100060/oneDayOneWarningPartManagement", method = RequestMethod.POST)
    public ResponseEntity oneDayOneWarningPartsManagement(String param) {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = indicatorWarningController.oneDayOneWarningPartsManagement();
        if (200 != retResult.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "大区服务一天一次的指标预警", notes = "大区服务一天一次的指标预警")
    @PostMapping("/api/task/100060/oneDayOneWarningDistrict")
    public ResponseEntity oneDayOneWarningDistrict() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = indicatorWarningController.oneDayOneWarningDistrict();
        if (200 != retResult.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "投诉处理一天一次的指标预警", notes = "投诉处理一天一次的指标预警")
    @PostMapping("/api/task/100060/oneDayOneWarningComplaint")
    public ResponseEntity oneDayOneWarningComplaint() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = indicatorWarningController.oneDayOneWarningComplaint();
        if (200 != retResult.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "技术品质一天一次的指标预警", notes = "技术品质一天一次的指标预警")
    @PostMapping("/api/task/100060/oneDayOneWarningCategory")
    public ResponseEntity oneDayOneWarningCategory() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = indicatorWarningController.oneDayOneWarningCategory();
        if (200 != retResult.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "3小时一次的指标预警", notes = "3小时一次的指标预警")
    @PostMapping("/api/task/100060/threeHoursOneWarning")
    public ResponseEntity threeHoursOneWarning() {
//        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = indicatorWarningController.threeHoursOneWarning();
        if (200 != retResult.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

}
