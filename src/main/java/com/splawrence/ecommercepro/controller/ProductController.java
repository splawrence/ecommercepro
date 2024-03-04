package com.splawrence.ecommercepro.controller;

import com.splawrence.ecommercepro.exception.ResourceNotFoundException;
import com.splawrence.ecommercepro.model.Product;
import com.splawrence.ecommercepro.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/products")
public class ProductController {
        ProductRepository productRepository;
        private static final String PRODUCT_NOT_FOUND_ERROR = "Product not found for Product Id: ";

        public ProductController(ProductRepository productRepository) {
                this.productRepository = productRepository;
        }

        @Operation(summary = "Get all Products")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All available Products", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Product[].class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Product Id supplied", content = @Content), })
        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public List<Product> getProducts() {
                log.debug("ProductController.getProducts called");
                
                return productRepository.findAll();
        }

        @Operation(summary = "Get a Product by Id")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Product found", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Product Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content), })
        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public Product getProductById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
                log.debug("ProductController.getProductsById called with Product Id: {}", id);

                return productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_ERROR + id));
        }

        @Operation(summary = "Save a Product")
        @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Product saved", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Product supplied", content = @Content), })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public Product postProduct(@Valid @RequestBody Product product) throws BadRequestException {
                log.debug("ProductController.postProduct called with Product: {}", product);

                product.setCreated(LocalDateTime.now());
                product.setUpdated(LocalDateTime.now());

                return productRepository.save(product);
        }

        @Operation(summary = "Update a Product")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Product updated", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Product or Product Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content), })
        @PutMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public Product putProductById(@PathVariable @NonNull Long id, @Valid @RequestBody Product newProductDetails)
                        throws ResourceNotFoundException, BadRequestException {
                log.debug("ProductController.putProductById called with Product Id: {} and Product: {}", id,
                                newProductDetails);
                
                Product existingProduct = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_ERROR + id));
                Product product = new Product();
                product.setId(existingProduct.getId());

                if (newProductDetails.getDescription() != null) {
                        product.setDescription(newProductDetails.getDescription());
                }

                if (newProductDetails.getPrice() != null) {
                        product.setPrice(newProductDetails.getPrice());
                }

                product.setCreated(existingProduct.getCreated());
                product.setUpdated(LocalDateTime.now());

                return productRepository.save(product);
        }

        @Operation(summary = "Delete a Product")
        @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Bad Product Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content), })
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteProductById(@PathVariable @NonNull Long id) {
                log.debug("ProductController.deleteProductById called with Product Id: {}", id);

                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_ERROR + id));

                productRepository.delete(product);
        }
}