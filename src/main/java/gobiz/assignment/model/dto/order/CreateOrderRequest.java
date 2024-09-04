package gobiz.assignment.model.dto.order;

import gobiz.assignment.model.dto.product.ProductInOrderRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private String userName;
    private String address;
    private String email;
    private String phone;
    private List<ProductInOrderRequest> product;
}
