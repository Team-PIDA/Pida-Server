# 푸시 알림(FCM) 시스템 문서

## 시스템 아키텍처

### 기술 스택

- **Firebase Cloud Messaging (FCM)**: Firebase Admin SDK를 통한 푸시 알림 발송
- **Spring Scheduler**: `@Scheduled`를 사용한 정기 알림 스케줄링
- **Spring Event**: `@TransactionalEventListener`를 통한 이벤트 기반 실시간 알림

### 발송 흐름

```
[트리거]                    [서비스 레이어]              [인프라 레이어]
스케줄러 / 이벤트 리스너 → NotificationService → FcmSender → FcmRepository → FirebaseCloudMessageSender → FCM
                         ├ EligibilityChecker              (최대 3회 재시도)    (MulticastMessage 배치 발송)
                         ├ MessageBuilder
                         └ NotificationStored 저장
```

### 핵심 컴포넌트

| 컴포넌트 | 역할 |
|----------|------|
| `EligibilityChecker` | 알림 수신 대상 사용자 필터링 |
| `MessageBuilder` | FCM 메시지(title, body, destination) 생성 |
| `NotificationService` | 대상 조회 → 메시지 생성 → 발송 → 히스토리 저장 오케스트레이션 |
| `FcmSender` | FCM 발송 및 재시도 로직 (최대 3회, 실패 메시지만 재발송) |
| `NotificationStored` | 발송 히스토리 DB 저장 (중복 방지 및 읽음 상태 추적) |

### FCM 메시지 구조

모든 알림은 아래 형식으로 발송됩니다:

- **Notification**: `title`, `body`
- **APNS Config**: `destination` (클라이언트 화면 이동 대상), `badge: 1`, `sound: default`
- **destination**: 모든 알림이 `"home"`으로 설정 (홈 화면으로 이동)

---

## 알림 유형 (`NotificationType`)

| enum | 설명 |
|------|------|
| `BLOOMED_ALERT` | 지역별 만개 알림 (개화 초기 + 만개 절정) |
| `WITHERED_ALERT` | 지역별 저물었어요 알림 |
| `BLOOMED_SPOT_ALERT` | 벚꽃길 만개 알림 |
| `BLOOMED_EVENT_ALERT` | 꽃 이벤트 만개 알림 |
| `WEEKDAY_HEALING` | 평일 힐링 알림 |
| `WEEKEND_HEALING` | 주말 힐링 알림 |
| `RAIN_FORECAST_ALERT` | 비 예보 알림 |
| `ADMIN_PUSH` | 관리자 수동 푸시 |
| `REGULAR` | 정기 푸시 알림 |

---

## 알림 케이스 상세

### 1. 내 주변 만개 알림 (`BLOOMED_SPOT_ALERT` / `BLOOMED_EVENT_ALERT`)

#### 트리거

- 내 현재 위치 기준 **3km 이내 벚꽃길**(FlowerSpot)에 **'만개' 투표** 발생
- 내 현재 위치 기준 **3km 이내 꽃 이벤트**(FlowerEvent)에 **'만개' 투표** 발생

> 이벤트 리스너(`BloomedSpotNotificationEventListener`, `BloomedEventNotificationEventListener`)를 통해 즉시 발송

#### 발송 대상

- **위치 권한을 허용한 사용자** 중 반경 **3km 이내**에 있는 사용자

#### 제외 대상

- 당일 이미 푸시를 받은 사용자
- 해당 벚꽃길/이벤트로 이미 만개 알림을 받은 사용자 (벚꽃 시즌당 1회)

#### 메시지 내용

**벚꽃길 (`BLOOMED_SPOT_ALERT`)**

| 항목 | 내용 |
|------|------|
| title | `우리 동네에 만개한 벚꽃길이 생겼어요 🌸` |
| body | `지금 제일 예쁜 {벚꽃길 명} 확인해보세요.` |

**꽃 이벤트 (`BLOOMED_EVENT_ALERT`)**

| 항목 | 내용 |
|------|------|
| title | `우리 동네에 만개한 꽃 이벤트가 열렸어요 🌸` |
| body | `지금 {이벤트명} 확인해보세요.` |

---

### 2. 비 예보 알림 (`RAIN_FORECAST_ALERT`)

#### 트리거

- **내일 강수확률 60% 이상**인 경우

