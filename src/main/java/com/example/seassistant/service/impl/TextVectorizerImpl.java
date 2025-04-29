package com.example.seassistant.service.impl;

import com.example.seassistant.service.TextVectorizer;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TextVectorizerImpl implements TextVectorizer {
    
    private static final int VECTOR_DIMENSION = 768;
    private SavedModelBundle model;
    
    public TextVectorizerImpl() {
        // 加载预训练模型
        try {
            model = SavedModelBundle.load("models/sentence-bert", "serve");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model", e);
        }
    }
    
    @Override
    public float[] vectorize(String text) {
        try {
            // 1. 文本预处理
            String processedText = preprocess(text);
            
            // 2. 创建输入张量
            Tensor<String> inputTensor = Tensor.create(
                    NdArrays.ofObjects(String.class, Shape.of(1)),
                    buf -> buf.setObject(processedText, 0));
            
            // 3. 运行模型
            try (Tensor<?> outputTensor = model.session()
                    .runner()
                    .feed("input", inputTensor)
                    .fetch("output")
                    .run()
                    .get(0)) {
                
                // 4. 获取输出向量
                TFloat32 output = (TFloat32) outputTensor;
                FloatBuffer buffer = FloatBuffer.allocate(VECTOR_DIMENSION);
                output.read(buffer);
                
                return buffer.array();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to vectorize text", e);
        }
    }
    
    @Override
    public List<float[]> batchVectorize(List<String> texts) {
        try {
            // 1. 文本预处理
            List<String> processedTexts = texts.stream()
                    .map(this::preprocess)
                    .toList();
            
            // 2. 创建输入张量
            Tensor<String> inputTensor = Tensor.create(
                    NdArrays.ofObjects(String.class, Shape.of(texts.size())),
                    buf -> {
                        for (int i = 0; i < processedTexts.size(); i++) {
                            buf.setObject(processedTexts.get(i), i);
                        }
                    });
            
            // 3. 运行模型
            try (Tensor<?> outputTensor = model.session()
                    .runner()
                    .feed("input", inputTensor)
                    .fetch("output")
                    .run()
                    .get(0)) {
                
                // 4. 获取输出向量
                TFloat32 output = (TFloat32) outputTensor;
                FloatBuffer buffer = FloatBuffer.allocate(texts.size() * VECTOR_DIMENSION);
                output.read(buffer);
                
                // 5. 转换为列表
                float[] allVectors = buffer.array();
                List<float[]> vectors = new ArrayList<>();
                for (int i = 0; i < texts.size(); i++) {
                    vectors.add(Arrays.copyOfRange(
                            allVectors, 
                            i * VECTOR_DIMENSION, 
                            (i + 1) * VECTOR_DIMENSION));
                }
                
                return vectors;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to batch vectorize texts", e);
        }
    }
    
    /**
     * 文本预处理
     */
    private String preprocess(String text) {
        // 1. 去除特殊字符
        text = text.replaceAll("[^\\p{L}\\p{N}\\s]", "");
        
        // 2. 去除多余空格
        text = text.trim().replaceAll("\\s+", " ");
        
        // 3. 转换为小写
        text = text.toLowerCase();
        
        return text;
    }
} 