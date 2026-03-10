# 🚀 Automation Framework

 **UI + API** test automation framework built with Java, Selenium WebDriver, RestAssured, TestNG, Allure, and Docker.

---

## 📐 Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         Test Layer                           │
│  LoginTest · UserAPITest · (your tests)                      │
├──────────────────────────────────────────────────────────────┤
│                       Page Objects                           │
│  LoginPage · SecureAreaPage · (your pages)                   │
├─────────────────────┬────────────────────────────────────────┤
│  Base Classes       │  API Layer                             │
│  BasePage           │  BaseAPI                               │
│  BaseTest           │  UserAPI · (your endpoints)            │
│  BaseAPITest        │                                        │
├─────────────────────┴────────────────────────────────────────┤
│  Self-Healing Engine │ Driver Factory │ Config Manager        │
├──────────────────────────────────────────────────────────────┤
│  Utilities: WaitUtil · ScreenshotUtil · JsonUtil · Logger    │
├──────────────────────────────────────────────────────────────┤
│  TestNG · Allure · Log4j2 · Selenium · RestAssured           │
└──────────────────────────────────────────────────────────────┘
```

---

## ✨ Key Features

| Feature | Description |
|---|---|
| **Page Object Model** | Clean separation of locators, actions, and tests |
| **Self-Healing Locators** | Auto-fallback to alternative locators when primary fails |
| **Configurable Browser** | Chrome, Firefox, Edge — switch via config or CLI |
| **Headless Mode** | Toggle headless with `-Dheadless=true` |
| **Parallel Execution** | TestNG parallel=methods with configurable thread count |
| **Selenium Grid (Docker)** | Containerised Grid with Chrome, Firefox, Edge nodes |
| **Allure Reports** | Rich HTML reports with screenshots, steps, and attachments |
| **Screenshot on Failure** | Auto-captured and attached to Allure |
| **Retry Mechanism** | Configurable auto-retry for flaky tests |
| **Data-Driven Testing** | JSON-based DataProvider for parameterised tests |
| **Environment Profiles** | dev / staging / prod config files |
| **RestAssured API Layer** | Generic HTTP methods with Allure request/response logging |
| **Log4j2 Logging** | Console + rolling file with configurable levels |
| **CI/CD Pipeline** | GitHub Actions with Grid services and Allure report |

---

## 📋 Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **Docker & Docker Compose** (for containerised execution)
- **Allure CLI** (optional, for `allure serve`)

---

## 🏃 Quick Start

### Run API tests (no browser needed)
```bash
mvn clean test -DsuiteXmlFile=testng-api.xml
```

### Run all tests locally
```bash
mvn clean test
```

### Run with specific browser
```bash
mvn clean test -Dbrowser=firefox
```

### Run headless
```bash
mvn clean test -Dheadless=true
```

### Run with different thread count
```bash
mvn clean test -Dparallel.thread.count=5
```

### Run against a specific environment
```bash
mvn clean test -Denvironment=staging
```

### Generate Allure report
```bash
mvn allure:serve
```

---

## 🐳 Docker Execution

### Start Grid and run tests
```bash
docker-compose up --build
```

### Scale Chrome nodes
```bash
docker-compose up --build --scale chrome=5
```

### Start Grid only (for local development against Grid)
```bash
docker-compose up -d selenium-hub chrome firefox
```
Then run tests locally with:
```bash
mvn clean test -Dselenium.grid.url=http://localhost:4444/wd/hub -Dheadless=true
```

### View Grid dashboard
Open [http://localhost:4444](http://localhost:4444) in your browser.

---

## ⚙️ Configuration Reference

All properties in `config/config.properties` can be overridden via:
1. Environment-specific files (`config/environments/{env}.properties`)
2. System properties (`-Dbrowser=firefox`)
3. Environment variables (`BROWSER=firefox`)

| Property | Default | Description |
|---|---|---|
| `browser` | `chrome` | Browser: chrome, firefox, edge |
| `headless` | `false` | Run in headless mode |
| `parallel.enabled` | `true` | Enable parallel execution |
| `parallel.thread.count` | `3` | Number of parallel threads |
| `base.url` | `https://the-internet.herokuapp.com` | UI base URL |
| `api.base.url` | `https://reqres.in/api` | API base URL |
| `explicit.wait` | `15` | Explicit wait timeout (seconds) |
| `page.load.timeout` | `30` | Page load timeout (seconds) |
| `retry.count` | `1` | Auto-retry count for failed tests |
| `screenshot.on.failure` | `true` | Capture screenshot on test failure |
| `self.healing.enabled` | `true` | Enable self-healing locators |
| `selenium.grid.url` | *(empty)* | Selenium Grid URL (empty = local) |
| `environment` | `dev` | Active environment profile |

---

## 🩹 Self-Healing Locators

The framework automatically tries alternative locators when the primary locator fails:

```java
private final SelfHealingLocator usernameField = SelfHealingLocator.builder("username")
    .primary(By.id("username"))                           // try first
    .addAlternative(By.name("username"))                  // fallback 1
    .addAlternative(By.cssSelector("input[name='username']")) // fallback 2
    .addAlternative(By.xpath("//input[@id='username']"))  // fallback 3
    .build();
```

When healing occurs, the framework:
1. Logs a warning with the original and healed locator
2. Attaches a **Self-Healing Report** to the Allure report
3. The test passes without manual intervention

---

## 📁 Adding New Tests

### New Page Object
1. Create a class in `src/main/java/com/automation/pages/`
2. Extend `BasePage`
3. Define self-healing locators and action methods

### New UI Test
1. Create a class in `src/test/java/com/automation/tests/ui/`
2. Extend `BaseTest`
3. Add `@Test` methods with Allure annotations

### New API Endpoint
1. Create a class in `src/main/java/com/automation/api/endpoints/`
2. Extend `BaseAPI`
3. Add endpoint methods with `@Step` annotations

### New API Test
1. Create a class in `src/test/java/com/automation/tests/api/`
2. Extend `BaseAPITest`
3. Add `@Test` methods

---

## 📊 Project Structure

```
├── pom.xml                         # Maven config & dependencies
├── Dockerfile                      # Multi-stage Docker build
├── docker-compose.yml              # Selenium Grid + test runner
├── testng.xml                      # Full suite (UI + API)
├── testng-api.xml                  # API-only suite
├── config/
│   ├── config.properties           # Default configuration
│   └── environments/               # Environment overrides
├── src/main/java/com/automation/
│   ├── api/                        # RestAssured API layer
│   ├── base/                       # BasePage, BaseTest, BaseAPITest
│   ├── config/                     # ConfigManager
│   ├── constants/                  # Constants
│   ├── driver/                     # DriverFactory (ThreadLocal)
│   ├── healing/                    # Self-healing locator engine
│   ├── listeners/                  # TestListener, RetryAnalyzer
│   ├── pages/                      # Page Objects
│   └── utils/                      # Utilities
├── src/test/java/com/automation/tests/
│   ├── api/                        # API test classes
│   └── ui/                         # UI test classes
└── src/test/resources/
    ├── log4j2.xml                  # Logging configuration
    └── testdata/                   # JSON test data files
```
