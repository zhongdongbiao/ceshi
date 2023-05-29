package utry.data.modular.sharding.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.data.modular.sharding.model.OrderDetail;
import utry.data.modular.sharding.service.impl.ShardingServiceImpl;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author zh
 * @Date 2022/4/27 14:22
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/subApi/settleManage")
@Api(tags = "结算管理数据统计")
public class ShardingController extends CommonController {

    @Resource
    private ShardingServiceImpl shardingService;
    /*@Resource
    private SettleManagementDao settleManagementDao;
    */

    /**以下为分表测试方法*/
    //
   /* @PostMapping("add")
    public RetResult<Object> add(@RequestBody TOrder order) {
        settleManagementService.addOrder(order);
        return RetResponse.makeOKRsp();
    }

    @GetMapping("query")
    public List<TOrder> query() {
        return settleManagementDao.getList();
    }*/

    /**根据时间分表*/
    @PostMapping("addDetail")
    public RetResult<Object> addDetail(@RequestBody OrderDetail order) {
        Integer rows = shardingService.addDetail(order);
        return RetResponse.makeOKRsp(rows);
    }

    @GetMapping("queryDetail")
    public List<OrderDetail> queryDetail() {
        return shardingService.getList();
    }
   /*

    @PostMapping("delDetail")
    public RetResult delDetail(@RequestBody OrderDetail order) {
        return RetResponse.makeOKRsp(shardingService.delDetail(order));
    }

    @PostMapping("updateDetail")
    public void updateDetail(@RequestBody OrderDetail order) {
        shardingService.updateDetail(order);
    }

    *//**分页测试*//*
    @PostMapping("queryDetailByPageHelper")
    public RetResult<JSONObject> queryDetailByPageHelper(@RequestBody OrderDetail order) {
        //开启分页插件
        PageBean pageBean = this.getPageBean(order.getCurrentPage(), order.getPageSize());
        com.github.pagehelper.Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        //参数校验
        List<TOrder> list = shardingService.queryDetailByPageHelper(order);
        PageInfo<TOrder> pageInfo = new PageInfo<>(list);
        JSONObject resJson = new JSONObject();
        resJson.put("data", pageInfo.getList());
        resJson.put("count", page.getTotal());
        resJson.put("code", "200");
        return RetResponse.makeOKRsp(resJson);

    }*/


}
