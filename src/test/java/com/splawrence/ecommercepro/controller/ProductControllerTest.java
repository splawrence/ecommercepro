package com.splawrence.ecommercepro.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splawrence.ecommercepro.exception.ResourceNotFoundException;
import com.splawrence.ecommercepro.model.Product;
import com.splawrence.ecommercepro.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  @Mock
  private WebApplicationContext webApplicationContext;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductController productController;

  private MockMvc mockMvc;

  private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found for Product Id: ";

  @BeforeEach
  void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
  }

  @Test
  void whenGetProduct_thenReturnProducts() throws Exception {
    // arrange
    List<Product> products = Arrays.asList(new Product(), new Product());

    when(productRepository.findAll()).thenReturn(products);

    // act & assert
    mockMvc
        .perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void givenProductId_whenGetProductById_thenReturnProduct() throws Exception {
    // arrange
    Long productId = 1L;

    Product product = new Product();
    product.setId(productId);
    product.setDescription("Test Product");
    product.setPrice(BigDecimal.valueOf(10.0));

    when(productRepository.findById(productId))
        .thenReturn(Optional.of(product));

    // act & assert
    mockMvc
        .perform(get("/api/products/{id}", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(productId.intValue()));
  }

  @Test
  void givenBadProductId_whenGetProductById_thenThrowResourceNotFound()
      throws Exception {
    // arrange
    Long productId = 1L;
    Product expectedProduct = new Product();
    expectedProduct.setId(productId);

    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    // act
    Exception exception = assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productController.getProductById(productId);
        });

    String actualMessage = exception.getMessage();

    // assert
    assertTrue(actualMessage.contains(PRODUCT_NOT_FOUND_MESSAGE + productId));
  }

  @Test
  void givenProduct_whenPostProduct_thenReturnProduct() throws Exception {
    // arrange
    Product product = new Product();
    product.setDescription("Test Product");
    product.setPrice(BigDecimal.valueOf(10.0));

    String jsonBody = new ObjectMapper().writeValueAsString(product);

    when(productRepository.save(any(Product.class))).thenReturn(product);

    // act & assert
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.description").value("Test Product"));
  }

  @Test
  void givenProduct_whenPutProduct_thenReturnUpdatedProduct() throws Exception {
    // arrange
    Long productId = 1L;
    Product existingProduct = new Product();
    existingProduct.setId(productId);
    existingProduct.setDescription("Test Product");
    existingProduct.setPrice(BigDecimal.valueOf(10.0));

    Product newProductDetails = new Product();
    newProductDetails.setDescription("Updated Product");
    newProductDetails.setPrice(BigDecimal.valueOf(20.0));

    Product expectedUpdatedProduct = new Product();
    expectedUpdatedProduct.setId(productId);
    expectedUpdatedProduct.setDescription("Updated Product");
    expectedUpdatedProduct.setPrice(BigDecimal.valueOf(20.0));

    String jsonBody = new ObjectMapper().writeValueAsString(newProductDetails);

    when(productRepository.findById(productId))
        .thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class)))
        .thenReturn(expectedUpdatedProduct);

    // act & assert
    mockMvc
        .perform(
            put("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.description")
                .value(expectedUpdatedProduct.getDescription()))
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.price")
                .value(expectedUpdatedProduct.getPrice()));
  }

  @Test
  void givenBadProductId_whenPutProduct_thenThrowResourceNotFound()
      throws Exception {
    // arrange
    Long productId = 1L;
    Product expectedProduct = new Product();
    expectedProduct.setId(productId);

    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    // act
    Exception exception = assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productController.putProductById(productId, expectedProduct);
        });

    String actualMessage = exception.getMessage();

    // assert
    assertTrue(actualMessage.contains(PRODUCT_NOT_FOUND_MESSAGE + productId));
  }

  @Test
  void givenProductId_whenDeleteProductById_thenReturnSuccessMessage()
      throws Exception {
    // arrange
    Long productId = 1L;
    Product existingProduct = new Product();
    existingProduct.setId(productId);

    when(productRepository.findById(productId))
        .thenReturn(Optional.of(existingProduct));

    // act & assert
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/products/{id}", productId))
        .andExpect(status().isNoContent());
  }

  @Test
  void givenBadProductId_whenDeleteProduct_thenThrowResourceNotFound()
      throws Exception {
    // arrange
    Long productId = 1L;
    Product expectedProduct = new Product();
    expectedProduct.setId(productId);

    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    // act
    Exception exception = assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productController.deleteProductById(productId);
        });

    String actualMessage = exception.getMessage();

    // assert
    assertTrue(actualMessage.contains(PRODUCT_NOT_FOUND_MESSAGE + productId));
  }
}
