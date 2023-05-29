package utry.data.modular.common.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.complaints.dto.ComplaintDto;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonTemplateDao {

    /**
     * 通过电话号获取key
     * @param phoneNum
     * @return
     */
    List<String> selectAccountIdByPhone(@Param("phoneNum") String phoneNum);
}
