package vn.fshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.fshop.model.Category;
import vn.fshop.model.Product;
import vn.fshop.model.Image;
import vn.fshop.repository.CategoryRepository;
import vn.fshop.repository.ProductRepository;
import vn.fshop.repository.ImageRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Product getProductById(Integer id) {
        return productRepository.findByIdWithImages(id).orElse(null);
    }

    public List<Product> getProductsByName(String namePattern) {
        if (namePattern == null || namePattern.trim().isEmpty()) {
            return getAllProduct();
        }
        return productRepository.searchProductsWithImages(namePattern.trim());
    }

    public List<Product> getProductsByCategoryId(Integer categoryId) {
        return productRepository.findProductsByCategoryIdWithImages(categoryId);
    }

    public List<Product> getAllProduct() {
        return productRepository.findAllWithImages();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    public List<String> getProductImages(Integer productId) {
        return productRepository.findImageURLsByProductId(productId);
    }

    // Admin functions for product management
    public Product createProduct(String name, String description, double price, double discountedPrice, Integer categoryId, boolean active) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice((int) price);
        product.setDiscountedPrice(discountedPrice > 0 ? (int) discountedPrice : (int) price);

        // Set category by finding it
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            product.setCategory(category);
        }

        product.setStatus(active ? "ACTIVE" : "INACTIVE");
        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, String name, String description, double price, double discountedPrice, Integer categoryId, boolean active) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice((int) price);
        product.setDiscountedPrice(discountedPrice > 0 ? (int) discountedPrice : (int) price);

        // Set category by finding it
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            product.setCategory(category);
        }

        product.setStatus(active ? "ACTIVE" : "INACTIVE");
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public Product toggleProductStatus(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        boolean isActive = "ACTIVE".equals(product.getStatus());
        product.setStatus(isActive ? "INACTIVE" : "ACTIVE");
        return productRepository.save(product);
    }

    public String saveProductImage(Integer productId, org.springframework.web.multipart.MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                return null;
            }

            // Create products directory if it doesn't exist
            String uploadDir = "src/main/resources/static/images/products/";
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
                System.out.println("Created directory: " + uploadPath);
            }

            // Get original filename and extension
            String originalFilename = image.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                originalFilename = "product_image.jpg";
            }

            // Extract file extension
            String fileExtension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileExtension = originalFilename.substring(lastDotIndex);
            } else {
                fileExtension = ".jpg"; // Default extension
            }

            // Create base filename (without extension)
            String baseFilename = originalFilename;
            if (lastDotIndex > 0) {
                baseFilename = originalFilename.substring(0, lastDotIndex);
            }

            // Handle duplicate names by adding counter
            String finalFilename = originalFilename;
            java.nio.file.Path targetPath = uploadPath.resolve(finalFilename);
            int counter = 1;

            while (java.nio.file.Files.exists(targetPath)) {
                finalFilename = baseFilename + "_" + counter + fileExtension;
                targetPath = uploadPath.resolve(finalFilename);
                counter++;
            }

            // Copy the file to the target location
            java.nio.file.Files.copy(image.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Return the URL path for database storage
            String imageUrl = "/images/products/" + finalFilename;
            System.out.println("Image saved successfully: " + imageUrl);

            return imageUrl;
        } catch (Exception e) {
            System.err.println("Failed to save image: " + e.getMessage());
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void addImageToProduct(Product product, String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image image = new Image(product, imageUrl);
                imageRepository.save(image);
                System.out.println("Added image to product " + product.getName() + ": " + imageUrl);
            }
        } catch (Exception e) {
            System.err.println("Error adding image to product " + product.getName() + ": " + e.getMessage());
            throw new RuntimeException("Failed to add image to product: " + e.getMessage());
        }
    }

    public Product createProductWithImage(String name, String description, double price, double discountedPrice,
                                        Integer categoryId, boolean active, org.springframework.web.multipart.MultipartFile image) {
        try {
            // Create the product first
            Product product = createProduct(name, description, price, discountedPrice, categoryId, active);

            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                String imageUrl = saveProductImage(product.getId(), image);
                if (imageUrl != null) {
                    addImageToProduct(product, imageUrl);
                }
            }

            return product;
        } catch (Exception e) {
            System.err.println("Error creating product with image: " + e.getMessage());
            throw new RuntimeException("Failed to create product with image: " + e.getMessage());
        }
    }

    public Product updateProductWithImage(Integer id, String name, String description, double price, double discountedPrice,
                                        Integer categoryId, boolean active, org.springframework.web.multipart.MultipartFile image) {
        try {
            // Update the product first
            Product product = updateProduct(id, name, description, price, discountedPrice, categoryId, active);

            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                String imageUrl = saveProductImage(product.getId(), image);
                if (imageUrl != null) {
                    addImageToProduct(product, imageUrl);
                }
            }

            return product;
        } catch (Exception e) {
            System.err.println("Error updating product with image: " + e.getMessage());
            throw new RuntimeException("Failed to update product with image: " + e.getMessage());
        }
    }
}
