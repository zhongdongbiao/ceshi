package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.data.base.BaseController;
import utry.data.modular.partsManagement.controller.FactoryController;
import utry.data.util.RetResult;

import javax.annotation.Resource;

/**
 * @Author: WJ
 * @Date: 2021/3/8 12:17
 */
@Api(tags = "定时任务执行类-获取工厂资料数据")
@ServiceApi
@Controller
public class FactoryDataTask extends BaseController {


    @Resource
    private FactoryController factoryController;

    @ApiOperation(value = "获取工厂资料数据", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/factoryData", method = RequestMethod.POST)
    public ResponseEntity executeTask(String param) {
        RetResult factory = factoryController.getFactory();
        if (200 != factory.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }
}
