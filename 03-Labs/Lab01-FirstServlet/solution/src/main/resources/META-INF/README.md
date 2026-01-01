# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# MicroProfile Configuration

## Overview

This directory contains the MicroProfile configuration file used by Open Liberty to configure the banking application.

## File: microprofile-config.properties

This file provides configuration properties for:

### Application Information
- `app.name`: Application name
- `app.version`: Current version
- `app.description`: Application description

### Server Configuration
- `server.host`: Server hostname
- `server.port`: HTTP port (9080 for Open Liberty)
- `server.context.root`: Application context root

### MicroProfile Features

#### Health Check
- `health.check.enabled`: Enable/disable health checks
- `health.check.interval`: Health check interval in seconds
- Access: http://localhost:9080/health

#### Metrics
- `metrics.enabled`: Enable/disable metrics
- `metrics.vendor.enabled`: Enable vendor-specific metrics
- Access: http://localhost:9080/metrics

#### OpenAPI
- `openapi.enabled`: Enable/disable OpenAPI documentation
- `openapi.title`: API title
- `openapi.version`: API version
- `openapi.description`: API description
- Access: http://localhost:9080/openapi

### Application Features
- `feature.client.management`: Enable client management
- `feature.welcome.page`: Enable welcome page

### Logging
- `logging.level`: Log level (INFO, DEBUG, WARN, ERROR)
- `logging.format`: Log format (json, simple)

### Future Features (Placeholders)
- `security.enabled`: Security configuration (for Lab 3)
- `database.enabled`: Database configuration (for Lab 2)

## Usage

### In Code

You can inject these properties in your Jakarta EE code:

```java
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.inject.Inject;

public class MyService {
    
    @Inject
    @ConfigProperty(name = "app.name")
    private String appName;
    
    @Inject
    @ConfigProperty(name = "server.port", defaultValue = "9080")
    private int serverPort;
}
```

### Environment Variables

You can override properties using environment variables:

```bash
# Override app name
export APP_NAME="My Banking App"

# Override server port
export SERVER_PORT=8080
```

### System Properties

You can also override using system properties:

```bash
java -Dapp.name="My Banking App" -jar app.jar
```

## Priority Order

MicroProfile Config uses the following priority (highest to lowest):

1. System properties (`-D` flags)
2. Environment variables
3. `microprofile-config.properties` file
4. Default values in code

## Testing

### Verify Configuration

```bash
# Check health endpoint
curl http://localhost:9080/health

# Check metrics
curl http://localhost:9080/metrics

# Check OpenAPI
curl http://localhost:9080/openapi
```

### Expected Health Response

```json
{
  "status": "UP",
  "checks": [
    {
      "name": "banking-app",
      "status": "UP"
    }
  ]
}
```

## References

- [MicroProfile Config Specification](https://microprofile.io/project/eclipse/microprofile-config)
- [Open Liberty MicroProfile Config](https://openliberty.io/docs/latest/microprofile-config.html)
- [Jakarta EE Configuration](https://jakarta.ee/specifications/config/)

## Notes

- This file is automatically copied to the container during Podman/Docker build
- Changes require rebuilding the application: `mvn clean package`
- For Podman deployment, rebuild the image: `podman build -t banking-app:lab01 .`