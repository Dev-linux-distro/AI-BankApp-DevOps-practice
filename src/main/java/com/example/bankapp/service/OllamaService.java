package com.example.bankapp.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.bankapp.model.Account;
import com.example.bankapp.model.Transaction;

@Service
public class OllamaService {

    private final RestClient restClient;

    @Value("${ollama.url}")
    private String ollamaUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public OllamaService() {
        this.restClient = RestClient.builder().build();
    }

    public String chat(
            String userMessage,
            Account account,
            List<Transaction> transactions) {

        StringBuilder transactionData = new StringBuilder();

        for (Transaction transaction : transactions) {

            transactionData.append(
                    String.format(
                            "- %s | Amount: %s | Date: %s%n",
                            transaction.getType(),
                            transaction.getAmount(),
                            transaction.getTimestamp()
                    )
            );
        }

        if (transactionData.length() == 0) {
            transactionData.append("No transactions found.");
        }

        String prompt = """
                You are the AI banking assistant for BankApp.

                You are helping the currently logged-in user.

                User:
                %s

                Current balance:
                %s

                User's transaction history:
                %s

                User question:
                %s

                Instructions:
                - Answer using the banking information provided above.
                - Do not invent transactions, balances, dates, or amounts.
                - If the requested information is not available, clearly say so.
                - Keep the answer concise and easy to understand.
                """.formatted(
                account.getUsername(),
                account.getBalance(),
                transactionData,
                userMessage
        );

        Map<String, Object> request = Map.of(
                "model", ollamaModel,
                "messages", new Object[]{
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                },
                "stream", false
        );

        Map<?, ?> response = restClient.post()
                .uri(ollamaUrl + "/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("message") == null) {
            return "Sorry, I couldn't get a response from the AI.";
        }

        Map<?, ?> responseMessage =
                (Map<?, ?>) response.get("message");

        Object content = responseMessage.get("content");

        return content != null
                ? content.toString()
                : "Sorry, the AI returned an empty response.";
    }
}