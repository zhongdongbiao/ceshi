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
import utry.data.modular.api.PartsDataApi;
import utry.data.modular.partsManagement.model.PartsInformation;
import utry.data.modular.partsManagement.service.ApiPartsService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 部件资料管理Controller
 * @author WJ
 */
@Controller
@RequestMapping("/parts")
@Api(tags = "部件资料全刪全增")
public class PartsInfoController extends CommonController {
    @Autowired
    ISysConfService iSysConfService;
    @Resource
    ApiPartsService apiPartsService;
    @Resource
    private PartsDataApi partsDataApi;

    @PostMapping("/getParts")
    @ResponseBody
    public RetResult getParts() {
        int i,j=0;
        //调用硕德接口
        RetResult retResult = partsDataApi.getParts();
        if (200 == retResult.getCode()) {
        //数据处理
        String res = (String) retResult.getData();
        JSONObject jsonObject = JSONObject.fromObject(res);
        String str = jsonObject.get("data").toString();
        List<PartsInformation> list = JSON.parseArray(str,PartsInformation.class);
        //删除全部数据
        i = apiPartsService.batchDelete();
        //保存数据
        j = apiPartsService.batchPartsData(list);
        return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }
}
