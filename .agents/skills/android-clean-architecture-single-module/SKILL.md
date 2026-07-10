---
name: android-clean-architecture-single-module
description: Clean Architecture for single-module Android apps using package layers (domain, data, presentation, di) instead of Gradle modules. Use when structuring packages, adding UseCases/Repositories/DataSources, wiring Hilt, or evolving sensor/motion features in a single :app module.
---

# Android Clean Architecture (Single Module)

Adapted from [android-clean-architecture](../android-clean-architecture/SKILL.md) for projects that stay on **one Gradle module** (`:app`) and enforce boundaries with **packages**, not multi-module Gradle graphs.

Use this skill for Android apps (Compose + Hilt + Flow) that are not ready for `domain`/`data`/`presentation` modules yet. Prefer the multi-module skill when splitting Gradle modules.

## When to Activate

- Structuring or refactoring packages inside `:app`
- Implementing UseCases, Repositories, or DataSources
- Designing data flow: Screen → ViewModel → UseCase → Repository → DataSource
- Setting up Hilt bindings for repository interfaces
- Adding sensor, motion, or similar domain logic without introducing new Gradle modules

## Package Structure

### Recommended Layout

Keep everything under the app package root (example: `com.mascill.myapplication`):

```
app/src/main/kotlin/<base>/
├── <Application>.kt          # Hilt Application
├── MainActivity.kt
├── di/                       # Hilt modules (@Module, @Binds, @Provides)
├── domain/                   # Pure Kotlin business logic
│   ├── model/                # Domain models (no Android imports)
│   ├── repository/           # Repository interfaces
│   ├── usecase/              # One operation per UseCase
│   └── <feature>/            # Optional: detectors, engines (e.g. motion/)
├── data/                     # Framework + I/O implementations
│   ├── datasource/           # SensorManager, Room, network, etc.
│   ├── repository/           # Repository implementations (*Impl)
│   ├── model/                # DTOs / raw data models (map to domain)
│   └── mapper/               # Entity/DTO ↔ domain mappers
└── presentation/             # Screens, ViewModels, theme, navigation
    ├── <Feature>Screen.kt
    ├── viewmodel/
    ├── theme/
    └── navigation/           # Optional
```

Optional later (still single module): group by feature under `domain/<feature>/`, `data/<feature>/`, `presentation/<feature>/` when the app grows — without creating Gradle modules.

### Dependency Rules (Packages)

```
presentation → domain, (Compose/AndroidX)
domain       → (nothing Android / nothing from data or presentation)
data         → domain, Android/framework APIs
di           → data, domain  (wiring only)
```

**Critical**

- `domain` must NEVER import `data`, `presentation`, Android framework, or Compose.
- `presentation` / ViewModels depend on **UseCases** (preferred) or domain repository interfaces — never on DataSources or `*Impl`.
- Repository **interfaces** live in `domain.repository`; **implementations** live in `data.repository`.

### vs Multi-Module Skill

| Multi-module skill | This skill |
|--------------------|------------|
| Gradle modules: `domain`, `data`, `presentation` | Packages inside `:app` |
| Compile-time module boundaries | Convention + review discipline |
| Koin + KMP + Room/SQLDelight/Ktor examples | Hilt + Android Sensor / local I/O first |
| Split when scale demands | Stay single-module until pain is real |

## Domain Layer

### UseCase Pattern

Each UseCase = one business operation. Prefer `operator fun invoke`:

```kotlin
class ObserveAccelerometerUseCase(
    private val repository: SensorRepository
) {
    operator fun invoke(): Flow<Vector3Data> = repository.accelerometer()
}

class DetectMovementUseCase(
    private val repository: SensorRepository,
    private val motionDetector: MotionDetector
) {
    operator fun invoke(): Flow<Boolean> =
        repository.linearAcceleration().map { data ->
            if (!data.isAvailable) false
            else motionDetector.update(data.x, data.y, data.z)
        }
}
```

Put analysis algorithms (thresholds, windows, fusion) in `domain` — not in ViewModels or DataSources.

### Domain Models

Plain Kotlin — no Room/Retrofit/Compose annotations:

```kotlin
data class Vector3Data(
    val x: Float,
    val y: Float,
    val z: Float,
    val isAvailable: Boolean = true,
    val timestamp: Long = 0L
) {
    companion object {
        val Unavailable = Vector3Data(0f, 0f, 0f, isAvailable = false)
    }
}
```

