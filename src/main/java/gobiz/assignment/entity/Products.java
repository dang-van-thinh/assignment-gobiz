package gobiz.assignment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {
    @Id
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    @Column(name = "delete_at")
    private LocalDate deleteAt;
}
