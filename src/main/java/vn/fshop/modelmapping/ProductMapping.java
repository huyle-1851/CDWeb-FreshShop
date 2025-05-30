package vn.fshop.modelmapping;
import java.util.List;

import vn.fshop.model.Product;
public class ProductMapping {

public static ProductModel toProductModel(Product product){
    ProductModel productModel = new ProductModel();
    productModel.setId(product.getId());
    productModel.setName(product.getName());
    productModel.setPrice(product.getPrice());
    productModel.setDiscountedPrice(product.getDiscountedPrice());
    productModel.setCategoryId(product.getCategory().getId());
    productModel.setDescription(product.getDescription());
    productModel.setStatus(product.getStatus());
    productModel.setQuantity(product.getQuantity());
    productModel.setProductImageUrls(product.getImages().stream().map(x -> x.getUrl()).toList());
    return productModel;
}
    public static List<ProductModel> toListProductModel(List<Product> models) {
        return models.stream().map(ProductMapping::toProductModel).toList();
    }
}
