package utry.data.enums;

/**
 * @Author: ldk
 * @Date: 2021/1/29 10:38
 */
public enum BaseDataEnum {

    HTTP("http://");

    private String name;

    BaseDataEnum(String name) {
        this.name = name;
    }

    public String code() {
        return name;
    }

}
