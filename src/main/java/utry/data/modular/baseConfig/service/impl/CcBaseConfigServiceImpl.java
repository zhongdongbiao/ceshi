package utry.data.modular.baseConfig.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utry.data.modular.baseConfig.dao.CcBaseConfigDao;
import utry.data.modular.baseConfig.model.CcCoreTarget;
import utry.data.modular.baseConfig.model.HumanResCoef;
import utry.data.modular.baseConfig.model.SeatStatusReminder;
import utry.data.modular.baseConfig.service.CcBaseConfigService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author lidakai
 */
@Service
@Transactional
@SuppressWarnings("all")
public class CcBaseConfigServiceImpl implements CcBaseConfigService {

    @Resource
    private CcBaseConfigDao ccBaseConfigDao;

    @Override
    public List<CcCoreTarget> selectCoreTarget(CcCoreTarget ccCoreTarget) {
        return ccBaseConfigDao.selectCoreTarget(ccCoreTarget);
    }

    @Override
    public RetResult saveCoreTarget(CcCoreTarget ccCoreTarget) {
        try {
            new SimpleDateFormat("yyyy-MM").parse(ccCoreTarget.getTargetMonth());
        } catch (ParseException e) {
            e.printStackTrace();
            return RetResponse.makeErrRsp("目标月格式错误,请检查后重新提交");
        }

        Integer saveRow = 0;
        if (StringUtils.isNotBlank(ccCoreTarget.getCcCoreTargetId())) {
            //校验所在月是否重复
            List<CcCoreTarget> ccCoreTargets = ccBaseConfigDao.selectCoreTargetNotExitSelf(ccCoreTarget);
            if (ccCoreTargets.size() > 0) {
                return RetResponse.makeErrRsp("校验所在月目标已存在");
            }
            saveRow = ccBaseConfigDao.updateCoreTarget(ccCoreTarget);
        } else {
            //校验所在月是否重复
            List<CcCoreTarget> ccCoreTargets = ccBaseConfigDao.selectCoreTarget(ccCoreTarget);
            if (ccCoreTargets.size() > 0) {
                return RetResponse.makeErrRsp("校验所在月目标已存在");
            }
            ccCoreTarget.setCcCoreTargetId(UUID.randomUUID().toString().replaceAll("-", ""));
            saveRow = ccBaseConfigDao.addCoreTarget(ccCoreTarget);
        }
        return RetResponse.makeOKRsp("更新了" + saveRow + "条");
    }

    @Override
    public RetResult delCoreTarget(CcCoreTarget ccCoreTarget) {
        Integer affectRow = ccBaseConfigDao.delCoreTarget(ccCoreTarget);
        return affectRow > 0 ? RetResponse.makeOKRsp("删除了" + affectRow + "行") : RetResponse.makeErrRsp("删除失败");


    }

    @Override
    public List<HumanResCoef> selectHumanResCoefByPage() {
        return ccBaseConfigDao.selectHumanResCoefByPage();
    }

    @Override
    public RetResult importHumanResCoef(List<HumanResCoef> ls) {
        List<String> exist = new ArrayList<>();
        for (HumanResCoef item : ls) {
            if (ccBaseConfigDao.findBindByJobNo(item.getJobNo()) > 0) {
                return RetResponse.makeErrRsp("工号" + item.getJobNo() + "已存在，系数自行页面编辑即可~");
            }
            if (item.getJobNo() == null || item.getSeats() == null || item.getManPowerCoef() == null || StringUtils.isBlank(item.getJobNo()) || StringUtils.isBlank(item.getSeats()) || Double.isNaN(item.getManPowerCoef())) {
                return RetResponse.makeErrRsp("请完善单元格"+"，优化格式重新上传");
            } else {
                item.setHumanResCoefId(UUID.randomUUID().toString().replaceAll("-", ""));
            }
            if (!(exist.contains(item.getJobNo()))) {
                exist.add(item.getJobNo());
            } else {
                return RetResponse.makeErrRsp("本次上传文件中存在重复工号" + item.getJobNo() + "，请优化格式重新上传");
            }
        }
        Integer affectRow = ccBaseConfigDao.batchInsertHumanResCoef(ls);
        return affectRow > 0 ? RetResponse.makeOKRsp("成功导入" + affectRow + "行") : RetResponse.makeErrRsp("导入失败");
    }

    @Override
    public RetResult editHumanResCoef(HumanResCoef humanResCoef) {
        Integer affectRow = ccBaseConfigDao.editHumanResCoef(humanResCoef);
        return affectRow > 0 ? RetResponse.makeOKRsp("成功编辑" + affectRow + "行") : RetResponse.makeErrRsp("编辑失败");

    }

    @Override
    public RetResult delHumanResCoef(HumanResCoef humanResCoef) {
        Integer affectRow = ccBaseConfigDao.delHumanResCoef(humanResCoef);
        return affectRow >= 0 ? RetResponse.makeOKRsp("成功删除" + affectRow + "行") : RetResponse.makeErrRsp("删除失败");
    }

    @Override
    public RetResult editSeatTimeOut(SeatStatusReminder seatStatusReminder) {
        Integer affectRow = ccBaseConfigDao.editSeatTimeOut(seatStatusReminder);
        return affectRow >= 0 ? RetResponse.makeOKRsp("成功编辑" + affectRow + "行") : RetResponse.makeErrRsp("编辑失败");
    }

    @Override
    public RetResult selectAllSeatTimeOut() {
        return RetResponse.makeOKRsp(ccBaseConfigDao.selectAllSeatTimeOut());
    }

    @Override
    public RetResult saveQueueBusiness(JSONArray jsonArray) {
        Integer affectRow = 0;
        for (Object object : jsonArray) {
            JSONObject item = (JSONObject) JSONObject.toJSON(object);
            //查询是否存在
            Integer row = ccBaseConfigDao.selectQueueBusinessIsBinding(item);
            Integer updateRow = 0;
            if (row > 0) {
                updateRow = ccBaseConfigDao.updateQueueBusiness(item);
            } else {
                updateRow = ccBaseConfigDao.insertQueueBusiness(item);
            }
            affectRow += updateRow;
        }

        return affectRow >= 0 ? RetResponse.makeOKRsp("成功更新" + affectRow + "行") : RetResponse.makeErrRsp("更新失败");
    }

    @Override
    public List<JSONObject> selectQueueBusinessByPage(JSONObject jsonObject) {
        return ccBaseConfigDao.selectQueueBusinessByPage(jsonObject);
    }

    @Override
    public List<HashMap> getOverTime() {
        return ccBaseConfigDao.getOverTime();
    }

   /* @Override
    public RetResult saveQueueTarget(QueueTarget queueTarget) {
        //保存队列目标
        //查询队列

        Integer affectRow = ccBaseConfigDao.saveQueueTarget(queueTarget);
        if (affectRow < 0) {
            throw new RuntimeException("保存队列失败");
        }
        //保存队列目标关系
//        ccBaseConfigDao.saveQueueTarget(queueTarget);
        return null;
    }*/

}

