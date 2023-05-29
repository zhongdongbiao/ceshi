package utry.data.modular.account.service;

import utry.data.modular.partsManagement.model.UserData;

import java.util.List;

/**
 * @author : ldk
 * @date : 09:31 2022/3/17
 */
public interface IAccountService {

    /**
     * 模糊搜索
     * @param name 姓名或者账号
     * @return 账号基本信息
     */
    List<UserData> fuzzySearch(String name);
}
