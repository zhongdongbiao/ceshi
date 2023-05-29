package utry.data.modular.partsManagement.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utry.core.common.BusinessException;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.data.constant.RedisKeyConstant;
import utry.data.constant.RpcUrlConstant;
import utry.data.constant.SystemConfigConstant;
import utry.data.enums.SiteCodeEnum;
import utry.data.modular.partsManagement.dao.PartDrawingStockDao;
import utry.data.modular.partsManagement.dto.PartDrawingStockDTO;
import utry.data.modular.partsManagement.model.PartDrawingStock;
import utry.data.modular.partsManagement.service.ApiPartDrawingStockService;
import utry.data.util.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: data
 * @description: 部品库存数据拉取业务接口实现类
 * @author: WangXinhao
 * @create: 2022-06-07 09:16
 **/
@Service
public class ApiPartDrawingStockServiceImpl implements ApiPartDrawingStockService {

    @Resource
    private SysConfServiceImpl sysConfService;

    @Autowired
    private IdGeneratorUtil idGeneratorUtil;

    @Autowired
    private PartDrawingStockDao partDrawingStockDao;

    /**
     * 部品库存数据拉取并保存
     * @return 统一返回格式
     */
    @Override
    public RetResult<Void> getPartDrawingStock() {
        String ip = sysConfService.getSystemConfig(SystemConfigConstant.SHUODE_HOST, SiteCodeEnum.DATA.code());
        String url = ip + RpcUrlConstant.GET_PART_DRAWING_STOCK_URL;
        List<PartDrawingStockDTO> dataList;
        try {
            String postResult = HttpClientUtil.post(url, HttpClientUtil.getParam(null));
            JSONObject jsonObject = JSONObject.parseObject(postResult);
            if ("T".equals(jsonObject.get("RESULT"))) {
                String dataStr = jsonObject.get("data").toString();
                dataList = JSON.parseArray(dataStr, PartDrawingStockDTO.class);
            } else {
                return RetResponse.makeErrRsp(jsonObject.get("ERRMSG").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }

        int size = dataList.size();
        Date date = new Date();
        // 拉取的数据量大约 40000，指定大小减少 arrayList 的 grow
        List<PartDrawingStock> partDrawingStockList = new ArrayList<>(size);
        LinkedList<String> idsList = idGeneratorUtil.generateIdBatchByToday(RedisKeyConstant.GENERATE_ID_PART_DRAWING_STOCK, 6, size);
        for (int i = 0; i < size; i++) {
            String id = idsList.pop();
            PartDrawingStockDTO partDrawingStockDTO = dataList.get(i);
            PartDrawingStock partDrawingStock = PartDrawingStock.builder()
                    .id(id)
                    .warehouseCode(partDrawingStockDTO.getWarehouseCode())
                    .partDrawingNumber(partDrawingStockDTO.getPartDrawingNumber())
                    .locationNumber(partDrawingStockDTO.getLocationNumber())
                    .describedDrawingNo(partDrawingStockDTO.getDescribedDrawingNo())
                    .openNumber(partDrawingStockDTO.getOpenNumber())
                    .currentReception(partDrawingStockDTO.getCurrentReception())
                    .currentProvide(partDrawingStockDTO.getCurrentProvide())
                    .currentAdjust(partDrawingStockDTO.getCurrentAdjust())
                    .realityNumber(partDrawingStockDTO.getRealityNumber())
                    .distributionNumber(partDrawingStockDTO.getDistributionNumber())
                    .stockoutNumber(partDrawingStockDTO.getStockoutNumber())
                    .costPrice(new BigDecimal(partDrawingStockDTO.getCostPrice()))
                    .costAmount(new BigDecimal(partDrawingStockDTO.getCostAmount()))
                    .factoryCode(partDrawingStockDTO.getFactoryCode())
                    .factoryName(partDrawingStockDTO.getFactoryName())
                    .partDrawingNo(partDrawingStockDTO.getPartDrawingNo())
                    .createTime(date)
                    .updateTime(date)
                    .build();

            partDrawingStockList.add(partDrawingStock);
        }

        int insertCount = partDrawingStockDao.insertBatch(partDrawingStockList);
        return RetResponse.makeOKRsp();
    }
}
