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
import utry.data.modular.partsManagement.controller.ProductTypeController;
import utry.data.util.RetResult;

import javax.annotation.Resource;

/**
 * @Author: WJ
 * @Date: 2021/3/8 12:17
 */
@Api(tags = "定时任务执行类-获取产品类型数据")
@ServiceApi
@Controller
public class ProductTypeDataTask extends BaseController {


    @Resource
    private ProductTypeController productTypeController;

    @ApiOperation(value = "获取产品类型数据", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/productTypeData", method = RequestMethod.POST)
    public ResponseEntity executeTask(String param) {
        RetResult productType = productTypeController.getProductType();
        if (200 != productType.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }
}
