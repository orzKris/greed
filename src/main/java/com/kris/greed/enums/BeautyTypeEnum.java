package com.kris.greed.enums;


import org.apache.commons.lang.StringUtils;

/**
 * @author Kris
 * @date 2019/5/10
 */
@SuppressWarnings("all")
public enum BeautyTypeEnum {

    A01("01", "淑女"),
    A02("02", "性感"),
    A03("03", "复古"),
    A04("04", "街头"),
    A05("05", "休闲"),
    A06("06", "民族"),
    A07("07", "甜美"),
    A08("08", "运动"),
    A09("09", "可爱"),
    A10("10", "大码"),
    A11("11", "欧美"),
    A12("12", "中老年"),
    A13("13", "韩版"),
    A14("14", "日系"),
    A15("15", "英伦"),
    A16("16", "OL风"),
    A17("17", "学院"),
    A18("18", "其他");
    private String beautyType;
    private String beautyTypeDesc;

    BeautyTypeEnum(String beautyType, String beautyTypeDesc) {
        this.beautyType = beautyType;
        this.beautyTypeDesc = beautyTypeDesc;
    }

    public static boolean hasType(String beautyType) {
        if (StringUtils.isBlank(beautyType)) {
            return false;
        }

        for (BeautyTypeEnum value : BeautyTypeEnum.values()) {
            if (value.getBeautyType().equalsIgnoreCase(beautyType.trim())) {
                return true;
            }
        }
        return false;
    }

    public static String getTypeDesc(String beautyType) {
        String typeDesc = null;
        for (BeautyTypeEnum value : BeautyTypeEnum.values()) {
            if (value.getBeautyType().equalsIgnoreCase(beautyType.trim())) {
                typeDesc = value.getBeautyTypeDesc();
            }
        }
        return typeDesc;
    }

    public String getBeautyType() {
        return beautyType;
    }

    public String getBeautyTypeDesc() {
        return beautyTypeDesc;
    }
}
