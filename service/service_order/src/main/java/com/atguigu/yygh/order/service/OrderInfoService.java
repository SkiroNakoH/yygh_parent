package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-25
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long subscribeOrder(String scheduleId,Long patientId);

    Map<String, Object> findPage(Integer page, Integer size, OrderQueryVo orderQueryVo);

    OrderInfo getDetailById(Long id);

    void paySuccess2Hosp(String hoscode, String hosRecordId);

    void updateOrderStatus(Long orderId, Integer status);

    boolean cancelOrder(Long orderId) throws Exception;

    void remind();
}
