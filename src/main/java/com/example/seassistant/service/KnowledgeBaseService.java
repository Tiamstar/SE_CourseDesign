package com.example.seassistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KnowledgeBaseService {
    
    @Autowired
    private MilvusService milvusService;
    
    @Autowired
    private TextVectorizer textVectorizer;  // 文本向量化服务
    
    /**
     * 添加知识条目
     */
    @Transactional
    public void addKnowledge(String content) {
        // 1. 向量化文本
        float[] vector = textVectorizer.vectorize(content);
        
        // 2. 生成ID（可以使用UUID或其他方式）
        Long id = System.currentTimeMillis();
        
        // 3. 存储到Milvus
        milvusService.insertData(id, vector, content);
    }
    
    /**
     * 搜索相似知识
     */
    public List<Long> searchKnowledge(String query, int topK) {
        // 1. 向量化查询文本
        float[] queryVector = textVectorizer.vectorize(query);
        
        // 2. 在Milvus中搜索
        return milvusService.searchSimilar(queryVector, topK);
    }
    
    /**
     * 初始化知识库
     */
    public void initKnowledgeBase() {
        // 1. 创建集合
        milvusService.createCollection();
        
        // 2. 加载集合
        milvusService.loadCollection();
    }
} 