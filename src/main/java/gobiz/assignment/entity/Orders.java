package gobiz.assignment.entity;

import gobiz.assignment.entity.enums.StatusOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {
    @Id
    private String id;
    @Column(name = "user_name")
    private String userName;
    private String address;
    private String email;
    @Column(name = "phone", length = 14)
    private String phone;
    @Column(name = "date_create")
    private LocalDate dateCreate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status",length = 50)
    private StatusOrder status;
    @Column(name = "pay_price")
    private Double payPrice;
}
