package utry.data.modular.partsManagement.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utry.core.common.LoginInfoParams;
import utry.data.modular.indicatorWarning.dao.IndicatorWarningDao;
import utry.data.modular.indicatorWarning.dto.AssumeUserDto;
import utry.data.modular.partsManagement.service.CoreIndexService;
import utry.data.modular.partsManagement.vo.*;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhongdongbiao
 * @date 2023/3/8 10:52
 */
@RestController
@RequestMapping("subApi/move")
@Api(tags = "移动端零件模块")
public class MovePartsManagement {
    @Autowired
    private IndicatorWarningDao indicatorWarningDao;
    @Autowired
    CoreIndexService coreIndexService;

    @PostMapping("/getCoreIndex")
    @ApiOperation(value = "获取核心指标", notes = "startDate 开始时间 endDate 结束时间 isGet 是否获取差值 0 获取 1 不获取 userId  userId 0 查询担当 1不查询担当")
    @ResponseBody
    public RetResult getCoreIndex(@RequestBody JSONObject jsonObject) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        // 开始时间
        String startDate = jsonObject.getString("startDate");
        // 结束时间
        String endDate = jsonObject.getString("endDate");
        // 是否获取差值 0 获取 1 不获取
        String isGet = jsonObject.getString("isGet");
        // 担当id
        String userId = jsonObject.getString("userId");
        List<Map<Object, Object>> maps = new ArrayList<>();
        if("1".equals(userId)){
            Map<Object, Object> coreIndex = coreIndexService.getCoreIndex(startDate, endDate, isGet, null, null);
            maps.add(coreIndex);
            return RetResponse.makeOKRsp(coreIndex);
        }
        List<AssumeUserDto> partsManagementAllAssume = indicatorWarningDao.getPartsManagementAllAssume();
        for (AssumeUserDto assumeUserDto : partsManagementAllAssume) {
            Map<Object, Object> coreIndex = coreIndexService.getCoreIndex(startDate, endDate, isGet, assumeUserDto.getUserId(), assumeUserDto.getRealName());
            maps.add(coreIndex);
        }
        return RetResponse.makeOKRsp(maps);
    }

    @ApiOperation(value = "零件管理异常监控", notes = "startDate 开始时间 endDate 结束时间")
    @PostMapping("/getAbnormalMonitoring")
    public RetResult getAbnormalMonitoring(@RequestBody JSONObject jsonObject) throws InterruptedException {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        AbnormalMonitoringVo abnormalMonitoringVo =coreIndexService.getAbnormalMonitoring(startDate,endDate);
        jsonObject.put("data", abnormalMonitoringVo);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取部品在库金额曲线", notes = "获取部品在库金额曲线 startDate 开始时间 endDate 结束时间 aggregateType 时间类型 0 按日聚合 1 按月聚合")
    @PostMapping("/getLibraryAmount")
    public RetResult getLibraryAmount(@RequestBody JSONObject jsonObject) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        String aggregateType = jsonObject.getString("aggregateType");
        LibraryAmountVO libraryAmountVO = coreIndexService.getLibraryAmount(startDate,endDate,aggregateType);
        jsonObject.put("data", libraryAmountVO);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取部品库存曲线", notes = "获取部品库存曲线 startDate 开始时间 endDate 结束时间 aggregateType 时间类型 0 按日聚合 1 按月聚合")
    @PostMapping("/getCount")
    public RetResult getCount(@RequestBody JSONObject jsonObject) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        String aggregateType = jsonObject.getString("aggregateType");
        LibraryCountVO count = coreIndexService.getCount(startDate, endDate, aggregateType);
        jsonObject.put("data", count);
        return RetResponse.makeOKRsp(jsonObject);
    }
    @ApiOperation(value = "部品出货即纳率曲线")
    @PostMapping("/shipmentCurve")
    public RetResult shipmentCurve(@RequestBody JSONObject jsonObject) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        List<PhoneShipmentVo> phoneShipmentVos =  coreIndexService.shipmentCurve(startDate, endDate);
        return RetResponse.makeOKRsp(phoneShipmentVos);
    }

    @ApiOperation(value = "nds2曲线")
    @PostMapping("/nds2Curve")
    public RetResult nds2Curve(@RequestBody JSONObject jsonObject) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        List<Nds2CurveVo> nds2Curve =  coreIndexService.nds2Curve(startDate, endDate);
        return RetResponse.makeOKRsp(nds2Curve);
    }

    @ApiOperation(value = "获取部品在库金额曲线", notes = "获取部品在库金额曲线 startDate 开始时间 endDate 结束时间 aggregateType 时间类型 0 按日聚合 1 按月聚合")
    @PostMapping("/amountCurve")
    public RetResult amountCurve(@RequestBody JSONObject jsonObject) {
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        LibraryAmountVO libraryAmountVO = coreIndexService.getLibraryAmount(startDate,endDate,"0");
        List<AmountVo> amountVos = new ArrayList<>();
        for (int i = 0; i < libraryAmountVO.getShijian().size(); i++) {
            AmountVo amountVo =new AmountVo();
            amountVo.setDate(libraryAmountVO.getShijian().get(i));
            amountVo.setAmount(libraryAmountVO.getZaikuquxian().get(i));
            amountVos.add(amountVo);
        }
        return RetResponse.makeOKRsp(amountVos);
    }
}
