package com.example.assistant.controller;

import com.example.assistant.service.agent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
public class AgentController {
    @Autowired
    private ConceptAgent conceptAgent;
    
    @Autowired
    private RequirementAgent requirementAgent;
    
    @Autowired
    private DesignAgent designAgent;
    
    @Autowired
    private TestAgent testAgent;
    
    @Autowired
    private CodeReviewAgent codeReviewAgent;
    
    @PostMapping("/concept")
    public ResponseEntity<String> explainConcept(@RequestBody String concept) {
        String result = conceptAgent.explainConcept(concept);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/requirement")
    public ResponseEntity<String> analyzeRequirement(@RequestBody String requirement) {
        String result = requirementAgent.analyzeRequirement(requirement);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/design")
    public ResponseEntity<String> generateDesign(@RequestBody String requirement) {
        String result = designAgent.generateDesign(requirement);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/test")
    public ResponseEntity<String> generateTestCases(@RequestBody String code) {
        String result = testAgent.generateTestCases(code);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/review")
    public ResponseEntity<String> reviewCode(@RequestBody String code) {
        String result = codeReviewAgent.reviewCode(code);
        return ResponseEntity.ok(result);
    }
} 