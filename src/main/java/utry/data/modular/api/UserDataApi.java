package utry.data.modular.api;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.baseConfig.model.CCSeatInfo;
import utry.data.util.HttpClientUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 人员基础数据
 * @author WJ
 */
@Controller
@RequestMapping("subApi/userData")
@Api(tags = "获取人员基础数据")
public class UserDataApi extends CommonController {

    @Resource
    private SysConfServiceImpl sysConfService;

    @PostMapping("/getUser")
    @ResponseBody
    public RetResult getUser() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = IP + "/GetUserData";
        try {
            postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(null));
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(postResult);
    }

    /**
     * 获取数策数据
     * @return
     */
    @PostMapping("/geShuCetUser")
    @ResponseBody
    public RetResult geShuCetUser() {
        String postResult;
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String HOST = sysConfService.getSystemConfig("SHUCE_URL", "100060");
        String url = HOST + "searchBindingData";
        try {
            postResult = HttpClientUtil.get(url);
        } catch (IOException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("request fail");
        }
        return RetResponse.makeOKRsp(JSONObject.parseObject(postResult));
    }



}