> 스케줄러(`EveningNotificationScheduler`) — **Cron**: `0 0 18 * * *` (매일 오후 6시)

#### 발송 대상

- **최근 30일 내 앱 활동 이력** 있는 사용자

#### 제외 대상

- 당일 이미 다른 푸시를 받은 사용자
- 당주 이미 비 예보 알림을 받은 사용자 (주 1회 제한)

#### 메시지 내용

| 항목 | 내용 |
|------|------|
| title | `내일 비 소식이 있어요 ☔️` |
| body | `마지막 꽃구경 찬스! 오늘 밤 산책을 놓치지 마세요.` |

---

### 3. 개화 초기 알림 (`BLOOMED_ALERT` — 첫 투표)

#### 트리거

- 사용자 접속 위치가 속한 **광역 자치단체(시/도)**에 **'만개했어요' 첫 투표** 시

> 이벤트 리스너(`BloomedFirstVoteNotificationEventListener`)를 통해 즉시 발송

#### 발송 대상

- 해당 광역 자치단체(시/도)에 **최근 접속 기록**이 있는 사용자

#### 제외 대상

- 해당 지역을 벗어난 사용자
- 해당 년도 내에 이미 알림을 **1회 받은** 사용자 (연 1회 제한)

#### 메시지 내용

| 항목 | 내용 |
|------|------|
| title | `{지역명}에도 벚꽃이 눈을 떴어요! 🌸` |
| body | `우리 동네 가장 빠른 봄을 만나보세요.` |

---

### 4. 만개 알림 (`BLOOMED_ALERT` — 80% 이상)

#### 트리거

- 사용자 접속 위치가 속한 **광역 자치단체(시/도)**에 **'만개했어요'가 전체 투표 중 80% 이상**일 시

> 스케줄러(`BloomedNotificationScheduler`) — **Cron**: `0 0 9 * * *` (매일 오전 9시)

#### 발송 대상

- 해당 광역 자치단체(시/도)에 **최근 접속 기록**이 있는 사용자

#### 제외 대상

- 해당 지역을 벗어난 사용자
- 해당 년도 내에 이미 알림을 **1회 받은** 사용자 (연 1회 제한)

#### 메시지 내용

| 항목 | 내용 |
|------|------|
| title | `지금이 절정! 1년 중 가장 예쁜 {지역명} 벚꽃의 만개 순간, 놓치면 후회해요. 🌸` |
| body | *(title에 통합)* |

---

### 5. 낙화 알림 (`WITHERED_ALERT`)

#### 트리거

- 사용자 접속 위치가 속한 **광역 자치단체(시/도)**에 **'저물었어요'가 전체 투표 중 30% 이상**일 시

> 스케줄러(`WitheredNotificationScheduler`) — **Cron**: `0 0 9 * * *` (매일 오전 9시), 하루 1회만 실행

#### 발송 대상

- 해당 광역 자치단체(시/도)에 **최근 접속 기록**이 있는 사용자

#### 제외 대상

- 해당 지역을 벗어난 사용자
- 해당 년도 내에 이미 알림을 **1회 받은** 사용자 (연 1회 제한)

#### 메시지 내용

| 항목 | 내용 |
|------|------|
| title | `이번 주말이 지나면 늦을 지도 몰라요.  🌸` |
| body | `엔딩 크레딧 올라가기 전, 마지막 벚꽃 산책 어때요?` |

---

### 6. 퇴근길 알림 (`WEEKDAY_HEALING`)

#### 트리거

- **평일(월~금) 중 랜덤으로 2회**, 오후 6시일 경우
- 그리고 사용자 접속 위치가 **개화 시기**일 경우

> 스케줄러(`EveningNotificationScheduler` → `WeekdayNotificationScheduler`) — **Cron**: `0 0 18 * * *`
> - 월~목: 40% 확률로 실행 (목요일에 아직 0회이면 60% 확률로 부스트)
> - 금요일: 남은 횟수만큼 연속 실행 (보장)

#### 발송 대상

- **최근 30일 내 앱 활동 이력** 있는 사용자

#### 제외 대상

- 해당 주에 이미 **2회의 알림**을 받은 사용자

#### 메시지 내용

| 항목 | 내용 |
|------|------|
| title | `오늘 하루도 고생했어요. 🌙` |
| body | `퇴근길은 가까운 벚꽃 구경 어때요?` |

---

