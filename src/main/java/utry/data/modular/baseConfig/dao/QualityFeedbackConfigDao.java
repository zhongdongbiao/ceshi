package utry.data.modular.baseConfig.dao;

import org.apache.ibatis.annotations.Mapper;
import utry.data.modular.baseConfig.dto.*;

import java.util.List;

/**
 * 目标配置
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface QualityFeedbackConfigDao {
    /**
     *查询所有产品资料的型号等信息
     */
    List<QualityFeedbackDTO> selectAllInformation();
    /**
     *批量更新
     *
     *
     */
    void updateTime(List<QualityFeedbackDTO> list);
    /**
     *根据新品发布月进行查询
     */
    List<ProductInformationDTO> select(ModelTimeDTO modelTimeDTO);
    /**
     *更新
     */
    void update(QualityFeedbackDTO qualityFeedbackDTO);
}
