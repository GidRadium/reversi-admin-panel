# reversi-admin-panel
CLI and GUI reversi games validator. Written in Kotlin.

## Usage

### Clone

```bash
git clone https://github.com/GidRadium/reversi-admin-panel.git
cd reversi-admin-panel
```

### Build

```bash
./gradlew installDist
```

### Run GUI

```bash
./app/build/install/app/bin/app
```

### Run CLI

```bash
./app/build/install/app/bin/cli
```

### Test

```bash
./gradlew test
```

## Architecture

### Main architecture

![Main architecture](docs/ARCHITECTURE.png)

[Mermaid source](docs/ARCHITECTURE.md)

### Domain model

![Domain model](docs/ARCHITECTURE_DOMAIN.png)

[Mermaid source](docs/ARCHITECTURE_DOMAIN.md)

### Data model

![Data model](docs/ARCHITECTURE_DATA.png)

[Mermaid source](docs/ARCHITECTURE_DATA.md)

### Database model

![Database model](docs/ARCHITECTURE_DATABASE.png)

[Mermaid source](docs/ARCHITECTURE_DATABASE.md)
