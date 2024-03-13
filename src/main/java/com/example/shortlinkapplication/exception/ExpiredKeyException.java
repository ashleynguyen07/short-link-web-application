package com.example.shortlinkapplication.exception;

public class ExpiredKeyException extends Exception{
    public ExpiredKeyException() {
        super("Shorten URL expired");
    }
}
