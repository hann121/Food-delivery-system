package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;


    /*
    * 新增分类
    * */
    @PostMapping
    public Result add(@RequestBody CategoryDTO categoryDTO){
        log.info("新增新的分类");
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /*
    *分页查询分类信息
    * */
    @GetMapping("/page")
    public Result<PageResult> FindByPage(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类信息,页码{},一面展示数量{}",categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        PageResult pageResult= categoryService.FindByPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 启用，禁用分类
    * */
    @PostMapping("/status/{status}")
    public Result StartOrStop(@PathVariable Integer status,
                              @RequestParam Long id)
    {
        log.info("要修改的分类id:{},状态修改为:{}",id,status);
        categoryService.StartOrStop(status,id);
        return Result.success();
    }

    /*
    * 修改分类
    * */
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("要修改的分类id:{}",categoryDTO.getId());
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /*
    * 根据id删除分类
    * */
    @DeleteMapping
    public Result delete(@RequestParam Long id){
        log.info("即将删除分类的id为{}",id);
        categoryService.delete(id);
        return Result.success();
    }

}
