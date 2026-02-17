# 构建上下文为项目根目录（与 daming-admin、daming-front 同级）
FROM node:18-alpine AS builder
WORKDIR /app

COPY daming-admin/ruoyi-ui/package*.json ./
RUN npm ci --prefer-offline --no-audit --registry=https://registry.npmmirror.com || npm install --registry=https://registry.npmmirror.com

COPY daming-admin/ruoyi-ui ./
RUN npm run build:prod

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY docker/ruoyi-ui/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
