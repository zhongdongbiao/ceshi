package utry.data.modular.settleManagement.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.modular.partsManagement.model.LocationInformation;
import utry.data.modular.settleManagement.dao.MissSettleManagementDao;
import utry.data.modular.settleManagement.dao.SettleManagementDao;
import utry.data.modular.settleManagement.dto.SettleDataDto;
import utry.data.modular.settleManagement.model.SettleDataVo;
import utry.data.modular.settleManagement.service.ApiSettleManagementService;
import utry.data.util.HttpClientUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/28 16:33
 */
@Service
public class ApiSettleManagementServiceImpl implements ApiSettleManagementService {

    @Resource
    private SysConfServiceImpl sysConfService;

    @Resource
    private SettleManagementDao settleManagementDao;

    @Resource
    private MissSettleManagementDao missSettleManagementDao;

    /**
     * 获取结算的数据
     * @return
     */
    @Override
    public String getSettleData() throws Exception {
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = "/GetSettlementData";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        String month = sdf.format(calendar.getTime());
        Map<Object,Object> param = new HashMap<>();
        param.put("settleDate",month);
        String postResult = "";
        List<SettleDataDto> dataList = null;
        String message = "success";
        try {
            postResult = HttpClientUtil.post(IP + url,HttpClientUtil.getParam(param));
            JSONObject jsonObject = JSONObject.parseObject(postResult);
            if ("T".equals(jsonObject.get("RESULT"))) {
                String dataStr = jsonObject.get("data").toString();
                dataList = JSON.parseArray(dataStr,SettleDataDto.class);
            }else {
                throw new Exception(jsonObject.get("ERRMSG").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        if (dataList != null && dataList.size() > 0) {

            int index = dataList.size() % 5000 == 0 ? dataList.size() / 5000 : dataList.size() / 5000 + 1;

            for (int i = 0; i < index; i++) {
                //stream流表达式，skip表示跳过前i*5000条记录，limit表示读取当前流的前5000条记录
                List<SettleDataDto> collect = dataList.stream().skip(i * 5000).limit(5000).collect(Collectors.toList());

                try {
                    //新增结算数据
                    settleManagementDao.insertSettleData(collect);

                    //更新待结算的状态
                    missSettleManagementDao.updateMissSettleState(collect);

                    //删除未结算数据
                    missSettleManagementDao.deleteMissSettleData(collect);
                }catch (Exception e) {
                    e.printStackTrace();
                    message = "fail";
                }
            }
        }
        return message;
    }

    /**
     * 获取未结算的数据
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String getMissSettleData() throws Exception {
        String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
        String url = "/GetUnSettlementData";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        String day = sdf.format(c.getTime());
        Map<Object,Object> param = new HashMap<>();
        param.put("createDate",day);
        String postResult = "";
        List<SettleDataDto> dataList = null;
        String message = "success";
        try {
            postResult = HttpClientUtil.post(IP + url,HttpClientUtil.getParam(param));
            JSONObject jsonObject = JSONObject.parseObject(postResult);
            if ("T".equals(jsonObject.get("RESULT"))) {
                String dataStr = jsonObject.get("data").toString();
                dataList = JSON.parseArray(dataStr,SettleDataDto.class);
            }else {
                throw new Exception(jsonObject.get("ERRMSG").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        if (dataList != null && dataList.size() > 0) {
            try {
                //新增未结算的数据
                missSettleManagementDao.insertMissSettleData(dataList);
            }catch (Exception e) {
                e.printStackTrace();
                message = "fail";
            }
        }
        return message;
    }

}
