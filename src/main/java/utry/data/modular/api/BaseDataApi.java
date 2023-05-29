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
 * 基础数据
 *
 * @author lidakai
 */
@Controller
@RequestMapping("subApi/baseData")
@Api(tags = "获取大区基础数据")
public class BaseDataApi extends CommonController {

    @Resource
    private SysConfServiceImpl sysConfService;

    @PostMapping("getDistrictAccounting")
    @ResponseBody
    public RetResult getDistrictAccounting() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetDistrictAccounting";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

}
