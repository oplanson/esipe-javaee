# © Copyright Olivier Planson - 2025
# Containerfile Documentation

## Overview

This Containerfile builds a container image for the Banking Application using Open Liberty as the application server.

## Base Image

```dockerfile
FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi
```

**Details:**
- **Registry:** IBM Container Registry (icr.io)
- **Image:** Open Liberty Full Profile
- **Java Version:** Java 17
- **JVM:** OpenJ9 (Eclipse OpenJ9 - optimized for containers)
- **Base OS:** Red Hat Universal Base Image (UBI)

## Build Steps

### Step 1: Copy Application WAR

```dockerfile
COPY --chown=1001:0 target/banking-app.war /config/apps/
```

- Copies the built WAR file to Open Liberty's apps directory
- `--chown=1001:0`: Sets ownership to Open Liberty user (1001) and root group (0)
- `/config/apps/`: Directory for applications explicitly configured in server.xml
- **Note:** We use `/config/apps/` instead of `/config/dropins/` to demonstrate explicit configuration in server.xml for educational purposes

### Step 2: Copy Server Configuration

```dockerfile
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 src/main/liberty/config/bootstrap.properties /config/
```

- Copies Liberty server configuration files
- `server.xml`: Defines features, ports, and application settings
- `bootstrap.properties`: Defines variables used in server.xml (ports, context root)
- Located at: `src/main/liberty/config/`

### Step 3: Copy MicroProfile Configuration

```dockerfile
COPY --chown=1001:0 src/main/resources/META-INF/microprofile-config.properties /config/
```

- Copies MicroProfile configuration properties
- Enables Health, Metrics, and OpenAPI features
- Located at: `src/main/resources/META-INF/microprofile-config.properties`

### Step 4: Configure Liberty Features

```dockerfile
RUN configure.sh
```

- Runs Open Liberty configuration script
- Installs required features based on server.xml
- Optimizes image size by removing unused features

### Step 5: Expose Ports

```dockerfile
EXPOSE 9080 9443
```

- **9080:** HTTP port
- **9443:** HTTPS port

### Step 6: Start Server

```dockerfile
CMD ["/opt/ol/wlp/bin/server", "run", "defaultServer"]
```

- Starts Open Liberty in foreground mode
- Server name: `defaultServer`

## Building the Image

### Using Podman (Recommended)

```bash
# Navigate to solution directory
cd solution

# Build the application first
mvn clean package

# Build the container image
podman build -t banking-app:lab01 -f Containerfile .

# Verify image
podman images | grep banking-app
```

### Using Docker

```bash
# Navigate to solution directory
cd solution

# Build the application first
mvn clean package

# Build the container image
docker build -t banking-app:lab01 -f Containerfile .

# Verify image
docker images | grep banking-app
```

## Running the Container

### Using Podman

```bash
# Run in detached mode
podman run -d \
  --name banking-app-lab01 \
  -p 9080:9080 \
  -p 9443:9443 \
  banking-app:lab01

# View logs
podman logs -f banking-app-lab01

# Stop container
podman stop banking-app-lab01

# Remove container
podman rm banking-app-lab01
```

### Using Docker

```bash
# Run in detached mode
docker run -d \
  --name banking-app-lab01 \
  -p 9080:9080 \
  -p 9443:9443 \
  banking-app:lab01

# View logs
docker logs -f banking-app-lab01

# Stop container
docker stop banking-app-lab01

# Remove container
docker rm banking-app-lab01
```

## Accessing the Application

Once the container is running:

### Application URLs
- **Home:** http://localhost:9080/
- **Welcome:** http://localhost:9080/welcome
- **Clients:** http://localhost:9080/clients
- **Add Client:** http://localhost:9080/add-client.html

### MicroProfile Endpoints
- **Health:** http://localhost:9080/health
- **Metrics:** http://localhost:9080/metrics
- **OpenAPI:** http://localhost:9080/openapi
- **OpenAPI UI:** http://localhost:9080/openapi/ui

## Image Optimization

### Current Image Size
Approximately 500-600 MB (full profile)

### Optimization Options

1. **Use Kernel Profile** (smaller, fewer features):
   ```dockerfile
   FROM icr.io/appcafe/open-liberty:kernel-java17-openj9-ubi
   ```

2. **Multi-stage Build** (separate build and runtime):
   ```dockerfile
   # Build stage
   FROM maven:3.9-eclipse-temurin-17 AS builder
   WORKDIR /app
   COPY pom.xml .
   COPY src ./src
   RUN mvn clean package
   
   # Runtime stage
   FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi
   COPY --from=builder /app/target/banking-app.war /config/dropins/
   # ... rest of configuration
   ```

3. **Remove Unnecessary Features** in server.xml

## Troubleshooting

### Issue: Container Fails to Start

**Check logs:**
```bash
podman logs banking-app-lab01
```

**Common causes:**
- Port already in use
- Insufficient memory
- Invalid server.xml configuration

### Issue: Application Not Accessible

**Verify container is running:**
```bash
podman ps | grep banking-app
```

**Check port mapping:**
```bash
podman port banking-app-lab01
```

**Test connectivity:**
```bash
curl http://localhost:9080/health
```

### Issue: Build Fails

**Ensure WAR file exists:**
```bash
ls -lh target/banking-app.war
```

**Rebuild application:**
```bash
mvn clean package
```

**Check Containerfile syntax:**
```bash
podman build --no-cache -t banking-app:lab01 -f Containerfile .
```

## Security Considerations

### User Permissions
- Container runs as non-root user (1001)
- Files owned by user 1001, group 0 (root group)
- Follows OpenShift security best practices

### Network Security
- Only necessary ports exposed (9080, 9443)
- HTTPS available on port 9443
- Consider using secrets for sensitive configuration

### Image Security
- Based on Red Hat UBI (regularly updated)
- Scan image for vulnerabilities:
  ```bash
  podman scan banking-app:lab01
  ```

## References

- [Open Liberty Container Images](https://openliberty.io/docs/latest/container-images.html)
- [Podman Documentation](https://docs.podman.io/)
- [Docker Documentation](https://docs.docker.com/)
- [Containerfile Best Practices](https://docs.podman.io/en/latest/markdown/podman-build.1.html)

## Notes

- The Containerfile is compatible with both Podman and Docker
- Open Liberty automatically deploys applications in `/config/dropins/`
- Configuration changes require rebuilding the image
- For development, consider mounting volumes for hot reload