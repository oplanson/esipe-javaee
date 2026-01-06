<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# API Gateway - URL Structure Documentation

## Overview

The API Gateway uses a clear separation between different types of endpoints:

- **Web UI**: `/web/*` - HTML pages served via JSP
- **REST API**: `/web/api/*` - JSON endpoints for programmatic access
- **MicroProfile Endpoints**: `/health`, `/metrics`, `/openapi` - Server-level monitoring (not under contextRoot)

## Configuration

### server.xml
```xml
<webApplication id="api-gateway" 
                location="api-gateway.war" 
                contextRoot="/web">
```

### RestApplication.java
```java
@ApplicationPath("/api")
public class RestApplication extends Application
```

## URL Mapping

### Web UI Endpoints (HTML/JSP)

| URL Pattern | Servlet | Description |
|-------------|---------|-------------|
| `http://localhost:9080/web/` | index.html | Home page |
| `http://localhost:9080/web/clients` | ClientWebController | List all clients with accounts |
| `http://localhost:9080/web/clients/new` | ClientWebController | New client form |
| `http://localhost:9080/web/clients/edit?id={id}` | ClientWebController | Edit client form |
| `http://localhost:9080/web/clients?action=view&id={id}` | ClientWebController | View client details |
| `http://localhost:9080/web/clients/delete?id={id}` | ClientWebController | Delete client |
| `http://localhost:9080/web/accounts` | AccountWebController | List all accounts |
| `http://localhost:9080/web/accounts?clientId={id}` | AccountWebController | List accounts for client |
| `http://localhost:9080/web/accounts/new?clientId={id}` | AccountWebController | New account form |
| `http://localhost:9080/web/accounts/view?id={id}` | AccountWebController | View account details |
| `http://localhost:9080/web/accounts/deposit?id={id}` | AccountWebController | Deposit form |
| `http://localhost:9080/web/accounts/withdraw?id={id}` | AccountWebController | Withdraw form |
| `http://localhost:9080/web/accounts/transfer?id={id}` | AccountWebController | Transfer form |

### REST API Endpoints (JSON)

#### Client Proxy Resource
| Method | URL Pattern | Description |
|--------|-------------|-------------|
| GET | `http://localhost:9080/web/api/clients` | Get all clients |
| GET | `http://localhost:9080/web/api/clients/{id}` | Get client by ID |
| POST | `http://localhost:9080/web/api/clients` | Create new client |
| PUT | `http://localhost:9080/web/api/clients/{id}` | Update client |
| DELETE | `http://localhost:9080/web/api/clients/{id}` | Delete client |

#### Account Proxy Resource
| Method | URL Pattern | Description |
|--------|-------------|-------------|
| GET | `http://localhost:9080/web/api/accounts` | Get all accounts |
| GET | `http://localhost:9080/web/api/accounts/{id}` | Get account by ID |
| GET | `http://localhost:9080/web/api/accounts/client/{clientId}` | Get accounts by client |
| POST | `http://localhost:9080/web/api/accounts` | Create new account |
| PUT | `http://localhost:9080/web/api/accounts/{id}` | Update account |
| DELETE | `http://localhost:9080/web/api/accounts/{id}` | Delete account |
| POST | `http://localhost:9080/web/api/accounts/{id}/deposit` | Deposit money |
| POST | `http://localhost:9080/web/api/accounts/{id}/withdraw` | Withdraw money |
| POST | `http://localhost:9080/web/api/accounts/{fromId}/transfer/{toId}` | Transfer money |
| PUT | `http://localhost:9080/web/api/accounts/{id}/suspend` | Suspend account |
| PUT | `http://localhost:9080/web/api/accounts/{id}/activate` | Activate account |
| PUT | `http://localhost:9080/web/api/accounts/{id}/close` | Close account |

### MicroProfile Endpoints (Server Level)

**Important**: MicroProfile endpoints are exposed at the server root, NOT under the webApplication contextRoot.

