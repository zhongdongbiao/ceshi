package utry.data.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: DJ
 * @Date: 2021/1/28 17:20
 */
@ApiModel(value = "分页对象" )
public class Page<T> {

    @ApiModelProperty(name="page",value="当前页")
    private Integer page = 1;

    @ApiModelProperty(name="size",value="每页展示多少条数据")
    private Integer size = 10;

    @ApiModelProperty(name="pageData",value="查询条件")
    private T pageData;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public T getPageData() {
        return pageData;
    }

    public void setPageData(T pageData) {
        this.pageData = pageData;
    }
}
