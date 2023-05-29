package utry.data.modular.technicalQuality.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.core.sysConfig.ISysConfService;
import utry.data.modular.api.TechnicalQualityApi;
import utry.data.modular.technicalQuality.model.EngineerInfo;
import utry.data.modular.technicalQuality.model.QualityFeedback;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 技术品质APIController
 *
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Controller
@RequestMapping("/apiTechnicalQuality")
@Api(tags = "技术品质APIController")
public class ApiTechnicalQualityController extends CommonController {

    @Autowired
    ISysConfService iSysConfService;
    @Resource
    private TechnicalQualityApi technicalQualityApi;

    @PostMapping("/getTechnicalQualityDetail")
    @ResponseBody
    public RetResult getTechnicalQualityDetail(@RequestBody Map map) {
        int i,j = 0;
        //调用硕德接口
        RetResult retResult = technicalQualityApi.getTechnicalQualityDetail(map);
        if (200 == retResult.getCode()) {
            //数据处理
            String res = (String) retResult.getData();
            JSONObject jsonObject = JSONObject.parseObject(res);
            if("F".equals(jsonObject.get("RESULT").toString())){
                return RetResponse.makeErrRsp(jsonObject.get("RESULT").toString());
            }
            String str = jsonObject.get("data").toString();
            QualityFeedback partInformation = JSONObject.parseObject(str, QualityFeedback.class);
            return RetResponse.makeOKRsp(partInformation);
        }
        return RetResponse.makeErrRsp("fail");
    }

    @PostMapping("/getEngineerInfo")
    @ResponseBody
    public RetResult getEngineerInfo(@RequestBody Map map) {
        int i,j = 0;
        //调用硕德接口
        RetResult retResult = technicalQualityApi.getEngineerInfo(map);
        if (200 == retResult.getCode()) {
            //数据处理
            String res = (String) retResult.getData();
            JSONObject jsonObject = JSONObject.parseObject(res);
            if("F".equals(jsonObject.get("RESULT").toString())){
                return RetResponse.makeErrRsp(jsonObject.get("RESULT").toString());
            }
            String str = jsonObject.get("data").toString();
            EngineerInfo engineerInfo = JSONObject.parseObject(str, EngineerInfo.class);
            return RetResponse.makeOKRsp(engineerInfo);
        }
        return RetResponse.makeErrRsp("fail");
    }
}
