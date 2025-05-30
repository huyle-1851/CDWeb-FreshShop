package vn.fshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.fshop.model.Product;
import vn.fshop.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(int id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByName(String namePattern) {
        return productRepository.findByNameContaining(namePattern);
    }

    public List<Product> getProductsByCategoryId(int id){
        return productRepository.findProductsByCategoryId(id);
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public List<String> getListImage(int idProduct) {
        return productRepository.findImageURLsById(idProduct);
    }

}
