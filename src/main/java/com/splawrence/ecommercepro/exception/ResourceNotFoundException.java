package com.splawrence.ecommercepro.exception;

public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -2196715886663105664L;

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
