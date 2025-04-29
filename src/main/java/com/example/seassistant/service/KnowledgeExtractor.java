package com.example.seassistant.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KnowledgeExtractor {
    
    /**
     * 概念类
     */
    public static class Concept {
        private String name;
        private String description;
        private String category;
        
        public Concept(String name, String description, String category) {
            this.name = name;
            this.description = description;
            this.category = category;
        }
        
        // Getters and Setters
    }
    
    /**
     * 关系类
     */
    public static class Relation {
        private String source;
        private String target;
        private String type;
        
        public Relation(String source, String target, String type) {
            this.source = source;
            this.target = target;
            this.type = type;
        }
        
        // Getters and Setters
    }
    
    /**
     * 抽取概念
     */
    public List<Concept> extractConcepts(String text) {
        List<Concept> concepts = new ArrayList<>();
        
        // 1. 提取定义性概念
        Pattern definitionPattern = Pattern.compile("([A-Z][a-zA-Z]+)\\s+是\\s+(.+?)[。.]");
        Matcher matcher = definitionPattern.matcher(text);
        while (matcher.find()) {
            concepts.add(new Concept(
                matcher.group(1),
                matcher.group(2),
                "定义"
            ));
        }
        
        // 2. 提取技术术语
        Pattern termPattern = Pattern.compile("([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)*)");
        matcher = termPattern.matcher(text);
        while (matcher.find()) {
            String term = matcher.group(1);
            if (!isCommonWord(term)) {
                concepts.add(new Concept(
                    term,
                    "技术术语",
                    "术语"
                ));
            }
        }
        
        return concepts;
    }
    
    /**
     * 抽取关系
     */
    public List<Relation> extractRelations(String text) {
        List<Relation> relations = new ArrayList<>();
        
        // 1. 提取包含关系
        Pattern containsPattern = Pattern.compile("([A-Z][a-zA-Z]+)\\s+包含\\s+([A-Z][a-zA-Z]+)");
        Matcher matcher = containsPattern.matcher(text);
        while (matcher.find()) {
            relations.add(new Relation(
                matcher.group(1),
                matcher.group(2),
                "包含"
            ));
        }
        
        // 2. 提取继承关系
        Pattern extendsPattern = Pattern.compile("([A-Z][a-zA-Z]+)\\s+继承\\s+([A-Z][a-zA-Z]+)");
        matcher = extendsPattern.matcher(text);
        while (matcher.find()) {
            relations.add(new Relation(
                matcher.group(1),
                matcher.group(2),
                "继承"
            ));
        }
        
        return relations;
    }
    
    /**
     * 构建知识图谱
     */
    public void buildKnowledgeGraph(String text) {
        // 1. 抽取概念
        List<Concept> concepts = extractConcepts(text);
        
        // 2. 抽取关系
        List<Relation> relations = extractRelations(text);
        
        // 3. 存储到图数据库
        // TODO: 实现图数据库存储
    }
    
    /**
     * 判断是否为常见词
     */
    private boolean isCommonWord(String word) {
        String[] commonWords = {"The", "This", "That", "These", "Those", "And", "Or", "But"};
        for (String common : commonWords) {
            if (word.equals(common)) {
                return true;
            }
        }
        return false;
    }
} 