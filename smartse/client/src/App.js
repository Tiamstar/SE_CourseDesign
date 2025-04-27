import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Layout } from 'antd';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import Home from './pages/Home';
import ChatPage from './pages/ChatPage';
import ConceptExplainerPage from './pages/ConceptExplainerPage';
import RequirementsAnalysisPage from './pages/RequirementsAnalysisPage';
import DesignPage from './pages/DesignPage';
import TestingPage from './pages/TestingPage';
import 'antd/dist/reset.css';
import './App.css';

const { Content } = Layout;

function App() {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header />
      <Layout>
        <Sidebar />
        <Layout style={{ padding: '24px' }}>
          <Content
            style={{
              background: '#fff',
              padding: 24,
              margin: 0,
              minHeight: 280,
              borderRadius: '4px'
            }}
          >
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/chat" element={<ChatPage />} />
              <Route path="/concept-explainer" element={<ConceptExplainerPage />} />
              <Route path="/requirements-analysis" element={<RequirementsAnalysisPage />} />
              <Route path="/design" element={<DesignPage />} />
              <Route path="/testing" element={<TestingPage />} />
            </Routes>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
}

export default App; 