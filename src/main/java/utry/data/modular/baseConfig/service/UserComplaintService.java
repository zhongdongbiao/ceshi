package utry.data.modular.baseConfig.service;

import utry.data.modular.baseConfig.dto.DepartmentStationLetterDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.dto.UserComplaintDTO;
import utry.data.modular.baseConfig.model.DepartmentData;

import java.util.List;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 10:02
 * description 用户投诉接口类
 */
public interface UserComplaintService {

    /**
     * 获取用户
     * @return
     */
    List<HrmAccountInfoDTO> selectUser();

    /**
     * 关联用户
     * @param userComplaintDTO 用户投诉DTO
     * @return
     */
    int associatedUser(UserComplaintDTO userComplaintDTO);

    /**
     * 查询所有担当用户列表
     * @return
     */
    List<UserComplaintDTO> queryAssumeUser();

    /**
     * 通过账户id删除担当用户
     * @param accountId
     * @return
     */
    int deleteAssumeUser(String accountId);

    /**
     * 查询担当用户的数量，通过账户id
     * @param accountId 账户id
     * @return
     */
    Long queryAssumeUserByAccountId(String accountId);

    /**
     * 编辑担当用户信息
     * @param userComplaintDTO 用户投诉DTO
     * @return
     */
    int editAssumeUser(UserComplaintDTO userComplaintDTO);

    /**
     * 查询部门站内信信息列表
     * @return
     */
    List<DepartmentStationLetterDTO> queryStationLetter();

    /**
     * 编辑部门站内信信息
     * @param stationLetterDTO 修改后的部门站内信
     * @return
     */
    int editStationLetter(DepartmentStationLetterDTO stationLetterDTO);

    /**
     * 插入部门站内信信息
     */
    int insertDepartmentStationLetter(List<DepartmentData> departmentDataList);
}
