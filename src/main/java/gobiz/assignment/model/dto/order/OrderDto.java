package gobiz.assignment.model.dto.order;

import gobiz.assignment.entity.Orders;
import gobiz.assignment.entity.enums.StatusOrder;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OrderDto {
    private String id;
    private String userName;
    private String address;
    private String email;
    private String phone;
    private LocalDate dateCreate;
    private StatusOrder status;
    private Double payPrice;

    public static OrderDto orderDto(Orders orders){
        return OrderDto.builder()
                .id(orders.getId())
                .userName(orders.getUserName())
                .address(orders.getAddress())
                .email(orders.getEmail())
                .phone(orders.getPhone())
                .dateCreate(orders.getDateCreate())
                .status(orders.getStatus())
                .payPrice(orders.getPayPrice())
                .build();
    }
}

