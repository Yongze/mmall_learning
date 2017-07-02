package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yw850 on 6/28/2017.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    //insert之前自增长id没有生成，要在Mybatis xml 里配置两个值useGeneratedKeys和keyProperty
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0 ){
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.CreateBySuccess("Succeed to create new shipping address.",result);
        }
        return ServerResponse.CreateByErrorMessage("Fail to create shipping address.");
    }

    public ServerResponse<String> del(Integer userId, Integer shippingId){
        int resultCount = shippingMapper.deleteByShippingIdAndUserId(userId,shippingId);
        if (resultCount > 0){
            return ServerResponse.CreateBySuccessMessage("Succeed to delete shipping address.");
        }
        return ServerResponse.CreateByErrorMessage("Fail to delete shipping address.");
    }

    public ServerResponse update(Integer userId, Shipping shipping){
        //重新复制给shipping设置登录用户的user ID，不能直接用传过来的shipping内的id。
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateBuShipping(shipping);
        if (rowCount > 0 ){
            return ServerResponse.CreateBySuccess("Succeed to update new shipping address.");
        }
        return ServerResponse.CreateByErrorMessage("Fail to update shipping address.");
    }

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(userId,shippingId);
        if (shipping == null){
            return ServerResponse.CreateByErrorMessage("Cannot find the shipping address");
        }
        return ServerResponse.CreateBySuccess("Succeed to find the shipping address", shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.CreateBySuccess(pageInfo);
    }

}
