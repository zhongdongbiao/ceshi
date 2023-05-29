package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.data.base.BaseController;
import utry.data.modular.baseConfig.service.CcBaseDataService;

import javax.annotation.Resource;

/**
 *
 * @author lidakai
 */
@Api(tags = "定时任务执行类-话务中心模块数据-有待补充")
@ServiceApi
@Controller
public class CcBaseDataTask extends BaseController {

    @Resource
    private CcBaseDataService ccBaseDataService;


    @ApiOperation(value = "坐席队列部门数据拉取", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/getQueueData", method = RequestMethod.POST)
    public ResponseEntity getQueueData() {
        ccBaseDataService.getQueueData();
        return this.result();
    }



    @ApiOperation(value = "坐席所有状态数据拉取", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/getCcStatus", method = RequestMethod.POST)
    public ResponseEntity getCcStatus() {
        this.ccBaseDataService.getCcStatus();
        return this.result();
    }
}
