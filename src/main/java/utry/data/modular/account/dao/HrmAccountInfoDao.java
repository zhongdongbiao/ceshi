package utry.data.modular.account.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.account.model.AccountInfoBO;

import java.util.List;

/**
 * @program: data
 * @description: hrm用户dao层
 * @author: WangXinhao
 * @create: 2022-06-10 16:44
 **/
@Mapper
public interface HrmAccountInfoDao {

    List<AccountInfoBO> selectAllAccountInfo();
}
