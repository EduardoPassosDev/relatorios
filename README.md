# 📊 Sistema de Relatórios Assíncronos

Sistema desenvolvido em **Spring Boot 3 + Java 17** para geração assíncrona de relatórios de vendas em formato XLS, com envio automático por e-mail utilizando **RabbitMQ** para mensageria.

## 🎯 Objetivo

Resolver o problema de timeout ao gerar relatórios grandes, processando-os de forma assíncrona através de filas.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (Autenticação)
- **Spring Data JPA** (Persistência)
- **MySQL 8.0** (Banco de Dados)
- **RabbitMQ 3.12** (Mensageria)
- **Apache POI** (Geração de arquivos Excel)
- **Thymeleaf** (Templates HTML)
- **Docker & Docker Compose** (Containerização)
- **Nginx** (Proxy Reverso)
- **MailTrap** (Envio de e-mails em ambiente de teste)

## 📋 Pré-requisitos

- Docker Desktop instalado
- Docker Compose instalado
- Conta no MailTrap (gratuita) - https://mailtrap.io
- Git instalado

## ⚙️ Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/EduardoPassosDev/relatorios.git
cd relatorios
```

### 2. Configure o MailTrap

1. Acesse https://mailtrap.io e crie uma conta gratuita
2. Vá em **Email Testing** > **Inboxes** > **My Inbox**
3. Clique em **SMTP Settings**
4. Copie o **Username** e **Password**
5. Edite o arquivo `docker-compose.yml` e substitua:

```yaml
SPRING_MAIL_USERNAME: seu_usuario_mailtrap  # ← Cole aqui
SPRING_MAIL_PASSWORD: sua_senha_mailtrap    # ← Cole aqui
```

## 🐳 Executando com Docker

### Subir toda a aplicação

```bash
docker-compose up -d
```

Isso irá subir:
- ✅ MySQL (porta 3306)
- ✅ RabbitMQ (porta 5672 e Management UI na 15672)
- ✅ Aplicação Spring Boot (porta 8080)
- ✅ Nginx (porta 80)

### Ver logs da aplicação

```bash
docker-compose logs -f app
```

### Verificar status dos containers

```bash
docker-compose ps
```

### Parar todos os containers

```bash
docker-compose down
```

### Parar e remover volumes (apaga dados do banco)

```bash
docker-compose down -v
```

## 🌐 Acessando a Aplicação

### Interface Principal
- **URL:** http://localhost (ou http://localhost:80)
- **Acesso Direto:** http://localhost:8080 (sem Nginx)

### Credenciais de Login
- **Email:** `miguel@empresa.com`
- **Senha:** `123456`

### RabbitMQ Management
- **URL:** http://localhost:15672
- **Usuário:** `admin`
- **Senha:** `admin123`

## 📖 Como Usar

1. **Login:** Acesse http://localhost e faça login com as credenciais acima
2. **Filtrar Vendas:** Use os filtros de Unidade e Ano para buscar vendas específicas
3. **Visualizar:** A tela mostra os 8 primeiros registros da busca
4. **Gerar Relatório:** Clique em "📧 Gerar XLS e Enviar por E-mail"
5. **Aguardar:** O sistema envia a solicitação para a fila do RabbitMQ
6. **Receber Email:** Verifique o MailTrap inbox - o relatório será enviado em segundos

## 🏗️ Estrutura do Projeto

```
relatorios-async/
├── src/
│   ├── main/
│   │   ├── java/com/miguel/relatorios/
│   │   │   ├── RelatoriosAsyncApplication.java
│   │   │   ├── config/
│   │   │   │   └── RabbitMQConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── LoginController.java
│   │   │   │   └── DashboardController.java
│   │   │   ├── model/
│   │   │   │   ├── Usuario.java
│   │   │   │   └── Venda.java
│   │   │   ├── repository/
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   └── VendaRepository.java
│   │   │   ├── service/
│   │   │   │   ├── VendaService.java
│   │   │   │   ├── RelatorioService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   └── RabbitMQService.java
│   │   │   ├── dto/
│   │   │   │   ├── LoginDTO.java
│   │   │   │   ├── FiltroRelatorioDTO.java
│   │   │   │   ├── RelatorioMessageDTO.java
│   │   │   │   └── VendaDTO.java
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── SHA1PasswordEncoder.java
│   │   │   └── consumer/
│   │   │       └── RelatorioConsumer.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   │           ├── login.html
│   │           ├── dashboard.html
│   │           └── relatorio-enviado.html
├── Dockerfile
├── docker-compose.yml
├── nginx.conf
├── init-db.sql
├── .dockerignore
├── pom.xml
└── README.md
```

## 🔄 Fluxo da Aplicação

1. **Login:** Usuário faz login com email/senha
2. **Dashboard:** Sistema exibe vendas com filtros (8 por página)
3. **Solicitar Relatório:** Usuário clica em "Gerar XLS e Enviar por E-mail"
4. **Fila RabbitMQ:** Sistema envia mensagem para a fila com os filtros
5. **Consumer:** Consumer escuta a fila e processa a mensagem
6. **Gerar XLS:** Consumer gera arquivo Excel com Apache POI
7. **Enviar Email:** Consumer envia email com anexo via MailTrap
8. **Limpar:** Consumer remove arquivo temporário

## 🧪 Testando

### Testar Diferentes Filtros

1. **Todas as vendas:** Deixe todos os filtros em branco
2. **Por unidade:** Selecione "Salvador", "Feira de Santana" ou "Lauro de Freitas"
3. **Por ano:** Selecione "2024" ou "2025"
4. **Combinado:** Selecione unidade E ano

### Verificar RabbitMQ

1. Acesse http://localhost:15672
2. Login: `admin` / `admin123`
3. Vá em **Queues** e veja a fila `relatorios.queue`
4. Você pode ver mensagens sendo processadas em tempo real

### Verificar Email no MailTrap

1. Acesse https://mailtrap.io
2. Vá em **Email Testing** > **Inboxes** > **My Inbox**
3. Você verá o email com o relatório anexado

## 🐛 Troubleshooting

### Erro ao conectar no MySQL
```bash
# Aguarde o MySQL inicializar completamente (pode levar 30-60 segundos)
docker-compose logs mysql
```

### Erro ao conectar no RabbitMQ
```bash
# Verifique se o RabbitMQ está rodando
docker-compose ps rabbitmq
docker-compose logs rabbitmq
```

### Aplicação não inicia
```bash
# Veja os logs detalhados
docker-compose logs -f app
```

### Rebuild da aplicação
```bash
# Rebuild completo
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### Resetar banco de dados
```bash
# Remove volumes e recria tudo
docker-compose down -v
docker-compose up -d
```

