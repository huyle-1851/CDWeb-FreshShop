package vn.fshop.modelmapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProductModel {
    private int id;
    private String name;
    private int price;
    private int discountedPrice;
    private int categoryId;
    private String description;
    private String status;
    private int quantity;
    List<MultipartFile> productImages;
    List<String> productImageUrls;
}
