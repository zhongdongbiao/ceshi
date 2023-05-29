package utry.data.modular.api;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.util.HttpClientUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * 工厂基础数据
 * @author WJ
 */
@Controller
@RequestMapping("subApi/productTypeData")
@Api(tags = "获取产品类型")
public class ProductTypeDataApi extends CommonController {

    @Resource
    private SysConfServiceImpl sysConfService;

    @PostMapping("/getProductType")
    @ResponseBody
    public RetResult getProductType() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetProductType";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

}
