package com.splawrence.ecommercepro.controller;

import java.time.LocalDateTime;
import java.util.List;

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

import com.splawrence.ecommercepro.exception.ResourceNotFoundException;
import com.splawrence.ecommercepro.model.Order;
import com.splawrence.ecommercepro.model.OrderItem;
import com.splawrence.ecommercepro.model.Product;
import com.splawrence.ecommercepro.repository.OrderItemRepository;
import com.splawrence.ecommercepro.repository.OrderRepository;
import com.splawrence.ecommercepro.repository.ProductRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/order-items")
public class OrderItemController {
        OrderItemRepository orderItemRepository;
        ProductRepository productRepository;
        OrderRepository orderRepository;
        private static final String ORDER_ITEM_NOT_FOUND_ERROR = "OrderItem not found for OrderItem Id: ";

        public OrderItemController(OrderItemRepository orderItemRepository, ProductRepository productRepository,
                        OrderRepository orderRepository) {
                this.orderItemRepository = orderItemRepository;
                this.productRepository = productRepository;
                this.orderRepository = orderRepository;
        }

        @Operation(summary = "Get all OrderItems")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All available OrderItems", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItem[].class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad OrderItem Id supplied", content = @Content), })
        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public List<OrderItem> getOrderItems() {
                log.debug("OrderItemController.getOrderItems called");
                return orderItemRepository.findAll();
        }

        @Operation(summary = "Get an OrderItem by Id")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OrderItem found", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItem.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad OrderItem Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "OrderItem not found", content = @Content), })
        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public OrderItem getOrderItemById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
                log.debug("OrderItemController.getOrderItemsById called with OrderItem Id: {}", id);
                return orderItemRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ORDER_ITEM_NOT_FOUND_ERROR + id));
        }

        @Operation(summary = "Get OrderItems by Order Id. This is useful for seeing which Order Items are associated with a particular order.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OrderItem found or OrderItem not found", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItem.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad OrderItem Id supplied", content = @Content), })
        @GetMapping("/search/order-id/{id}")
        @ResponseStatus(HttpStatus.OK)
        public List<OrderItem> getOrderItemByOrderId(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
                log.debug("OrderItemController.getOrderItemsById called with OrderItem Id: {}", id);

                return orderItemRepository.findByOrderId(id);
        }

        @Operation(summary = "Save an OrderItem")
        @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "OrderItem saved", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItem.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad OrderItem supplied", content = @Content), })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public OrderItem postOrderItem(@Valid @RequestBody OrderItem orderItem) throws BadRequestException {
                log.debug("OrderItemController.postOrderItem called with OrderItem: {}", orderItem);
                orderItem.setCreated(LocalDateTime.now());
                orderItem.setUpdated(LocalDateTime.now());
                orderItemRepository.save(orderItem);
                Product product = productRepository.findById(orderItem.getProduct().getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                Order order = orderRepository.findById(orderItem.getOrder().getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
                orderItem.setProduct(product);
                orderItem.setOrder(order);
                return orderItem;
        }

        @Operation(summary = "Update an OrderItem")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OrderItem updated", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItem.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad OrderItem or OrderItem Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "OrderItem not found", content = @Content), })
        @PutMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public OrderItem putOrderItemById(@PathVariable @NonNull Long id,
                        @Valid @RequestBody OrderItem newOrderItemDetails)
                        throws ResourceNotFoundException, BadRequestException {
                log.debug("OrderItemController.putOrderItemById called with OrderItem Id: {} and OrderItem: {}", id,
                                newOrderItemDetails);
                OrderItem existingOrderItem = orderItemRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ORDER_ITEM_NOT_FOUND_ERROR + id));
                OrderItem orderItem = new OrderItem();
                orderItem.setId(existingOrderItem.getId());
                orderItem.setQuantity(newOrderItemDetails.getQuantity());
                orderItem.setCreated(existingOrderItem.getCreated());
                orderItem.setUpdated(LocalDateTime.now());
                return orderItemRepository.save(orderItem);
        }

        @Operation(summary = "Delete an OrderItem")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "OrderItem deleted", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Bad OrderItem Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "OrderItem not found", content = @Content), })
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteOrderItemById(@PathVariable @NonNull Long id) {
                log.debug("OrderItemController.deleteOrderItemById called with OrderItem Id: {}", id);
                OrderItem orderItem = orderItemRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ORDER_ITEM_NOT_FOUND_ERROR + id));
                orderItemRepository.delete(orderItem);
        }
}