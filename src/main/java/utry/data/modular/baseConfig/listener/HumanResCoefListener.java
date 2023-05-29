package utry.data.modular.baseConfig.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import utry.data.modular.baseConfig.model.HumanResCoef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : ldk
 * @date : 09:47 2022/10/21
 */
@Slf4j
public class HumanResCoefListener implements ReadListener<HumanResCoef> {

    @Override
    public void invoke(HumanResCoef data, AnalysisContext context) {
        log.info("解析到一条数据:{}", data.toString());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }




    //可以通过实例获取该值
    private List<Object> datas = new ArrayList<Object>();


    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }





}
