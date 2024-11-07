package com.zhu.handle;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.modal.enums.RegionEnums;
import com.zhu.service.AreaService;
import com.zhu.service.CityService;
import com.zhu.service.ProvinceService;
import com.zhu.service.StreetService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class RegionHandleRegister {

    @Resource
    private ProvinceService provinceService;

    @Resource
    private CityService cityService;

    @Resource
    private AreaService areaService;

    @Resource
    private StreetService streetService;

    private Map<String, IService> typeHandlerMap;

    @PostConstruct
    public void doInit() {
        typeHandlerMap = new HashMap() {{
            put(RegionEnums.REGION_PROVINCE.getValue(), provinceService);
            put(RegionEnums.REGION_CITY.getValue(), cityService);
            put(RegionEnums.REGION_AREA.getValue(), areaService);
            put(RegionEnums.REGION_STREET.getValue(),streetService);
        }};
    }

    public <T> IService<T> getDataSourceByType(String type,Class<T> c) {
        if (typeHandlerMap == null) {
            return null;
        }
        return typeHandlerMap.get(type);
    }

}
