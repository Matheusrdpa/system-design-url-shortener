# URL Shortener

Um encurtador de URLs que eu construí para estudar system design na prática, explorando preocupações mais reais de backend como geração de codigos curtos, cache, rate limiting, deploy e integracao entre servicos.

Além da API, o projeto também tem uma interface web para criar links de forma rapida e testar o fluxo completo de ponta a ponta.

## O que tem aqui

- API REST em `Java 21 + Spring Boot`
- Persistencia com `PostgreSQL`
- `Redis` para cache e controle de fluxo
- Frontend em `React + Vite + Tailwind CSS`
- Containerização com `Docker`
- Deploy e infraestrutura na `AWS`

## Tecnologias que merecem destaque

Esse projeto foi onde eu usei e conectei, na prática:

- `Spring Boot 4`
- `Spring Data JPA`
- `Spring Data Redis`
- `PostgreSQL`
- `Redis`
- `Docker`
- `Amazon EC2`
- `Application Load Balancer (ALB)`
- `Amazon S3`
- `Amazon RDS`
- `Amazon ElastiCache`

## O problema que eu quis resolver

O objetivo nao era so "encurtar links". Eu queria usar um projeto pequeno o bastante para construir sozinho, mas rico o suficiente para estudar alguns pontos importantes de arquitetura:

- geração de identificadores curtos em `Base62`
- persistencia e consulta rapida de URLs
- cache para reduzir leituras repetidas no banco
- rate limiting para proteger o endpoint de redirecionamento
- separação entre frontend e backend
- preparação para rodar em ambiente cloud com variaveis de ambiente
- deploy de uma aplicacao full stack usando servicos gerenciados da AWS

## Como a aplicacao funciona

1. O usuario envia uma URL original para a API.
2. A aplicacao valida e normaliza essa URL.
3. O link é salvo no PostgreSQL.
4. O `id` gerado no banco é convertido para um codigo curto em `Base62`.
5. A API devolve a URL encurtada.
6. Quando alguem acessa o codigo curto, a aplicacao:
   - verifica o rate limit no Redis
   - busca a URL original no cache
   - se não encontrar, consulta o banco e popula o cache
   - responde com redirecionamento `302`

## Infraestrutura e deploy

Esse projeto tambem serviu para eu praticar a parte de infraestrutura, e nao so a implementação da aplicação.

- O backend roda em `Amazon EC2`
- O trafego passa por um `Application Load Balancer (ALB)`
- O frontend foi publicado no `Amazon S3`
- O banco relacional roda no `Amazon RDS`
- O Redis roda no `Amazon ElastiCache`

Foi uma boa forma de entender melhor como uma aplicação simples ganha outra complexidade quando sai do ambiente local e passa a depender de rede, configuração, serviços gerenciados e distribuicao de trafego.

## Estrutura do projeto

```text
.
|-- src/main/java/com/url/shortener
|   |-- controllers
|   |-- services
|   |-- repositories
|   |-- entities
|   `-- config
|-- src/main/resources
|-- frontend
|   `-- src
`-- Dockerfile
```

## Endpoints principais

### Criar URL curta

`POST /url`

Exemplo de body:

```json
{
  "originalUrl": "https://github.com/"
}
```

Resposta:

```text
https://lazyurl.dev/abc123 (Ou localhost em ambiente local)
```

### Redirecionar para a URL original

`GET /{code}`

Exemplo:

```text
GET /abc123
```

Resposta:

- `302 Found` redirecionando para a URL original

## Como rodar localmente

### Backend

Defina as variaveis de ambiente:

```env
SPRING_DATA_URL=jdbc:postgresql://localhost:5432/shortener
SPRING_DATA_USERNAME=postgres
SPRING_DATA_PASSWORD=postgres
redisURL=localhost
awsURL=http://localhost:8080/
```

Depois rode:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

### Frontend

Entre na pasta `frontend` e rode:

```bash
npm install
npm run dev
```

Se quiser apontar o frontend para outra URL da API:

```env
VITE_API_URL=http://localhost:8080
```

## Docker

O projeto possui `Dockerfile` para empacotar a aplicacao Java:

```bash
./mvnw clean package
docker build -t shortener .
docker run -p 8080:8080 shortener
```

## O que eu aprendi construindo esse projeto

Mais do que framework, esse projeto me ajudou a consolidar alguns conceitos que aparecem bastante em cenarios reais:

- quando vale usar banco relacional e quando faz sentido adicionar Redis
- como reduzir custo de leitura com estratégia de cache
- como um detalhe simples como geracao de codigo pode impactar legibilidade e escalabilidade
- como pensar em proteção basica de abuso com rate limiting
- como publicar frontend e backend separadamente na AWS
- como usar `ALB` na frente da aplicacao para distribuição de trafego
- como integrar aplicacao Java com `RDS` e `ElastiCache`
- como servir uma aplicacao frontend estaticamente com `S3`
- como pensar a infraestrutura junto com a aplicacao, e não só o codigo


## Observação

Esse projeto foi construido com foco em aprendizado. A ideia foi usar um problema simples para praticar decisoes de arquitetura, integracao entre tecnologias e trade-offs comuns em sistemas web.
