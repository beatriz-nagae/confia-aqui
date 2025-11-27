<div align="center">
  <img src="../docs/Logo.png" width="150" alt="Logo" />
  <h1>Documentação – Backend</h1>
</div>

Este é o backend do projeto **Confia Aqui**, desenvolvido utilizando uma arquitetura de **microsserviços em Java com Spring Boot**, com o objetivo de fornecer APIs REST responsáveis por **autenticação, gerenciamento de usuários, FAQ e gamificação**. Baseados em um banco PostgreSQL hospedado na Azure Database for PostgreSQL.

Cada domínio do sistema é separado em um microserviço para garantir **escalabilidade**, **boa organização** e **manutenção simplificada**.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Arquitetura MVC (Model-View-Controller)**
- **API RESTful**
- **Maven / Maven Wrapper**
- **Banco PostgreSQL (Azure)**
- **Docker (MailHog)**

## 🧩 Arquitetura do backend (microsserviços)
```txt
backend/
├── .mvn/ → Configurações Maven Wrapper
├── auth-service → Serviço de autenticação e geração de tokens JWT 
├── common-lib → Biblioteca compartilhada (DTOs, utilitários, validações)
├── faq-service → Gerenciamento das perguntas frequentes (FAQ)
├── game-service → Regras de gamificação (pontos, níveis, badges)
├── user-service → CRUD de usuários
├── postman → Collection pronta para importação no Postman
├── .env → Variáveis de ambiente
├── mvnw / mvnw.cmd → Scripts Maven Wrapper
└── pom.xml → POM raiz com módulos
```
Cada serviço possui seu próprio `pom.xml`e porta de execução.

## 🧠 Conceitos Aplicados

- **Arquitetura de microsserviços**  
  Cada domínio tem sua própria API, banco e responsabilidades.

- **Camadas MVC (Controller → Service → Repository)**  
  Mantém código organizado e fácil de manter, separando: Controller, Service, Repository e Entity.  

- **DTOs para troca de dados**  
  Padronizam a comunicação entre serviços e com o frontend.

- **Autenticação via JWT**  
  O auth-service gera e valida tokens utilizados nos outros serviços.

- **Common Library**  
  Evita duplicação de código e mantém padronização entre microserviços.

- **Injeção de dependência com Spring**  
  Objetos são injetados automaticamente, evitando `new` manual e promovendo desacoplamento.    

- **Tratamento de exceções customizadas**  
  Internamente, erros são tratados de forma padronizada.
                                                                  

## 📬 Como Instalar e Rodar o MailHog (Docker)

O projeto utiliza o **MailHog** para simular o envio de e-mails em ambiente de desenvolvimento, especialmente no fluxo de **recuperação de senha**.  
Ele permite visualizar todos os e-mails enviados localmente sem necessidade de um servidor real de SMTP.

### 1. Instalar o Docker (caso ainda não tenha)
Para usar o MailHog, é necessário ter o **Docker** instalado.

👉 Download: https://www.docker.com/products/docker-desktop/

Após instalar, verifique se está funcionando:
```bash
docker --version
```
### 2. Rodar o MailHog com Docker
Execute o seguinte comando:
```bash
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```
Isso faz com que:
- 1025 → Porta SMTP usada pelos microsserviços
- 8025 → Interface web para visualizar os e-mails recebidos

### 3. Acessar o Painel do MailHog
Após subir o container, abra:

http://localhost:8025

Neste painel você verá todos os e-mails enviados pelo backend, como:
- Link de redefinição de senha

## 📌 Observações Importantes
- Cada serviço tem seu próprio banco PostgreSQL no Azure.
- O .env deve estar na raiz do backend.
- As comunicações (quando necessárias) ocorrem via HTTP entre serviços.
- Portas usadas:

  | Serviço           | Porta |
  |------------------|-------|
  | **auth-service**  | 8083  |
  | **user-service**  | 8081  |
  | **faq-service**   | 8082  |
  | **game-service**  | 8090  |
> ⚠️ Atenção: Caso modifique a porta, ajuste no application.properties de cada serviço.


## 🛠️ Como Rodar o Backend

### 1. Clone o repositório
```bash
git clone https://github.com/AndradeRayssa/confia-aqui.git
```

### 2. Configure as variáveis de ambiente

Cada microserviço utiliza seu próprio banco.
Defina as variáveis no sistema ou em um .env:
```txt
# JWT Configurações
JWT_SECRET=
JWT_EXPIRATION_MS=

# Usuário e senha do banco 
DB_USER=
DB_PASS=

# URLs dos bancos no Azure
FAQ_DB_URL=
USER_DB_URL=
REDEFINIR_SENHA_DB_URL=
GAME_DB_URL=
```

### 3. Acesse a pasta do projeto dentro do diretório:

```bash
cd backend
```
### 4. Compile o projeto com Maven para baixar dependências e gerar os arquivos necessários:
```bash
./mvnw clean install
```
### 5. Execute a aplicação Spring Boot para iniciar a API localmente de cada serviço:
```bash
cd auth-service
```
```bash
./mvnw spring-boot:run
```
Repita para:
- user-service
- faq-service
- game-service
  
### 6. Como testar os endpoints:
Para testar o backend do **Confia Aqui** no Postman ou Insomnia, siga a ordem recomendada abaixo.  
Essa sequência garante que **os dados necessários existam antes de cada operação**, evitando erros de integridade, autenticação ou referência cruzada entre microserviços.

#### 1. Rotas

| Serviço          | Responsabilidade          | Rotas |
| ---------------- | ------------------------- | ----- |
| **auth-service** | Login / autenticação      | `http://localhost:8083/api/auth/`|
| **user-service** | Gerenciamento de usuários | `http://localhost:8081/api/user/`|
| **faq-service**  | Perguntas frequentes      | `http://localhost:8082/api/admin/faq`|
| **game-service** | Badges, pontos, missões   | ` http://localhost:8090/quiz/`|

#### 2. Cadastrar usuários
No sistema, o administrador já é criado automaticamente via código, então você precisará cadastrar apenas usuários comuns.

Rota *POST*:
```txt
http://localhost:8083/api/auth/cadastrarUsuario
```
> 💡 Observação: Esta rota não precisa de token.

#### 3. Fazer login
Antes de acessar a maioria das rotas, obtenha o token JWT:

Rota *POST*:
```txt
  http://localhost:8083/api/auth/login
```
Após fazer login, copie o **token JWT** retornado e use em todas as rotas protegidas:
```txt
Header → Authorization: Bearer SEU_TOKEN_AQUI
```
> ⚠️ Atenção:
> - Token de usuário comum não acessa rotas admin.
> - Token de admin não acessa rotas de usuário comum.

#### 4. Após o login
Todas as rotas detalhadas (corpos, headers e exemplos) podem ser importadas diretamente no Postman através da collection disponível no repositório:

📁 [Arquivo da collection](postman/confia-aqui-collection.json)

Para utilizar:

    1. Abra o Postman  
    2. Vá em **File → Import**  
    3. Selecione o arquivo **confia-aqui-collection.json**  
    4. A collection completa aparecerá automaticamente com todas as rotas já organizadas:
      - API-ADMIN  
      - API-USUARIO  
      - API-GAME  

## 📝 Contribuições

| Nome           | O que fez                                                                                                           |
|----------------|---------------------------------------------------------------------------------------------------------------------|
| Beatriz Nagae  | Microsserviço: game-service                                                                                         |
| Rayssa Andrade | Criação do backend, microsserviços: auth, user, faq, configuração do MailHog/Docker e documentação backend (README) |
