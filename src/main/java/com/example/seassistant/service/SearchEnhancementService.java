package com.example.seassistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchEnhancementService {
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private TextVectorizer textVectorizer;
    
    /**
     * 同义词扩展
     */
    private static final Map<String, List<String>> SYNONYM_MAP = new HashMap<>() {{
        put("软件工程", Arrays.asList("软件开发", "软件工程学", "软件工程方法学"));
        put("需求分析", Arrays.asList("需求工程", "需求获取", "需求调研"));
        put("系统设计", Arrays.asList("软件设计", "架构设计", "详细设计"));
        // 可以继续添加更多软件工程领域的同义词
    }};
    
    /**
     * 检索增强的主要方法
     */
    public List<SearchResult> enhancedSearch(String query, int topK) {
        // 1. 查询扩展
        List<String> expandedQueries = expandQuery(query);
        
        // 2. 多路召回
        Set<SearchResult> results = new HashSet<>();
        for (String expandedQuery : expandedQueries) {
            results.addAll(multiSourceRetrieval(expandedQuery, topK));
        }
        
        // 3. 结果重排序
        List<SearchResult> rankedResults = reRankResults(new ArrayList<>(results), query);
        
        // 4. 结果去重和截断
        return deduplicateAndTruncate(rankedResults, topK);
    }
    
    /**
     * 查询扩展
     */
    private List<String> expandQuery(String query) {
        List<String> expandedQueries = new ArrayList<>();
        expandedQueries.add(query);  // 添加原始查询
        
        // 1. 同义词扩展
        for (Map.Entry<String, List<String>> entry : SYNONYM_MAP.entrySet()) {
            if (query.contains(entry.getKey())) {
                for (String synonym : entry.getValue()) {
                    expandedQueries.add(query.replace(entry.getKey(), synonym));
                }
            }
        }
        
        // 2. 分词扩展
        String[] terms = query.split("\\s+");
        if (terms.length > 1) {
            for (String term : terms) {
                if (term.length() > 1) {  // 避免单字查询
                    expandedQueries.add(term);
                }
            }
        }
        
        return expandedQueries;
    }
    
    /**
     * 多路召回
     */
    private List<SearchResult> multiSourceRetrieval(String query, int topK) {
        List<SearchResult> results = new ArrayList<>();
        
        // 1. 向量检索
        float[] queryVector = textVectorizer.vectorize(query);
        List<Long> vectorResults = knowledgeBaseService.searchKnowledge(query, topK);
        for (Long id : vectorResults) {
            results.add(new SearchResult(id, 1.0f, "vector"));
        }
        
        // 2. 关键词检索
        List<Long> keywordResults = keywordSearch(query, topK);
        for (Long id : keywordResults) {
            results.add(new SearchResult(id, 0.8f, "keyword"));
        }
        
        // 3. 知识图谱检索
        List<Long> graphResults = graphSearch(query, topK);
        for (Long id : graphResults) {
            results.add(new SearchResult(id, 0.6f, "graph"));
        }
        
        return results;
    }
    
    /**
     * 关键词检索
     */
    private List<Long> keywordSearch(String query, int topK) {
        // TODO: 实现关键词检索
        return new ArrayList<>();
    }
    
    /**
     * 知识图谱检索
     */
    private List<Long> graphSearch(String query, int topK) {
        // TODO: 实现知识图谱检索
        return new ArrayList<>();
    }
    
    /**
     * 结果重排序
     */
    private List<SearchResult> reRankResults(List<SearchResult> results, String query) {
        // 1. 计算相关性分数
        for (SearchResult result : results) {
            float relevanceScore = calculateRelevanceScore(result, query);
            result.setFinalScore(relevanceScore);
        }
        
        // 2. 排序
        results.sort((a, b) -> Float.compare(b.getFinalScore(), a.getFinalScore()));
        
        return results;
    }
    
    /**
     * 计算相关性分数
     */
    private float calculateRelevanceScore(SearchResult result, String query) {
        float score = result.getInitialScore();
        
        // 1. 来源权重
        switch (result.getSource()) {
            case "vector":
                score *= 1.0;
                break;
            case "keyword":
                score *= 0.8;
                break;
            case "graph":
                score *= 0.6;
                break;
        }
        
        // TODO: 添加更多评分因素
        
        return score;
    }
    
    /**
     * 结果去重和截断
     */
    private List<SearchResult> deduplicateAndTruncate(List<SearchResult> results, int topK) {
        // 1. 去重
        Set<Long> seen = new HashSet<>();
        List<SearchResult> uniqueResults = new ArrayList<>();
        
        for (SearchResult result : results) {
            if (!seen.contains(result.getId())) {
                seen.add(result.getId());
                uniqueResults.add(result);
            }
        }
        
        // 2. 截断
        return uniqueResults.subList(0, Math.min(uniqueResults.size(), topK));
    }
    
    /**
     * 搜索结果类
     */
    public static class SearchResult {
        private Long id;
        private float initialScore;
        private float finalScore;
        private String source;
        
        public SearchResult(Long id, float initialScore, String source) {
            this.id = id;
            this.initialScore = initialScore;
            this.source = source;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public float getInitialScore() { return initialScore; }
        public void setInitialScore(float initialScore) { this.initialScore = initialScore; }
        public float getFinalScore() { return finalScore; }
        public void setFinalScore(float finalScore) { this.finalScore = finalScore; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }
} 