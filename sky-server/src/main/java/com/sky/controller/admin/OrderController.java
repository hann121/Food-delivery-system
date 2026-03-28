package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    /*
    * 订单搜索
    * */
    @GetMapping("/conditionSearch")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("搜索订单:{}",ordersPageQueryDTO);

        PageResult pageRes = orderService.page(ordersPageQueryDTO);
        return Result.success(pageRes);
    }

    /*
    * 各个状态的订单数量统计
    * */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        log.info("各个状态订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /*
    * 查询订单详情
    * */
    @GetMapping("/details/{id}")
    public Result<OrderVO> findOrderDetail(@PathVariable Long id){
        log.info("查询订单的id:{}",id);

        OrderVO orderVO = orderService.getDetailById(id);
        return Result.success(orderVO);
    }

    /*
    * 接单
    * */
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单的单号:{}",ordersConfirmDTO);

        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /*
    *拒绝接单
    * */
    @PutMapping("/rejection")
    public Result reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒绝接单:{}",ordersRejectionDTO);

        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /*
    * 取消订单
    * */
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单:{}",ordersCancelDTO);

        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /*
    * 派送订单
    * */
    @PutMapping("/delivery/{id}")
    public Result deliver(@PathVariable Long id){
        log.info("派送订单{}",id);

        orderService.deliver(id);
        return Result.success();
    }

    /*
    * 完成订单
    * */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id){
        log.info("完成订单:{}",id);

        orderService.complete(id);
        return Result.success();
    }
}
