package com.example.shortlinkapplication.exception;

public class KeyNotFoundException extends Exception{
    public KeyNotFoundException(String message) {
        super("Shorten link not found: " + message);
    }
}
