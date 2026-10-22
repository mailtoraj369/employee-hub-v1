package com.acme.employeehub.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface EmployeeHubAiAgent {

    @SystemMessage("""
             You are an employee information assistant.
             Answer questions using the provided employee tools.
             Never invent employee details.
             Only return information supplied by the tools.
             Do not expose confidential employee information.
             If the available data is insufficient, say so.
             Keep your response concise and helpful.
            """)
    String chat(String message);
}
