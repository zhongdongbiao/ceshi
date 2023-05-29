package utry.data.modular.baseConfig.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utry.data.modular.baseConfig.dao.UserComplaintDao;
import utry.data.modular.baseConfig.dto.DepartmentStationLetterDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.dto.UserComplaintDTO;
import utry.data.modular.baseConfig.model.DepartmentData;
import utry.data.modular.baseConfig.service.UserComplaintService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 10:02
 * description 人员投诉实现类
 */
@Service
public class UserComplaintServiceImpl implements UserComplaintService {

    @Resource
    private UserComplaintDao userComplaintDao;

    @Override
    public List<HrmAccountInfoDTO> selectUser() {
        return userComplaintDao.selectUser();
    }

    @Override
    public int associatedUser(UserComplaintDTO userComplaintDTO) {
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取操作时间
        userComplaintDTO.setCreateTime(dateFormat.format(new Date()));
        return userComplaintDao.associatedUser(userComplaintDTO);
    }

    @Override
    public List<UserComplaintDTO> queryAssumeUser() {
        List<UserComplaintDTO> userComplaintDTOList = userComplaintDao.queryAssumeUser();
        return userComplaintDTOList;
    }

    @Override
    public int deleteAssumeUser(String accountId) {
        return userComplaintDao.deleteAssumeUser(accountId);
    }

    @Override
    public Long queryAssumeUserByAccountId(String accountId) {
        return userComplaintDao.queryAssumeUserByAccountId(accountId);
    }

    @Override
    public int editAssumeUser(UserComplaintDTO userComplaintDTO) {
        userComplaintDao.deleteAssumeUser(userComplaintDTO.getOldAccountId());
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取操作时间
        userComplaintDTO.setCreateTime(dateFormat.format(new Date()));
        return userComplaintDao.associatedUser(userComplaintDTO);
    }

    @Override
    public List<DepartmentStationLetterDTO> queryStationLetter() {
        List<DepartmentStationLetterDTO> stationLetterDTOList = userComplaintDao.queryStationLetter();
        return stationLetterDTOList;
    }

    @Override
    public int editStationLetter(DepartmentStationLetterDTO stationLetterDTO) {
        if (StringUtils.isNotBlank(stationLetterDTO.getRelationProject())) {
            stationLetterDTO.setStatus("已绑定部门");
        } else {
            stationLetterDTO.setStatus("未绑定部门");
        }
        return userComplaintDao.editStationLetter(stationLetterDTO);
    }

    @Override
    public int insertDepartmentStationLetter(List<DepartmentData> departmentDataList) {
        userComplaintDao.deleteDepartmentData();
        userComplaintDao.insertDepartmentData(departmentDataList);
        List<DepartmentStationLetterDTO> stationLetterDTOList = new ArrayList<>();
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (DepartmentData departmentData : departmentDataList) {
            DepartmentStationLetterDTO stationLetterDTO = new DepartmentStationLetterDTO();
            stationLetterDTO.setDepartmentNumber(departmentData.getDepartmentNumber());
            stationLetterDTO.setDepartmentName(departmentData.getDepartmentName());
            // 获取部门站内信当前部门以绑定的部门信息
            DepartmentStationLetterDTO letterDTO = userComplaintDao.getStationLetterByDepartmentNumber(departmentData.getDepartmentNumber());
            if (letterDTO != null) {
                stationLetterDTO.setRelationProject(letterDTO.getRelationProject());
                stationLetterDTO.setCreateTime(letterDTO.getCreateTime());
                stationLetterDTO.setUpdateTime(letterDTO.getUpdateTime());
            } else {
                stationLetterDTO.setCreateTime(dateFormat.format(new Date()));
                stationLetterDTO.setUpdateTime(dateFormat.format(new Date()));
            }
            if (stationLetterDTO.getRelationProject() != null) {
                stationLetterDTO.setStatus("已绑定部门");
            } else {
                stationLetterDTO.setStatus("未绑定部门");
            }
            stationLetterDTOList.add(stationLetterDTO);
        }
        // 批量删除部门站内信中所有数据
        userComplaintDao.batchDeleteDepartmentStationLetter();
        if (!stationLetterDTOList.isEmpty()) {
            return userComplaintDao.insertStationLetter(stationLetterDTOList);
        }
        return 0;
    }

}
