package com.splawrence.ecommercepro.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
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
import com.splawrence.ecommercepro.model.Order;
import com.splawrence.ecommercepro.model.OrderItem;
import com.splawrence.ecommercepro.model.Product;
import com.splawrence.ecommercepro.repository.OrderItemRepository;
import com.splawrence.ecommercepro.repository.OrderRepository;
import com.splawrence.ecommercepro.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderItemControllerTest {

    @Mock
    private WebApplicationContext webApplicationContext;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemController orderItemController;

    private MockMvc mockMvc;
    private static final String ORDER_ITEM_NOT_FOUND_MESSAGE = "OrderItem not found for OrderItem Id: ";

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(orderItemController).build();
    }

    @Test
    void whenGetOrderItem_thenReturnOrderItems() throws Exception {
        // arrange
        OrderItem order1 = new OrderItem();
        OrderItem order2 = new OrderItem();
        List<OrderItem> expectedOrderItems = Arrays.asList(order1, order2);

        // act
        when(orderItemRepository.findAll()).thenReturn(expectedOrderItems);

        // assert
        mockMvc.perform(get("/api/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void givenOrderItemId_whenGetOrderItemById_thenReturnOrderItem()
            throws Exception {
        // arrange
        Long orderId = 1L;
        OrderItem expectedOrderItem = new OrderItem();
        expectedOrderItem.setId(orderId);

        when(orderItemRepository.findById(orderId))
                .thenReturn(Optional.of(expectedOrderItem));

        // act & assert
        mockMvc.perform(get("/api/order-items/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.intValue()));
    }

    @Test
    void givenBadOrderItemId_whenGetOrderItemById_thenThrowResourceNotFound()
            throws Exception {
        // arrange
        Long orderItemId = 1L;
        OrderItem expectedOrderItem = new OrderItem();
        expectedOrderItem.setId(orderItemId);

        when(orderItemRepository.findById(orderItemId))
                .thenReturn(Optional.empty());

        // act
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderItemController.getOrderItemById(orderItemId);
        });

        String actualMessage = exception.getMessage();

        // assert
        assertTrue(actualMessage.contains(ORDER_ITEM_NOT_FOUND_MESSAGE + orderItemId));
    }

    @Test
    void givenOrderItem_whenPostOrderItem_thenReturnOrderItem() throws Exception {
        // arrange
        Optional<Product> product = Optional.of(new Product());
        product.get().setId(1L);

        Optional<Order> order = Optional.of(new Order());
        order.get().setId(1L);
        OrderItem createdOrderItem = new OrderItem();
        createdOrderItem.setId(1L);
        createdOrderItem.setQuantity(1);

        createdOrderItem.setOrder(order.get());
        createdOrderItem.setProduct(product.get());

        String jsonBody = new ObjectMapper().writeValueAsString(createdOrderItem);

        when(productRepository.findById(anyLong()))
                .thenReturn(product);
        when(orderRepository.findById(anyLong()))
                .thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(createdOrderItem);

        // act & assert
        mockMvc.perform(post("/api/order-items").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.order.id").value(1L)).andExpect(jsonPath("$.product.id").value(1L));
    }

    @Test
    void givenOrderItem_whenPutOrderItem_thenReturnUpdatedOrderItem()
            throws Exception {
        // arrange
        Long orderId = 1L;
        OrderItem existingOrderItem = new OrderItem();
        existingOrderItem.setId(orderId);
        existingOrderItem.setQuantity(2);

        OrderItem newOrderItemDetails = new OrderItem();
        newOrderItemDetails.setQuantity(1);

        OrderItem expectedUpdatedOrderItem = new OrderItem();
        expectedUpdatedOrderItem.setId(orderId);
        expectedUpdatedOrderItem.setQuantity(1);

        String jsonBody = new ObjectMapper()
                .writeValueAsString(newOrderItemDetails);
        when(orderItemRepository.findById(orderId))
                .thenReturn(Optional.of(existingOrderItem));
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(expectedUpdatedOrderItem);

        // act & assert
        mockMvc.perform(put("/api/order-items/{id}", orderId).contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedUpdatedOrderItem.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(expectedUpdatedOrderItem.getQuantity()));
    }

    @Test
    void givenBadOrderItemId_whenPutOrderItem_thenThrowResourceNotFound()
            throws Exception {
        // arrange
        Long orderItemId = 1L;
        OrderItem expectedOrderItem = new OrderItem();
        expectedOrderItem.setId(orderItemId);

        when(orderItemRepository.findById(orderItemId))
                .thenReturn(Optional.empty());

        // act
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderItemController.putOrderItemById(orderItemId, expectedOrderItem);
        });

        String actualMessage = exception.getMessage();

        // assert
        assertTrue(actualMessage.contains(ORDER_ITEM_NOT_FOUND_MESSAGE + orderItemId));
    }

    @Test
    void givenOrderItemId_whenDeleteOrderItemById_thenReturnSuccessMessage()
            throws Exception {
        // arrange
        Long orderId = 1L;
        OrderItem expectedOrderItem = new OrderItem();
        expectedOrderItem.setId(orderId);

        when(orderItemRepository.findById(orderId))
                .thenReturn(Optional.of(expectedOrderItem));

        // act & assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/order-items/{id}", orderId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void givenBadOrderItemId_whenDeleteOrderItem_thenThrowResourceNotFound()
            throws Exception {
        // arrange
        Long orderItemId = 1L;
        OrderItem expectedOrderItem = new OrderItem();
        expectedOrderItem.setId(orderItemId);

        when(orderItemRepository.findById(orderItemId))
                .thenReturn(Optional.empty());

        // act
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderItemController.deleteOrderItemById(orderItemId);
        });
        String actualMessage = exception.getMessage();

        // assert
        assertTrue(actualMessage.contains(ORDER_ITEM_NOT_FOUND_MESSAGE + orderItemId));
    }

    @Test
    void givenOrderId_whenGetOrderItemByOrderId_thenReturnOrderItems() throws Exception {
        // arrange
        Long orderId = 1L;
        OrderItem order1 = new OrderItem();
        OrderItem order2 = new OrderItem();
        List<OrderItem> expectedOrderItems = Arrays.asList(order1, order2);

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(expectedOrderItems);

        // act & assert
        mockMvc.perform(get("/api/order-items/search/order-id/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}