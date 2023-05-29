package utry.data.modular.baseConfig.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.baseConfig.dao.UserCategoryDao;
import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.service.UserCategoryService;
import utry.data.modular.partsManagement.model.ProductType;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 人员品质管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class UserCategoryServiceImpl implements UserCategoryService {

    @Resource
    private UserCategoryDao userCategoryDao;

    @Override
    public List<HrmAccountInfoDTO> selectUser() {
        return userCategoryDao.selectUser();
    }

    @Override
    public List<String> selectCategory() {
        return userCategoryDao.selectCategory();
    }

    @Override
    public int addCategory(UserTypeDTO userTypeDTO) {
        return userCategoryDao.addCategory(userTypeDTO);
    }

    @Override
    public List<UserCategoryConfigDTO> selectConfig() {
        return userCategoryDao.selectConfig();
    }

    @Override
    public List<CategoryRootDTO> selectTypeTree() {
        //查询所有列表
        List<ProductType> productTypeList = userCategoryDao.selectTypeTree();
        List<CategoryRootDTO> list = getChildren(productTypeList);
        return list;
    }

    private List<CategoryRootDTO> getChildren(List<ProductType> productTypeList) {
        List<CategoryRootDTO> list = new ArrayList<>();
        //查询根节点id
        List<CategoryRootDTO> categoryRootDTOS = userCategoryDao.selectRoot();
        //遍历根节点
        for (CategoryRootDTO categoryRootDTO : categoryRootDTOS) {
            List<TypeChildrenDTO> typeChildrenDTOS = new ArrayList<>();
            //寻找这个根节点菜单下的子节点菜单
            for (ProductType productType : productTypeList) {
                if (StringUtils.isEmpty(productType.getProductTypeCode())) {
                    continue;
                }
                //当与根节点相同时增加子节点
                if (productType.getProductCategoryCode().equals(categoryRootDTO.getProductCategoryCode())) {
                    TypeChildrenDTO typeChildrenDTO = new TypeChildrenDTO();
                    typeChildrenDTO.setName(productType.getProductType());
                    typeChildrenDTO.setProductTypeCode(productType.getProductTypeCode());
                    typeChildrenDTOS.add(typeChildrenDTO);
                }
            }
            if (CollectionUtils.isNotEmpty(typeChildrenDTOS)) {
                categoryRootDTO.setChildren(typeChildrenDTOS);
            }
//            //添加到根节点的列表中
            list.add(categoryRootDTO);
        }
        return list;
    }

//    @Override
//    public boolean ifExist(List<String> list) {
//        boolean flag = false;
//        if(StringUtils.isNotEmpty(userCategoryDao.ifExist(list))){
//            flag = true;
//        }
//        return flag;
//    }

    @Override
    public int deleteConfig(String accountId) {
        //查询核心id
        List<String> list = userCategoryDao.selectTargetId(accountId);
        if(CollectionUtils.isNotEmpty(list)){
            //删除用户核心目标
            userCategoryDao.deleteUserTargetId(list,accountId);
            List<String> lastList = userCategoryDao.selectLast(list,accountId);
            if (CollectionUtils.isNotEmpty(lastList)){
                list = list.stream().filter(item -> !lastList.contains(item)).collect(Collectors.toList());
            }
            if(CollectionUtils.isNotEmpty(list)){
                userCategoryDao.deleteTargetId(list);
            }
        }
        return userCategoryDao.deleteConfig(accountId);
    }

    @Override
    public List<String> selectUserType(String accountId) {
        //查询所有列表
        List<String> list = userCategoryDao.selectUserType(accountId);
        return list;
    }

    @Override
    public HrmAccountInfoDTO selectDefault() {
        return userCategoryDao.selectDefault();
    }

    @Override
    public int insertOrUpdateDefault(String accountId,String id) {
        if(StringUtils.isEmpty(id)){
            return userCategoryDao.insertDefault(accountId);
        }
        return userCategoryDao.updateDefault(accountId,id);
    }

    @Override
    public boolean ifExist(List<String> list, String accountId) { ;
        boolean flag = false;
        if(StringUtils.isNotEmpty(userCategoryDao.ifEditExist(accountId,list))){
            flag = true;
        }
        return flag;
    }

    @Override
    public void deleteMyself(String accountId) {
        userCategoryDao.deleteMyself(accountId);
    }
}

