package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.User;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    /*
    * 统计营业额
    * */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime begins = begin.atStartOfDay();
        LocalDateTime ends = end.plusDays(1).atStartOfDay();
        //营业额
        List<Double> turnoverList = new ArrayList<>();

        //计算日期
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        LocalDateTime tomorrow =begin.plusDays(1).atStartOfDay();

        while(!begin.equals(end)){

            //计算当日营业额
            Double turnover = reportMapper.turnoverOneDayStatistics(begins,tomorrow);

            //判断是否有空值
            if(turnover==null){
                turnover = 0.0;
            }

            //更新日期变量
            begins = begins.plusDays(1);
            tomorrow = tomorrow.plusDays(1);
            begin = begin.plusDays(1);

            dates.add(begin);
            turnoverList.add(turnover);
        }



        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dates,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();

        return turnoverReportVO;
    }

    /*
    * 统计用户
    * */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime begins = begin.atStartOfDay();
        LocalDateTime ends = end.plusDays(1).atStartOfDay();

        //总用户表
        List<Integer> totalUsers = new ArrayList<>();
        //新增用户表
        List<Integer> newUsers = new ArrayList<>();

        //计算日期
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        LocalDateTime tomorrow =begin.plusDays(1).atStartOfDay();
        Integer tempNum=0;
        LocalDate temp = begin;
        LocalDateTime temps = begins;
        Integer newUserNum = 0;

        //进入循环
        while(!begin.equals(end)){

            //计算每日总用户量
            Integer totalNum = reportMapper.countTotalUserNum(temps,tomorrow);
            if(totalNum==null){
                totalNum = 0 ;
            }

            if(!begin.equals(temp)) {
                //计算新增用户量
                newUserNum = totalNum - tempNum;
                if(newUserNum == null){
                    newUserNum = 0;
                }
            }


            //更新日期变量
            begins = begins.plusDays(1);
            tomorrow = tomorrow.plusDays(1);
            begin = begin.plusDays(1);
            tempNum = totalNum;

            dates.add(begin);
            totalUsers.add(totalNum);
            newUsers.add(newUserNum);
        }

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dates,","))
                .totalUserList(StringUtils.join(totalUsers,","))
                .newUserList(StringUtils.join(newUsers,","))
                .build();

        return userReportVO;
    }

    /*
    * 订单统计
    * */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime begins = begin.atStartOfDay();
        LocalDateTime ends = end.plusDays(1).atStartOfDay();
        //订单数量
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> trueOrderCountList = new ArrayList<>();
        Integer totalCount =0;
        Integer trueTotalCount = 0;
        //计算日期
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        LocalDateTime tomorrow =begin.plusDays(1).atStartOfDay();

        while(!begin.equals(end)){
            //计算当日营业额
            Integer count = reportMapper.countAllOrder(begins,tomorrow);

            Integer trueCount = reportMapper.countOrder(begins,tomorrow);
            //判断是否有空值
            if(count == null){
                count=0;
            }
            if(trueCount == null){
                trueCount=0;
            }
            totalCount+=count;
            trueTotalCount+=trueCount;

            //更新日期变量
            begins = begins.plusDays(1);
            tomorrow = tomorrow.plusDays(1);
            begin = begin.plusDays(1);

            dates.add(begin);
            orderCountList.add(count);
            trueOrderCountList.add(trueCount);
        }
        //求完成率
        double rate = (double) trueTotalCount / totalCount ;

        //new返回对象
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dates,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .totalOrderCount(totalCount)
                .validOrderCount(trueTotalCount)
                .validOrderCountList(StringUtils.join(orderCountList,","))
                .orderCompletionRate(rate)
                .build();

        return orderReportVO;
    }

    /*
    * 查询销量top10
    * */
    @Override
    public SalesTop10ReportVO findTop10(LocalDate begin, LocalDate end) {
        LocalDateTime begins = begin.atStartOfDay();
        LocalDateTime ends = end.plusDays(1).atStartOfDay();

        //
        List<GoodsSalesDTO> goodsSalesDTOS = reportMapper.findTop10(begins,ends);

        //用流转化成列表
        List<String> names = goodsSalesDTOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());

        List<Integer> numbers = goodsSalesDTOS.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        log.info("top10数量：{}",goodsSalesDTOS);
        return new SalesTop10ReportVO(StringUtils.join(names,","),StringUtils.join(numbers,","));
    }
}
