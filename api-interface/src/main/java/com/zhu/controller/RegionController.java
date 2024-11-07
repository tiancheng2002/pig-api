package com.zhu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.exception.ApiException;
import com.zhu.handle.RegionHandleRegister;
import com.zhu.modal.dto.region.RegionQueryRequest;
import com.zhu.utils.QueryUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/region")
public class RegionController {

    @Resource
    private RegionHandleRegister regionHandleRegister;

    private final String classPrefix = "com.zhu.modal.entity.";

    @GetMapping("/{regionName}")
    public <T> List<T> getRegionData(RegionQueryRequest regionQueryRequest,@PathVariable String regionName){
        Class<T> regionClass;
        try {
            regionClass = (Class<T>) Class.forName(classPrefix + (Character.toUpperCase(regionName.charAt(0))+regionName.substring(1)));
        } catch (ClassNotFoundException e) {
            throw new ApiException(40000,"接口信息错误");
        }
        //获取查询参数信息
        QueryWrapper<T> queryData = QueryUtils.getQueryData(regionQueryRequest, regionName, regionClass);
        //获取对应service
        IService<T> regionService = regionHandleRegister.getDataSourceByType(regionName, regionClass);
        return regionService.list(queryData);
    }

//    @GetMapping("/province")
//    public List<Province> getProvinceData(ProvinceQueryRequest provinceQueryRequest){
//        return provinceService.list(QueryUtils.getQueryData(provinceQueryRequest,Province.class));
//    }
//
//    @GetMapping("/city")
//    public List<City> getCityData(CityQueryRequest cityQueryRequest){
//        return cityService.list(QueryUtils.getQueryData(cityQueryRequest,City.class));
//    }
//
//    @GetMapping("/area")
//    public List<Area> getAreaData(AreaQueryRequest areaQueryRequest){
//        return areaService.list(QueryUtils.getQueryData(areaQueryRequest,Area.class));
//    }
//
//    @GetMapping("/street")
//    public List<Street> getStreetData(StreetQueryRequest streetQueryRequest){
//        return streetService.list(QueryUtils.getQueryData(streetQueryRequest,Street.class));
//    }

}
