package com.uttkarsh.InstaStudio.exceptions;

public class AdminAlreadyAssignedException extends RuntimeException {
    public AdminAlreadyAssignedException(String message) {
        super(message);
    }
}