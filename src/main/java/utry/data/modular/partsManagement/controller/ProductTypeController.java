package utry.data.modular.partsManagement.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.ISysConfService;
import utry.data.modular.api.ProductTypeDataApi;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.model.ProductType;
import utry.data.modular.partsManagement.service.ApiProductTypeService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 产品类型管理Controller
 * @author WJ
 */
@Controller
@RequestMapping("/productType")
@Api(tags = "产品类型全刪全增")
public class ProductTypeController extends CommonController {
    @Autowired
    ISysConfService iSysConfService;
    @Resource
    ApiProductTypeService apiProductTypeService;
    @Resource
    private ProductTypeDataApi productTypeDataApi;

    @PostMapping("/getProductType")
    @ResponseBody
    public RetResult getProductType() {
        int i,j=0;
        //调用硕德接口
        RetResult retResult = productTypeDataApi.getProductType();
        if (200 == retResult.getCode()) {
        //数据处理
        String res = (String) retResult.getData();
        JSONObject jsonObject = JSONObject.fromObject(res);
        String str = jsonObject.get("data").toString();
        List<ProductType> list = JSON.parseArray(str,ProductType.class);
        //删除全部数据
        i = apiProductTypeService.batchDelete();
        //保存数据
        j = apiProductTypeService.batchProductTypeData(list);
        return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }
}
