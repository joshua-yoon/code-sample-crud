package com.kotelking.event.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private HttpStatus httpStatus;


    public ApiException(Exception e, HttpStatus status){
        this(e,e.getMessage(),status);
    }

    public ApiException(String msg, HttpStatus httpStatus){
        super(msg);
        this.httpStatus=httpStatus;
    }

    public ApiException(Exception e, String msg, HttpStatus status){
        super(msg,e);
        this.httpStatus=status;
    }

    public HttpStatus getHttpStatus(){
        return httpStatus;
    }

}
