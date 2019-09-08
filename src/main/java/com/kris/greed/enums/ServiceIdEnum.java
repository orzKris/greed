package com.kris.greed.enums;

/**
 * @author Kris
 * @date 2019/2/1
 */
@SuppressWarnings("all")
public enum ServiceIdEnum {

    D000("D000", "大数据输出"),
    D001("D001", "语言识别"),
    D002("D002", "经纬度地址解析"),
    D003("D003", "人脸检测"),
    D004("D004", "影讯合集"),
    D005("D005", "手机归属地"),
    D006("D006", "地区生产总值");

    private String id;
    private String desc;

    private ServiceIdEnum(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }
}
