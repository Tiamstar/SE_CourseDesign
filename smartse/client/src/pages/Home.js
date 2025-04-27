import React from 'react';
import { Typography, Card, Row, Col, Button } from 'antd';
import { useNavigate } from 'react-router-dom';
import {
  CommentOutlined,
  ReadOutlined,
  FileSearchOutlined,
  CodeOutlined,
  BugOutlined,
} from '@ant-design/icons';

const { Title, Paragraph } = Typography;

const Home = () => {
  const navigate = useNavigate();

  const features = [
    {
      title: '智能对话',
      icon: <CommentOutlined style={{ fontSize: '32px', color: '#1890ff' }} />,
      description: '与AI助手进行自然语言对话，回答软件工程相关问题',
      path: '/chat',
    },
    {
      title: '概念解释',
      icon: <ReadOutlined style={{ fontSize: '32px', color: '#1890ff' }} />,
      description: '获取软件工程中关键概念的详细解释和示例',
      path: '/concept-explainer',
    },
    {
      title: '需求分析',
      icon: <FileSearchOutlined style={{ fontSize: '32px', color: '#1890ff' }} />,
      description: '获取关于需求分析方法、技术和最佳实践的指导',
      path: '/requirements-analysis',
    },
    {
      title: '软件设计',
      icon: <CodeOutlined style={{ fontSize: '32px', color: '#1890ff' }} />,
      description: '获取关于软件架构、设计模式和原则的指导',
      path: '/design',
    },
    {
      title: '软件测试',
      icon: <BugOutlined style={{ fontSize: '32px', color: '#1890ff' }} />,
      description: '获取关于软件测试方法、技术和最佳实践的指导',
      path: '/testing',
    },
  ];

  return (
    <div>
      <div style={{ textAlign: 'center', margin: '20px 0 40px' }}>
        <Title>基于大语言模型的软件工程课程助手</Title>
        <Paragraph style={{ fontSize: '16px' }}>
          通过先进的AI技术，为软件工程学习提供全方位支持
        </Paragraph>
      </div>

      <Row gutter={[16, 16]}>
        {features.map((feature, index) => (
          <Col xs={24} sm={12} md={8} key={index}>
            <Card
              hoverable
              style={{ height: '100%' }}
              onClick={() => navigate(feature.path)}
            >
              <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                {feature.icon}
              </div>
              <Title level={4} style={{ textAlign: 'center' }}>
                {feature.title}
              </Title>
              <Paragraph style={{ textAlign: 'center' }}>
                {feature.description}
              </Paragraph>
              <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <Button type="primary" onClick={() => navigate(feature.path)}>
                  开始使用
                </Button>
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  );
};

export default Home; 