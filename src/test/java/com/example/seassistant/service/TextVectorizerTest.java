package com.example.seassistant.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TextVectorizerTest {
    
    @Autowired
    private TextVectorizer textVectorizer;
    
    @Test
    public void testVectorize() {
        // 测试单个文本向量化
        String text = "软件工程是一门研究用工程化方法构建和维护有效的、实用的和高质量的软件的学科";
        float[] vector = textVectorizer.vectorize(text);
        
        // 验证向量维度
        assertEquals(768, vector.length);
        
        // 验证向量值范围
        for (float value : vector) {
            assertTrue(value >= -1 && value <= 1);
        }
    }
    
    @Test
    public void testBatchVectorize() {
        // 测试批量文本向量化
        List<String> texts = Arrays.asList(
            "软件工程是一门研究用工程化方法构建和维护有效的、实用的和高质量的软件的学科",
            "需求分析是软件工程中的重要阶段",
            "设计模式是解决特定问题的通用解决方案"
        );
        
        List<float[]> vectors = textVectorizer.batchVectorize(texts);
        
        // 验证向量数量
        assertEquals(3, vectors.size());
        
        // 验证每个向量的维度
        for (float[] vector : vectors) {
            assertEquals(768, vector.length);
            for (float value : vector) {
                assertTrue(value >= -1 && value <= 1);
            }
        }
    }
    
    @Test
    public void testSimilarity() {
        // 测试相似文本的向量相似度
        String text1 = "软件工程是一门研究用工程化方法构建和维护有效的、实用的和高质量的软件的学科";
        String text2 = "软件工程是研究如何用工程化方法开发软件的学科";
        
        float[] vector1 = textVectorizer.vectorize(text1);
        float[] vector2 = textVectorizer.vectorize(text2);
        
        // 计算余弦相似度
        float similarity = cosineSimilarity(vector1, vector2);
        
        // 验证相似度在合理范围内
        assertTrue(similarity > 0.7);
    }
    
    /**
     * 计算余弦相似度
     */
    private float cosineSimilarity(float[] vector1, float[] vector2) {
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