## 📊 Dados de Teste

O banco vem populado com:
- **1 usuário:** Miguel (miguel@empresa.com / 123456)
- **30 vendas:** 15 em 2024 e 15 em 2025
- **3 unidades:** Salvador, Feira de Santana, Lauro de Freitas

## 🚀 Deploy em Produção

Para deploy em VPS/Cloud:

1. **Configurar domínio no nginx.conf**
2. **Adicionar SSL/HTTPS** (Let's Encrypt)
3. **Alterar senhas** do MySQL e RabbitMQ
4. **Configurar SMTP real** (ao invés do MailTrap)
5. **Ajustar variáveis de ambiente** no docker-compose.yml

## 📝 Critérios de Aceite Atendidos

- ✅ Sistema protegido por autenticação (login/senha)
- ✅ Banco de dados MySQL com tabelas usuarios e vendas
- ✅ Filtros por unidade e ano
- ✅ Lista 8 primeiros registros
- ✅ Botão para gerar relatório e enviar por email
- ✅ Mensagem enviada para fila RabbitMQ
- ✅ Consumer escutando a fila
- ✅ Geração de relatório XLS real
- ✅ Envio de email via MailTrap (não real)
- ✅ Dockerizado (app, banco, RabbitMQ)
- ✅ Docker Compose configurado
- ✅ Volumes para persistência de dados
- ✅ Nginx como proxy reverso

## 👥 Equipe

- Eduardo Passos
- Gustavo Centenno

## 📅 Data de Entrega

**16 de Novembro de 2025**

**Desenvolvido com ❤️ usando Spring Boot e Docker**
