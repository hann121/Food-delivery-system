package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;

    //    用户下单
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        Long userId= BaseContext.getCurrentId();
        //先判断地址簿，购物车是否为空
        AddressBook addressBook =addressBookMapper.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        List<ShoppingCart> shoppingCarts=shoppingCartMapper.getByUserId(userId);
        if(shoppingCarts==null){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //更新对象orders信息，并插入orders表
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        //添加地址信息
        String addr  = addressBook.getProvinceName()+addressBook.getDistrictName()+addressBook.getDetail();
        orders.setAddress(addr);

        orderMapper.insert(orders);
        //插入订单明细表(与上表为一对多关系)
        List<OrderDetail> orderDetails=new ArrayList<>();
        //拷贝购物车列表信息完善订单明细
        for(ShoppingCart sc:shoppingCarts){
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(sc,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        //插入
        orderDetailMapper.insertBatch(orderDetails);

        //返回信息
        OrderSubmitVO orderSubmitVO=OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /*
    * 用户支付
    * */
    @Override
    public void pay(String orderNumber) {
        orderMapper.pay(orderNumber);
    }

//    /*
//    * 历史订单查询
//    * */
//    @Override
//    public PageResult findByPage(int page, int pageSize, Integer status) {
//        OrdersPageQueryDTO ordersPageQueryDTO =new OrdersPageQueryDTO();
//        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
//        ordersPageQueryDTO.setStatus(status);
//
//        PageHelper.startPage(page,pageSize);
//        //查询所有订单信息
//        Page<Orders> list = orderMapper.findByPage(ordersPageQueryDTO);
//        Long total = list.getTotal();
//        List<OrderVO> ordersList = new ArrayList<>();
//        if(total>0){
//            //查询该订单下的订单详细信息
//            for(Orders order:list){
//                Long orderId = order.getId();
//
//                List<OrderDetail> orderDetailList = orderDetailMapper.findByOrderId(orderId);
//
//                OrderVO orderVO = new OrderVO();
//                BeanUtils.copyProperties(order,orderVO);
//                orderVO.setOrderDetailList(orderDetailList);
//                ordersList.add(orderVO);
//            }
//        }
//
//        return new PageResult(total,ordersList);
//    }

    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /*
    * 根据id查询订单详情
    * */
    @Override
    public OrderVO getDetailById(Long id) {
        Orders orders = orderMapper.getDetailById(id);

        AddressBook addressBook = addressBookMapper.getById(orders.getAddressBookId());

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);

        //设置地址
        String addr = addressBook.getProvinceName()+addressBook.getDistrictName()+addressBook.getDetail();
        orderVO.setAddress(addr);

        //设置商品详情
        orderVO.setOrderDetailList(orderDetails);

        return orderVO;
    }

    /*
    * 取消订单
    * */
    @Override
    public void cancelOrder(Long id) {
        //取消订单表中的订单
        orderMapper.cancelOrder(id);

    }

    /*
    * 再来一单
    * */
    @Override
    public void repetition(Long id) {
        Orders orders = orderMapper.getDetailById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);

    }

    /*
    * 用户搜索
    * */
    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        //查询订单信息
        Page<Orders> orders = orderMapper.findByPage(ordersPageQueryDTO);

        List<OrderVO> orderVOS = new ArrayList<>();
        //查询订单下商品的信息
        for(Orders order:orders) {
            List<OrderDetail> details = orderDetailMapper.getByOrderId(order.getId());

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order,orderVO);

            AddressBook addressBook = addressBookMapper.getById(order.getAddressBookId());
            //设置地址
            String addr = addressBook.getProvinceName()+addressBook.getDistrictName()+addressBook.getDetail();
            orderVO.setAddress(addr);

            orderVO.setOrderDetailList(details);
            orderVOS.add(orderVO);
        }

        return new PageResult(orders.getTotal(),orderVOS);
    }

    /*
    * 统计各个状态的订单数量
    * */
    @Override
    public OrderStatisticsVO statistics() {

        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = OrderStatisticsVO.builder()
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .toBeConfirmed(toBeConfirmed)
                .build();
        return orderStatisticsVO;
    }

    /*
    * 接单
    * */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        orderMapper.confirm(ordersConfirmDTO.getId());
    }

    /*
    * 拒绝接单
    * */
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        //给用户退款
        orderMapper.reject(ordersRejectionDTO.getId(),ordersRejectionDTO.getRejectionReason());
        //取消对应的菜品
        orderDetailMapper.deleteByOrderId(ordersRejectionDTO.getId());
    }

    /*
    * 取消订单
    * */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders order = orderMapper.getDetailById(ordersCancelDTO.getId());

        //如果没支付，直接取消。如果已经支付了，退款
            orderMapper.cancel(ordersCancelDTO.getId(),ordersCancelDTO.getCancelReason());
        if(order.getPayStatus() == 1){
            orderMapper.refund(order);
        }
        //取消对应的菜品
        orderDetailMapper.deleteByOrderId(ordersCancelDTO.getId());
    }

    /*
    * 派送订单
    * */
    @Override
    public void deliver(Long id) {
        orderMapper.deliver(id);
    }

    /*
    * 完成订单
    * */
    @Override
    public void complete(Long id) {
        orderMapper.complete(id);
    }


}
