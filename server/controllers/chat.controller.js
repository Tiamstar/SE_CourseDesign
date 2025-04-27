const { processMessage } = require('../services/llm.service');
const Chat = require('../models/chat.model');

/**
 * 发送消息到聊天助手
 */
exports.sendMessage = async (req, res) => {
  try {
    const { message, agent = 'general', sessionId } = req.body;

    if (!message) {
      return res.status(400).json({ error: '消息内容不能为空' });
    }

    // 处理消息，调用大语言模型服务
    const response = await processMessage(message, agent);

    // 保存聊天记录
    await Chat.create({
      sessionId: sessionId || new Date().toISOString(),
      agent,
      messages: [
        { content: message, sender: 'user' },
        { content: response.content, sender: 'assistant' }
      ],
      timestamp: new Date()
    });

    // 返回响应
    return res.status(200).json({
      success: true,
      data: response
    });
  } catch (error) {
    console.error('处理聊天消息错误:', error);
    return res.status(500).json({
      error: {
        message: '处理消息时发生错误',
        details: error.message
      }
    });
  }
};

/**
 * 获取聊天历史记录
 */
exports.getChatHistory = async (req, res) => {
  try {
    const { sessionId, limit = 50 } = req.query;

    if (!sessionId) {
      return res.status(400).json({ error: '会话ID不能为空' });
    }

    // 查询聊天记录
    const chatHistory = await Chat.find({ sessionId })
      .sort({ timestamp: -1 })
      .limit(parseInt(limit));

    return res.status(200).json({
      success: true,
      data: chatHistory
    });
  } catch (error) {
    console.error('获取聊天历史记录错误:', error);
    return res.status(500).json({
      error: {
        message: '获取聊天历史记录时发生错误',
        details: error.message
      }
    });
  }
}; 