package utry.data.modular.sharding.model;

import lombok.Data;


/**
 * @author lidakai
 */
@Data
public class OrderDetail {

    private Long orderId;
    private String name;
    private String upTime;
    private String currentPage;
    private String pageSize;
}
