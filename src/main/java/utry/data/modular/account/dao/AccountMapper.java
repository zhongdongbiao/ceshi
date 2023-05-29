package utry.data.modular.account.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.dto.User;
import utry.data.modular.partsManagement.model.UserData;

import java.util.List;

/**
 * @author : ldk
 * @date : 09:32 2022/3/17
 */
@Mapper
public interface AccountMapper {
    List<UserData> fuzzySearch(String name);
}
