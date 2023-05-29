package utry.data.modular.baseConfig.service;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.multipart.MultipartFile;
import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.model.Target;
import utry.data.util.RetResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface QualityFeedbackService {
    /**
     * 导出
     */
    void export(HttpServletResponse response, HSSFWorkbook book, String fileName);
    /**
     * 设置表头格式
     */
    HSSFCellStyle getHeadStyle(HSSFWorkbook book);
    /**
     * 检验参数
     */
    void validParams(MultipartFile file) throws IOException;
    /**
     * 查询新品
     */
    List<ProductInformationDTO> select(ModelTimeDTO modelTimeDTO);
    /**
     * 更新新品
     */
    void update(QualityFeedbackDTO qualityFeedbackDTO);
}
