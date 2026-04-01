package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    //订单统计
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
      log.info("订单统计起始至终止日期:{},{}",begin,end);

      TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin,end);
      return Result.success(turnoverReportVO);
    }

    /*
    * 用户统计
    * */
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin ,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end ){
        log.info("用户统计:{},{}",begin,end);

        UserReportVO userReportVO = reportService.userStatistics(begin,end);
        return Result.success(userReportVO);
    }
}
