package utry.data.modular.baseConfig.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.dto.DepartmentStationLetterDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.dto.UserComplaintDTO;
import utry.data.modular.baseConfig.model.DepartmentData;

import java.util.List;

/**
 * @author machaojie
 * email machaojie@utry.cn
 * @date 2022/6/15 10:04
 * description 用户投诉
 */
@Mapper
public interface UserComplaintDao {

    /**
     * 查询用户
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
     * 通过账户id删除该用户担当
     * @param accountId 用户id
     * @return
     */
    int deleteAssumeUser(String accountId);

    /**
     * 通过账户id获取担当用户的数量
     * @param accountId 账户id
     * @return
     */
    Long queryAssumeUserByAccountId(String accountId);

    /**
     * 查询部门站内信列表
     * @return
     */
    List<DepartmentStationLetterDTO> queryStationLetter();

    /**
     * 编辑站内信信息
     * @param stationLetterDTO 修改后的站内信信息
     * @return
     */
    int editStationLetter(DepartmentStationLetterDTO stationLetterDTO);

    /**
     * 插入站内信信息
     * @param stationLetterDTOList 站内信信息列表
     * @return
     */
    int insertStationLetter(@Param("stationLetterDTOList") List<DepartmentStationLetterDTO> stationLetterDTOList);

    /**
     * 批量删除站内信信息
     * @return
     */
    int batchDeleteDepartmentStationLetter();

    /**
     * 插入部门信息
     * @param departmentDataList 部门信息数据列表
     * @return
     */
    void insertDepartmentData(@Param("departmentDataList") List<DepartmentData> departmentDataList);

    /**
     * 删除部门信息
     */
    void deleteDepartmentData();

    /**
     * 通过部门编号获取部门站内信信息
     * @param departmentNumber 部门编号
     * @return
     */
    DepartmentStationLetterDTO getStationLetterByDepartmentNumber(String departmentNumber);
}
