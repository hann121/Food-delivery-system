package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {


    List<Double> turnoverStatistics(LocalDateTime begins, LocalDateTime ends);

    @Select("select sum(amount) from orders where status = 5 and order_time between #{cur} and #{tomorrow}")
    Double turnoverOneDayStatistics(LocalDateTime cur,LocalDateTime tomorrow);

    @Select("select count(id) from user where create_time between #{cur} and #{tomorrow}")
    Integer countTotalUserNum(LocalDateTime cur, LocalDateTime tomorrow);
}
