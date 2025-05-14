package com.chsbk.pioner_pixel.controller;

import com.chsbk.pioner_pixel.dto.TransferRequest;
import com.chsbk.pioner_pixel.service.TransferService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
@Validated
public class TransferController {

    private final TransferService svc;
    public TransferController(TransferService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<Void> transfer(@AuthenticationPrincipal UserDetails principal,
                                         @Valid @RequestBody TransferRequest req) {

        Long from = Long.parseLong(principal.getUsername());
        svc.transferUniversal(from, req.getTo(), req.getAmount());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> notFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("user is not present");
    }
}