### 7. 주말 알림 (`WEEKEND_HEALING`)

#### 트리거

- **주말(토, 일) 중 랜덤으로 1회** 오전 10시일 경우
- 그리고 사용자 접속 위치가 **개화 시기**일 경우

> 스케줄러(`WeekendNotificationScheduler`) — **Cron**: `0 0 10 ? * SAT,SUN`
> - 주차 key 기준으로 실행 요일 결정 (짝수주 → 토요일, 홀수주 → 일요일)

#### 발송 대상

- **최근 30일 내 앱 활동 이력** 있는 사용자

#### 제외 대상

- 사용자가 있는 위치의 **미세먼지 '나쁨' 이상**일 때 (PM10 >= 81µg/m³)

#### 메시지 내용

| 항목 | 내용 |
|------|------|
| title | `이번 주도 치열하게 보낸 당신에게 🎁` |
| body | `걷기만 해도 힐링되는 벚꽃길이 기다려요.` |

---

### 8. 관리자 푸시 (`ADMIN_PUSH`)

- 관리자가 수동으로 발송하는 알림
- 별도의 자동 트리거 없음

---

## 스케줄링 요약

| 시간 | 알림 | 주기 제한 | 비고 |
|------|------|----------|------|
| 실시간 | BLOOMED_SPOT_ALERT | 벚꽃 시즌 1회/벚꽃길 | BLOOMED 투표 이벤트 기반 |
| 실시간 | BLOOMED_EVENT_ALERT | 벚꽃 시즌 1회/이벤트 | BLOOMED 투표 이벤트 기반 |
| 매일 18:00 | RAIN_FORECAST_ALERT | 주 1회/사용자 | 내일 POP 60% 이상 시 |
| 실시간 | BLOOMED_ALERT (첫 투표) | 연 1회/사용자 | 지역 첫 BLOOMED 투표 시 |
| 매일 09:00 | BLOOMED_ALERT (80%) | 연 1회/사용자 | 지역 BLOOMED 80% 이상 시 |
| 매일 09:00 | WITHERED_ALERT | 연 1회/사용자 | 지역 WITHERED 30% 이상 시 |
| 매일 18:00 | WEEKDAY_HEALING | 주 2회 | 평일만, 확률 기반 + 금요일 보장 |
| 토/일 10:00 | WEEKEND_HEALING | 주 1회 | 짝수주-토, 홀수주-일 |

## 중복 방지 메커니즘

| 메커니즘 | 적용 대상 | 설명 |
|---------|----------|------|
| 연 1회 | BLOOMED_ALERT, WITHERED_ALERT | 올해 1/1 이후 수신 이력 체크 |
| 벚꽃 시즌 1회/대상 | BLOOMED_SPOT_ALERT, BLOOMED_EVENT_ALERT | 같은 연도 내 동일 spot/event에 대해 1회 |
| 일 1회 | BLOOMED_SPOT_ALERT, BLOOMED_EVENT_ALERT | 당일 어떤 푸시도 받지 않은 사용자만 |
| 주 1회 | WEEKEND_HEALING, RAIN_FORECAST_ALERT | 주차 key 또는 월요일 기준 |
| 주 2회 | WEEKDAY_HEALING | 확률 기반 + 금요일 보장으로 정확히 2회 |
| 당일 다른 알림 제외 | RAIN_FORECAST_ALERT | 당일 다른 종류의 푸시를 받은 사용자 제외 |
| 분산 락 | 저녁 알림(18:00) | `NotificationExecutionLock`으로 다중 인스턴스 중복 실행 방지 |

## 재시도 정책

- `FcmSender`에서 **최대 3회** 재시도
- 실패한 메시지만 선별하여 재발송
- 성공/실패 결과를 토큰별로 추적

## 테스트 API

수동 트리거용 테스트 엔드포인트 (`NotificationTestController`):

| Method | Path | 파라미터 |
|--------|------|---------|
| POST | `/test/weekend-notification` | - |
| POST | `/test/weekday-notification` | - |
| POST | `/test/withered-notification` | - |
| POST | `/test/bloomed-notification` | `region` (query), `alertType` (query, default: FIRST_VOTE) |
| POST | `/test/bloomed-spot-notification` | `spotId` (query) |
| POST | `/test/bloomed-event-notification` | `eventId` (query) |
| POST | `/test/rain-forecast-notification` | - |

