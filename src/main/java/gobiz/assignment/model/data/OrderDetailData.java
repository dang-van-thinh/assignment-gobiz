package gobiz.assignment.model.data;

import gobiz.assignment.entity.OrderDetail;
import gobiz.assignment.entity.Orders;
import gobiz.assignment.entity.Products;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDetailData {
    private Orders orders;
    private OrderDetail orderDetail;
    private Products products;

    public OrderDetailData(Orders orders, OrderDetail orderDetail, Products products) {
        this.orders = orders;
        this.orderDetail = orderDetail;
        this.products = products;
    }
}
