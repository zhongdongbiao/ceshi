package utry.data.modular.settleManagement.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utry.data.modular.settleManagement.dao.MissSettleManagementDao;
import utry.data.modular.settleManagement.dao.SettleManagementDao;
import utry.data.modular.settleManagement.dto.SettleDataDto;
import utry.data.modular.settleManagement.service.SpiSettleManagementService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/26 16:22
 */
@Service
public class SpiSettleManagementServiceImpl implements SpiSettleManagementService {

    @Resource
    private SettleManagementDao settleManagementDao;

    @Resource
    private MissSettleManagementDao missSettleManagementDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void missSettleModify(List<SettleDataDto> settleList) throws Exception {

        try {
            //存放需要修改的数据
            List<SettleDataDto> updateMissSettleDataList = new ArrayList<>();
            //存放需要删除的数据
            List<SettleDataDto> deleteMissSettleDataList = new ArrayList<>();
            if (settleList != null && settleList.size() > 0) {
                for (SettleDataDto settleDataDto : settleList) {
                    if ("更新".equals(settleDataDto.getAction())) {
                        updateMissSettleDataList.add(settleDataDto);
                    }else {
                        deleteMissSettleDataList.add(settleDataDto);
                    }
                }
            }
            //修改未结算表中的数据
            if (updateMissSettleDataList.size() > 0) {
                missSettleManagementDao.updateMissSettleData(updateMissSettleDataList);
            }

            //删除未结算表中的数据
            if (deleteMissSettleDataList.size() > 0) {
                missSettleManagementDao.deleteMissSettleData(deleteMissSettleDataList);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void settleModify(List<SettleDataDto> settleList) throws Exception {

        try {
            //存放需要修改的数据
            List<SettleDataDto> updateSettleDataList = new ArrayList<>();
            //存放需要删除的数据
            List<SettleDataDto> deleteSettleDataList = new ArrayList<>();
            if (settleList != null && settleList.size() > 0) {
                for (SettleDataDto settleDataDto : settleList) {
                    if ("更新".equals(settleDataDto.getAction())) {
                        updateSettleDataList.add(settleDataDto);
                    }else {
                        deleteSettleDataList.add(settleDataDto);
                    }
                }
            }
            //修改结算表中的数据
            if (updateSettleDataList.size() > 0) {
                settleManagementDao.updateSettleData(updateSettleDataList);
            }
            //删除结算表中的数据
            if (deleteSettleDataList.size() > 0) {
                settleManagementDao.deleteSettleData(deleteSettleDataList);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }

}