### Repository Interfaces

Defined in domain, implemented in data:

```kotlin
interface SensorRepository {
    fun accelerometer(): Flow<Vector3Data>
    fun linearAcceleration(): Flow<Vector3Data>
}
```

## Data Layer

### DataSource

Talks to Android APIs (e.g. `SensorManager`). Emits raw/data models; no UI logic.

```kotlin
@Singleton
class SensorDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    fun accelerometer(): Flow</* data model */> = sensorFlow(Sensor.TYPE_ACCELEROMETER)
    // ...
}
```

### Repository Implementation

Thin coordinator: DataSource → map to domain → expose as interface:

```kotlin
class SensorRepositoryImpl @Inject constructor(
    private val dataSource: SensorDataSource
) : SensorRepository {

    override fun accelerometer(): Flow<Vector3Data> =
        dataSource.accelerometer().map { it.toDomain() }

    override fun linearAcceleration(): Flow<Vector3Data> =
        dataSource.linearAcceleration().map { it.toDomain() }
}
```

### Mapper Pattern

Keep mappers as extension functions near data models:

```kotlin
fun AccelerometerDto.toDomain() = Vector3Data(
    x = x,
    y = y,
    z = z,
    isAvailable = isAvailable
)
```

If data and domain models are identical early on, still place the **canonical** type in `domain.model` and keep data types as implementation details when they diverge (timestamps, sensor accuracy, DTOs).

## Presentation Layer

### ViewModel

- Inject UseCases (preferred) or domain repository interfaces
- Expose `StateFlow` / UI state
- Use `viewModelScope` + `stateIn` / `SharingStarted.WhileSubscribed`
- No SensorManager, no DataSource, no business thresholds

```kotlin
@HiltViewModel
class SensorViewModel @Inject constructor(
    observeAccelerometer: ObserveAccelerometerUseCase,
    detectMovement: DetectMovementUseCase
) : ViewModel() {

    val accelerometer = observeAccelerometer()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Vector3Data.Unavailable)

    val isMoving = detectMovement()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
}
```

### Screens (Compose)

Screens under `presentation` collect ViewModel state only. No repository or domain algorithm calls from Composables.

## Dependency Injection (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class SensorModule {

    @Binds
    @Singleton
    abstract fun bindSensorRepository(
        impl: SensorRepositoryImpl
    ): SensorRepository
}
```

Register UseCases as `@Inject constructor` or provide via `@Provides` / `@Binds` if they need a custom scope. Prefer constructor injection.

## Error & Unavailable Handling

For sensors and hardware:

- Model unavailability in domain (`isAvailable`, sealed state, or `Result`)
- Close/cleanup listeners in `callbackFlow` `awaitClose`
- Map failures to UI messages in ViewModel — keep domain free of string resources

## Migration Path (Current → Target)

When the codebase still has shortcuts, move in this order:

1. Move repository **interfaces** from `data.repository` → `domain.repository`
2. Move shared models to `domain.model`; map from `data.model` if needed
3. Extract ViewModel business logic into UseCases / domain detectors
4. Keep `data.repository` (or `data.repositoryImpl`) for `*Impl` only
5. Split Gradle modules only when package discipline is no longer enough

## Anti-Patterns to Avoid

- Android / Compose imports in `domain`
- Exposing DataSource or `*Impl` to ViewModels
- Business logic (thresholds, fusion, state machines) in ViewModels or Screens
- Repository interfaces living under `data` long-term
- Fat repositories — split focused DataSources
- `GlobalScope` / unstructured coroutines — use `viewModelScope`
- Creating multi-module / KMP / Room scaffolding “because Clean Architecture” when the app only needs sensors + Flow

## Checklist for New Features

- [ ] Domain model in `domain` (no Android types)
- [ ] Repository interface in `domain.repository`
- [ ] DataSource + `*Impl` in `data`
- [ ] UseCase for the operation ViewModel needs
- [ ] Hilt `@Binds` for the repository
- [ ] ViewModel only talks to UseCase / domain API
- [ ] Screen only talks to ViewModel

## References

- Full multi-module / KMP / Room / Ktor patterns: [android-clean-architecture](../android-clean-architecture/SKILL.md)
- Project roadmap may define feature phases (sensor → motion → GPS); keep algorithms in `domain` as phases grow
