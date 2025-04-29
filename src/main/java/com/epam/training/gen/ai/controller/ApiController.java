package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.api.ApiRequest;
import com.epam.training.gen.ai.api.ApiResponse;
import com.epam.training.gen.ai.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ApiController {

    private final ChatService chatService;

    public ApiController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping(value = "/deployment-names", produces = APPLICATION_JSON_VALUE)
    public Set<String> getDeploymentNames() {
        var deploymentNames = chatService.getDeploymentNames();
        log.trace("Deployment names: {}", deploymentNames);
        return deploymentNames;
    }

    @GetMapping(value = "/reset-history")
    public void resetChatHistory() {
        chatService.resetChatHistory();
        log.trace("Chat history has been reset");
    }

    @PostMapping(value = "/{deploymentName}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse handlePostChatRequest(@PathVariable String deploymentName, @RequestBody ApiRequest apiRequest) {
        log.trace("Input API request: {}", apiRequest);
        var response = chatService.getResponse(deploymentName, apiRequest.getInput());
        var apiResponse = new ApiResponse(response);
        log.trace("Output API response: {}", apiResponse);
        return apiResponse;
    }

    @PostMapping(value = "/{deploymentName}/rag", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse handlePostChatRagRequest(@PathVariable String deploymentName, @RequestBody ApiRequest apiRequest) {
        log.trace("Input API RAG request: {}", apiRequest);
        var response = chatService.getRagResponse(deploymentName, apiRequest.getInput());
        var apiResponse = new ApiResponse(response);
        log.trace("Output API RAG response: {}", apiResponse);
        return apiResponse;
    }
}
