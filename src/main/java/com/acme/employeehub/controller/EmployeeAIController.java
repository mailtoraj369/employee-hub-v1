package com.acme.employeehub.controller;

import com.acme.employeehub.service.ai.EmployeeHubAiAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hub/ai-agent")
public class EmployeeAIController {

    private EmployeeHubAiAgent employeeHubAiAgent;

    public EmployeeAIController(EmployeeHubAiAgent employeeHubAiAgent) {
        this.employeeHubAiAgent = employeeHubAiAgent;
    }

    @GetMapping
    public String chat(@RequestParam String message) {
        return employeeHubAiAgent.chat(message);
    }
}
