# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Open Liberty Configuration Files

## Overview

This directory contains the configuration files for Open Liberty server.

## Files

### 1. server.xml

The main server configuration file that defines:
- **Features**: Jakarta EE and MicroProfile features to enable
- **HTTP Endpoint**: HTTP/HTTPS ports configuration
- **Web Application**: Explicit application deployment configuration
- **Logging**: Log configuration
- **Application Manager**: Auto-expansion settings

**Key Configurations:**

**HTTP Endpoint:**
```xml
<httpEndpoint id="defaultHttpEndpoint"
              host="*"
              httpPort="${default.http.port}"
              httpsPort="${default.https.port}" />
```

**Web Application (Explicit Declaration):**
```xml
<webApplication id="banking-app"
                location="banking-app.war"
                contextRoot="${app.context.root}">
    <classloader apiTypeVisibility="spec, ibm-api, third-party"/>
</webApplication>
```

This explicit configuration demonstrates:
- **Application ID**: Unique identifier for the application
- **Location**: WAR file location (relative to /config/apps/)
- **Context Root**: URL path where the application is accessible
- **Classloader**: API visibility configuration for Jakarta EE

Variables like `${default.http.port}` and `${app.context.root}` are resolved from `bootstrap.properties`.

### 2. bootstrap.properties

Bootstrap properties file that defines variables used in `server.xml`. This file is loaded **before** `server.xml` is processed.

**Properties:**
- `default.http.port=9080` - HTTP port
- `default.https.port=9443` - HTTPS port
- `app.context.root=/` - Application context root
- `server.name=defaultServer` - Server name

**Why use bootstrap.properties?**
- ✅ Separates configuration from structure
- ✅ Easy to override for different environments
- ✅ Can be externalized for deployment
- ✅ Supports environment-specific values

## Variable Resolution Order

Open Liberty resolves variables in this order (highest priority first):

1. **Environment variables** (e.g., `DEFAULT_HTTP_PORT`)
2. **System properties** (e.g., `-Ddefault.http.port=9080`)
3. **bootstrap.properties** file
4. **Default values** in server.xml

## Deployment Methods: Explicit vs Auto-Deploy

### Explicit Configuration (Used in This Lab)

**Location:** `/config/apps/banking-app.war`
**Configuration:** Declared in `server.xml`

```xml
<webApplication id="banking-app"
                location="banking-app.war"
                contextRoot="/">
    <classloader apiTypeVisibility="spec, ibm-api, third-party"/>
</webApplication>
```

**Advantages:**
- ✅ **Educational**: Shows complete configuration
- ✅ **Explicit control**: Full control over deployment settings
- ✅ **Context root**: Can be configured per application
- ✅ **Classloader**: Can customize API visibility
- ✅ **Application ID**: Explicit naming for management

**Use when:**
- Learning Open Liberty configuration
- Need fine-grained control over deployment
- Multiple applications with different settings
- Custom classloader requirements

### Auto-Deploy (Alternative Method)

**Location:** `/config/dropins/banking-app.war`
**Configuration:** Automatic, no server.xml entry needed

```xml
<!-- No explicit configuration needed -->
<!-- Application auto-deployed from dropins -->
```

**Advantages:**
- ✅ **Simplicity**: Just drop the WAR file
- ✅ **Convention**: Follows standard practices
- ✅ **Hot reload**: Automatic detection of changes

**Use when:**
- Quick prototyping
- Simple applications
- Standard deployment requirements

### Why We Use Explicit Configuration

For **educational purposes**, this lab uses explicit configuration to demonstrate:
1. How to declare applications in `server.xml`
2. How to use variables from `bootstrap.properties`
3. How to configure context roots
4. How to set up classloaders
5. Complete control over application deployment

## Usage Examples

### Override Port via Environment Variable

```bash
# Set environment variable
export DEFAULT_HTTP_PORT=8080

# Start Liberty
mvn liberty:dev
```

### Override Port via System Property

```bash
# Using Maven
mvn liberty:dev -Ddefault.http.port=8080

# Using Liberty directly
$LIBERTY_HOME/bin/server run defaultServer -- -Ddefault.http.port=8080
```

### Override in Container

```bash
# Podman/Docker
podman run -e DEFAULT_HTTP_PORT=8080 -p 8080:8080 banking-app:lab01
```

## Environment-Specific Configuration

### Development (bootstrap.properties)
```properties
default.http.port=9080
default.https.port=9443
app.context.root=/
```

### Production (override with environment variables)
```bash
export DEFAULT_HTTP_PORT=80
export DEFAULT_HTTPS_PORT=443
export APP_CONTEXT_ROOT=/banking
```

## Containerfile Integration

The Containerfile copies both configuration files:

```dockerfile
# Copy Liberty server configuration
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 src/main/liberty/config/bootstrap.properties /config/
```

Both files are placed in `/config/` directory in the container.

## Troubleshooting

### Variables Not Resolved

**Problem:** Variables like `${default.http.port}` show as literal text

**Solution:**
1. Ensure `bootstrap.properties` exists in `/config/` directory
2. Check file permissions (should be readable by Liberty user 1001)
3. Verify property names match exactly (case-sensitive)
4. Check Liberty logs for configuration errors

### Port Already in Use

**Problem:** `Address already in use: bind`

**Solution:**
```bash
# Change port in bootstrap.properties
default.http.port=9081

# Or override with environment variable
export DEFAULT_HTTP_PORT=9081
```

## References

- [Open Liberty Server Configuration](https://openliberty.io/docs/latest/reference/config/server-configuration-overview.html)
- [Liberty Variables](https://openliberty.io/docs/latest/reference/config/server-configuration-overview.html#variable-substitution)
- [Bootstrap Properties](https://openliberty.io/docs/latest/reference/config/server-configuration-overview.html#bootstrap-properties)

## Notes

- Bootstrap properties are loaded once at server startup
- Changes require server restart
- Use environment variables for runtime overrides
- Property names are case-sensitive
- Underscores in environment variables map to dots in property names
  - `DEFAULT_HTTP_PORT` → `default.http.port`