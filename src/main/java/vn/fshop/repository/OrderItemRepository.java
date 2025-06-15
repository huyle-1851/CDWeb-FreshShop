package vn.fshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fshop.model.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    // Find order items by order ID
    List<OrderItem> findByOrder_Id(Integer orderId);
    
    // Find order items by product ID
    List<OrderItem> findByProduct_Id(Integer productId);
    
    // Get total quantity sold for a product
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long getTotalQuantitySoldForProduct(@Param("productId") Integer productId);
    
    // Get best selling products
    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalSold " +
           "FROM OrderItem oi " +
           "GROUP BY oi.product.id, oi.product.name " +
           "ORDER BY totalSold DESC")
    List<Object[]> getBestSellingProducts();
}
