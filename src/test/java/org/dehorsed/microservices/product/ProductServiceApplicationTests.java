package org.dehorsed.microservices.product;

import java.math.BigDecimal;

import org.dehorsed.microservices.product.dto.ProductRequest;
import org.dehorsed.microservices.product.model.Product;
import org.dehorsed.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import io.restassured.RestAssured;
import tools.jackson.databind.ObjectMapper;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductserviceApplicationTests {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @LocalServerPort
    private Integer serverPort;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;
        productRepository.deleteAll();
    }

    private ProductRequest createProductRequest() {
        return new ProductRequest("IPhone", "Phone", BigDecimal.valueOf(1000));
    }

    @Test
    void createProduct_withValidOutput_createsOneUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest())))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        var products = productRepository.findAll();

        Assertions.assertEquals(1, products.size());
        Assertions.assertEquals("IPhone", products.get(0).getName());
    }

    @Test
    void createProduct_withInvalidOutput_returnsBadRequest() throws Exception {
        ProductRequest request = new ProductRequest("IPhone", "Phone", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAllProducts_returnsListOfProducts() throws Exception {
        productRepository.save(new Product("IPhone", "Phone", BigDecimal.valueOf(1000)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("IPhone"));
    }

    @Test
    void getById_withExistingId_returnsProduct() throws Exception {
        Product savedProduct = productRepository.save(new Product("IPhone", "Phone", BigDecimal.valueOf(1000)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{id}", savedProduct.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("IPhone"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Phone"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(1000));
    }

    @Test
    void getById_withNonExistingId_returnsNotFound() throws Exception {
        String nonExistingId = "67c0f8a2f9b5c6d4e2a1b3c5";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{id}", nonExistingId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
