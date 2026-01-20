# Weather Client

기상청 단기예보 API를 연동한 날씨 정보 조회 클라이언트 모듈입니다.

## 기능

- 기상청 단기예보 API 연동
- GPS 좌표를 기상청 격자 좌표로 자동 변환
- 강수량, 강수확률, 기온, 습도 등 날씨 정보 조회
- 비 예보 여부 확인

## 설정

### 1. 공공데이터포털에서 API 키 발급

1. [공공데이터포털](https://www.data.go.kr) 회원가입 및 로그인
2. [기상청_단기예보 조회서비스](https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084) 활용신청
3. 발급받은 API 인증키를 환경변수에 설정

### 2. 환경변수 설정

`.dev.env`, `.local.env`, `.prod.env` 파일에 API 키 추가:

```bash
### KMA Weather API ###
KMA_API_SERVICE_KEY=your_api_key_here
```

## 사용 예시

### 1. WeatherService를 통한 날씨 조회

```kotlin
@Service
class YourService(
    private val weatherService: WeatherService
) {
    fun checkWeather() {
        // 서울의 날씨 조회
        val location = WeatherLocation.fromCoordinates(37.5665, 126.9780)
        val weather = weatherService.getWeather(location)

        println("기온: ${weather.temperature}℃")
        println("강수확률: ${weather.precipitationProbability}%")
        println("강수량: ${weather.precipitation}mm")

        // 비가 오는지 확인
        if (weather.isRaining()) {
            println("현재 비가 오고 있습니다.")
        }

        // 비 예보 확인 (강수확률 30% 이상)
        if (weather.hasRainForecast(30)) {
            println("비 예보가 있습니다.")
        }
    }

    fun checkRain() {
        // GPS 좌표로 비 올지 확인
        val willRain = weatherService.willItRain(
            WeatherLocation.fromCoordinates(37.5665, 126.9780),
            probabilityThreshold = 30
        )

        if (willRain) {
            println("비가 올 예정입니다.")
        }
    }
}
```

### 2. 주요 도시 좌표 상수 사용

```kotlin
import com.pida.weather.KoreanCities

val seoulWeather = weatherService.getWeather(KoreanCities.SEOUL)
val busanWeather = weatherService.getWeather(KoreanCities.BUSAN)
```

## API 응답 카테고리

기상청 API는 다음 카테고리의 데이터를 제공합니다:

- `PTY`: 강수형태 (0=없음, 1=비, 2=비/눈, 3=눈, 4=소나기)
- `POP`: 강수확률 (%)
- `PCP`: 1시간 강수량 (mm)
- `SKY`: 하늘상태 (1=맑음, 3=구름많음, 4=흐림)
- `TMP`: 기온 (℃)
- `REH`: 습도 (%)
- `SNO`: 1시간 신적설 (cm)
- `UUU`, `VVV`: 풍속 성분
- `WAV`: 파고
- `VEC`: 풍향
- `WSD`: 풍속

## 좌표 변환

GPS 좌표(위도/경도)를 기상청 격자 좌표(nx/ny)로 자동 변환합니다.

```kotlin
val location = WeatherLocation.fromCoordinates(
    latitude = 37.5665,  // 위도
    longitude = 126.9780 // 경도
)
// location.nx, location.ny에 격자 좌표가 자동으로 설정됨
```

## 주의사항

1. API 키 없이는 실제 날씨 데이터를 조회할 수 없습니다.
2. 기상청 단기예보는 하루 8번 발표됩니다 (02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00)
3. API 제공 시간은 발표시각 + 10분입니다.
4. 공공데이터포털 API는 일일 트래픽 제한이 있을 수 있습니다.

## 개발 참고

- 기상청 API 문서: https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084
- 격자 좌표 변환 공식: https://www.kma.go.kr/images/weather/lifenindustry/timeseries_XML.pdf
