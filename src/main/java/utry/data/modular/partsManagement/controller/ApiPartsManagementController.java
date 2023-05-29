package utry.data.modular.partsManagement.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.core.sysConfig.ISysConfService;
import utry.data.modular.api.PartsManagementApi;
import utry.data.modular.baseConfig.dto.QualityFeedbackDTO;
import utry.data.modular.partsManagement.model.*;
import utry.data.modular.partsManagement.service.ApiPartsManagementService;
import utry.data.modular.partsManagement.service.CoreIndexService;
import utry.data.modular.technicalQuality.service.TechnicalQualityService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;


/**
 * 零件模块APIController
 *
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Controller
@RequestMapping("/apiPartsManagement")
@Api(tags = "零件模块APIController")
public class ApiPartsManagementController extends CommonController {

    @Autowired
    ISysConfService iSysConfService;
    @Resource
    ApiPartsManagementService apiPartsManagementService;
    @Autowired
    CoreIndexService coreIndexService;
    @Resource
    private PartsManagementApi partsManagementControllerApi;
    @Resource
    TechnicalQualityService technicalQualityService;
    @Resource
    private RedisTemplate<String,?> redisTemplate;


    @PostMapping("/getDistributionCycle")
    @ResponseBody
    public RetResult getDistributionCycle() {
        int i,j = 0;

        //调用硕德接口
        RetResult retResult = partsManagementControllerApi.getDistributionCycle();
        if (200 == retResult.getCode()) {
            //数据处理
            String res = (String) retResult.getData();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String str = jsonObject.get("data").toString();
            List<DistributionCycle> list = JSON.parseArray(str,DistributionCycle.class);
            //删除全部数据
            i = apiPartsManagementService.batchDistributionCycleDelete();
            //保存数据
            j = apiPartsManagementService.batchDistributionCycle(list);
            return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }

    @PostMapping("/getLocationInformation")
    @ResponseBody
    public RetResult getLocationInformation() {
        int i,j = 0;
        //调用硕德接口
        RetResult retResult = partsManagementControllerApi.getLocationInformation();
        if (200 == retResult.getCode()) {
            //数据处理
            String res = (String) retResult.getData();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String str = jsonObject.get("data").toString();
            List<LocationInformation> list = JSON.parseArray(str,LocationInformation.class);
            //删除全部数据
            i = apiPartsManagementService.batchLocationInformationDelete();
            //保存数据
            j = apiPartsManagementService.batchLocationInformation(list);
            return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }


    @PostMapping("/getScanDetail")
    @ResponseBody
    public RetResult getScanDetail() {
        int i,j = 0;
        //调用硕德接口
        RetResult retResult = partsManagementControllerApi.getScanDetail();
        if (200 == retResult.getCode()) {
            //数据处理
            String res = (String) retResult.getData();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String str = jsonObject.get("data").toString();
            List<ScanDetail> list = JSON.parseArray(str,ScanDetail.class);
            //删除全部数据
            i = apiPartsManagementService.batchScanDetailDelete();
            //保存数据
            j = apiPartsManagementService.batchScanDetail(list);
            return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }

    public RetResult getProductInformation() {
        int i,j = 0;
        //调用硕德接口
        RetResult retResult = partsManagementControllerApi.getProductInformation();
        if (200 == retResult.getCode()) {
            //数据处理
            String res = (String) retResult.getData();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String str = jsonObject.get("data").toString();
            List<ProductInformation> list = JSON.parseArray(str,ProductInformation.class);
            //查询产品资料时间数据
            List<QualityFeedbackDTO> olds = technicalQualityService.queryOldInformation();
            //删除全部数据
            i = apiPartsManagementService.batchProductInformationDelete();
            //保存数据
            for(int k = 0; k<list.size(); k=k+1000){
                int to = k+1000;
                if(k+1000>list.size()){
                    to = list.size();
                }
                final int finalForm = k;
                final int finalTo = to;
                List<ProductInformation> list1 = list.subList(finalForm,finalTo);
                j += apiPartsManagementService.batchProductInformation(list1);
            }
            //删除树缓存
            redisTemplate.delete("category");
            technicalQualityService.selectCategoryTree();
            //更新数据
            technicalQualityService.updateInformation(olds);
            return RetResponse.makeOKRsp("删除了"+i+"条数据"+"，"+"插入了"+j+"条数据");
        }
        return RetResponse.makeErrRsp("fail");
    }

    public RetResult getModelPartRelationship() {
        int i,j = 0;
        //调用硕德接口
        RetResult retResult = partsManagementControllerApi.getModelPartRelationship();
        if (200 == retResult.getCode()) {
            //数据处理
            String res = (String) retResult.getData();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String str = jsonObject.get("data").toString();
            List<ModelParts> list = JSON.parseArray(str,ModelParts.class);
            //删除全部数据
            i = apiPartsManagementService.batchModelPartsDelete();
            //保存数据
            for(int k = 0; k<list.size(); k=k+1000){
                int to = k+1000;
                if(k+1000>list.size()){
                    to = list.size();
                }
                final int finalForm = k;
                final int finalTo = to;
                List<ModelParts> list1 = list.subList(finalForm,finalTo);
                j += apiPartsManagementService.batchModelParts(list1);
            }
        }
        return RetResponse.makeErrRsp("fail");
    }


}
