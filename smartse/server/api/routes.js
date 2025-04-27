const express = require('express');
const router = express.Router();

const chatController = require('../controllers/chat.controller');
const conceptController = require('../controllers/concept.controller');
const requirementsController = require('../controllers/requirements.controller');
const designController = require('../controllers/design.controller');
const testingController = require('../controllers/testing.controller');

// 聊天路由
router.post('/chat', chatController.sendMessage);
router.get('/chat/history', chatController.getChatHistory);

// 概念解释路由
router.post('/concept', conceptController.explainConcept);
router.get('/concept/search', conceptController.searchConcepts);
router.get('/concept/popular', conceptController.getPopularConcepts);

// 需求分析路由
router.post('/requirements/analyze', requirementsController.analyzeRequirements);
router.get('/requirements/templates', requirementsController.getTemplates);

// 软件设计路由
router.post('/design/suggest', designController.suggestDesign);
router.get('/design/patterns', designController.getDesignPatterns);

// 软件测试路由
router.post('/testing/suggest', testingController.suggestTestingStrategy);
router.get('/testing/techniques', testingController.getTestingTechniques);

module.exports = router; 