package gobiz.assignment.model.dto.product;

import gobiz.assignment.entity.Products;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private Double price;
    private int quantity;
    private LocalDate deleteAt;

    public static ProductDto productDto(Products products){
        return ProductDto.builder()
                .id(products.getId())
                .name(products.getName())
                .description(products.getDescription())
                .price(products.getPrice())
                .quantity(products.getQuantity())
                .deleteAt(products.getDeleteAt())
                .build();
    }
}
