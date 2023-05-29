package utry.data.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utry.core.cloud.caller.rest.ServiceApi;
import utry.data.base.BaseController;
import utry.data.modular.partsManagement.controller.PartsInfoController;
import utry.data.modular.partsManagement.controller.ProductCategoryController;
import utry.data.util.RetResult;

import javax.annotation.Resource;

/**
 * @Author: WJ
 * @Date: 2021/3/8 12:17
 */
@Api(tags = "定时任务执行类-获取产品品类数据")
@ServiceApi
@Controller
public class ProductCategoryDataTask extends BaseController {

    @Resource
    private ProductCategoryController productCategoryController;

    @ApiOperation(value = "获取产品品类数据", notes = "定时任务执行方法入口")
    @RequestMapping(value = "/api/task/100060/productCategoryData", method = RequestMethod.POST)
    public ResponseEntity executeTask(String param) {
        RetResult productCategory = productCategoryController.getProductCategory();
        if (200 != productCategory.getCode()) {
            return this.errorResult();
        }
        return this.result();
    }
}
