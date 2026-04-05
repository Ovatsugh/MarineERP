# MarineERP

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-7.x-brightgreen?logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?logo=postgresql)
![License](https://img.shields.io/badge/License-MIT-yellow)

Um ERP voltaro para o gerenciamento de lojas, permite cadastro de produtos, clientes, vendas e controle de acesso por empresa. Idealmente desenvolvido pensando em uma concessionária, mas flexivel para outros setores.

## Tecnologias

- Java 17
- Spring Boot 4.x
- Spring Security 7.x + JWT (jjwt 0.12.6)
- Spring Data JPA + Hibernate
- PostgreSQL
- Flyway
- Lombok
- SpringDoc OpenAPI 3 (Swagger UI)

## Autenticação

Autenticação stateless via JWT. O token deve ser enviado no header:

```
Authorization: Bearer <token>
```

| Endpoint            | Acesso    | Descrição                               |
|---------------------|-----------|-----------------------------------------|
| POST /auth/login    | Público   | Retorna JWT                             |
| POST /auth/register | Developer | Cria usuário ADMIN vinculado a empresa  |

## Perfis de acesso

| Role      | Permissões                                                |
|-----------|-----------------------------------------------------------|
| DEVELOPER | `/companies/**`, `/auth/register`                         |
| ADMIN     | `/users/**`, `/customers/**`, `/products/**`, `/sales/**` |
| EMPLOYEE  | Rotas autenticadas                                        |

Todos os dados são isolados por empresa — cada usuário só acessa registros da sua própria empresa.

## Domínios

- **Company** — cadastro de empresas
- **User** — usuários vinculados a uma empresa
- **Customer** — clientes
- **Product** — produtos com preço e estoque
- **Sale** — vendas com itens e cálculo automático de valor total

## Configuração

Copie o arquivo de exemplo e preencha as variáveis:

```bash
cp .env.example .env
```

| Variável                 | Descrição                        |
|--------------------------|----------------------------------|
| `JWT_SECRET`             | Chave secreta para assinar o JWT |
| `DB_URL`                 | URL JDBC do banco PostgreSQL     |
| `DB_USERNAME`            | Usuário do banco                 |
| `DB_PASSWORD`            | Senha do banco                   |

### Desenvolvimento 

```bash
docker compose -f docker-compose.yml -f docker-compose-dev.yml up --build
```

### Producão

```bash
docker compose up --build
```

A documentação interativa estará disponível em `http://localhost:8080/swagger-ui/index.html`.

