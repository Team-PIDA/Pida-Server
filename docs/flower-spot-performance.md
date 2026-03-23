# Flower Spot Performance Notes

## Refactoring Direction

- `spot:all` Redis 캐시를 기준으로 `region`과 `bbox(swLat/swLng/neLat/neLng)` 필터를 애플리케이션에서 적용한다.
- `GET /flower-spot` 목록 응답은 preview 이미지 1장만 조회하고, 상세 응답만 전체 이미지 조회를 유지한다.
- 목록 응답 조합 시 `recentlyBlooming.groupBy { it.flowerSpotId }`는 한 번만 계산한다.
- 빈 spot 목록에서는 bloomings, S3 preview 조회를 생략한다.
- synthetic latency benchmark를 추가해 이전 구현과 현재 구현의 median latency를 비교한다.

## Why This Changed

- bbox 요청은 이전까지 PostGIS 쿼리를 매번 수행했다.
- 목록 응답은 preview URL 하나만 사용하면서도 spot별 전체 이미지 목록을 매번 S3에서 조회했다.
- 동일한 bloomings를 spot 수만큼 다시 `groupBy` 하면서 CPU 비용이 추가됐다.

## Performance Benchmark

### Run

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :pida-core:core-domain:flowerSpotPerformanceBenchmark --no-daemon
```

### Output

- report path: `pida-core/core-domain/build/reports/performance/flower-spot-latency.md`
- benchmark scope:
  - repeated `groupBy` 제거 전후 latency
  - preview 이미지 1장 조회와 전체 이미지 조회 latency
  - `GET /flower-spot` 목록 조합의 end-to-end synthetic latency

### Interpretation

- 이 benchmark는 애플리케이션 레이어의 이전 구현과 현재 구현을 같은 입력으로 비교한다.
- 네트워크, Redis, PostgreSQL, PostGIS 실행 계획은 포함하지 않는다.
- bbox DB 경로 성능은 아래 인덱스 체크리스트와 `EXPLAIN (ANALYZE, BUFFERS)`로 별도 확인한다.

## Bbox Index Checklist

현재 bbox 경로는 캐시 기반 필터를 우선 사용하지만, 데이터 볼륨이 커져 DB 공간 쿼리로 되돌리거나 fallback이 필요해질 수 있다. 그때는 아래 인덱스를 먼저 확인한다.

1. `t_flower_spot.pin_point`에 GIST 인덱스가 있는지 확인한다.
2. soft delete 비중이 높다면 `WHERE deleted_at IS NULL` 조건의 partial GIST 인덱스를 검토한다.
3. `region` 조건과 함께 쓰는 경로가 많다면 `t_flower_spot(region, deleted_at)` B-tree 인덱스를 확인한다.
4. 최근 개화 조회용으로 `t_blooming(flower_spot_id, created_at)` 또는 `t_blooming(flower_spot_id, status, created_at)` 복합 인덱스를 확인한다.
5. 대표 bbox 쿼리와 최근 개화 쿼리에 대해 `EXPLAIN (ANALYZE, BUFFERS)`를 실행해 sequential scan 여부를 확인한다.
6. 실제 실행 계획에서 `pin_point` GIST 인덱스와 `t_blooming` 복합 인덱스가 선택되는지 점검한다.

## Revisit Signals

아래 신호가 보이면 캐시 기반 필터 대신 DB 공간 쿼리를 다시 검토한다.

- Redis에서 `spot:all` 역직렬화 비용이 응답 시간의 대부분을 차지할 때
- flower spot 개수가 커져 애플리케이션 전수 스캔 비용이 커질 때
- bbox 요청이 매우 다양해서 캐시 hit 이점보다 CPU 비용이 커질 때
