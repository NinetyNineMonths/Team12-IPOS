package tests.service;

import main.db.DatabaseManager;
import main.model.Product;
import main.service.CatalogueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CatalogueServiceTest {

    private CatalogueService catalogueService;

    @BeforeEach
    void setUp() {
        DatabaseManager.initialise();
        catalogueService = new CatalogueService();
    }

    // Expected: returns all seeded catalogue products.
    @Test
    void testGetAllProducts_ReturnsExpectedNumberOfProducts() {
        List<Product> products = catalogueService.getAllProducts();

        assertNotNull(products);
        assertEquals(5, products.size(), "Catalogue should return 5 products.");
    }

    // Expected: returned catalogue includes the known seeded product IDs.
    @Test
    void testGetAllProducts_ContainsKnownProductIds() {
        List<Product> products = catalogueService.getAllProducts();
        Set<String> ids = products.stream().map(Product::getId).collect(Collectors.toSet());

        assertTrue(ids.contains("PARA001"));
        assertTrue(ids.contains("IBU002"));
        assertTrue(ids.contains("VIT003"));
        assertTrue(ids.contains("ALL004"));
        assertTrue(ids.contains("BAND005"));
    }

    // Expected: each product has valid basic data (id/name/category/price/stock).
    @Test
    void testGetAllProducts_ProductFieldsAreValid() {
        List<Product> products = catalogueService.getAllProducts();

        for (Product product : products) {
            assertNotNull(product.getId());
            assertFalse(product.getId().trim().isEmpty());
            assertNotNull(product.getName());
            assertFalse(product.getName().trim().isEmpty());
            assertNotNull(product.getCategory());
            assertFalse(product.getCategory().trim().isEmpty());
            assertTrue(product.getPrice() >= 0, "Price should not be negative.");
            assertTrue(product.getStock() >= 0, "Stock should not be negative.");
        }
    }

    // Expected: each call returns an independent list instance.
    @Test
    void testGetAllProducts_ReturnsIndependentListEachCall() {
        List<Product> firstCall = catalogueService.getAllProducts();
        List<Product> secondCall = catalogueService.getAllProducts();

        firstCall.clear();

        assertEquals(5, secondCall.size(), "Second call should not be affected by first call mutations.");
    }
}
