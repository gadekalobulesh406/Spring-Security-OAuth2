package com.itkedu.exception;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@ControllerAdvice	
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {

        model.addAttribute("error", ex.getMessage());

        return "error";
    }
}