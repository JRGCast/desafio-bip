# 🖥️ Frontend — Desafio BIP

Interface web para gerenciamento de benefícios empresariais com transferência dinâmica de valores.

**Stack:** Angular 21 · TypeScript 5.9 · Signals · Vitest

---

## 📋 Pré-requisitos

Certifique-se de ter instalado na sua máquina:

| Ferramenta | Versão mínima | Verificação |
|---|---|---|
| Node.js | 20 (LTS) | `node -v` |
| npm | 9+ | `npm -v` |
| Angular CLI | 21 | `ng version` |

> **Nota:** O Angular CLI não precisa ser instalado globalmente — todos os comandos podem ser executados via `npx ng` ou pelo script `npm run ng` definido no `package.json`.

---

## ⚙️ Variáveis de Ambiente

Por padrão, a aplicação aponta para o backend rodando localmente. A URL base da API está configurada em:

```
src/app/services/api.service.ts → baseUrl = 'http://localhost:8080/api/v1'
```

Certifique-se de que o **backend-module** está rodando na porta `8080` antes de iniciar o frontend. Consulte o [README do backend](../backend-module/README.md) para instruções de execução.

---

## 🚀 Como Rodar

### 1. Instalar as dependências

```bash
cd frontend
npm install
```

### 2. Iniciar o servidor de desenvolvimento

```bash
npm start
```

Ou equivalente:

```bash
ng serve
```

A aplicação estará disponível em: **http://localhost:4200**

O servidor recarrega automaticamente ao detectar alterações nos arquivos fonte.

---

## 🏗️ Estrutura do Projeto

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── beneficio-slider/   # Componente do slider de proporção de benefícios
│   │   │   └── extrato/            # Componente de listagem de transações
│   │   ├── pages/
│   │   │   └── beneficio/          # Página principal com layout e navegação
│   │   ├── services/
│   │   │   ├── api.service.ts      # Comunicação com a API REST do backend
│   │   │   └── beneficio-state.service.ts  # Estado global com Angular Signals
│   │   ├── app.config.ts           # Configuração da aplicação (providers)
│   │   ├── app.routes.ts           # Definição de rotas com lazy loading
│   │   └── app.ts                  # Componente raiz
│   ├── index.html                  # Entry point HTML
│   ├── main.ts                     # Bootstrap da aplicação
│   └── styles.css                  # Estilos globais
├── angular.json                    # Configuração do Angular CLI
├── package.json                    # Dependências e scripts
└── tsconfig.json                   # Configuração do TypeScript
```

---

## 🔌 Rotas da Aplicação

| Rota | Componente | Descrição |
|---|---|---|
| `/` | — | Redireciona para `/beneficio` |
| `/beneficio` | `BeneficioComponent` | Página principal com navegação |
| `/beneficio/gerenciar` | `BeneficioSliderComponent` | Ajuste de proporção entre benefícios via slider |
| `/beneficio/extrato` | `ExtratoComponent` | Histórico de transferências realizadas |

---

## 🧱 Funcionalidades

### Gerenciar Proporção (`/beneficio/gerenciar`)
- Exibe os dois benefícios cadastrados no backend com seus valores atuais.
- Permite redistribuir o valor total entre os dois benefícios arrastando um slider.
- Ao confirmar, executa uma transferência via API (`POST /api/v1/beneficios/transferencia`).
- Exibe mensagem de sucesso ou erro ao término da operação.

### Extrato (`/beneficio/extrato`)
- Lista todas as transferências realizadas entre benefícios.
- Exibe: data, benefício de origem, benefício de destino, valor e status da transação.

---

## 🧪 Testes

### Executar todos os testes

```bash
npm test
```

### Executar com modo `watch` (desenvolvimento)

```bash
npm run test:watch
```

Os testes utilizam **Vitest** integrado ao `@angular/build` e não requerem o backend rodando.

Saída esperada:
```
✓ BeneficioSliderComponent > alterar() - transfer benefits > should show success message after successful transfer
✓ BeneficioSliderComponent > alterar() - transfer benefits > should show error message after failed transfer
✓ BeneficioSliderComponent > refreshData() - refresh data > should show success message after successful refresh
✓ ExtratoComponent > empty transacoes > should show empty message when no transacoes

Test Files  2 passed (2)
Tests       4 passed (4)
```

---

## 🔨 Build para Produção

```bash
npm run build
```

Os artefatos compilados serão gerados em `dist/`. A build de produção aplica otimizações de performance automaticamente.

---

## ❗ Solução de Problemas

### `ERR_CONNECTION_REFUSED` ao carregar dados

O backend não está rodando. Suba o `backend-module` antes de iniciar o frontend:

```bash
cd ../backend-module
mvn spring-boot:run
```

### `Port 4200 is already in use`

Já há uma instância do `ng serve` rodando. Encerre-a primeiro:

```bash
# Encontrar o processo
lsof -i :4200

# Encerrar pelo PID
kill <PID>
```

Ou use uma porta alternativa:

```bash
ng serve --port 4201
```

### `npm install` falha com erros de permissão

Nunca execute `npm install` com `sudo`. Se houver problemas de permissão no diretório `node_modules`, remova-o e reinstale:

```bash
rm -rf node_modules
npm install
```

### Erro de CORS nas chamadas à API

Verifique se o backend está configurado para aceitar requisições de `http://localhost:4200`. O `backend-module` já tem CORS liberado para desenvolvimento.

---

## 📦 Scripts Disponíveis

| Script | Descrição |
|---|---|
| `npm start` | Inicia o servidor de desenvolvimento (`ng serve`) |
| `npm run build` | Gera a build de produção |
| `npm run watch` | Build em modo watch (desenvolvimento) |
| `npm test` | Executa os testes unitários com Vitest |
| `npm run test:watch` | Testes em modo watch |
| `npm run doc` | Gera documentação da API com Compodoc |

---

## 📚 Documentação da API

Gerar documentação HTML a partir dos comentários JSDoc no código:

```bash
npm run doc
```

Abrir no navegador após gerar:

```bash
npm run doc -- --serve
```

A documentação será gerada em `documentation/` e estará disponível em **http://localhost:6060**.
