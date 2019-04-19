//package com.easylancer.api
//
//
//
//import org.springframework.http.HttpHeaders
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
//import kotlin.Exception
//
//
//@ControllerAdvice
//class RestResponseEntityExceptionHandler: ResponseEntityExceptionHandler() {
//
//    @ExceptionHandler(value = [(Exception::class)])
//    fun handleInternal(ex: Exception, request: WebRequest): ResponseEntity<Any> {
//        System.out.println("500 Status Code ${ex.message}");
//        val bodyOfResponse = "This should be application specific";
//        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }
//
//}