package com.example.seassistant.controller;

import com.example.seassistant.service.DataSourceProcessor;
import com.example.seassistant.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    
    @Autowired
    private DataSourceProcessor dataSourceProcessor;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    /**
     * 上传PDF文档
     */
    @PostMapping("/upload/pdf")
    public void uploadPDF(@RequestParam("file") MultipartFile file) throws IOException {
        dataSourceProcessor.processPDF(file);
    }
    
    /**
     * 上传PPT文档
     */
    @PostMapping("/upload/ppt")
    public void uploadPPT(@RequestParam("file") MultipartFile file) throws IOException {
        dataSourceProcessor.processPPT(file);
    }
    
    /**
     * 上传视频
     */
    @PostMapping("/upload/video")
    public void uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        dataSourceProcessor.processVideo(file);
    }
    
    /**
     * 上传代码
     */
    @PostMapping("/upload/code")
    public void uploadCode(@RequestParam("file") MultipartFile file) throws IOException {
        dataSourceProcessor.processCode(file);
    }
    
    /**
     * 搜索知识
     */
    @GetMapping("/search")
    public List<Long> searchKnowledge(@RequestParam String query) {
        return knowledgeBaseService.searchKnowledge(query, 10);
    }
    
    /**
     * 初始化知识库
     */
    @PostMapping("/init")
    public void initKnowledgeBase() {
        knowledgeBaseService.initKnowledgeBase();
    }
} 