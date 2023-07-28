package com.aml.controller;

import com.aml.dto.ProductDto;
import com.aml.service.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Value("${app.message}")
    private String message;

    @PostMapping("/infor")
    public ResponseEntity<String> publishProductDetails(@RequestBody ProductDto product) {
        rabbitMQSender.send(product);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
