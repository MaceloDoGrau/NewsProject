# Predial Notícias API

Backend REST separado do HTML existente. A aplicação usa Java 21, Spring Boot e PostgreSQL; o frontend não é alterado por este projeto.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL

## Configuração e execução

Configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` no ambiente. Por padrão, `DB_URL` aponta para `jdbc:postgresql://localhost:5432/predial_news`. As migrações Flyway criam o esquema e as categorias já usadas pela página.

Para criar o primeiro administrador, configure também `BOOTSTRAP_ADMIN_USERNAME`, `BOOTSTRAP_ADMIN_EMAIL` e `BOOTSTRAP_ADMIN_PASSWORD` antes da primeira execução. A senha precisa ter ao menos 12 caracteres e é armazenada com BCrypt. O bootstrap não é uma rota pública; após a criação, remova essas variáveis. Inicializações posteriores com a mesma identidade não alteram a senha existente.

Em desenvolvimento local, mantenha `SESSION_COOKIE_SECURE=false`; em HTTPS, defina `SESSION_COOKIE_SECURE=true`.

No PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/predial_news"
$env:DB_USERNAME = "predial_news"
$env:DB_PASSWORD = "<senha do banco>"
$env:BOOTSTRAP_ADMIN_USERNAME = "<usuario administrador>"
$env:BOOTSTRAP_ADMIN_EMAIL = "<email administrador>"
$env:BOOTSTRAP_ADMIN_PASSWORD = "<senha de pelo menos 12 caracteres>"
mvn spring-boot:run
```

Para executar os testes: `mvn test`.

## API alinhada à página

A página HTML continua usando o runtime Claude. Estes contratos reproduzem os campos e as operações que o script já monta, para a ligação futura sem mudar o layout.

- `GET /api/news` e `GET /api/news/{id}`: notícias publicadas. O `id` é texto. Cada item traz `title`, `category`, `kind`, `summary`, `body`, `date` (`aaaa-mm-dd`), `overlay`, `imageId`, `featured`, `popular`, `readMin`, `createdAt` e `updatedAt`.
- `GET /api/categories`: categorias já usadas pelos filtros da página (`Segurança`, `Tecnologia`, `Condomínios`).
- `GET /_blob/{id}`: bytes da foto de capa. É o caminho que a função `img()` da página já usa.
- `GET /api/auth/csrf`: token CSRF antes de operações protegidas.
- `POST /api/auth/login`: sessão com `username` e `password`.
- `POST /api/auth/logout`: encerra a sessão; CSRF também é obrigatório.
- `POST /api/admin/assets`: upload `multipart` no campo `file` (JPEG, PNG, WEBP ou GIF, até 3 MB). A resposta é `{ "id": "..." }`, no mesmo formato de `assets.upload`.
- `POST /api/admin/news`: cria e já publica. O corpo aceita o objeto da página (`title`, `category`, `kind`, `summary`, `body`, `date`, `overlay`, `imageId`, `featured`, `popular`). `readMin`, `createdAt` e `updatedAt` enviados a mais são ignorados; o servidor recalcula o tempo de leitura e as datas.
- `PUT /api/admin/news/{id}`: substitui a notícia, como o `set` da página.
- `PATCH /api/admin/news/{id}`: atualização parcial. `{ "featured": false }` cobre o ajuste das demais notícias quando uma vira destaque. O servidor também zera o destaque anterior ao gravar `featured: true`.
- `DELETE /api/admin/news/{id}`: exclui a notícia.

Criar, editar, enviar foto e excluir exigem sessão de `ADMIN` ou `EDITOR` e token CSRF. O cookie de sessão é HttpOnly e SameSite=Lax.

O HTML ainda não chama estes endereços. Enquanto o script depender de `claude.use("db")`, `claude.use("user")` e `claude.use("assets")`, abrir a página continua mostrando o banco do Claude como indisponível.
