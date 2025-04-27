const { searchKnowledgeBase } = require('../services/elasticsearch.service');
const { processMessage } = require('../services/llm.service');

/**
 * 解释软件工程概念
 */
exports.explainConcept = async (req, res) => {
  try {
    const { concept, detail = 'medium' } = req.body;

    if (!concept) {
      return res.status(400).json({ error: '概念名称不能为空' });
    }

    // 构建提示词
    let prompt = `请解释软件工程中的"${concept}"概念`;
    
    // 根据用户请求的详细程度调整提示词
    switch (detail) {
      case 'simple':
        prompt += '，请简明扼要地解释，适合初学者理解。';
        break;
      case 'medium':
        prompt += '，包括定义、主要特点和应用场景。';
        break;
      case 'detailed':
        prompt += '，请提供详细的解释，包括定义、历史背景、核心原则、最佳实践、优缺点分析和应用示例。';
        break;
      default:
        prompt += '，包括定义、主要特点和应用场景。';
    }

    // 调用大语言模型服务生成解释
    const response = await processMessage(prompt, 'concept');

    // 返回响应
    return res.status(200).json({
      success: true,
      data: {
        concept,
        explanation: response.content,
        detail
      }
    });
  } catch (error) {
    console.error('解释概念错误:', error);
    return res.status(500).json({
      error: {
        message: '处理请求时发生错误',
        details: error.message
      }
    });
  }
};

/**
 * 搜索软件工程概念
 */
exports.searchConcepts = async (req, res) => {
  try {
    const { query, limit = 10 } = req.query;

    if (!query) {
      return res.status(400).json({ error: '搜索关键词不能为空' });
    }

    // 从知识库搜索相关概念
    const searchResults = await searchKnowledgeBase(query, 'concept', parseInt(limit));

    return res.status(200).json({
      success: true,
      data: searchResults
    });
  } catch (error) {
    console.error('搜索概念错误:', error);
    return res.status(500).json({
      error: {
        message: '搜索概念时发生错误',
        details: error.message
      }
    });
  }
};

/**
 * 获取热门概念
 */
exports.getPopularConcepts = async (req, res) => {
  try {
    // 在实际项目中，这里会从数据库获取热门概念
    // 这里使用模拟数据
    const popularConcepts = [
      {
        title: '软件工程概述',
        description: '软件工程是应用计算机科学理论和技术以及工程管理原则，以经济有效的方式获取高质量软件的一种工程学科。'
      },
      {
        title: '软件生命周期',
        description: '软件从概念、需求分析、设计、编码、测试、运行维护直至废弃的整个过程。'
      },
      {
        title: '敏捷开发',
        description: '一种以人为核心、迭代、循序渐进的开发方法，强调适应性而非预测性。'
      },
      {
        title: '设计模式',
        description: '在软件设计中，针对特定问题的可复用解决方案。'
      },
      {
        title: '软件测试',
        description: '评估和验证软件产品或服务质量的过程。'
      }
    ];

    return res.status(200).json({
      success: true,
      data: popularConcepts
    });
  } catch (error) {
    console.error('获取热门概念错误:', error);
    return res.status(500).json({
      error: {
        message: '获取热门概念时发生错误',
        details: error.message
      }
    });
  }
}; 