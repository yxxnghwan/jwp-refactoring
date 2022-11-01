package kitchenpos.application.dto;

import java.math.BigDecimal;
import kitchenpos.domain.product.Product;

public class ProductDto {

    private final Long id;
    private final String name;
    private final BigDecimal price;

    private ProductDto(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static ProductDto from(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}