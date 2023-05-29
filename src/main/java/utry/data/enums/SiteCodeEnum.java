package utry.data.enums;

/**
 * @Author: DJ
 * @Date: 2021/1/29 10:38
 */
public enum SiteCodeEnum {

    HRM("100100"),
    SYS("100001"),
    GATEWAY("100000"),
    WORKFLOW("100021"),
    TASK("100022"),
    REPORT("100020"),
    OAM("100009"),
    FILEMANAGER("100058"),
    DATA("100060");


    /**
     * 子站编码
     */
    private String siteCode;

    SiteCodeEnum(String siteCode) {
        this.siteCode = siteCode;
    }

    public String code() {
        return siteCode;
    }

}
