package com.splawrence.ecommercepro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.splawrence.ecommercepro.exception.ResourceNotFoundException;
import com.splawrence.ecommercepro.model.ErrorMessage;
import java.time.LocalDateTime;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerControllerTest {

        @Mock
        private ResourceNotFoundException resourceNotFoundException;

        @Mock
        private BadRequestException badRequestException;

        @Mock
        private WebRequest webRequest;

        @InjectMocks
        private ExceptionHandlerController exceptionHandlerController;

        @Test
        void givenResourceNotFoundException_thenReturnNotFoundMessage() {
                // arrange
                String errorMessage = "Resource not found";
                ErrorMessage expectedErrorMessage = new ErrorMessage(
                                404,
                                LocalDateTime.now(),
                                errorMessage,
                                "Request description");
                when(resourceNotFoundException.getMessage()).thenReturn(errorMessage);
                when(webRequest.getDescription(false)).thenReturn("Request description");

                // act
                ResponseEntity<?> responseEntity = exceptionHandlerController.resourceNotFoundException(
                                resourceNotFoundException,
                                webRequest);

                // assert
                assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
                assertNotNull(responseEntity.getBody());
                assertEquals(expectedErrorMessage.getDescription(),
                                ((ErrorMessage) responseEntity.getBody()).getDescription());
        }

        @Test
        void givenBadRequestException_thenReturnBadRequestMessage() {
                // arrange
                String errorMessage = "Bad request";
                ErrorMessage expectedErrorMessage = new ErrorMessage(
                                400,
                                LocalDateTime.now(),
                                errorMessage,
                                "Bad request");
                when(badRequestException.getMessage()).thenReturn(errorMessage);
                when(webRequest.getDescription(false)).thenReturn("Bad request");

                // act
                ResponseEntity<?> responseEntity = exceptionHandlerController.badRequestException(
                                badRequestException,
                                webRequest);

                // assert
                assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
                assertNotNull(responseEntity.getBody());
                assertEquals(expectedErrorMessage.getDescription(),
                                ((ErrorMessage) responseEntity.getBody()).getDescription());
        }
}
