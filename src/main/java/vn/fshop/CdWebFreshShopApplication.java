package vn.fshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import vn.fshop.service.UserService;
import vn.fshop.service.CategoryService;
import vn.fshop.service.ProductService;
import vn.fshop.model.Product;
import vn.fshop.model.Image;
import vn.fshop.repository.ImageRepository;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "vn.fshop.repository")
@EntityScan(basePackages = "vn.fshop.model")
public class CdWebFreshShopApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ImageRepository imageRepository;

	public static void main(String[] args) {
		SpringApplication.run(CdWebFreshShopApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Initialize default admin user if none exists
		userService.createDefaultAdminIfNotExists();

		// Initialize sample data if none exists
		initializeSampleData();
	}

	private void initializeSampleData() {
		try {
			// Create categories if none exist
			if (categoryService.getAllCategory().isEmpty()) {
				var category1 = categoryService.createCategory("Rau củ quả");
				var category2 = categoryService.createCategory("Trái cây");
				var category3 = categoryService.createCategory("Thịt cá");
				var category4 = categoryService.createCategory("Đồ uống");

				// Create sample products with images
				List<Product> existingProducts = productService.getAllProduct();
				if (existingProducts.isEmpty()) {
					createSampleProductsWithImages(category1, category2, category3, category4);
				}
			}
		} catch (Exception e) {
			System.err.println("Error initializing sample data: " + e.getMessage());
		}
	}

	private void createSampleProductsWithImages(vn.fshop.model.Category category1, vn.fshop.model.Category category2,
												vn.fshop.model.Category category3, vn.fshop.model.Category category4) {
		// Fruits (Trái cây)
		Product apple = productService.createProduct("Táo Fuji New Zealand", "Táo Fuji nhập khẩu từ New Zealand, giòn ngọt", 45000, 40000, category2.getId(), true);
		addImageToProduct(apple, "/images/products/Tao Fuji Newzealand - 1kg.jpg");

		Product orange = productService.createProduct("Cam Sành", "Cam sành tươi ngon, nhiều nước", 35000, 30000, category2.getId(), true);
		addImageToProduct(orange, "/images/products/Cam Sanh - 1Kg.jpg");

		Product banana = productService.createProduct("Chuối Sứ", "Chuối sứ chín vàng, thơm ngon", 20000, 18000, category2.getId(), true);
		addImageToProduct(banana, "/images/products/Chuoi Su - 1kg.jpg");

		Product watermelon = productService.createProduct("Dưa Hấu Không Hạt", "Dưa hấu không hạt, ngọt mát", 25000, 22000, category2.getId(), true);
		addImageToProduct(watermelon, "/images/Dua Hau Khong Hat SEL Organicfood - 3kg.jpg");

		Product pineapple = productService.createProduct("Thơm (Dứa)", "Thơm tươi ngon, thơm ngọt", 30000, 28000, category2.getId(), true);
		addImageToProduct(pineapple, "/images/Thom (Dua) - 1 trai.jpg");

		Product grape = productService.createProduct("Nho Xanh Ninh Thuận", "Nho xanh organic từ Ninh Thuận", 80000, 75000, category2.getId(), true);
		addImageToProduct(grape, "/images/Nho Xanh Ninh Thuan OrganicFood - 1kg.jpg");

		Product kiwi = productService.createProduct("Kiwi Vàng", "Kiwi vàng nhập khẩu, giàu vitamin C", 120000, 110000, category2.getId(), true);
		addImageToProduct(kiwi, "/images/Kiwi vang - 500g.jpg");

		Product mango = productService.createProduct("Xoài Cát Chu", "Xoài cát chu ngọt thơm", 55000, 50000, category2.getId(), true);
		addImageToProduct(mango, "/images/Xoai Cat Chu - 1kg.jpg");

		// Vegetables (Rau củ quả)
		Product beef = productService.createProduct("Bò Úc Organic", "Thịt bò Úc organic, tươi ngon", 180000, 170000, category3.getId(), true);
		addImageToProduct(beef, "/images/Bo Booth Org - 1kg.jpg");

		Product beef2 = productService.createProduct("Bò Sạp Giống Cỏ", "Thịt bò sạp giống cỏ, chất lượng cao", 200000, 190000, category3.getId(), true);
		addImageToProduct(beef2, "/images/Bo Sap Giong Co - 1Kg.jpg");

		// Add more products to have a good variety
		Product papaya = productService.createProduct("Đu Đủ Ruột Vàng", "Đu đủ ruột vàng, ngọt mát", 25000, 22000, category2.getId(), true);
		addImageToProduct(papaya, "/images/Du Du Ruot Vang - 1kg.jpg");

		Product persimmon = productService.createProduct("Hồng Giòn Hàn Quốc", "Hồng giòn nhập khẩu từ Hàn Quốc", 90000, 85000, category2.getId(), true);
		addImageToProduct(persimmon, "/images/Hong Gion Han Quoc - Tui 1kg.jpg");
	}



	private void addImageToProduct(Product product, String imageUrl) {
		try {
			Image image = new Image(product, imageUrl);
			imageRepository.save(image);
		} catch (Exception e) {
			System.err.println("Error adding image to product " + product.getName() + ": " + e.getMessage());
		}
	}


}
