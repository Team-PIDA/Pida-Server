# 🌸 Pida Server

---

## 💻 Tech Stack

- Spring Boot 3.4.x
- Spring Data JPA
- Java 21
- Kotlin 2.1.x
- Kotlin JDSL
- ktlint
- OpenFeign
- Gradle 8.12.1
- PostgreSQL 17.2 (+ PostGIS)
- Redis 7.1.0

---
## 🗺️ Cloud Architecture

- ECS + Fargate 구조 컨테이너 서비스로 구성.
- RDS를 사용하며 Postgres 데이터베이스를 구성.
- ElastiCache를 사용하여 Redis를 구성.

[//]: # (TODO: 클라우드 아키텍처 이미지)

---
## 🏰️ Architecture

본 프로젝트는 3-Tier Layerd Architecture 와 Clean Architecture를 적절히 조합한 아키텍처 위에 설계되어 있으며,
`Database`는 Application Layer가 아니므로 4-Tier로 설명하는 것은 올바르지 않다고 간주합니다.

### Presentation Layer

`:core:core-api` 모듈이 해당하는 계층이며, Spring Boot Application 및 Controller가 위치합니다.
Application의 가장 상단부에 위치하고 외부의 요청에 대한 응답과 처리하는 계층입니다.

> **주요 Annotation** : `@RestController` , `@Configuration`
>
> API의 경우 Filter, Interceptor, Controller가 해당하며 Batch 등 실행가능한 Application이 속합니다.



### Business Layer

`:core:core-domain` 모듈이 해달하는 계층이며, 일반적으로 우리가 부르는 서비스를 의미합니다.

서비스의 경우, 최대한 비즈니스 로직을 설명하는 데에 집중하며
자세한 구현은 `Component Layer` 로 위임해 비즈니스 로직이 침해받지 않는 것에 집중합니다.

> **주요 Annotation** : `@Service`



### Component Layer

`:domain` 모듈이 해당하는 계층이며, 서비스의 자세한 구현을 담당하는 각 Component로 설명됩니다.

컴포넌트의 경우, 각 로직의 상세 구현을 담당해 각 비즈니스 로직의 구현이 너무 높은 응집도를 갖지 않도록 합니다.

> **주요 Annotation** : `@Component`



### DataSource Layer

`:datasource` 모듈이 해당하는 계층이며, 비즈니스 로직에서 필요한 DataSource 들에 대한 실제 DB, 메모리 등의
구현을 담당합니다.

DB에 대해서는 JPA를 사용하여 구현체를 제공하며, Redis 등 다양한 DataSource에 대한 제어를 담당합니다.

> **주요 Annotation** : `@Entity`, `@Repository`, `@Transactional`


### Clients Layer
`:clients` 이 모듈의 하위 모듈은 외부 시스템과의 통합을 담당합니다.
> **주요 Annotation** : `@EnableFeignClients`, `@FeignClient`, `@RequestMapping`, `@Component`, `@Service`
- `:notification` 이 모듈은 알림에 대한 기능을 처리하며 Spring-Cloud-Open-Feign을 사용하여 HTTP 통신을 처리합니다.

### Module Layer

#### Library

- `logging` : Application의 log-back에 대한 구현을 제공합니다.
- `monitoring` : Application의 모니터링 관련 구현을 제공합니다.
- `swagger` : OAS를 기반으로 REST API를 약속된 규칙에 맞게 API Spec을 json과 yaml 형식으로 표현하는 기능을 제공합니다.
- `authentication` : Spring Security를 기반으로 인증 / 인가 관련 기능을 제공합니다
- `flyway` : DataBase Schema 형상관리를 제공합니다.

#### Tests

- `api-docs` : SpringRestDocs를 기반으로 Controller 테스트에 대해 API 명세를 작성하는 기능을 제공합니다.
- `test-container` : 컨테이너 기반의 일회용 인스턴스로 멱등성을 유지할 수 있으며 DB, 메모리 등의 DataSource 기능을 제공합니다.
- `test-helper` : Naver-Fixture-Monkey 라이브러리 기반으로 테스트 객체를 쉽게 생성하고 조작할 수 있는 기능을 제공합니다.

------
