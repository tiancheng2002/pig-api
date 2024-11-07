package com.zhu.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhu.modal.dto.region.RegionQueryRequest;
import com.zhu.modal.enums.RegionEnums;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryUtils {

    public static <T> QueryWrapper<T> getQueryData(RegionQueryRequest regionQueryRequest, String regionName, Class<T> c) {
        //根据对应查询区域数据去枚举类中获取对应的查询请求
        Class queryClass = RegionEnums.getEnumByValue(regionName).getText();
        //转换成通用的查询请求
        Object queryRequest = BeanCopeUtils.copyBean(regionQueryRequest, queryClass);
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        for (Field filed : getAllFields(queryRequest)) {
            String filedName = filed.getName();
            Object fileValue = null;
            // 为了访问私有属性，需要关闭Java的访问控制检查
            filed.setAccessible(true);
            try {
                fileValue = filed.get(queryRequest);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            if(fileValue==null){
                continue;
            }
            if(filedName.equals("name")){
                queryWrapper.like(filedName,fileValue);
            }else{
                queryWrapper.eq(filedName,fileValue);
            }
        }
        return queryWrapper;
    }

    /**
     * 获取类的所有属性，包括父类
     *
     * @param object
     * @return
     */
    public static Field[] getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return (true);
        }
        if ("".equals(object)) {
            return (true);
        }
        if ("null".equals(object)) {
            return (true);
        }
        return (false);
    }

}
