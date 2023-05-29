package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.core.common.LoginInfoParams;
import utry.data.modular.partsManagement.service.ApiPartDrawingStockService;
import utry.data.util.RetResult;

/**
 * @program: data
 * @description: 定时任务执行类-部品库存数据拉取
 * @author: WangXinhao
 * @create: 2022-06-06 17:30
 **/
@Api(tags = "定时任务执行类-部品库存数据拉取")
@ServiceApi
@RestController
public class ApiPartDrawingStockTask {

    @Autowired
    private ApiPartDrawingStockService apiPartDrawingStockService;

    @ApiOperation(value = "部品库存数据拉取", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/getPartDrawingStock", method = RequestMethod.POST)
    public RetResult<Void> getPartDrawingStock() {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        return apiPartDrawingStockService.getPartDrawingStock();
    }
}
