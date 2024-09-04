package gobiz.assignment.model.dto.order;

import gobiz.assignment.entity.enums.StatusOrder;
import gobiz.assignment.model.dto.orderDetail.OrderDetailResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {
    private String id;
    private String userName;
    private String address;
    private String email;
    private String phone;
    private LocalDate dateCreate;
    private StatusOrder status;
    private Double payPrice;
    List<OrderDetailResponse> orderDetail;
}
