package com.example.bankapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankapp.model.Account;
import com.example.bankapp.model.Transaction;
import com.example.bankapp.service.AccountService;
import com.example.bankapp.service.OllamaService;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final OllamaService ollamaService;
    private final AccountService accountService;

    public AIController(OllamaService ollamaService,
                        AccountService accountService) {
        this.ollamaService = ollamaService;
        this.accountService = accountService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(
            @AuthenticationPrincipal Account account,
            @RequestBody Map<String, String> request) {

        String message = request.get("message");

        if (message == null || message.isBlank()) {
            return Map.of("response", "Please enter a message.");
        }

        List<Transaction> transactions =
                accountService.getTransactionHistory(account);

        String response =
                ollamaService.chat(message, account, transactions);

        return Map.of("response", response);
    }
}