| URL Pattern | Description |
|-------------|-------------|
| `http://localhost:9080/health` | Health check endpoint (aggregate) |
| `http://localhost:9080/health/live` | Liveness probe |
| `http://localhost:9080/health/ready` | Readiness probe |
| `http://localhost:9080/health/started` | Startup probe |
| `http://localhost:9080/metrics` | Metrics endpoint |
| `http://localhost:9080/metrics/application` | Application metrics |
| `http://localhost:9080/metrics/base` | Base metrics |
| `http://localhost:9080/metrics/vendor` | Vendor metrics |
| `http://localhost:9080/openapi` | OpenAPI specification |
| `http://localhost:9080/openapi/ui` | OpenAPI UI (Swagger) |

## Backend Services

### Client Service (Port 9081)
- REST API: `http://localhost:9081/api/clients`
- Health: `http://localhost:9081/health`
- Metrics: `http://localhost:9081/metrics`
- OpenAPI: `http://localhost:9081/openapi/ui/`

### Account Service (Port 9082)
- REST API: `http://localhost:9081/api/accounts`
- Health: `http://localhost:9082/health`
- Metrics: `http://localhost:9082/metrics`
- OpenAPI: `http://localhost:9082/openapi/ui/`

## Benefits of This Structure

### 1. Clear Separation
- **Web UI** (`/web/*`): Human-readable HTML pages
- **REST API** (`/api/*`): Machine-readable JSON responses

### 2. No URL Conflicts
- Web servlets and REST resources have distinct paths
- Easy to apply different security policies
- Clear routing for load balancers

### 3. MicroProfile at Server Level
- Health checks accessible without contextRoot for monitoring tools
- Metrics available for Prometheus/Grafana integration
- OpenAPI documentation at standard location

### 4. Flexibility
- Web UI can be replaced with a modern SPA framework
- REST API remains stable for other consumers
- Easy to version APIs (e.g., `/api/v2/*`)

### 5. Best Practices
- Follows RESTful conventions
- Aligns with microservices patterns
- Supports BFF (Backend For Frontend) architecture
- MicroProfile endpoints follow standard conventions

## Implementation Details

### JSP Files
All JSP files use `${pageContext.request.contextPath}` which automatically resolves to `/web`:

```jsp
<a href="${pageContext.request.contextPath}/clients">View Clients</a>
<!-- Resolves to: /web/clients -->
```

### Servlets
All servlets use `request.getContextPath()` for redirects:

```java
response.sendRedirect(request.getContextPath() + "/clients");
// Redirects to: /web/clients
```

### Static Resources
CSS, JavaScript, and images are served relative to the context path:

```html
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<!-- Resolves to: /web/css/style.css -->
```

# Testing

### Web UI
1. Open browser: `http://localhost:9080/web/`
2. Navigate through the interface
3. All links should work correctly

### REST API
```bash
# Get all clients
curl http://localhost:9080/web/api/clients

# Get client by ID
curl http://localhost:9080/web/api/clients/1

# Create new client
curl -X POST http://localhost:9080/web/api/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com"}'
```

## Migration Notes

If migrating from contextRoot="/":
1. Update `server.xml` to use `contextRoot="/web"`
2. Update `index.html` links to use `/web/` prefix for Web UI
3. JSP files with `${pageContext.request.contextPath}` work automatically
4. Servlets with `request.getContextPath()` work automatically
5. No changes needed to REST API endpoints (already under `/api`)
6. No changes needed to MicroProfile endpoints (always at server root: `/health`, `/metrics`, `/openapi`)

## Important Notes

### MicroProfile Endpoints Location

MicroProfile Health, Metrics, and OpenAPI endpoints are **always** exposed at the server root level, regardless of the webApplication's contextRoot setting. This is by design in Liberty and follows MicroProfile specifications.

**Example**:
- Even with `contextRoot="/web"`, health checks are at `/health`, NOT `/web/health`
- This allows monitoring tools to access these endpoints at predictable locations
- Kubernetes liveness/readiness probes can use standard paths

### URL Accessibility Matrix

| Endpoint Type | Example URL | Accessible Via |
|---------------|-------------|----------------|
| Web UI | `/web/clients` | Browser, direct access |
| REST API | `/api/clients` | HTTP clients, curl, JavaScript |
| MicroProfile | `/health`, `/metrics` | Monitoring tools, Kubernetes |

---

© 2025-2026 Olivier Planson - Banking Microservices Lab 08