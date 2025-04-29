package com.example.seassistant.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MilvusService {
    
    private static final String COLLECTION_NAME = "knowledge_base";
    private static final String VECTOR_FIELD = "vector";
    private static final String ID_FIELD = "id";
    private static final String CONTENT_FIELD = "content";
    
    @Autowired
    private MilvusServiceClient milvusClient;
    
    public void createCollection() {
        // 定义集合字段
        List<FieldType> fields = new ArrayList<>();
        fields.add(FieldType.newBuilder()
                .withName(ID_FIELD)
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(false)
                .build());
        
        fields.add(FieldType.newBuilder()
                .withName(VECTOR_FIELD)
                .withDataType(DataType.FloatVector)
                .withDimension(768)  // 向量维度，根据使用的模型确定
                .build());
        
        fields.add(FieldType.newBuilder()
                .withName(CONTENT_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build());
        
        // 创建集合
        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldTypes(fields)
                .build();
        
        milvusClient.createCollection(createCollectionParam);
        
        // 创建索引
        CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldName(VECTOR_FIELD)
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.L2)
                .withExtraParam("{\"nlist\":1024}")
                .build();
        
        milvusClient.createIndex(createIndexParam);
    }
    
    public void insertData(Long id, float[] vector, String content) {
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(ID_FIELD, List.of(id)));
        fields.add(new InsertParam.Field(VECTOR_FIELD, List.of(vector)));
        fields.add(new InsertParam.Field(CONTENT_FIELD, List.of(content)));
        
        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build();
        
        milvusClient.insert(insertParam);
    }
    
    public List<Long> searchSimilar(float[] queryVector, int topK) {
        List<String> outputFields = List.of(ID_FIELD, CONTENT_FIELD);
        
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName(VECTOR_FIELD)
                .withVectors(List.of(queryVector))
                .withTopK(topK)
                .withMetricType(MetricType.L2)
                .withOutputFields(outputFields)
                .build();
        
        SearchResultsWrapper searchResults = new SearchResultsWrapper(
                milvusClient.search(searchParam).getData().getResults());
        
        return searchResults.getIDScore(0).getLeft();
    }
    
    public void loadCollection() {
        LoadCollectionParam loadCollectionParam = LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build();
        milvusClient.loadCollection(loadCollectionParam);
    }
    
    public void releaseCollection() {
        ReleaseCollectionParam releaseCollectionParam = ReleaseCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build();
        milvusClient.releaseCollection(releaseCollectionParam);
    }
} 