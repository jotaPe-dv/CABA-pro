# Dockerfile para Node.js API
FROM node:18-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias de producción
RUN npm ci --only=production

# Copiar código fuente
COPY . .

# Exponer puerto
EXPOSE 3000

# Variables de entorno
ENV NODE_ENV=production
ENV PORT=3000
ENV SPRING_API_URL=http://springboot-api:8081

# Usuario no root por seguridad
USER node

# Comando de inicio
CMD ["node", "src/app.js"]
