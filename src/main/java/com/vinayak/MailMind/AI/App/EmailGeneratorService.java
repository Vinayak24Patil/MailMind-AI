package com.vinayak.MailMind.AI.App;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApikey;

    public EmailGeneratorService(WebClient.Builder webClientBuidler) {
        this.webClient = webClientBuidler.build();
    }

    public String generateEmailReply(EmailRequest emailRequest){

        // Build the prompt
        String prompt = buildPrompt(emailRequest);

        // Craft Request
        Map<String, Object> requestBody=Map.of(
                "contents",new Object[]{
                        //parts
                        Map.of("parts",new Object[]{
                                //text
                                Map.of("text",prompt)
                        })
                }
        );

        // Do Request and get response
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApikey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //Extract Response and Return
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper  = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }catch (Exception e){
            return "Error processing request:" + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Write a professional, structured email reply based on the message below. ")
                .append("Start with a greeting, respond to the main point, and end with a polite closing and sign-off. ")
                .append("Do not include the subject line. Keep it concise and business-like.\n")
                .append("Original email:\n")
                .append(emailRequest.getEmailContent());


        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append(" Use a ").append(emailRequest.getTone()).append(" tone.");
        }

        prompt.append("\nOriginal email:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }

}
