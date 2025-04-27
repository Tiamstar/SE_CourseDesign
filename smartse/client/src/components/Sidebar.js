import React from 'react';
import { Layout, Menu } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  HomeOutlined,
  CommentOutlined,
  ReadOutlined,
  FileSearchOutlined,
  CodeOutlined,
  BugOutlined,
} from '@ant-design/icons';

const { Sider } = Layout;

const Sidebar = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/chat',
      icon: <CommentOutlined />,
      label: '智能对话',
    },
    {
      key: '/concept-explainer',
      icon: <ReadOutlined />,
      label: '概念解释',
    },
    {
      key: '/requirements-analysis',
      icon: <FileSearchOutlined />,
      label: '需求分析',
    },
    {
      key: '/design',
      icon: <CodeOutlined />,
      label: '软件设计',
    },
    {
      key: '/testing',
      icon: <BugOutlined />,
      label: '软件测试',
    },
  ];

  const handleMenuClick = (e) => {
    navigate(e.key);
  };

  return (
    <Sider width={200} style={{ background: '#fff' }} collapsible>
      <Menu
        mode="inline"
        selectedKeys={[location.pathname]}
        style={{ height: '100%', borderRight: 0 }}
        items={menuItems}
        onClick={handleMenuClick}
      />
    </Sider>
  );
};

export default Sidebar; 