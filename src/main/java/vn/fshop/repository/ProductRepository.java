package vn.fshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fshop.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByNameContainingIgnoreCase(String name);

    // Enhanced search with images
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCaseWithImages(@Param("name") String name);

    // Search by name and description
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchProductsWithImages(@Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("SELECT i.url FROM Image i WHERE i.product.id = :productId")
    List<String> findImageURLsByProductId(@Param("productId") Integer productId);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE'")
    List<Product> findActiveProducts();

    // Methods with eager fetching for images
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images")
    List<Product> findAllWithImages();

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Integer id);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.category.id = :categoryId")
    List<Product> findProductsByCategoryIdWithImages(@Param("categoryId") Integer categoryId);
}
