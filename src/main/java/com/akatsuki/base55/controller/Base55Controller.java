package com.akatsuki.base55.controller;

import com.akatsuki.base55.dto.AiRequest;
import com.akatsuki.base55.dto.AiResponse;
import com.akatsuki.base55.service.Base55Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/base55")
public class Base55Controller {

    private final Base55Service base55Service;

    public Base55Controller(Base55Service base55Service) {
        this.base55Service = base55Service;
    }

    @PostMapping("/ask")
    public AiResponse askLlm(@RequestBody AiRequest request) {
        return base55Service.askLlm(request);
    }

    @GetMapping("/tools")
    public String getTools(){
        return base55Service.getTools();
    }
}
