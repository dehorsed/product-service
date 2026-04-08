package org.dehorsed.productservice;

import java.math.BigDecimal;

import org.dehorsed.productservice.dto.ProductRequest;
import org.dehorsed.productservice.model.Product;
import org.dehorsed.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import tools.jackson.databind.ObjectMapper;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ProductserviceApplicationTests {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void createProduct_withValidOutput_createsOneUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product").contentType(MediaType.APPLICATION_JSON)
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

    private ProductRequest createProductRequest() {
        return new ProductRequest("IPhone", "Phone", BigDecimal.valueOf(1000));
    }

    @Test
    void getById_withExistingId_returnsProduct() throws Exception {
        // Given
        Product savedProduct = productRepository.save(new Product("IPhone", "Phone", BigDecimal.valueOf(1000)));

        // When & Then
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
