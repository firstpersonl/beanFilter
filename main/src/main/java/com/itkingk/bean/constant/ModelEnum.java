package com.itkingk.bean.constant;

/**
 * 模块枚举类型
 * @author itkingk
 */

public enum ModelEnum {
    modelA("com.itkingk.bean.model.a","model A"),
    modelB("com.itkingk.bean.model.b","model b"),;
    private String packageName;
    private String desc;

    ModelEnum(String packageName, String desc) {
        this.packageName = packageName;
        this.desc = desc;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDesc() {
        return desc;
    }
    public static ModelEnum valueOfByName(String name) {
        for (ModelEnum value : values()) {
            if (value.name().endsWith(name)) {
                return value;
            }
        }
        return null;
    }
}
