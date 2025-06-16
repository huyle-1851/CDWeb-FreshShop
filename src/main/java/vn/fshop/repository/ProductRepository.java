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

    // Find products that should be visible in shop (ACTIVE and PAUSED)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.status IN ('ACTIVE', 'PAUSED')")
    List<Product> findVisibleProductsWithImages();

    // Find products by category that should be visible in shop
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.category.id = :categoryId AND p.status IN ('ACTIVE', 'PAUSED')")
    List<Product> findVisibleProductsByCategoryIdWithImages(@Param("categoryId") Integer categoryId);

    // Search visible products by name only
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "p.status IN ('ACTIVE', 'PAUSED')")
    List<Product> searchVisibleProductsWithImages(@Param("searchTerm") String searchTerm);

    // Methods with eager fetching for images (admin use - shows all products)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images")
    List<Product> findAllWithImages();

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Integer id);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.category.id = :categoryId")
    List<Product> findProductsByCategoryIdWithImages(@Param("categoryId") Integer categoryId);
}
