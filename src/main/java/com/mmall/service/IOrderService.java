package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Flash on 2018/5/6.
 */
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
}
