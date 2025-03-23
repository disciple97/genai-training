package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.api.ApiRequest;
import com.epam.training.gen.ai.api.ApiResponse;
import com.epam.training.gen.ai.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private final ChatService chatService;

    public ApiController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/chat", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse handlePostChatRequest(@RequestBody ApiRequest apiRequest) {
        logger.trace("Input API request: {}", apiRequest);
        var response = chatService.getResponse(apiRequest.getInput());
        var apiResponse = new ApiResponse(response);
        logger.trace("Output API response: {}", apiResponse);
        return apiResponse;
    }
}
