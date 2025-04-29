package com.example.seassistant.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataSourceProcessor {
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    /**
     * 处理PDF文档
     */
    public void processPDF(MultipartFile file) throws IOException {
        // 1. 保存文件
        String fileName = saveFile(file);
        
        // 2. 提取文本
        String text = extractPDFText(fileName);
        
        // 3. 添加到知识库
        knowledgeBaseService.addKnowledge(text);
        
        // 4. 删除临时文件
        deleteFile(fileName);
    }
    
    /**
     * 处理PPT文档
     */
    public void processPPT(MultipartFile file) throws IOException {
        // 1. 保存文件
        String fileName = saveFile(file);
        
        // 2. 提取文本
        String text = extractPPTText(fileName);
        
        // 3. 添加到知识库
        knowledgeBaseService.addKnowledge(text);
        
        // 4. 删除临时文件
        deleteFile(fileName);
    }
    
    /**
     * 处理视频字幕
     */
    public void processVideo(MultipartFile file) throws IOException {
        // 1. 保存文件
        String fileName = saveFile(file);
        
        // 2. 提取字幕
        String text = extractVideoSubtitles(fileName);
        
        // 3. 添加到知识库
        knowledgeBaseService.addKnowledge(text);
        
        // 4. 删除临时文件
        deleteFile(fileName);
    }
    
    /**
     * 处理代码示例
     */
    public void processCode(MultipartFile file) throws IOException {
        // 1. 保存文件
        String fileName = saveFile(file);
        
        // 2. 提取代码和注释
        String text = extractCodeAndComments(fileName);
        
        // 3. 添加到知识库
        knowledgeBaseService.addKnowledge(text);
        
        // 4. 删除临时文件
        deleteFile(fileName);
    }
    
    /**
     * 保存上传的文件
     */
    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get("temp", fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return path.toString();
    }
    
    /**
     * 删除临时文件
     */
    private void deleteFile(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
    }
    
    /**
     * 从PDF提取文本
     */
    private String extractPDFText(String fileName) throws IOException {
        try (PDDocument document = PDDocument.load(new File(fileName))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * 从PPT提取文本
     */
    private String extractPPTText(String fileName) throws IOException {
        StringBuilder text = new StringBuilder();
        try (XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(fileName))) {
            for (XSLFSlide slide : ppt.getSlides()) {
                text.append(slide.getTitle()).append("\n");
                // 可以添加更多文本提取逻辑
            }
        }
        return text.toString();
    }
    
    /**
     * 提取视频字幕
     */
    private String extractVideoSubtitles(String fileName) {
        // TODO: 实现视频字幕提取
        // 可以使用FFmpeg或其他视频处理库
        return "";
    }
    
    /**
     * 提取代码和注释
     */
    private String extractCodeAndComments(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        StringBuilder text = new StringBuilder();
        
        for (String line : lines) {
            // 提取注释
            if (line.trim().startsWith("//") || line.trim().startsWith("/*")) {
                text.append(line).append("\n");
            }
            // 提取代码结构
            if (line.contains("class ") || line.contains("interface ") || 
                line.contains("public ") || line.contains("private ")) {
                text.append(line).append("\n");
            }
        }
        
        return text.toString();
    }
} 