const elasticsearch = require('elasticsearch');
const dotenv = require('dotenv');

dotenv.config();

// Elasticsearch配置
const ES_HOST = process.env.ES_HOST || 'localhost:9200';
const ES_INDEX = process.env.ES_INDEX || 'smartse_knowledge';

// 创建Elasticsearch客户端
let client;

/**
 * 设置Elasticsearch连接和索引
 */
exports.setupElasticsearch = async () => {
  try {
    // 创建Elasticsearch客户端
    client = new elasticsearch.Client({
      host: ES_HOST,
      log: 'error'
    });

    // 检查Elasticsearch是否正常运行
    await client.ping({ requestTimeout: 3000 });
    console.log('Elasticsearch连接成功');

    // 检查索引是否存在
    const indexExists = await client.indices.exists({ index: ES_INDEX });
    
    if (!indexExists) {
      console.log(`索引 ${ES_INDEX} 不存在，开始创建...`);
      await createIndex();
      await seedKnowledgeBase();
    }
    
    return true;
  } catch (error) {
    console.error('Elasticsearch设置失败:', error.message);
    throw error;
  }
};

/**
 * 创建知识库索引
 */
const createIndex = async () => {
  try {
    await client.indices.create({
      index: ES_INDEX,
      body: {
        mappings: {
          properties: {
            title: { type: 'text', analyzer: 'standard' },
            content: { type: 'text', analyzer: 'standard' },
            category: { type: 'keyword' },
            tags: { type: 'keyword' },
            source: { type: 'keyword' },
            created_at: { type: 'date' }
          }
        }
      }
    });
    console.log(`索引 ${ES_INDEX} 创建成功`);
    return true;
  } catch (error) {
    console.error('创建索引失败:', error);
    throw error;
  }
};

/**
 * 向知识库中添加初始数据
 */
const seedKnowledgeBase = async () => {
  try {
    // 软件工程基本概念示例数据
    const knowledgeItems = [
      {
        title: '软件工程概述',
        content: '软件工程是应用计算机科学理论和技术以及工程管理原则，以经济有效的方式获取高质量软件的一种工程学科。它涉及程序设计语言、数据库、软件开发工具、系统平台、标准、设计模式等方面。',
        category: 'concept',
        tags: ['软件工程', '概述', '定义'],
        source: 'SE教材',
        created_at: new Date()
      },
      {
        title: '软件生命周期',
        content: '软件生命周期是软件从概念、需求分析、设计、编码、测试、运行维护直至废弃的整个过程。常见的生命周期模型包括瀑布模型、增量模型、螺旋模型和敏捷开发模型等。',
        category: 'concept',
        tags: ['软件生命周期', '开发模型', '瀑布模型', '敏捷开发'],
        source: 'SE教材',
        created_at: new Date()
      },
      {
        title: '需求工程',
        content: '需求工程是识别、建模、通信和记录软件需求的系统化过程。它包括需求获取、需求分析、需求规格说明和需求验证四个主要活动。好的需求应该是正确的、无歧义的、完整的、一致的、可验证的、可修改的、可跟踪的。',
        category: 'requirements',
        tags: ['需求工程', '需求分析', '需求规格说明'],
        source: 'SE教材',
        created_at: new Date()
      },
      {
        title: '软件架构',
        content: '软件架构是有关软件整体结构与组件的抽象描述，用于指导大型软件系统各个方面的设计。常见的架构风格包括分层架构、客户端-服务器架构、微服务架构、事件驱动架构等。好的架构应当支持系统的可维护性、可扩展性、可靠性和性能等质量属性。',
        category: 'design',
        tags: ['软件架构', '架构风格', '设计模式'],
        source: 'SE教材',
        created_at: new Date()
      },
      {
        title: '软件测试',
        content: '软件测试是在规定的条件下对程序进行操作，以发现程序错误，衡量软件质量，并对其是否能满足设计要求进行评估的过程。测试方法包括黑盒测试、白盒测试、单元测试、集成测试、系统测试和验收测试等。',
        category: 'testing',
        tags: ['软件测试', '测试方法', '测试策略'],
        source: 'SE教材',
        created_at: new Date()
      }
    ];

    // 批量添加数据
    const bulkOps = knowledgeItems.flatMap(item => [
      { index: { _index: ES_INDEX } },
      item
    ]);

    await client.bulk({ body: bulkOps });
    console.log('知识库初始数据添加成功');
    return true;
  } catch (error) {
    console.error('添加知识库数据失败:', error);
    throw error;
  }
};

/**
 * 搜索知识库
 * @param {string} query - 搜索查询
 * @param {string} agent - 智能体类型（用于过滤结果）
 * @param {number} size - 返回结果数量
 * @returns {Promise<Array>} - 搜索结果
 */
exports.searchKnowledgeBase = async (query, agent = 'general', size = 3) => {
  try {
    if (!client) {
      await exports.setupElasticsearch();
    }

    // 根据智能体类型映射到相应的类别
    let category;
    switch (agent) {
      case 'concept':
        category = 'concept';
        break;
      case 'requirements':
        category = 'requirements';
        break;
      case 'design':
        category = 'design';
        break;
      case 'testing':
        category = 'testing';
        break;
      default:
        category = null; // 不限制类别
    }

    // 构建查询
    const searchQuery = {
      index: ES_INDEX,
      body: {
        query: {
          bool: {
            must: {
              multi_match: {
                query: query,
                fields: ['title^2', 'content', 'tags^1.5'],
                fuzziness: 'AUTO'
              }
            },
            filter: category ? { term: { category } } : []
          }
        },
        size: size
      }
    };

    // 执行搜索
    const response = await client.search(searchQuery);
    
    // 处理和返回结果
    return response.hits.hits.map(hit => ({
      id: hit._id,
      title: hit._source.title,
      content: hit._source.content,
      category: hit._source.category,
      tags: hit._source.tags,
      score: hit._score
    }));
  } catch (error) {
    console.error('知识库搜索失败:', error);
    // 在搜索失败时，返回空数组而不是抛出错误，以免中断整个处理流程
    return [];
  }
};

/**
 * 添加新知识到知识库
 * @param {Object} knowledge - 知识项
 * @returns {Promise<boolean>} - 是否添加成功
 */
exports.addKnowledge = async (knowledge) => {
  try {
    if (!client) {
      await exports.setupElasticsearch();
    }

    await client.index({
      index: ES_INDEX,
      body: {
        ...knowledge,
        created_at: new Date()
      }
    });

    return true;
  } catch (error) {
    console.error('添加知识失败:', error);
    throw error;
  }
}; 