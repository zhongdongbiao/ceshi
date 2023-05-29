package utry.data.task;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.base.BaseController;
import utry.data.modular.partsManagement.controller.ApiPartsManagementController;
import utry.data.modular.partsManagement.controller.CoreIndexController;
import utry.data.modular.partsManagement.service.CoreIndexService;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * 零件管理定时任务
 *
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Api(tags = "定时任务执行类-零件管理定时任务")
@ServiceApi
@Controller
public class PartsManagementTask extends BaseController {


    @Resource
    private CoreIndexService coreIndexService;

    @ApiOperation(value = "定时修改作业订单以及标签", notes = "定时修改作业订单以及标签")
    @RequestMapping(value = "/api/task/100060/addTime", method = RequestMethod.POST)
    public ResponseEntity executeTask(String param) throws ExecutionException, InterruptedException, ParseException {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        coreIndexService.addDeadline();
        Thread.sleep(3000);
        coreIndexService.updateCancelOrderDetail();
        Thread.sleep(3000);
        coreIndexService.updateOrderDetail();
        Thread.sleep(3000);
        coreIndexService.updateOrder();
        Thread.sleep(3000);
        coreIndexService.addTimeOrder();
        Thread.sleep(3000);
        JSONObject parse;
        String startDate=null;
        String endDate=null;
        if(param!=null){
             parse = JSONObject.parseObject(param);
             startDate = parse.getString("startDate");
             endDate = parse.getString("endDate");
        }

//        if( startDate == null || "".equals(startDate) || endDate==null || "".equals(endDate)){
//            startDate = simpleDateFormat.format(new Date())+" 00:00:00";
//            endDate = simpleDateFormat.format(new Date())+" 23:59:59";
//        }
        coreIndexService.addTime(startDate,endDate);
        return this.result();
    }


}
