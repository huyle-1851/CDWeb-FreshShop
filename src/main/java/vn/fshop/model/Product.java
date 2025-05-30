package vn.fshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@Entity
public class Product {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;
    @Column(name = "discounted_price")
    private int discountedPrice;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column
    private String description;
    @OneToMany(mappedBy = "product")
    private List<Image> images;
    @Column
    private String status;
    @Column
    private int quantity;
}
