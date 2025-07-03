package com.maneth.zikhron.exception;

//A custom exception like OurException makes your errors clearer and gives you more control over how to handle them. This is especially useful in bigger apps where you want to separate your app’s errors from Java’s built-in errors.
public class OurException extends RuntimeException{
    public OurException(String message){
        super(message);
    }
}
