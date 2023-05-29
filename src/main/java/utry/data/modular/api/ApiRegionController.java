package utry.data.modular.api;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.common.service.CommonTemplateService;
import utry.data.modular.region.controller.dto.RegionComplaintDto;
import utry.data.modular.region.service.RegionService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 大区服务API接口Controller
 *
 * @author wanlei
 */
@Controller
@RequestMapping("subApi/apiRegion")
@Api(tags = "大区服务API")
public class ApiRegionController extends CommonController {

    @Resource
    private RegionService regionService;

    @Resource
    private CommonTemplateService commonTemplateService;

    @ApiOperation(value = "服务门店信息更新", notes = "服务门店信息更新")
    @PostMapping("store")
    @ResponseBody
    public RetResult store(@RequestBody Map map) {
        //此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return  regionService.storeApi(map);
    }

    @ApiOperation(value = "工程师管理信息获取", notes = "工程师管理信息获取")
    @PostMapping("engineerManagement")
    @ResponseBody
    public RetResult engineerManagement(@RequestBody Map map) {
        //此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return  regionService.engineerManagementApi(map);
    }

    @ApiOperation(value = "派工单详情获取", notes = "派工单详情获取")
    @PostMapping("dispatchingDetail")
    @ResponseBody
    public RetResult dispatchingDetail(@RequestBody Map map) {
        //此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return  regionService.dispatchingDetailApi(map);
    }

    @ApiOperation(value = "投诉处理单拉取", notes = "投诉处理单拉取")
    @PostMapping("complaintHandling")
    @ResponseBody
    public RetResult complaintHandling(@RequestBody Map map) {
        //此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return  regionService.complaintHandlingApi(map);
    }

    @ApiOperation(value = "排名", notes = "排名")
    @PostMapping("ranking")
    @ResponseBody
    public RetResult ranking(@RequestBody Map map) {
        //此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = regionService.totalRanking(map);
        return  retResult;
    }

    @ApiOperation(value = "指标", notes = "指标")
    @PostMapping("target")
    @ResponseBody
    public RetResult target(@RequestBody Map map) {
        //此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = regionService.target(map);
        return  retResult;
    }
    @ApiOperation(value = "指标折线图", notes = "指标折线图")
    @PostMapping("lineChart")
    @ResponseBody
    public RetResult lineChart(@RequestBody Map map) {
        //此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult retResult = regionService.lineChart(map);
        return  retResult;
    }

    @ApiOperation(value = "投诉类型下拉框", notes = "投诉类型下拉框")
    @RequestMapping(value = "/selectComplaintType")
    @ResponseBody
    public RetResult selectComplaintType(@RequestBody Map map) {
        if(map.get("planId")!=null&&!"".equals(map.get("planId"))) {
            TemplateQueryDataDto templateQueryDto = new TemplateQueryDataDto();
            templateQueryDto.setStartTime(map.get("startTime").toString());
            templateQueryDto.setPlanId(map.get("planId").toString());
            templateQueryDto.setEndTime(map.get("endTime").toString());
            templateQueryDto.setPlanName(map.get("planName").toString());
            JSONObject jsonObject = commonTemplateService.selectDataByKey(templateQueryDto);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        dateConversion(map);
        RegionComplaintDto complaintDto = new RegionComplaintDto();
        complaintDto.setBeginDate((String) map.get("startTime"));
        complaintDto.setEndDate((String) map.get("endTime"));
        complaintDto.setStoreNumber((String) map.get("storeNumber"));
        complaintDto.setAccountingAreaCode((String) map.get("accountingAreaCode"));
        complaintDto.setProductCategoryCode((List<String>) map.get("productCategoryCode"));
        complaintDto.setProductTypeCode((List<String>) map.get("productTypeCode"));
        List<String> message = regionService.selectComplaintType(complaintDto);
        return RetResponse.makeOKRsp(message);
    }

    Map dateConversion(Map map){
        if(map.get("startTime")!=null&&!"".equals(map.get("startTime"))){
            map.put("startTime",map.get("startTime")+" 00:00:00");
        }
        if(map.get("endTime")!=null&&!"".equals(map.get("endTime"))){
            map.put("endTime",map.get("endTime")+" 23:59:59");
        }
        return map;
    }

}
