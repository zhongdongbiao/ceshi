package utry.data.modular.api;

import cn.hutool.core.lang.hash.Hash;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.baseConfig.service.CcBaseConfigService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.util.SmsUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 人员基础数据
 *
 * @author WJ
 */
@Controller
@RequestMapping("subApi")
@Api(tags = "获取人员基础数据")
public class CcForShuCeApi extends CommonController {

    @Resource
    private SmsUtils smsUtils;
    @Resource
    private CcBaseConfigService ccBaseConfigService;

    @PostMapping("/getTimeOutParameter")
    @ResponseBody
    public RetResult getTimeOutParameter() {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        Map<String, String> resultMap = smsUtils.getDictList("OVER_TIME");
        List<HashMap> overTimeList = ccBaseConfigService.getOverTime();
        JSONObject jsonObject = new JSONObject();
        for (HashMap hashMap : overTimeList) {
            jsonObject.put(hashMap.get("statusName").toString(), hashMap.get("timeout"));
        }
        Set<String> strings = resultMap.keySet();
        for (String string : strings) {
            jsonObject.put(string, resultMap.get(string));
        }
        return RetResponse.makeOKRsp(jsonObject);
    }


}
