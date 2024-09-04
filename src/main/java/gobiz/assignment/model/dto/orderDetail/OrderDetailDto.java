package gobiz.assignment.model.dto.orderDetail;

import gobiz.assignment.entity.OrderDetail;
import gobiz.assignment.entity.Orders;
import gobiz.assignment.model.dto.order.OrderDto;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailDto {
    private String id;
    private String orderId;
    private String productId;
    private Double price;
    private Integer quantity;

    public OrderDetailDto orderDetailDto(OrderDetail orderDetail) {
        return OrderDetailDto.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getId())
                .productId(orderDetail.getId())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .build();
    }
}
