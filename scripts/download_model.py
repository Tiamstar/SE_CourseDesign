import os
import torch
from transformers import AutoTokenizer, AutoModel
from sentence_transformers import SentenceTransformer
import tensorflow as tf

def download_and_convert_model():
    # 1. 下载中文BERT模型
    model_name = "shibing624/text2vec-base-chinese"
    save_path = "models/sentence-bert"
    
    # 创建保存目录
    os.makedirs(save_path, exist_ok=True)
    
    # 2. 加载模型
    model = SentenceTransformer(model_name)
    
    # 3. 转换为TensorFlow格式
    class TFModel(tf.keras.Model):
        def __init__(self, model):
            super(TFModel, self).__init__()
            self.bert = model
        
        @tf.function(input_signature=[tf.TensorSpec([None], tf.string)])
        def call(self, inputs):
            embeddings = self.bert.encode(inputs.numpy().tolist(), convert_to_tensor=True)
            return embeddings
    
    # 4. 创建TF模型
    tf_model = TFModel(model)
    
    # 5. 保存模型
    tf.saved_model.save(tf_model, save_path)
    print(f"Model saved to {save_path}")

if __name__ == "__main__":
    download_and_convert_model() 