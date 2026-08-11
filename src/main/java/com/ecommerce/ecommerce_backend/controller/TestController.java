package com.ecommerce.ecommerce_backend.controller;


import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        return "E-Commerce Backend is running!";
    }
    @PostMapping("/api")
    public String test1(){
        return "Data is saved succesfully!";
    }
    @PutMapping("/api/product")
    public String test2(){
        return "Data Updated successully!";
    }
@DeleteMapping("/api")
    public String test3(){
        return "Deleted Successfully!";
}

}
