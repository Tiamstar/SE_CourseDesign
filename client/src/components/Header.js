import React from 'react';
import { Layout, Typography } from 'antd';

const { Header: AntHeader } = Layout;
const { Title } = Typography;

const Header = () => {
  return (
    <AntHeader style={{ background: '#fff', padding: '0 20px', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Title level={3} style={{ margin: '16px 0' }}>
          <span style={{ color: '#1890ff' }}>Smart</span>SE - 软件工程课程助手
        </Title>
      </div>
    </AntHeader>
  );
};

export default Header; 