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
import utry.data.modular.api.FactoryDataApi;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.service.ApiFactoryService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 工厂管理Controller
 * @author WJ
 */
@Controller
@RequestMapping("/factory")
@Api(tags = "工厂信息全刪全增")
public class FactoryController extends CommonController {
    @Autowired
    ISysConfService iSysConfService;
    @Resource
    ApiFactoryService apiFactoryService;
    @Resource
    private FactoryDataApi factoryDataApi;

    @PostMapping("/getFactory")
    @ResponseBody
    public RetResult getFactory() {
        int i,j=0;
        //调用硕德接口
        RetResult retResult = factoryDataApi.getFactory();
        if (200 == retResult.getCode()) {
        //数据处理
        String res = (String) retResult.getData();
        JSONObject jsonObject = JSONObject.fromObject(res);
        String str = jsonObject.get("data").toString();
        List<FactoryData> list = JSON.parseArray(str,FactoryData.class);
        //删除全部数据
        i = apiFactoryService.batchDelete();
        //保存数据
        j = apiFactoryService.batchFactoryData(list);
        return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }
}
