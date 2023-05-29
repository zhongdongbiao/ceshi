package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.partsManagement.model.UserData;

import java.util.List;

/**
 * 用户数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface UserDataDao{

    /**
     *用户数据添加
     * @param userData
     */
    void insertUserData(UserData userData);

    /**
     *用户数据批量添加
     * @param list
     */
    int batchUserData(List<UserData> list);

    /**
     *用户数据查询
     */
    @DS("git_adb")
    List<UserData> selectAllUser();

    /**
     *用户数据批量删除
     */
    int batchDelete();
}
