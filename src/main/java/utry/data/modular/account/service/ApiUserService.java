package utry.data.modular.account.service;

import utry.data.modular.partsManagement.model.UserData;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface ApiUserService {
    /**
     * 查询全部人员信息
     */
    List<UserData> selectAllUser();
    /**
     * 删除旧人员数据
     */
    int batchDelete();
    /**
     * 添加新人员数据
     * @param list
     */
    int batchUserData(List<UserData> list);
    /**
     * 对比数据
     * @param list oldUserList
     */
    List<UserData> sendMessage(List<UserData> list, List<UserData> oldUserList);
    /**
     * 站内信通知
     * @param info
     */
    void sendMessage(List<UserData> info);
}
