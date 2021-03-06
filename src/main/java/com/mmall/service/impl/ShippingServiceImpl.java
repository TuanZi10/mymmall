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
 * Created by Flash on 2018/5/3.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowcount = shippingMapper.insert(shipping);
        if (rowcount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",result);
            return ServerResponse.createBySuccess("新建地址成功!",result);
        }
        return ServerResponse.createByErrorMsg("新建地址失败!");
    }

    public ServerResponse<String> del(Integer userId,Integer shippingId){
        int returncount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if (returncount > 0){
            return ServerResponse.createBySuccessMsg("删除成功!");
        }
        return ServerResponse.createByErrorMsg("删除失败！");
    }

    public ServerResponse update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);//防止横向越权
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMsg("更新地址成功!");
        }
        return ServerResponse.createByErrorMsg("更新地址失败！");
    }

    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (shipping == null){
            return ServerResponse.createByErrorMsg("无法查询到地址!");
        }
        return ServerResponse.createBySuccess("更新地址成功!",shipping);
    }


    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }






}
