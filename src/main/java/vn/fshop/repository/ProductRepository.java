package vn.fshop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fshop.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findById(int ID);
    List<Product> findByNameContaining(String name);
    List<Product> findAll();
    List<String> findImageURLsById(int id);
    List<Product> findProductsByCategoryId(int id);
}
