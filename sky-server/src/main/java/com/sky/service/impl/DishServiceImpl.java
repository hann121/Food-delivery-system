package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void add(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dish.setStatus(1);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());

        //记录当前创建和修改人的id
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());

        dishMapper.add(dish);

        //获取菜品的id
        Long dishId=dish.getId();

        List<DishFlavor> flavors=dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            for(DishFlavor flavor:flavors){
                flavor.setDishId(dishId);
            }
        }
        dishFlavorMapper.addFlavors(flavors);
    }

    @Override
    public PageResult findByPage(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());


        Page<DishVO> dishes=dishMapper.findByPage(dishPageQueryDTO);

        Long total=dishes.getTotal();
        List<DishVO> records=dishes.getResult();

        return new PageResult(total,records);
    }

    /*
    * 批量删除菜品
    * */
    @Override
    public void delete(List<Long> ids) {
        //查询ids中的菜品
        List<Dish> dishes= dishMapper.getByids(ids);
        //查询对应id的菜品是否正在出售
        for(Dish dish:dishes){
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //查询菜品是否和套餐绑定
        List<Long> setmealIds = setmealDishMapper.getSetmealId(ids);
        if(setmealIds !=null &&setmealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //确认无关后，删除dish菜品表对应id的菜品
        dishMapper.delete(ids);

        //删除菜品相关的口味表dish_flavor
        dishFlavorMapper.deleteByDishId(ids);
    }

//  修改菜品的起售与停售状态
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish =new Dish();
        dish.setId(id);
        dish.setStatus(status);

        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(BaseContext.getCurrentId());

        dishMapper.update(dish);
    }

    /*
    * 修改菜品信息
    * */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish =new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();

        Long dishId=dishDTO.getId();
        //修改菜品表
        dishMapper.update(dish);

        //先删除菜品对应的口味表信息
        dishFlavorMapper.deleteByDishId(Collections.singletonList(dishId));

        //批量插入传入的口味
        if(flavors !=null && flavors.size()>0){
            for(DishFlavor flavor:flavors){
                flavor.setDishId(dishId);
            }
        }
        dishFlavorMapper.addFlavors(flavors);
    }

    /*
    * 查询回显菜品
    * */
    @Override
    public DishVO getById(Long id) {
        //在菜品表中查询
        Dish dish = dishMapper.getById(id);

        //在口味表中查询

        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /*
    * 根据分类id查询菜品
    * */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

}
