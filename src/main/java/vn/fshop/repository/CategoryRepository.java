package vn.fshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.fshop.model.Category;
import vn.fshop.model.Product;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Product> findProductsById(int id);
    int countProductsById(int id);
}
