FROM node:14 AS build

# 设置工作目录
WORKDIR /app

# 复制前端代码和安装依赖
COPY client/package*.json ./client/
RUN cd client && npm install

# 复制后端代码和安装依赖
COPY server/package*.json ./server/
RUN cd server && npm install

# 复制所有代码
COPY . .

# 构建前端代码
RUN cd client && npm run build

# 基础镜像
FROM node:14-alpine

# 设置工作目录
WORKDIR /app

# 复制后端代码和依赖
COPY --from=build /app/server ./server
# 复制前端构建好的代码到后端的public目录
COPY --from=build /app/client/build ./server/public

# 设置工作目录为server
WORKDIR /app/server

# 暴露端口
EXPOSE 5000

# 设置环境变量
ENV NODE_ENV=production

# 启动命令
CMD ["node", "index.js"] 