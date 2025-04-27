import React, { useState, useRef, useEffect } from 'react';
import { Input, Button, Select, Typography, Spin, List, Avatar } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined } from '@ant-design/icons';
import axios from 'axios';

const { TextArea } = Input;
const { Title, Paragraph } = Typography;
const { Option } = Select;

const ChatPage = () => {
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [selectedAgent, setSelectedAgent] = useState('general');
  const messagesEndRef = useRef(null);

  const agents = [
    { value: 'general', label: '通用助手' },
    { value: 'concept', label: '概念解释' },
    { value: 'requirements', label: '需求分析' },
    { value: 'design', label: '软件设计' },
    { value: 'testing', label: '软件测试' },
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async () => {
    if (!inputValue.trim()) return;

    const userMessage = {
      content: inputValue,
      sender: 'user',
      timestamp: new Date().toISOString(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue('');
    setLoading(true);

    try {
      // 在实际项目中，这里会调用后端API
      // const response = await axios.post('http://localhost:5000/api/chat', {
      //   message: userMessage.content,
      //   agent: selectedAgent,
      // });

      // 模拟API响应
      await new Promise((resolve) => setTimeout(resolve, 1500));
      
      const botResponse = {
        content: `这是来自${agents.find(a => a.value === selectedAgent).label}的回复：\n\n您询问的是关于"${userMessage.content}"。在软件工程中，这是一个重要的概念。我可以为您提供详细的解释和示例。`,
        sender: 'assistant',
        timestamp: new Date().toISOString(),
      };

      setMessages((prev) => [...prev, botResponse]);
    } catch (error) {
      console.error('Error sending message:', error);
      
      const errorMessage = {
        content: '抱歉，发生了错误。请稍后再试。',
        sender: 'assistant',
        timestamp: new Date().toISOString(),
      };
      
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div className="chat-page">
      <Title level={2}>智能对话</Title>
      <Paragraph>
        选择一个专业的智能体，开始关于软件工程的对话。
      </Paragraph>
      
      <div className="agent-selector">
        <Select
          defaultValue="general"
          style={{ width: 200 }}
          onChange={setSelectedAgent}
        >
          {agents.map((agent) => (
            <Option key={agent.value} value={agent.value}>
              {agent.label}
            </Option>
          ))}
        </Select>
      </div>
      
      <div className="chat-container">
        <List
          className="chat-messages"
          itemLayout="horizontal"
          dataSource={messages}
          renderItem={(message) => (
            <List.Item
              style={{
                justifyContent: message.sender === 'user' ? 'flex-end' : 'flex-start',
                padding: '8px 16px',
              }}
            >
              <div
                className={`message ${
                  message.sender === 'user' ? 'user-message' : 'assistant-message'
                }`}
                style={{
                  display: 'flex',
                  alignItems: 'flex-start',
                  maxWidth: '80%',
                }}
              >
                <Avatar
                  icon={message.sender === 'user' ? <UserOutlined /> : <RobotOutlined />}
                  style={{
                    backgroundColor: message.sender === 'user' ? '#1890ff' : '#f0f2f5',
                    color: message.sender === 'user' ? '#fff' : '#1890ff',
                    marginRight: '12px',
                  }}
                />
                <div>
                  <div style={{ whiteSpace: 'pre-wrap' }}>{message.content}</div>
                  <div style={{ fontSize: '12px', color: '#999', marginTop: '4px' }}>
                    {new Date(message.timestamp).toLocaleTimeString()}
                  </div>
                </div>
              </div>
            </List.Item>
          )}
        />
        <div ref={messagesEndRef} />
        
        <div className="message-input">
          <TextArea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="输入您的问题..."
            autoSize={{ minRows: 2, maxRows: 6 }}
            disabled={loading}
            style={{ marginRight: '8px' }}
          />
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={handleSendMessage}
            loading={loading}
            disabled={!inputValue.trim()}
          >
            发送
          </Button>
        </div>
      </div>
      
      {loading && (
        <div style={{ textAlign: 'center', marginTop: '16px' }}>
          <Spin tip="AI正在思考中..." />
        </div>
      )}
    </div>
  );
};

export default ChatPage; 