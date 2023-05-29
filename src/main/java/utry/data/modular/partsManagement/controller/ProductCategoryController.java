package utry.data.modular.partsManagement.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.ISysConfService;
import utry.data.modular.api.ProductCategoryDataApi;
import utry.data.modular.partsManagement.model.ProductCategory;
import utry.data.modular.partsManagement.service.ApiProductCategoryService;
import utry.data.modular.technicalQuality.service.TechnicalQualityService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 产品品类管理Controller
 * @author WJ
 */
@Controller
@RequestMapping("/productCategory")
@Api(tags = "产品品类全刪全增")
public class ProductCategoryController extends CommonController {
    @Autowired
    ISysConfService iSysConfService;
    @Resource
    ProductCategoryDataApi productCategoryDataApi;
    @Resource
    private ApiProductCategoryService apiProductCategoryService;
    @Resource
    TechnicalQualityService technicalQualityService;
    @Resource
    private RedisTemplate<String,?> redisTemplate;

    @PostMapping("/getProductCategory")
    @ResponseBody
    public RetResult getProductCategory() {
        int i,j=0;
        //调用硕德接口
        RetResult retResult = productCategoryDataApi.getProductCategory();
        if (200 == retResult.getCode()) {
        //数据处理
        String res = (String) retResult.getData();
        JSONObject jsonObject = JSONObject.fromObject(res);
        String str = jsonObject.get("data").toString();
        List<ProductCategory> list = JSON.parseArray(str,ProductCategory.class);
        //删除全部数据
        i = apiProductCategoryService.batchDelete();
        //保存数据
        j = apiProductCategoryService.batchProductCategoryData(list);
        //删除大区模板缓存
        redisTemplate.delete("district");
        technicalQualityService.selectDistrictTemplate();
        return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }
}
