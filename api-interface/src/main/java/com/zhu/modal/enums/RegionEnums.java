package com.zhu.modal.enums;

import com.zhu.modal.dto.region.AreaQueryRequest;
import com.zhu.modal.dto.region.CityQueryRequest;
import com.zhu.modal.dto.region.ProvinceQueryRequest;
import com.zhu.modal.dto.region.StreetQueryRequest;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum RegionEnums {

    REGION_PROVINCE(ProvinceQueryRequest.class,"province"),
    REGION_CITY(CityQueryRequest.class,"city"),
    REGION_AREA(AreaQueryRequest.class,"area"),
    REGION_STREET(StreetQueryRequest.class,"street");

    private final Class text;

    private final String value;

    RegionEnums(Class text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static RegionEnums getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (RegionEnums anEnum : RegionEnums.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public Class getText() {
        return text;
    }

}
