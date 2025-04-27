import React, { useState } from 'react';
import {
  Typography,
  Input,
  Button,
  Card,
  Radio,
  List,
  Space,
  Divider,
  Tag,
  Spin,
  Alert,
  Row,
  Col
} from 'antd';
import { SearchOutlined, BookOutlined } from '@ant-design/icons';

const { Title, Paragraph, Text } = Typography;
const { Search } = Input;

const ConceptExplainerPage = () => {
  const [loading, setLoading] = useState(false);
  const [concept, setConcept] = useState('');
  const [detailLevel, setDetailLevel] = useState('medium');
  const [explanation, setExplanation] = useState(null);
  const [error, setError] = useState(null);
  
  // 模拟热门概念
  const popularConcepts = [
    { title: '软件工程', tags: ['基础', '概述'] },
    { title: '需求工程', tags: ['需求', '分析'] },
    { title: '软件架构', tags: ['设计', '架构'] },
    { title: '敏捷开发', tags: ['方法论', '流程'] },
    { title: '设计模式', tags: ['设计', '模式'] },
  ];

  const handleSearch = async (value) => {
    if (!value.trim()) return;
    
    setConcept(value);
    setLoading(true);
    setError(null);
    
    try {
      // 在实际项目中，这里会调用后端API
      // const response = await axios.post('http://localhost:5000/api/concept', {
      //   concept: value,
      //   detail: detailLevel
      // });
      
      // 模拟API响应
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const mockResponse = {
        success: true,
        data: {
          concept: value,
          explanation: `${value} 是软件工程中的一个重要概念。\n\n它的定义是...\n\n主要特点包括：\n1. 特点一\n2. 特点二\n3. 特点三\n\n应用场景：\n- 场景一\n- 场景二\n- 场景三`,
          detail: detailLevel
        }
      };
      
      setExplanation(mockResponse.data);
    } catch (error) {
      console.error('获取概念解释失败:', error);
      setError('获取概念解释失败，请稍后再试');
    } finally {
      setLoading(false);
    }
  };

  const handleConceptClick = (concept) => {
    setConcept(concept);
    handleSearch(concept);
  };

  return (
    <div className="concept-explainer-page">
      <Title level={2}>概念解释</Title>
      <Paragraph>
        输入软件工程相关概念，获取详细解释和示例。
      </Paragraph>
      
      <Card style={{ marginBottom: 20 }}>
        <Space direction="vertical" style={{ width: '100%' }}>
          <Search
            placeholder="输入您想了解的软件工程概念"
            allowClear
            enterButton={<Button type="primary" icon={<SearchOutlined />}>查询</Button>}
            size="large"
            value={concept}
            onChange={(e) => setConcept(e.target.value)}
            onSearch={handleSearch}
            loading={loading}
          />
          
          <div style={{ marginTop: 10 }}>
            <Text strong>详细程度：</Text>
            <Radio.Group 
              value={detailLevel} 
              onChange={(e) => setDetailLevel(e.target.value)}
              buttonStyle="solid"
            >
              <Radio.Button value="simple">简要</Radio.Button>
              <Radio.Button value="medium">中等</Radio.Button>
              <Radio.Button value="detailed">详细</Radio.Button>
            </Radio.Group>
          </div>
        </Space>
      </Card>
      
      {error && (
        <Alert
          message="错误"
          description={error}
          type="error"
          showIcon
          style={{ marginBottom: 20 }}
        />
      )}
      
      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px 0' }}>
          <Spin size="large" tip="正在查询中..." />
        </div>
      ) : explanation ? (
        <Card title={`概念: ${explanation.concept}`}>
          <Paragraph style={{ whiteSpace: 'pre-wrap' }}>
            {explanation.explanation}
          </Paragraph>
          <div style={{ marginTop: 16 }}>
            <Tag color="blue">详细程度: {
              explanation.detail === 'simple' ? '简要' :
              explanation.detail === 'medium' ? '中等' :
              '详细'
            }</Tag>
          </div>
        </Card>
      ) : (
        <Row gutter={[16, 16]}>
          <Col span={24}>
            <Divider orientation="left">
              <Space>
                <BookOutlined />
                热门概念
              </Space>
            </Divider>
            <List
              grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 3, xl: 4, xxl: 4 }}
              dataSource={popularConcepts}
              renderItem={(item) => (
                <List.Item>
                  <Card
                    hoverable
                    onClick={() => handleConceptClick(item.title)}
                  >
                    <Card.Meta
                      title={item.title}
                      description={
                        <div>
                          {item.tags.map(tag => (
                            <Tag key={tag}>{tag}</Tag>
                          ))}
                        </div>
                      }
                    />
                  </Card>
                </List.Item>
              )}
            />
          </Col>
        </Row>
      )}
    </div>
  );
};

export default ConceptExplainerPage; 