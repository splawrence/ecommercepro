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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splawrence.ecommercepro.exception.ResourceNotFoundException;
import com.splawrence.ecommercepro.model.Order;
import com.splawrence.ecommercepro.repository.OrderRepository;
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

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

  @Mock
  private WebApplicationContext webApplicationContext;

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderController orderController;

  private MockMvc mockMvc;

  private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found for Order Id: ";

  @BeforeEach
  void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
  }

  @Test
  void whenGetOrders_thenReturnOrders() throws Exception {
    // arrange
    Order order1 = new Order();
    Order order2 = new Order();
    List<Order> expectedOrders = Arrays.asList(order1, order2);

    when(orderRepository.findAll()).thenReturn(expectedOrders);

    // act & assert
    mockMvc
        .perform(get("/api/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void givenOrderId_whenGetOrderById_thenReturnOrder() throws Exception {
    // arrange
    Long orderId = 1L;
    Order expectedOrder = new Order();
    expectedOrder.setId(orderId);

    when(orderRepository.findById(orderId))
        .thenReturn(Optional.of(expectedOrder));

    // act & assert
    mockMvc
        .perform(get("/api/orders/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId.intValue()));
  }

  @Test
  void givenBadOrderId_whenGetOrderById_thenThrowResourceNotFound()
      throws Exception {
    // arrange
    Long orderId = 1L;
    Order expectedOrder = new Order();
    expectedOrder.setId(orderId);

    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // act
    Exception exception = assertThrows(
        ResourceNotFoundException.class,
        () -> {
          orderController.getOrderById(orderId);
        });

    String actualMessage = exception.getMessage();

    // assert
    assertTrue(actualMessage.contains(ORDER_NOT_FOUND_MESSAGE + orderId));
  }

  @Test
  void givenOrder_whenPostOrder_thenReturnOrder() throws Exception {
    // arrange
    Order createdOrder = new Order();
    createdOrder.setId(1L);
    createdOrder.setStatus("New");

    String jsonBody = new ObjectMapper().writeValueAsString(createdOrder);

    when(orderRepository.save(any(Order.class))).thenReturn(createdOrder);

    // act & assert
    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("New"));
  }

  @Test
  void givenOrder_whenPutOrder_thenReturnUpdatedOrder() throws Exception {
    // arrange
    Long orderId = 1L;
    Order existingOrder = new Order();
    existingOrder.setId(orderId);
    existingOrder.setStatus("New");

    Order newOrderDetails = new Order();
    newOrderDetails.setStatus("Shipped");

    Order expectedUpdatedOrder = new Order();
    expectedUpdatedOrder.setId(orderId);
    expectedUpdatedOrder.setStatus("Shipped");

    String jsonBody = new ObjectMapper().writeValueAsString(newOrderDetails);
    when(orderRepository.findById(orderId))
        .thenReturn(Optional.of(existingOrder));
    when(orderRepository.save(any(Order.class)))
        .thenReturn(expectedUpdatedOrder);

    // act & assert
    mockMvc
        .perform(
            put("/api/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.id")
                .value(expectedUpdatedOrder.getId()))
        .andExpect(
            MockMvcResultMatchers
                .jsonPath("$.status")
                .value(expectedUpdatedOrder.getStatus()));
  }

  @Test
  void givenBadOrderId_whenPutOrder_thenThrowResourceNotFound()
      throws Exception {
    // arrange
    Long orderId = 1L;
    Order expectedOrder = new Order();
    expectedOrder.setId(orderId);

    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // act
    Exception exception = assertThrows(
        ResourceNotFoundException.class,
        () -> {
          orderController.putOrder(orderId, expectedOrder);
        });

    String actualMessage = exception.getMessage();

    // assert
    assertTrue(actualMessage.contains(ORDER_NOT_FOUND_MESSAGE + orderId));
  }

  @Test
  void givenOrderId_whenDeleteOrderById_thenReturnSuccessMessage()
      throws Exception {
    // arrange
    Long orderId = 1L;
    Order expectedOrder = new Order();
    expectedOrder.setId(orderId);

    when(orderRepository.findById(orderId))
        .thenReturn(Optional.of(expectedOrder));

    // act & assert
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/orders/{id}", orderId))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  void givenBadOrderId_whenDeleteOrderById_thenThrowResourceNotFound()
      throws Exception {
    // arrange
    Long orderId = 1L;
    Order expectedOrder = new Order();
    expectedOrder.setId(orderId);

    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // act
    Exception exception = assertThrows(
        ResourceNotFoundException.class,
        () -> {
          orderController.deleteOrderById(orderId);
        });

    String actualMessage = exception.getMessage();

    // assert
    assertTrue(actualMessage.contains(ORDER_NOT_FOUND_MESSAGE + orderId));
  }
}
