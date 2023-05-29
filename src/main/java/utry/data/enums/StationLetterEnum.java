package utry.data.enums;

/**
 * @Author: ldk
 * @Date: 2021/1/29 10:38
 * @Desc： 站内信类型枚举
 */
public enum StationLetterEnum {

    NDS2("[NDS2预警]"),
    JOB_ORDER("[作业订单预警]"),
    PARTS_INVENTORY("[零件库存预警]"),

    BASE_INFO("[基础信息变更]");

    private String name;

    StationLetterEnum(String name) {
        this.name = name;
    }

    public String code() {
        return name;
    }

}
