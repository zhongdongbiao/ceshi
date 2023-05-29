package utry.data.modular.account.service.impl;

import org.springframework.stereotype.Service;
import utry.data.modular.account.dao.AccountMapper;
import utry.data.modular.account.service.IAccountService;
import utry.data.modular.partsManagement.model.UserData;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjh
 * Created on 2018/5/4 上午9:32
 */
@Service
public class AccountServiceImpl implements IAccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public List<UserData> fuzzySearch(String name) {
        return accountMapper.fuzzySearch(name);
    }
}
