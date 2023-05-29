package utry.data.task;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.data.base.BaseController;
import utry.data.modular.api.ApiRegionController;
import utry.data.modular.baseData.controller.BaseDataController;
import utry.data.modular.region.controller.RegionController;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: DJ
 * @Date: 2021/3/8 12:17
 */
@Api(tags = "定时任务执行类-获取大区基本信息")
@ServiceApi
@Controller
public class RegionTask extends BaseController {


    @Resource
    private RegionController regionController;

    @Resource
    private ApiRegionController apiRegionController;

    @ApiOperation(value = "打标签", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/makeNote", method = RequestMethod.POST)
    public ResponseEntity makeNote(String param) {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        JSONObject parse = JSONObject.parseObject(param);
        Map map = new HashMap<>();
        map.put("startTime",parse.get("startTime"));
        map.put("endTime",parse.get("endTime"));
        RetResult ret = regionController.updateAll(map);
        if(ret.getCode()==200){
            return this.result();
        }else {
            return this.errorResult();
        }
    }

    @ApiOperation(value = "更新服务门店", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/updateStore", method = RequestMethod.POST)
    public ResponseEntity updateStore(String param) {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map map = new HashMap<>();
        RetResult ret = apiRegionController.store(map);
        if(ret.getCode()==200){
            return this.result();
        }else {
            return this.errorResult();
        }


    }

    @ApiOperation(value = "更新TAT服务时长", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/updateTAT", method = RequestMethod.POST)
    public ResponseEntity updateTAT(String param) {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        JSONObject parse = JSONObject.parseObject(param);
        Map map = new HashMap<>();
        map.put("startTime",parse.get("startTime"));
        map.put("endTime",parse.get("endTime"));
        RetResult ret = regionController.updateTAT(map);
        if(ret.getCode()==200){
            return this.result();
        }else {
            return this.errorResult();
        }
    }

    @ApiOperation(value = "补偿更新二次上门标签", notes = "补偿更新二次上门标签")
    @RequestMapping(value = "/api/task/100060/updateTwoUp", method = RequestMethod.POST)
    public ResponseEntity updateTwoUp(String param) {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        JSONObject parse = JSONObject.parseObject(param);
        Map map = new HashMap<>();
        map.put("startTime",parse.get("startTime"));
        map.put("endTime",parse.get("endTime"));
        RetResult ret = regionController.updateTwoUp(map);
        if(ret.getCode()==200){
            return this.result();
        }else {
            return this.errorResult();
        }
    }

    @ApiOperation(value = "批量设置大区", notes = "批量设置大区")
    @RequestMapping(value = "/api/task/100060/regionAll", method = RequestMethod.POST)
    public ResponseEntity regionAll(String param) {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult ret = regionController.regionAll();
        if(ret.getCode()==200){
            return this.result();
        }else {
            return this.errorResult();
        }
    }


    @ApiOperation(value = "处理乱序派工单", notes = "处理乱序派工单")
    @RequestMapping(value = "/api/task/100060/error", method = RequestMethod.POST)
    public ResponseEntity error(String param) {
//        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        RetResult ret = regionController.error();
        if(ret.getCode()==200){
            return this.result();
        }else {
            return this.errorResult();
        }
    }
}
