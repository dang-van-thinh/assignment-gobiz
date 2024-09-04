package gobiz.assignment.model.dto.order;

import gobiz.assignment.entity.enums.StatusOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderRequest {
    private StatusOrder status;
    private String address;
    private String email;
    private String phone;
}
