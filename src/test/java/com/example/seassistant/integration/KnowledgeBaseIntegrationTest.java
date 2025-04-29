package com.example.seassistant.integration;

import com.example.seassistant.service.DataSourceProcessor;
import com.example.seassistant.service.KnowledgeBaseService;
import com.example.seassistant.service.TextVectorizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class KnowledgeBaseIntegrationTest {
    
    @Autowired
    private DataSourceProcessor dataSourceProcessor;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private TextVectorizer textVectorizer;
    
    @Test
    public void testCompleteFlow() throws Exception {
        // 1. 初始化知识库
        knowledgeBaseService.initKnowledgeBase();
        
        // 2. 准备测试数据
        String content = "软件工程是一门研究用工程化方法构建和维护有效的、实用的和高质量的软件的学科";
        MockMultipartFile file = new MockMultipartFile(
            "test.txt",
            "test.txt",
            "text/plain",
            content.getBytes(StandardCharsets.UTF_8)
        );
        
        // 3. 处理文件
        dataSourceProcessor.processCode(file);
        
        // 4. 搜索相似内容
        String query = "软件工程的定义";
        List<Long> results = knowledgeBaseService.searchKnowledge(query, 5);
        
        // 5. 验证结果
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }
    
    @Test
    public void testVectorization() {
        // 1. 准备测试数据
        String text1 = "软件工程是一门研究用工程化方法构建和维护有效的、实用的和高质量的软件的学科";
        String text2 = "软件工程强调以系统化、规范化、可度量的方法来开发和维护软件";
        
        // 2. 向量化
        float[] vector1 = textVectorizer.vectorize(text1);
        float[] vector2 = textVectorizer.vectorize(text2);
        
        // 3. 计算相似度
        float similarity = calculateCosineSimilarity(vector1, vector2);
        
        // 4. 验证结果
        assertTrue(similarity > 0.5);
    }
    
    @Test
    public void testBatchProcessing() throws Exception {
        // 1. 准备多个测试文件
        String[] contents = {
            "软件工程的基本原理",
            "软件开发的生命周期",
            "软件测试的方法和技术"
        };
        
        // 2. 批量处理
        for (int i = 0; i < contents.length; i++) {
            MockMultipartFile file = new MockMultipartFile(
                "test" + i + ".txt",
                "test" + i + ".txt",
                "text/plain",
                contents[i].getBytes(StandardCharsets.UTF_8)
            );
            dataSourceProcessor.processCode(file);
        }
        
        // 3. 验证处理结果
        String query = "软件工程";
        List<Long> results = knowledgeBaseService.searchKnowledge(query, 10);
        
        assertTrue(results.size() >= 3);
    }
    
    private float calculateCosineSimilarity(float[] vector1, float[] vector2) {
        float dotProduct = 0;
        float norm1 = 0;
        float norm2 = 0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        return (float) (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2)));
    }
} 