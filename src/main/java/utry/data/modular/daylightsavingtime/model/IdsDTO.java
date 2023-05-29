package utry.data.modular.daylightsavingtime.model;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: DJ
 * @Date: 2021/2/18 13:10
 */
public class IdsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "ids", value = "ids", required = true)
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
