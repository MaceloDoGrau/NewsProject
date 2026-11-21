# Backend Predial Notícias

Este diretório contém a API Spring Boot, as migrações Flyway e a imagem Docker de produção.

A documentação completa de arquitetura, variáveis de ambiente, execução local, banco, API, autenticação, deploy e testes está no [README da raiz](../README.md).

## Atalhos

Execute da raiz do repositório:

```powershell
mvn -f backend\pom.xml spring-boot:run
mvn -f backend\pom.xml verify
```

O serviço de produção é implantado no Render como Web Service Docker com `backend` como Root Directory. Configure as credenciais e demais variáveis no ambiente do Render; não as salve no repositório.
