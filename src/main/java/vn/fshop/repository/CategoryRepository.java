package vn.fshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.fshop.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    int countProductsById(@Param("categoryId") Integer categoryId);
}
