package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐{}",setmealDTO.toString());
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("查询的信息:{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除的id:{}",ids.toString());

        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /*
    * 设置套餐停售，起售
    * */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id){
        log.info("修改的id{},状态修改为{}",id,status);

        setmealService.startOrStop(status,id);
        return Result.success();
    }

    /*
    *修改套餐
    * */
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("要修改的套餐:{}",setmealDTO.toString());

        setmealService.update(setmealDTO);
        return Result.success();
    }

    /*
    * 根据id查询套餐
    * */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("查询的id:{}",id);

        SetmealVO setmealVO = setmealService.getByid(id);
        return Result.success(setmealVO);
    }

}
