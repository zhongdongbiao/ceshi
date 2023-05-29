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
 * 零件管理API数据
 * @author WJ
 */
@Controller
@RequestMapping("subApi/partsManagementApi")
@Api(tags = "获取零件管理API数据")
public class PartsManagementApi extends CommonController {

    @Resource
    private SysConfServiceImpl sysConfService;

    @PostMapping("/getDistributionCycle")
    @ResponseBody
    public RetResult getDistributionCycle() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetDistributionCycle";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

    @PostMapping("/getLocationInformation")
    @ResponseBody
    public RetResult getLocationInformation() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetLocationInformation";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

    @PostMapping("/getScanDetail")
    @ResponseBody
    public RetResult getScanDetail() {
        String postResult;
        /*LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/lhms/getdistrictAccounting";
        try {
            postResult = HttpClientUtil.post(url);
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }*/
        postResult=
                "{\"code\":200,\"msg\":\"success\",\"data\":[{\n" +
                        "\"id\" : \"1\",\n" +
                        "\"account\" : \"A1234\",\n" +
                        "\"sex\" : \"1\",\n" +
                        "\"mobilePhone\" : \"13555097618\",\n" +
                        "\"phone\" : \"11\",\n" +
                        "\"email\" : \"@@@\",\n" +
                        "\"dept\" : \"10000\",\n" +
                        "\"description\" : \"这也是一个描述\",\n" +
                        "\"state\" : \"1\",\n" +
                        "\"name\" : \"张三\"\n" +
                        "},{\n" +
                        "\"id\" : \"2\",\n" +
                        "\"account\" : \"A12345\",\n" +
                        "\"sex\" : \"1\",\n" +
                        "\"mobilePhone\" : \"13555097618\",\n" +
                        "\"phone\" : \"22\",\n" +
                        "\"email\" : \"@@@\",\n" +
                        "\"dept\" : \"10000\",\n" +
                        "\"description\" : \"这也是一个描述\",\n" +
                        "\"state\" : \"1\",\n" +
                        "\"name\" : \"李四\"\n" +
                        "}]}";
        return RetResponse.makeOKRsp(postResult);
    }

    @PostMapping("/getProductInformation")
    @ResponseBody
    public RetResult getProductInformation() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetProductMaterial";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

    @PostMapping("/getModelPartRelationship")
    @ResponseBody
    public RetResult getModelPartRelationship() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetModelPartRelationship";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }
}
