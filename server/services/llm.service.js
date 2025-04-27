const axios = require('axios');
const { searchKnowledgeBase } = require('./elasticsearch.service');
const dotenv = require('dotenv');

dotenv.config();

// 华为云大模型API配置
const HW_LLM_API_URL = process.env.HW_LLM_API_URL || 'https://huawei-llm-api-url';
const HW_LLM_API_KEY = process.env.HW_LLM_API_KEY || 'your-api-key';

/**
 * 处理用户消息
 * @param {string} message - 用户消息
 * @param {string} agent - 智能体类型
 * @returns {Promise<object>} - 处理结果
 */
exports.processMessage = async (message, agent = 'general') => {
  try {
    // 1. 检索知识库获取相关信息
    const searchResults = await searchKnowledgeBase(message, agent);
    
    // 2. 构建提示词
    const prompt = buildPrompt(message, searchResults, agent);
    
    // 3. 调用大语言模型API
    const llmResponse = await callLLMApi(prompt);
    
    // 4. 处理响应
    return {
      content: llmResponse,
      agent,
      timestamp: new Date().toISOString()
    };
  } catch (error) {
    console.error('处理消息错误:', error);
    throw new Error(`处理消息失败: ${error.message}`);
  }
};

/**
 * 构建大语言模型的提示词
 * @param {string} message - 用户消息
 * @param {Array} searchResults - 检索结果
 * @param {string} agent - 智能体类型
 * @returns {string} - 构建的提示词
 */
const buildPrompt = (message, searchResults, agent) => {
  let systemPrompt = '';
  
  // 根据不同的智能体类型设置不同的系统提示词
  switch (agent) {
    case 'concept':
      systemPrompt = '你是一个软件工程概念解释专家，擅长解释各种软件工程概念、原则和方法论。';
      break;
    case 'requirements':
      systemPrompt = '你是一个需求分析专家，擅长需求获取、分析、规格说明和验证。';
      break;
    case 'design':
      systemPrompt = '你是一个软件设计专家，擅长软件架构、设计模式和代码组织。';
      break;
    case 'testing':
      systemPrompt = '你是一个软件测试专家，擅长测试策略、测试方法和质量保证。';
      break;
    default:
      systemPrompt = '你是一个软件工程课程助手，可以回答各种软件工程相关问题。';
  }
  
  // 添加检索到的知识
  let retrievedContext = '';
  if (searchResults && searchResults.length > 0) {
    retrievedContext = '以下是一些与问题相关的信息：\n\n';
    searchResults.forEach((result, index) => {
      retrievedContext += `[${index + 1}] ${result.content}\n`;
    });
    retrievedContext += '\n请根据以上信息和你的知识回答问题。\n\n';
  }
  
  // 构建完整提示词
  return `${systemPrompt}\n\n${retrievedContext}用户问题: ${message}`;
};

/**
 * 调用大语言模型API
 * @param {string} prompt - 提示词
 * @returns {Promise<string>} - 模型响应
 */
const callLLMApi = async (prompt) => {
  try {
    // 真实环境中这里会调用华为云大模型API
    // 这里做一个模拟调用
    
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // 为了开发测试，返回一个模拟响应
    // 在实际实现中，这里会发起一个HTTP请求到华为云LLM API
    /*
    const response = await axios.post(
      HW_LLM_API_URL,
      {
        prompt: prompt,
        max_tokens: 1000,
        temperature: 0.7
      },
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${HW_LLM_API_KEY}`
        }
      }
    );
    
    return response.data.choices[0].text;
    */
    
    return `根据您的问题"${prompt.split('用户问题: ')[1]}"，我可以提供以下解答：\n\n这是一个软件工程中的重要概念。软件工程是应用计算机科学理论和技术以及工程管理原则，以经济有效的方式获取高质量软件的一种工程学科。它涉及到需求分析、软件设计、编程实现、软件测试和维护等多个阶段。`;
  } catch (error) {
    console.error('调用LLM API错误:', error);
    throw new Error(`调用大语言模型API失败: ${error.message}`);
  }
}; 