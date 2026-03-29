package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    void pay(String orderNumber);

    Page<Orders> findByPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    Orders getDetailById(Long id);
    //用户端取消订单
    void cancelOrder(Long id);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer confirmed);

//  接单
    @Update("update orders set status = 3 where id = #{id}")
    void confirm(Long id);

//    拒绝接单
    void reject(Long id, String rejectionReason);

    //取消订单
    @Update("update orders set cancel_reason = #{cancelReason} , cancel_time = now() , status = 6 where id = #{id}")
    void cancel(Long id, String cancelReason);

//    退款
    @Update("update orders set status = 7,pay_status = 2,cancel_time = now() where id = #{id}")
    void refund(Orders order);

    @Update("update orders set status = 4 where id = #{id}")
    void deliver(Long id);

    @Update("update orders set status = 5 , delivery_time = NOW()+INTERVAL 1 HOUR where id = #{id}")
    void complete(Long id);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime} ")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    void update(Orders order);
}
