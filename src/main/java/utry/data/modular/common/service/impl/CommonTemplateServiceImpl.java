package utry.data.modular.common.service.impl;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import utry.core.common.BusinessException;
import utry.data.modular.common.dao.CommonTemplateDao;
import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.common.dto.TemplateQueryDto;
import utry.data.modular.common.dto.TemplateResultDto;
import utry.data.modular.common.service.CommonTemplateService;
import utry.data.modular.technicalQuality.dao.TechnicalQualityCopyDao;
import utry.data.modular.technicalQuality.dao.TechnicalQualityDao;
import utry.data.modular.technicalQuality.dto.DistrictTemplateDTO;
import utry.data.modular.technicalQuality.dto.DistrictTemplateQueryDTO;
import utry.data.modular.technicalQuality.service.TechnicalQualityService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 技术品质实现类
 *
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class CommonTemplateServiceImpl implements CommonTemplateService {

    @Resource
    private CommonTemplateDao commonTemplateDao;
    @Resource
    private RedisTemplate<String,?> redisTemplate;
    @Resource
    private TechnicalQualityDao technicalQualityDao;

    /**
     * 通过电话号获取key
     * @param templateQueryDto
     * @return
     */
    @Override
    public List<TemplateResultDto> selectTemplateKey(TemplateQueryDto templateQueryDto) {
        List<String> accountIds = commonTemplateDao.selectAccountIdByPhone(templateQueryDto.getPhoneNum());
        List<TemplateResultDto> templateResultDtoList = new ArrayList<>();
        if(CollectionUtils.isEmpty(accountIds)){
            throw new BusinessException("无法通过手机号找到此用户！");
        }
        if (accountIds.size()>1){
            throw new BusinessException("该手机号被多个用户注册！");
        }
        String accountId = accountIds.get(0);
        String key = templateQueryDto.getBusinessCode()+":"+accountId;
        //通过key查询所有模板
        ListOperations<String, JSONObject> listOps = (ListOperations<String, JSONObject>) redisTemplate.opsForList();
        // 取出所有list配置
        List<JSONObject> list = listOps.range(key, 0, -1);
        String name;
        //如果业务是热线
        if("call".equals(templateQueryDto.getBusinessCode())){
            name = "planName";
        }else{
            name = "name";
        }
        //如果业务是大区
        if("district".equals(templateQueryDto.getBusinessCode())){
            TemplateResultDto ap = new TemplateResultDto();
            ap.setPlanId(key);
            ap.setPlanName("AP上海");
            TemplateResultDto wmhz = new TemplateResultDto();
            wmhz.setPlanId(key);
            wmhz.setPlanName("WMHZ");
            templateResultDtoList.add(ap);
            templateResultDtoList.add(wmhz);
        }

        if(CollectionUtils.isNotEmpty(list)){
            for(JSONObject jsonObject : list){
                TemplateResultDto templateResultDto = new TemplateResultDto();
                templateResultDto.setPlanName(jsonObject.getString(name));
                templateResultDto.setPlanId(key);
                templateResultDtoList.add(templateResultDto);
            }
        }
        return templateResultDtoList;
    }

    /**
     * 通过key和name解析数据
     * @param templateQueryDto
     * @return
     */
    @Override
    public JSONObject selectDataByKey(TemplateQueryDataDto templateQueryDto) {
        String key = templateQueryDto.getPlanId();
        ListOperations<String, JSONObject> listOps = (ListOperations<String, JSONObject>) redisTemplate.opsForList();
        // 取出所有list配置
        List<JSONObject> list = new ArrayList<>();
        if(redisTemplate.hasKey(key)){
            list = listOps.range(key, 0, -1);
        }
        JSONObject json = new JSONObject();

        String businessCode = key.substring(0, key.lastIndexOf(":"));
        //如果是大区解析
        if(("AP上海".equals(templateQueryDto.getPlanName()) || "WMHZ".equals(templateQueryDto.getPlanName()))
                && "district".equals(businessCode)){
            List<DistrictTemplateQueryDTO> categoryTemplate = technicalQualityDao.selectCategoryTemplate();
            Map<String, List<DistrictTemplateQueryDTO>> categoryTemplateMap;
            if(CollectionUtils.isNotEmpty(categoryTemplate)){
                categoryTemplateMap = categoryTemplate.stream().collect(Collectors.groupingBy(DistrictTemplateQueryDTO::getName));
                List<DistrictTemplateQueryDTO> categoryList = categoryTemplateMap.getOrDefault(templateQueryDto.getPlanName(),new ArrayList<>());
                List<String> productCategoryCode = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(categoryList)){
                    for(DistrictTemplateQueryDTO templateQueryDTO : categoryList){
                        productCategoryCode.add(templateQueryDTO.getCode());
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("startTime",templateQueryDto.getStartTime());
                jsonObject.put("endTime",templateQueryDto.getEndTime());
                jsonObject.put("productCategoryCode",productCategoryCode);
                return jsonObject;
            }
        }

        if(CollectionUtils.isNotEmpty(list)){
            for(JSONObject jsonObject: list){
                String name = jsonObject.getString("name");
                if(StringUtils.isEmpty(name)){
                    name = jsonObject.getString("planName");
                }
                if(name.equals(templateQueryDto.getPlanName())){
                    jsonObject.put("startTime",templateQueryDto.getStartTime());
                    jsonObject.put("endTime",templateQueryDto.getEndTime());
                    json = jsonObject;
                    break;
                }
            }
        }
        return json;
    }
}
