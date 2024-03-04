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
import com.splawrence.ecommercepro.repository.OrderRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/orders")
public class OrderController {

        private OrderRepository orderRepository;

        private static final String ORDER_ITEM_NOT_FOUND_ERROR = "Order not found for Order Id: ";

        public OrderController(OrderRepository orderRepository) {
                this.orderRepository = orderRepository;
        }

        @Operation(summary = "Get all Orders")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All available Orders", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Order[].class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Order Id supplied", content = @Content) })
        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public List<Order> getOrders() {
                log.debug("OrderController.getOrders called");

                return orderRepository.findAll();
        }

        @Operation(summary = "Get an Order by Id")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Order found", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Order Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content), })
        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public Order getOrderById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
                log.debug("OrderController.getOrderById called with Order Id: {}", id);

                return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                                ORDER_ITEM_NOT_FOUND_ERROR + id));
        }

        @Operation(summary = "Save an Order")
        @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Order saved", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Order supplied", content = @Content), })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public Order postOrder(@Valid @RequestBody Order order) throws BadRequestException {
                log.debug("OrderController.postOrder called with Order: {}", order);

                order.setCreated(LocalDateTime.now());
                order.setUpdated(LocalDateTime.now());

                return orderRepository.save(order);
        }

        @Operation(summary = "Update an Order")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Order updated", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)), }),
                        @ApiResponse(responseCode = "400", description = "Bad Order or Order Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content), })
        @PutMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public Order putOrder(@PathVariable @NonNull Long id,
                        @Valid @RequestBody Order newOrderDetails)
                        throws ResourceNotFoundException, BadRequestException {
                log.debug("OrderController.putOrderById called with Order Id: {} and Order: {}", id,
                                newOrderDetails);

                // Get the existing order
                Order existingOrder = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ORDER_ITEM_NOT_FOUND_ERROR + id));
                // Update the order
                Order order = new Order();
                order.setId(existingOrder.getId());

                if (newOrderDetails.getStatus() != null) {
                        order.setStatus(newOrderDetails.getStatus());
                }
                order.setCreated(existingOrder.getCreated());
                order.setUpdated(LocalDateTime.now());

                // Save the updated order
                return orderRepository.save(order);
        }

        @Operation(summary = "Delete an Order")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Order deleted", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Bad Order Id supplied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content), })
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteOrderById(@PathVariable @NonNull Long id) {
                log.debug("OrderController.deleteOrderById called with Order Id: {}", id);

                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ORDER_ITEM_NOT_FOUND_ERROR + id));

                orderRepository.delete(order);

        }
}
