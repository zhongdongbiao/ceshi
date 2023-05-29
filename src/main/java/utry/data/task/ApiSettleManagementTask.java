package utry.data.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.base.BaseController;
import utry.data.modular.region.service.RegionService;
import utry.data.modular.settleManagement.dto.SettleDataDto;
import utry.data.modular.settleManagement.model.SettleDataVo;
import utry.data.modular.settleManagement.service.ApiSettleManagementService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 结算数据API拉取
 * @Author zh
 * @Date 2022/4/28 16:29
 */
@Api(tags = "定时任务执行类-获取结算管理模块数据")
@ServiceApi
@Controller
public class ApiSettleManagementTask extends BaseController {

    @Resource
    private ApiSettleManagementService apiSettleManagementService;

    @Resource
    private RegionService regionService;

    @ApiOperation(value = "已结算数据拉取", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/settleDataPull", method = RequestMethod.POST)
    public ResponseEntity settleDataPull() {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try {
            String message = apiSettleManagementService.getSettleData();
            if ("fail".equals(message)) {
                return this.errorResult();
            }
        }catch (Exception e) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "未结算数据拉取", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/missSettleDataPull", method = RequestMethod.POST)
    public ResponseEntity missSettleDataPull() {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try {
            String message = apiSettleManagementService.getMissSettleData();
            if ("fail".equals(message)) {
                return this.errorResult();
            }
        }catch (Exception e) {
            return this.errorResult();
        }
        return this.result();
    }

    @ApiOperation(value = "投诉处理单拉取", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/complaintHandling", method = RequestMethod.POST)
    @ResponseBody
    public RetResult complaintHandling() {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map map = new HashMap();
        return  regionService.complaintHandlingApi(map);
    }

}
