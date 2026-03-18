# 🌸 우리 동네 꽃길 안내 서비스, 피다 (PIDA)

<p align="center">
  <img src="./.github/image/pida_cover.png" alt="pida_cover" width="100%" />
</p>

<p align="center">
  <a href="https://wealthy-session-98c.notion.site/pida-1b490c66759880519df7f9c93c1ed2dd?pvs=4">
    <img src="https://img.shields.io/badge/소개페이지-blue?style=for-the-badge&logo=notion&logoColor=white" style="border-radius: 8px;" />
  </a>
  <a href="https://www.instagram.com/_pida.flower/">
    <img src="https://img.shields.io/badge/@_pida.flower-E4405F?style=for-the-badge&logo=instagram&logoColor=white" style="border-radius: 8px;" />
  </a>
  <a href="https://github.com/Team-PIDA/PIDA_iOS">
    <img src="https://img.shields.io/badge/PIDA--iOS-000000?style=for-the-badge&logo=github&logoColor=white" style="border-radius: 8px;" />
  </a>
</p>

---

## 📸 서비스 미리보기

| <img src="https://github.com/user-attachments/assets/8605f071-dbb8-416e-85c9-fa889f4361fe" width="300" style="border-radius: 8px;" /> | <img src="https://github.com/user-attachments/assets/68b918ae-396b-4f1d-b52d-5aaa30e67ec9" width="300" style="border-radius: 8px;" /> | <img src="https://github.com/user-attachments/assets/e9df1a20-d723-4129-ab94-2a822aa811e1" width="300" style="border-radius: 8px;" /> |
|:--:|:--:|:--:|


---

##  🚀 **Server Tech Stack**

- **Language & Framework**:
    - Spring Boot 3.4.x
    - Java 21
    - Kotlin 2.1.x


- **Data Access**:
    - Spring Data JPA
    - Kotlin JDSL
    - PostgreSQL 17.2 (+ PostGIS)
    - Redis 7.1.0


- **External API Client**:
    - OpenFeign


- **Build Tool**:
    - Gradle 8.12.1


- **Code Quality & Style**:
    - ktlint


---

## 🗺️ Cloud Architecture

- 컨테이너 환경: **AWS ECS + Fargate**
- 데이터베이스: **AWS RDS (PostgreSQL)**
- 캐시 관리: **AWS ElastiCache (Redis)**

> _클라우드 아키텍처 이미지 추가 예정_

---

## 🏰 **Architecture Overview**


### 📌 **Presentation Layer** (`:pida-core:core-api`)
- 사용자 요청 처리 및 응답
- Spring Boot Application, Controllers
- 주요 Annotation: `@RestController`, `@Configuration`

### 📌 **Business Layer** (`:pida-core:core-domain`)
- 순수 비즈니스 로직을 담당하는 서비스 계층
- 서비스의 세부 구현 로직을 모듈화하여 담당
- 주요 Annotation: `@Service`, `@Component`

### 📌 **DataSource Layer** (`:pida-storage`)
- 실제 데이터 소스 관리 및 DB 제어
- 주요 Annotation: `@Entity`, `@Repository`, `@Transactional`

### 📌 **Clients Layer** (`:pida-clients`)
- 외부 시스템과의 연동 및 API 호출 관리
- 주요 Annotation: `@FeignClient`, `@EnableFeignClients`
- 예시: 알림 서비스, 외부 API 통합


### 📚 **Other Modules**

#### 🔖 pida-supports module

- logging: Logback 기반 로그 관리

- monitoring: 애플리케이션 모니터링

- swagger: OAS 기반의 API 문서화 및 스펙 제공

#### 🧪 pida-tests module

- api-docs: Spring Rest Docs를 활용한 API 명세 자동화

- test-container: TestContainers 기반의 독립적이고 멱등성 높은 데이터 소스 테스트 환경

- test-helper: Naver Fixture Monkey를 활용한 테스트 데이터 생성 및 관리

---

## 🤖 Codex Workflow

- 저장소 루트에서 `codex -C .` 를 실행하면 `.codex/skills/` 와 `.codex/agents/` 의 repo-local 설정이 자동 로드됩니다.
- 개발자는 별도 skill 설치 없이 이 저장소 안에서 바로 PIDA 전용 skill 과 custom agent 를 사용할 수 있습니다.
- `/agent` 화면에는 정의된 agent 목록이 아니라 현재 세션에서 실제로 실행 중인 agent thread 만 표시됩니다.
- 주요 custom agent:
  - `feature_mapper`
  - `ci_triager`
  - `api_reviewer`
  - `db_core_specialist`
  - `code_reviewer`
  - `commit_push_guard`
- 예시 명령:
  - `codex exec -C . "Spawn code_reviewer to review this branch against main for bugs, regressions, and missing tests. Wait for it and summarize the concrete findings."`
  - `codex exec -C . "Spawn commit_push_guard to inspect the current changes, propose safe commit groups, and list the Java 21 checks to run before pushing. Do not mutate git."`
  - `codex exec -C . "Review this branch against main. Spawn api_reviewer, db_core_specialist, and code_reviewer in parallel. Have api_reviewer focus on contract drift, docs, and tests. Have db_core_specialist focus on repository, transaction, cache, and soft-delete consistency. Have code_reviewer focus on bugs and regressions. Wait for all of them and summarize only concrete findings."`
  - `codex exec -C . 'Use $pida-commit-push to propose a safe commit and push sequence for the current branch. Do not mutate git yet.'`
