package gobiz.assignment.model.dto.orderDetail;

import gobiz.assignment.model.dto.product.ProductDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class OrderDetailResponse {
    private String id;
    private String orderId;
    private ProductDto product;
    private Double price;
    private Integer quantity;
}
