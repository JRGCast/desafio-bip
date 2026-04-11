# 🏗️ Backend Module — Desafio BIP

API REST para gerenciamento de benefícios empresariais com transferência segura de valores.

**Stack:** Java 17 · Spring Boot 3.2.5 · PostgreSQL · Lombok · Swagger/OpenAPI

---

## 📋 Pré-requisitos

Certifique-se de ter instalado na sua máquina:

| Ferramenta | Versão mínima | Verificação |
|---|---|---|
| Java (JDK) | 17 | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| PostgreSQL | 14+ | `psql --version` |

---

## 🐘 Configuração do PostgreSQL

O projeto usa PostgreSQL como banco de dados. Você precisa criar o banco e o usuário antes de rodar a aplicação.

### 1. Acesse o PostgreSQL como superusuário

```bash
sudo -u postgres psql
```

### 2. Crie o usuário e o banco

```sql
CREATE USER bip WITH PASSWORD 'bip';
CREATE DATABASE bipdb OWNER bip;
GRANT ALL PRIVILEGES ON DATABASE bipdb TO bip;
\q
```

### 3. Verifique a conexão

```bash
psql -h localhost -U bip -d bipdb
```

Se conectou com sucesso, o banco está pronto. Digite `\q` para sair.

> **Nota:** O schema (`tb_beneficio`, `tb_transacao`) e os dados iniciais são criados **automaticamente** pelo Spring Boot ao subir a aplicação — você não precisa rodar nenhum script SQL manualmente.

---

## ⚙️ Variáveis de Ambiente

Por padrão, a aplicação usa as seguintes configurações (definidas em `src/main/resources/application.yml`):

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_USER` | `bip` | Usuário do PostgreSQL |
| `DB_PASSWORD` | `bip` | Senha do PostgreSQL |

Para sobrescrever em produção ou num ambiente diferente:

```bash
export DB_USER=meu_usuario
export DB_PASSWORD=minha_senha
mvn spring-boot:run
```

---

## 🚀 Como Rodar

### Compilar e rodar os testes

```bash
cd backend-module
mvn clean install
```

Saída esperada:
```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Subir a aplicação

```bash
mvn spring-boot:run
```

Saída esperada:
```
Tomcat started on port 8080
Started BackendApplication in ~3 seconds
```

A aplicação estará disponível em: **http://localhost:8080**

---

## 📖 Documentação da API (Swagger)

Com a aplicação rodando, acesse:

**http://localhost:8080/swagger-ui.html**

---

## 🔌 Endpoints disponíveis

### Benefícios — `/api/v1/beneficios`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/v1/beneficios` | Lista todos os benefícios ativos |
| `GET` | `/api/v1/beneficios/{id}` | Busca um benefício por ID |
| `POST` | `/api/v1/beneficios` | Cria um novo benefício |
| `PUT` | `/api/v1/beneficios/{id}` | Atualiza nome, descrição e valor |
| `DELETE` | `/api/v1/beneficios/{id}` | Desativa um benefício (soft-delete) |
| `PATCH` | `/api/v1/beneficios/{id}/status` | Ativa ou desativa diretamente (`true`/`false`) |
| `POST` | `/api/v1/beneficios/transferencia` | Transfere valor entre dois benefícios |

### Transações — `/api/v1/transacoes`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/v1/transacoes` | Extrato global de todas as transferências |
| `GET` | `/api/v1/transacoes/{id}` | Recibo de uma transferência específica |
| `GET` | `/api/v1/transacoes/beneficio/{id}` | Extrato de um benefício (origem ou destino) |

---

## 📦 Exemplos de Request

### Criar benefício
```http
POST /api/v1/beneficios
Content-Type: application/json

{
  "nome": "Vale Refeição",
  "descricao": "Benefício de alimentação",
  "valor": 800.00
}
```

### Transferir valor
```http
POST /api/v1/beneficios/transferencia
Content-Type: application/json

{
  "fromId": 1,
  "toId": 2,
  "amount": 300.00
}
```

### Ativar / Desativar
```http
PATCH /api/v1/beneficios/1/status
Content-Type: application/json

false
```

---

## ❗ Solução de Problemas

### `password authentication failed for user "bip"`
O usuário PostgreSQL não existe. Siga o passo [Configuração do PostgreSQL](#-configuração-do-postgresql).

### `Port 8080 was already in use`
Já há uma instância rodando. Pare-a primeiro:
```bash
# Encontrar o processo
lsof -i :8080

# Encerrar pelo PID
kill <PID>
```

### `HHH90000025: PostgreSQLDialect does not need to be specified`
Aviso inofensivo do Hibernate — pode ser ignorado. Para suprimi-lo, remova a linha `database-platform` do `application.yml`.

### Schema não encontrado (`SchemaManagementException`)
O banco `bipdb` existe mas o schema não foi criado. Verifique se `spring.sql.init.mode=always` está no `application.yml` e que o usuário `bip` tem permissão de `CREATE TABLE` no banco.

---

## 🧪 Rodar apenas os testes

```bash
mvn test
```

Os testes unitários usam **Mockito** e não requerem PostgreSQL rodando.
