package com.example.seassistant.service;

import org.springframework.stereotype.Service;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class KeywordSearchService {
    
    private static final String INDEX_PATH = "lucene_index";
    private static final String CONTENT_FIELD = "content";
    private static final String ID_FIELD = "id";
    
    private Directory directory;
    private Analyzer analyzer;
    private IndexWriter indexWriter;
    
    public KeywordSearchService() throws IOException {
        this.directory = FSDirectory.open(Paths.get(INDEX_PATH));
        this.analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(directory, config);
    }
    
    /**
     * 添加文档到索引
     */
    public void addDocument(Long id, String content) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(ID_FIELD, id.toString(), Field.Store.YES));
        doc.add(new TextField(CONTENT_FIELD, content, Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.commit();
    }
    
    /**
     * 搜索文档
     */
    public List<Long> search(String queryString, int topK) throws Exception {
        List<Long> results = new ArrayList<>();
        
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(CONTENT_FIELD, analyzer);
            Query query = parser.parse(queryString);
            
            ScoreDoc[] hits = searcher.search(query, topK).scoreDocs;
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                results.add(Long.parseLong(doc.get(ID_FIELD)));
            }
        }
        
        return results;
    }
    
    /**
     * 删除文档
     */
    public void deleteDocument(Long id) throws IOException {
        indexWriter.deleteDocuments(new Term(ID_FIELD, id.toString()));
        indexWriter.commit();
    }
    
    /**
     * 更新文档
     */
    public void updateDocument(Long id, String content) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(ID_FIELD, id.toString(), Field.Store.YES));
        doc.add(new TextField(CONTENT_FIELD, content, Field.Store.YES));
        
        indexWriter.updateDocument(new Term(ID_FIELD, id.toString()), doc);
        indexWriter.commit();
    }
    
    /**
     * 关闭索引写入器
     */
    public void close() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
        if (directory != null) {
            directory.close();
        }
    }
} 