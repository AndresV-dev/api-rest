package com.andresv2.apirest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("v1/shoppingCart")
public class PaymentController {

    @PostMapping(value = "/add/product")
    public ResponseEntity<?> createPayment(@RequestBody HashMap<String, Object> data){

        return null;
    }

    @PostMapping(value = "payment/generate")
    public ResponseEntity<?> generatePaymentUrl(){
        return null;
    }

    @GetMapping(value = "payment/validate")
    public ResponseEntity<?> getPaymentUrlvalidate(){
        return null;
    }
}
