package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.data.base.BaseController;
import utry.data.modular.partsManagement.controller.ApiPartsManagementController;
import utry.data.util.RetResult;

import javax.annotation.Resource;

/**
 * 获取零件管理模块API数据
 *
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Api(tags = "定时任务执行类-获取零件管理模块API数据")
@ServiceApi
@Controller
public class ApiPartsManagementTask extends BaseController {


    @Resource
    private ApiPartsManagementController apiPartsManagementController;

    @ApiOperation(value = "获取配货周期数据", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/distributionCycle", method = RequestMethod.POST)
    public ResponseEntity executeTask(String param) {
        RetResult distributionCycle = apiPartsManagementController.getDistributionCycle();
        if (200 != distributionCycle.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "获取库位资料数据", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/locationInformation", method = RequestMethod.POST)
    public ResponseEntity executeLocationInformationTask(String param) {
        RetResult distributionCycle = apiPartsManagementController.getLocationInformation();
        if (200 != distributionCycle.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "获取服务店收货单扫描明细", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/getScanDetail", method = RequestMethod.POST)
    public ResponseEntity getScanDetail(String param) {
        RetResult distributionCycle = apiPartsManagementController.getScanDetail();
        if (200 != distributionCycle.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "获取产品资料数据", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/getProductInformation", method = RequestMethod.POST)
    public ResponseEntity getProductInformation(String param) {
        RetResult distributionCycle = apiPartsManagementController.getProductInformation();
        if (200 != distributionCycle.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "获取型号部件对应数据", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/getModelPartRelationship", method = RequestMethod.POST)
    public ResponseEntity getModelPartRelationship(String param) {
        RetResult distributionCycle = apiPartsManagementController.getModelPartRelationship();
        if (200 != distributionCycle.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }
}
