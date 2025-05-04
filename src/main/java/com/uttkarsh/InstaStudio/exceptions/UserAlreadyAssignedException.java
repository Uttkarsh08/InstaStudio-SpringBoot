package com.uttkarsh.InstaStudio.exceptions;

public class UserAlreadyAssignedException extends RuntimeException {
    public UserAlreadyAssignedException(String s) {
        super(s);
    }
}
