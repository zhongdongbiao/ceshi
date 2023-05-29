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
import java.util.HashMap;
import java.util.Map;


/**
 * 技术品质API数据
 * @author WJ
 */
@Controller
@RequestMapping("subApi/technicalQualityApi")
@Api(tags = "获取技术品质API数据")
public class TechnicalQualityApi extends CommonController {

    @Resource
    private SysConfServiceImpl sysConfService;

    @PostMapping("/getTechnicalQualityDetail")
    @ResponseBody
    public RetResult getTechnicalQualityDetail(Map<Object,Object> map) {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/QueryQualityFeedback";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(map));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

    @PostMapping("/getEngineerInfo")
    @ResponseBody
    public RetResult getEngineerInfo(Map<Object,Object> map) {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/QueryEngineerInfo";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(map));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }
}
