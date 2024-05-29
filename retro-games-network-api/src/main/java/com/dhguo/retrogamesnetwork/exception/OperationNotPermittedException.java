package com.dhguo.retrogamesnetwork.exception;

public class OperationNotPermittedException extends RuntimeException {
  public OperationNotPermittedException(String msg) {
    super(msg);
  }
}
