package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /*
    * 新增菜品
    * */
    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO.toString());

        dishService.add(dishDTO);
        return Result.success();
    }

    /*
    * 分页查询
    * */
    @GetMapping("/page")
    public Result<PageResult> findByPage( DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品{}",dishPageQueryDTO.toString());

        PageResult pageResult= dishService.findByPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 删除菜品
    * */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品id:{}",ids.toString());

        dishService.delete(ids);
        return Result.success();
    }

    /*
    * 菜品起售，停售
    * */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status ,@RequestParam Long id){
        log.info("修改的菜品id:{},修改后的状态:",id,status);

        dishService.startOrStop(status,id);
        return Result.success();
    }

    //修改菜品
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改的菜品,{}",dishDTO.toString());

        dishService.update(dishDTO);
        return Result.success();
    }

    //根据id查询菜品，查询回显
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("要查询回显的id:{}",id);

        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /*
    * 根据分类id查询菜品
    * */
    @GetMapping("/list")
    public Result<List<Dish>> list(@RequestParam Long categoryId){
        log.info("要查询的分类id:{}",categoryId);
        List<Dish> dishList=dishService.list(categoryId);
        return Result.success(dishList);
    }
}
