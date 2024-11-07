package com.zhu.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//拷贝bean的工具类
public class BeanCopeUtils {
    private BeanCopeUtils() {}

    //单个类拷贝
    public static <V> V copyBean(Object source, Class<V> clazz) {

        //创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return result;
    }

    //集合拷贝
    public static <O,V> List<V> copyBeanList(List<O> list, Class<V> clazz){
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }

    public static <V> V convertMapToBean(Map<String,Object> map,Class<V> clazz){
        V result = null;
        try {
            //遍历对象的所有属性
            result = clazz.newInstance();
            Field[] fields = result.getClass().getDeclaredFields();
            for (Field field : fields) {
                //给私有属性设置允许访问
                field.setAccessible(true);
                //从Map当中获取对应属性的值
                Object fieldValue = map.getOrDefault(field.getName(), null);
                //将获取到的值进行赋值
                field.set(result,fieldValue);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
