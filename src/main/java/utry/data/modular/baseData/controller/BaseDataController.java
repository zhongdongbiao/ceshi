package utry.data.modular.baseData.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utry.core.base.controller.CommonController;
import utry.data.modular.api.BaseDataApi;
import utry.data.modular.baseData.dto.HistoriDataDto;
import utry.data.modular.baseData.service.IBaseDataService;
import utry.data.modular.partsManagement.dao.DistrictAccountingDao;
import utry.data.modular.partsManagement.model.DistrictAccounting;
import utry.data.util.CrossSubStationUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 账号管理管理类
 *
 * @author lidakai
 */
@Controller
@RequestMapping("/baseData")
@Api(tags = "基本信息管理")
public class BaseDataController extends CommonController {


    @Resource
    private DistrictAccountingDao districtAccountingDao;
    @Resource
    private BaseDataApi baseDataApi;
    @Resource
    private CrossSubStationUtil crossSubStationUtil;
    @Resource
    private IBaseDataService iBaseDataService;

    @PostMapping("/getDistrictAccount")
    @ApiOperation("大区基本信息全删全增")
    @ResponseBody
    public RetResult getDistrictAccount() {
        // 调用硕德接口
        RetResult districtAccounting = baseDataApi.getDistrictAccounting();
        if (200 == districtAccounting.getCode()) {
            // 存储数据
            JSONObject jsonObject = JSONObject.fromObject((String) districtAccounting.getData());
            List<DistrictAccounting> newList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.fromObject(jsonObject.get("data"));
            newList = JSONArray.toList(jsonArray, new DistrictAccounting(), new JsonConfig());
            List<DistrictAccounting> oldList = districtAccountingDao.selectDistrictAccounting(new DistrictAccounting());
            int i = districtAccountingDao.delDistrictAccounting();
            // 判断数据是否有变动，若有站内信通知数据变动
            if (newList.size() > 0) {
                outer:
                for (DistrictAccounting newDto : newList) {
                    if (oldList.size() > 0) {
                        inner:
                        for (int j = oldList.size() - 1; j >= 0; j--) {
                            DistrictAccounting oldDto = oldList.get(j);
                            //  状态和id均相同，说明数据没变即从oldList中移除
                            if (newDto.getRegionalCode().equals(oldDto.getRegionalCode())
                                    && (newDto.getSystemState().equals(oldDto.getSystemState()))) {
                                oldList.remove(oldDto);
                            }
                        }
                    }
                    // 每条new均执行insert
                    newDto.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    districtAccountingDao.insertDistrictAccounting(newDto);
                }
            }

            // 针对发生变化的oldList数据 发站内信通知（不需要发送站内信）
            /*if (oldList.size() > 0) {
                List<String> idList = oldList.stream()
                        .map(DistrictAccounting::getRegional)
                        .collect(Collectors.toList());
                String regMsg = StringUtils.join(idList.toArray(), ",");
                ResponseEntity responseEntity = crossSubStationUtil.getAccount();
                if (200 == responseEntity.getCode()){
                    JSONArray sysUsers = JSONArray.fromObject(responseEntity.getData());
                    List<UserInfo> userInfos = new ArrayList<>();
                    for (Object userObj : sysUsers) {
                        JSONObject sysUser = (JSONObject) userObj;
                        UserInfo userInfo = new UserInfo();
                        String accountId = sysUser.getString("accountID");
                        userInfo.setAccount(sysUser.getString("account"));
                        userInfo.setAccountID(accountId);
                        userInfo.setCompanyID("08d181119a7b4c0e94ff368942fd4420");
                        userInfos.add(userInfo);
                    }
                    MessageUtil.send("数据变化通知", "您好，大区数据" + regMsg + "状态发生变化，请自行排查系统~", "auto", userInfos);
                }
            }*/
            return RetResponse.makeOKRsp();
        }
        return RetResponse.makeErrRsp("fail");
    }

    public RetResult getHistoryDisposable() {
       return RetResponse.makeOKRsp(this.iBaseDataService.getHistoryDisposable());
    }

    public RetResult insertHistoryDisposable(List<HistoriDataDto> historiDataDtos) {
        if (historiDataDtos.size()>0){
           return RetResponse.makeOKRsp(this.iBaseDataService.insertHistoryDisposable(historiDataDtos));
        }else {
            return RetResponse.makeErrRsp("插入数据为空");
        }
    }

    public RetResult getHistoryBybatch(Integer start, Integer end) {
        return RetResponse.makeOKRsp(iBaseDataService.getHistoryBybatch(start,end));
    }
}
