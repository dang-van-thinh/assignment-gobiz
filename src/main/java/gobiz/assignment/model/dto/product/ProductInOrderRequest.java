package gobiz.assignment.model.dto.product;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductInOrderRequest {
    private String productId;
    private Integer quantity;
}
