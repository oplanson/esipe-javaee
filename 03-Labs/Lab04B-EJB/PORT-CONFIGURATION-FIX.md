<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab04B-EJB Port Configuration Fix

**Date:** January 18, 2026  
**Status:** ✅ Complete

---

## 🎯 Problem

Port mismatch between docker-compose configuration and Liberty server configuration causing connectivity issues.

### Initial State (Incorrect)

| Component | Port Configuration | Issue |
|-----------|-------------------|-------|
| docker-compose.yml | `9081:9081` | ✅ Correct |
| bootstrap.properties | `9080` | ❌ Wrong |
| server.xml | `9080` | ❌ Wrong |
| podman-test.sh | `APP_PORT=9080` | ❌ Wrong |
| podman-test.sh | `-p 9080:9080` | ❌ Wrong |
| podman-test.sh | `DB_NAME=bankdb` | ❌ Wrong (should be bankingdb) |

**Result:** Application starts on port 9081 inside container, but tests try to access port 9080.

---

## ✅ Solution

Aligned all configurations to use port **9081** (matching docker-compose.yml).

### Files Modified

#### 1. solution/src/main/liberty/config/bootstrap.properties
```properties
# Before
default.http.port=9080
default.https.port=9443

# After
default.http.port=9081
default.https.port=9444
```

#### 2. solution/src/main/liberty/config/server.xml
```xml
<!-- Before -->
<variable name="http.port" defaultValue="9080" />
<variable name="https.port" defaultValue="9443" />

<!-- After -->
<variable name="http.port" defaultValue="9081" />
<variable name="https.port" defaultValue="9444" />
```

#### 3. starter/src/main/liberty/config/bootstrap.properties
```properties
# Before
default.http.port=9080
default.https.port=9443

# After
default.http.port=9081
default.https.port=9444
```

#### 4. starter/src/main/liberty/config/server.xml
```xml
<!-- Before -->
<variable name="http.port" defaultValue="9080" />
<variable name="https.port" defaultValue="9443" />

<!-- After -->
<variable name="http.port" defaultValue="9081" />
<variable name="https.port" defaultValue="9444" />
```

#### 5. podman-test.sh
```bash
# Before
APP_PORT=9080
DB_NAME="bankdb"
-p "$APP_PORT:9080"

# After
APP_PORT=9081
DB_NAME="bankingdb"
-p "$APP_PORT:9081"
```

---

## 📊 Final Configuration

### Port Mapping (All Aligned)

| Component | HTTP Port | HTTPS Port | Status |
|-----------|-----------|------------|--------|
| docker-compose.yml | 9081 | 9444 | ✅ |
| bootstrap.properties (solution) | 9081 | 9444 | ✅ |
| bootstrap.properties (starter) | 9081 | 9444 | ✅ |
| server.xml (solution) | 9081 | 9444 | ✅ |
| server.xml (starter) | 9081 | 9444 | ✅ |
| podman-test.sh | 9081 | - | ✅ |

### Database Configuration (All Aligned)

| Component | DB Name | Status |
|-----------|---------|--------|
| docker-compose.yml | bankingdb | ✅ |
| bootstrap.properties | bankingdb | ✅ |
| server.xml | bankingdb | ✅ |
| podman-test.sh | bankingdb | ✅ |

---

## 🔍 Verification

### Health Check URLs
```bash
# Liveness
curl -f http://localhost:9081/health/live

# Readiness
curl -f http://localhost:9081/health/ready

# Application
curl -f http://localhost:9081/banking
```

### Test Execution
```bash
cd esipe-javaee/03-Labs/Lab04B-EJB
./podman-test.sh              # Test solution
./podman-test.sh -dir starter # Test starter
```

---

## 📝 Why Port 9081?

The port 9081 was chosen to:
1. **Avoid conflicts** with other labs using 9080
2. **Match docker-compose** configuration already in place
3. **Distinguish EJB lab** from other labs (unique port per lab)

---

## ✅ Impact

### Before Fix
- ❌ Health checks fail (wrong port)
- ❌ Application tests fail (wrong port)
- ❌ Database name mismatch
- ❌ Container starts but unreachable

### After Fix
- ✅ Health checks pass
- ✅ Application tests pass
- ✅ Database connectivity works
- ✅ All components aligned

---

## 🎯 Related Files

- `docker-compose.yml` - Container orchestration
- `bootstrap.properties` - Liberty bootstrap config
- `server.xml` - Liberty server config
- `podman-test.sh` - Test script
- `Containerfile` - Container build

---

**Status:** ✅ All port configurations aligned and tested