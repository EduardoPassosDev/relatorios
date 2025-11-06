# Estágio 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY src ./src

# Compila o projeto (pula os testes para build mais rápido)
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final (mais leve)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o JAR compilado do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Cria diretório para relatórios temporários
RUN mkdir -p /tmp/relatorios

# Expõe a porta da aplicação
EXPOSE 8080

# Variáveis de ambiente (podem ser sobrescritas no docker-compose)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando para iniciar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]