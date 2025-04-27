const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');
const dotenv = require('dotenv');
const winston = require('winston');

// 导入路由
const apiRoutes = require('./api/routes');
const { setupElasticsearch } = require('./services/elasticsearch.service');

// 环境变量配置
dotenv.config();

// 创建Express应用
const app = express();
const PORT = process.env.PORT || 5000;

// 日志配置
const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'combined.log' }),
    new winston.transports.Console({
      format: winston.format.simple(),
    }),
  ],
});

// 中间件
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 请求日志中间件
app.use((req, res, next) => {
  logger.info(`${req.method} ${req.url}`);
  next();
});

// 路由
app.use('/api', apiRoutes);

// 错误处理中间件
app.use((err, req, res, next) => {
  logger.error(`${err.status || 500} - ${err.message} - ${req.originalUrl} - ${req.method} - ${req.ip}`);
  res.status(err.status || 500).json({
    error: {
      message: err.message,
    },
  });
});

// 未找到路由的处理
app.use((req, res) => {
  res.status(404).json({
    error: {
      message: '未找到请求的资源',
    },
  });
});

// 连接到MongoDB数据库
const connectDB = async () => {
  try {
    // 使用环境变量中的MongoDB连接字符串
    // 在实际部署中需要设置
    const mongoURI = process.env.MONGO_URI || 'mongodb://localhost:27017/smartse';
    await mongoose.connect(mongoURI);
    logger.info('MongoDB连接成功');
  } catch (error) {
    logger.error('MongoDB连接失败:', error.message);
    process.exit(1);
  }
};

// 初始化Elasticsearch
const initElasticsearch = async () => {
  try {
    await setupElasticsearch();
    logger.info('Elasticsearch初始化成功');
  } catch (error) {
    logger.error('Elasticsearch初始化失败:', error.message);
    // 不退出进程，因为应用可以在没有Elasticsearch的情况下运行（尽管功能有限）
  }
};

// 启动服务器
const startServer = async () => {
  try {
    await connectDB();
    await initElasticsearch();
    
    app.listen(PORT, () => {
      logger.info(`服务器运行在端口 ${PORT}`);
    });
  } catch (error) {
    logger.error('服务器启动失败:', error.message);
    process.exit(1);
  }
};

startServer(); 