# Predial Notícias

Aplicação de notícias com frontend HTML conectado à API REST Spring Boot e persistência PostgreSQL. O banco remoto é hospedado no Neon; as migrações Flyway criam as tabelas e as categorias iniciais.

## Requisitos

- Java 21
- Maven 3.9+
- Node.js e Neon CLI apenas para configurar o projeto Neon

## Neon e execução local

O projeto está vinculado ao Neon por `.neon` e `neon.ts`. Para obter ou atualizar as variáveis locais, execute `neon env pull` na raiz do repositório. O arquivo raiz `.env.local` é ignorado pelo Git; não publique nem compartilhe seus valores.

O backend lê `DATABASE_URL` desse arquivo, converte a URL do Neon para JDBC e extrai as credenciais sem mantê-las na URL JDBC. Variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` explícitas têm precedência, permitindo usar outro PostgreSQL local.

Na raiz do repositório, inicie o backend:

```powershell
mvn -f backend\pom.xml spring-boot:run
```

O servidor inicia em `http://localhost:8080`. Verifique a conexão com o Neon em `/api/health`; a resposta deve ser `{"status":"up"}`.

Para criar o primeiro administrador, configure `BOOTSTRAP_ADMIN_USERNAME`, `BOOTSTRAP_ADMIN_EMAIL` e `BOOTSTRAP_ADMIN_PASSWORD` no ambiente antes da primeira execução. A senha precisa ter ao menos 12 caracteres e é armazenada com BCrypt. Após a criação, remova essas variáveis. A criação inicial é feita pelo backend, não pelo formulário público.

## Frontend e API

O HTML chama `GET /api/news`, `GET /api/categories` e `GET /api/auth/me`. O login usa sessão do Spring Security; operações de publicação, edição, upload e exclusão passam pela API e enviam token CSRF. `ADMIN` pode excluir; `EDITOR` pode publicar e editar.

Por padrão, o frontend usa a API na mesma origem. Se hospedado em outro domínio, defina `window.PREDIAL_API_BASE_URL` antes do script ou preencha o `<meta name="api-base-url">` no HTML com a URL base da API. Configure no backend a origem exata do frontend em `CORS_ALLOWED_ORIGINS` (uma ou mais origens separadas por vírgula). Em HTTPS entre domínios diferentes, configure também:

```text
SESSION_COOKIE_SECURE=true
SESSION_COOKIE_SAME_SITE=none
```

Sem uma origem permitida, requisições cross-origin são bloqueadas. Não use `*` com credenciais.

Principais rotas:

- `GET /api/news` e `GET /api/news/{id}`: notícias publicadas.
- `GET /api/categories`: categorias.
- `GET /_blob/{id}`: imagem armazenada no PostgreSQL.
- `GET /api/auth/csrf`, `GET /api/auth/me`, `POST /api/auth/login` e `POST /api/auth/logout`: CSRF e sessão.
- `GET /api/admin/news`, `POST /api/admin/news`, `PUT /api/admin/news/{id}` e `DELETE /api/admin/news/{id}`: operações administrativas.
- `POST /api/admin/assets`: upload de imagem multipart (JPEG, PNG, WEBP ou GIF; até 3 MB).

## Validação

```powershell
mvn -f backend\pom.xml verify
```

Este é um projeto Java/Spring Boot sem etapa de build JavaScript; o `npm run build` genérico não se aplica. O `package.json` da raiz é usado pela configuração da CLI Neon.
