# Development Environment Setup Guide
## Jakarta EE and Microservices Course

**Version:** 1.0  
**Last Updated:** December 2025  
**Estimated Setup Time:** 30-45 minutes

---

## 📋 Overview

This guide will help you set up a complete development environment for the Jakarta EE and Microservices course. Follow each section carefully to ensure all tools are properly installed and configured.

---

## 🖥️ System Requirements

### Minimum Requirements
- **OS:** Windows 10/11, macOS 10.15+, or Linux (Ubuntu 20.04+)
- **RAM:** 8 GB (16 GB recommended)
- **Disk Space:** 10 GB free space
- **Internet:** Stable connection for downloads

### Recommended Specifications
- **RAM:** 16 GB or more
- **CPU:** Quad-core processor
- **SSD:** For faster build times

---

## 1️⃣ Java Development Kit (JDK)

### Installation

#### Option A: OpenJDK (Recommended)

**macOS (using Homebrew):**
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install OpenJDK 17
brew install openjdk@17

# Link it
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Windows:**
1. Download OpenJDK 17 from: https://adoptium.net/
2. Run the installer
3. Follow installation wizard

#### Option B: Oracle JDK
Download from: https://www.oracle.com/java/technologies/downloads/#java17

### Verification

```bash
java -version
```

**Expected output:**
```
openjdk version "17.0.x" 2023-xx-xx
OpenJDK Runtime Environment (build 17.0.x+x)
OpenJDK 64-Bit Server VM (build 17.0.x+x, mixed mode, sharing)
```

### Set JAVA_HOME

**macOS/Linux (add to ~/.bashrc or ~/.zshrc):**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH
```

**Windows:**
1. Open System Properties → Environment Variables
2. Add new system variable:
   - Name: `JAVA_HOME`
   - Value: `C:\Program Files\Java\jdk-17`
3. Add to PATH: `%JAVA_HOME%\bin`

---

## 2️⃣ Apache Maven

### Installation

**macOS:**
```bash
brew install maven
```

**Linux:**
```bash
sudo apt update
sudo apt install maven
```

**Windows:**
1. Download from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH: `C:\Program Files\Apache\maven\bin`

### Verification

```bash
mvn -version
```

**Expected output:**
```
Apache Maven 3.8.x or higher
Maven home: /path/to/maven
Java version: 17.0.x
```

### Configure Maven Settings

Create/edit `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <localRepository>${user.home}/.m2/repository</localRepository>
    
    <mirrors>
        <mirror>
            <id>central</id>
            <mirrorOf>central</mirrorOf>
            <url>https://repo.maven.apache.org/maven2</url>
        </mirror>
    </mirrors>
    
    <profiles>
        <profile>
            <id>jdk-17</id>
            <activation>
                <jdk>17</jdk>
            </activation>
            <properties>
                <maven.compiler.source>17</maven.compiler.source>
                <maven.compiler.target>17</maven.compiler.target>
            </properties>
        </profile>
    </profiles>
</settings>
```

---

## 3️⃣ OpenLiberty Application Server

### Automatic Installation via Maven

**Good News!** OpenLiberty is automatically downloaded by Maven when you build the labs. No manual installation required!

### Manual Installation (Optional)

If you want to install OpenLiberty manually:

1. **Download OpenLiberty 24.0+:**
   - Visit: https://openliberty.io/downloads/
   - Download: OpenLiberty Runtime (ZIP)

2. **Extract:**

**macOS/Linux:**
```bash
cd ~/Downloads
unzip openliberty-*.zip
sudo mv wlp /opt/openliberty
```

**Windows:**
- Extract to: `C:\openliberty`

3. **Set Environment Variable (Optional):**

**macOS/Linux (add to ~/.bashrc or ~/.zshrc):**
```bash
export LIBERTY_HOME=/opt/openliberty/wlp
export PATH=$LIBERTY_HOME/bin:$PATH
```

**Windows:**
- Add system variable: `LIBERTY_HOME` = `C:\openliberty\wlp`
- Add to PATH: `%LIBERTY_HOME%\bin`

### Verification

OpenLiberty will be automatically started by the lab deployment scripts. You can verify it's working when you run your first lab.

**Default Ports:**
- Application: http://localhost:9080
- Admin Center: http://localhost:9443 (if enabled)

---

## 4️⃣ Podman or Docker (Container Runtime)

### Why Podman/Docker?

The labs use containerized deployment for:
- ✅ Consistent environment across all platforms
- ✅ Easy cleanup and reset
- ✅ No manual server configuration
- ✅ Automated testing

### Option A: Podman (Recommended for Linux/macOS)

**macOS:**
```bash
brew install podman
podman machine init
podman machine start
```

**Linux:**
```bash
sudo apt update
sudo apt install podman
```

**Verification:**
```bash
podman --version
podman ps
```

### Option B: Docker

**macOS/Windows:**
- Download Docker Desktop: https://www.docker.com/products/docker-desktop/
- Install and start Docker Desktop

**Linux:**
```bash
sudo apt install docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
# Log out and back in for group changes
```

**Verification:**
```bash
docker --version
docker ps
```

### Choose One

You only need **either** Podman **or** Docker, not both. The lab scripts support both.

---

## 5️⃣ PostgreSQL Database (For Later Labs)

**Note:** PostgreSQL is not required for Labs 1-2. You'll need it for Lab 3 (JPA) onwards.

### Installation

**macOS:**
```bash
brew install postgresql@14
brew services start postgresql@14
```

**Linux:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**Windows:**
1. Download from: https://www.postgresql.org/download/windows/
2. Run installer
3. Remember the password you set for postgres user

### Create Database and User (When Needed)

```bash
# Connect to PostgreSQL
psql -U postgres

# In PostgreSQL prompt:
CREATE DATABASE bankingdb;
CREATE USER bankuser WITH ENCRYPTED PASSWORD 'bankpass';
GRANT ALL PRIVILEGES ON DATABASE bankingdb TO bankuser;
\q
```

### Verification

```bash
psql -U bankuser -d bankingdb -h localhost
```

**You can skip this section for now and return to it before Lab 3.**

---

## 6️⃣ Integrated Development Environment (IDE)

### Option A: IntelliJ IDEA (Recommended)

1. **Download:**
   - Community Edition (Free): https://www.jetbrains.com/idea/download/
   - Ultimate Edition (Paid, better Jakarta EE support)

2. **Install Required Plugins:**
   - Jakarta EE
   - Maven
   - Database Tools
   - Git

3. **Configure:**
   - File → Project Structure → SDKs → Add JDK 17
   - File → Settings → Build Tools → Maven → Set Maven home
   - No server configuration needed (labs use containers)

### Option B: Eclipse IDE

1. **Download Eclipse IDE for Enterprise Java:**
   https://www.eclipse.org/downloads/packages/

2. **Install Plugins:**
   - Maven Integration
   - Docker/Podman Tools (optional)

### Option C: Visual Studio Code

1. **Download:** https://code.visualstudio.com/

2. **Install Extensions:**
   - Extension Pack for Java
   - Maven for Java
   - Community Server Connectors
   - Database Client

---

## 7️⃣ Git Version Control

### Installation

**macOS:**
```bash
brew install git
```

**Linux:**
```bash
sudo apt install git
```

**Windows:**
Download from: https://git-scm.com/download/win

### Configuration

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
git config --global init.defaultBranch main
```

### Verification

```bash
git --version
```

---

## 8️⃣ Additional Tools (Optional but Recommended)

### Postman (API Testing)

1. Download: https://www.postman.com/downloads/
2. Install and create free account
3. Import course API collections (provided in later labs)

### DBeaver (Database Management)

1. Download: https://dbeaver.io/download/
2. Install and configure PostgreSQL connection (when needed for Lab 3+)

---

## 9️⃣ Verify Complete Setup

### Run Verification Script

Create `verify-setup.sh` (macOS/Linux) or `verify-setup.bat` (Windows):

```bash
#!/bin/bash

echo "=== Jakarta EE Environment Verification ==="
echo ""

echo "1. Java Version:"
java -version
echo ""

echo "2. Maven Version:"
mvn -version
echo ""

echo "3. Podman/Docker:"
if command -v podman &> /dev/null; then
    echo "Podman version:"
    podman --version
elif command -v docker &> /dev/null; then
    echo "Docker version:"
    docker --version
else
    echo "Neither Podman nor Docker found!"
fi
echo ""

echo "4. PostgreSQL (Optional for Lab 1-2):"
psql --version 2>/dev/null || echo "Not installed (OK for early labs)"
echo ""

echo "5. Git:"
git --version
echo ""

echo "=== Verification Complete ==="
```

Run it:
```bash
chmod +x verify-setup.sh
./verify-setup.sh
```

---

## 🔟 Clone Course Repository

```bash
# Navigate to your workspace
cd ~/workspace

# Clone repository
git clone <repository-url> esipe-javaee

# Navigate to project
cd esipe-javaee

# Verify structure
ls -la
```

---

## 1️⃣1️⃣ Test First Application

### Option 1: Using Liberty Maven Plugin (Recommended for Development)

```bash
cd 03-Labs/Lab01-FirstServlet/solution

# Build and run with Liberty
mvn clean liberty:dev

# In another terminal, test
curl http://localhost:9080/banking-app/welcome
```

**Expected:** HTML response with welcome message

Press `Ctrl+C` to stop the server.

### Option 2: Using Podman/Docker (Recommended for Production-like Testing)

```bash
cd 03-Labs/Lab01-FirstServlet/solution

# Build the application
mvn clean package

# Build and run container with Podman
podman build -t banking-app .
podman run -d -p 9080:9080 --name banking-app banking-app

# Or with Docker
docker build -t banking-app .
docker run -d -p 9080:9080 --name banking-app banking-app

# Test
curl http://localhost:9080/banking-app/welcome

# View logs
podman logs banking-app  # or docker logs banking-app

# Stop and remove
podman stop banking-app && podman rm banking-app
# or
docker stop banking-app && docker rm banking-app
```

**Expected:** HTML response with welcome message

---

## 🆘 Troubleshooting

### Issue: Port 9080 Already in Use

**Solution:**
```bash
# Find process
lsof -i :9080  # macOS/Linux
netstat -ano | findstr :9080  # Windows

# Kill process or change Liberty port
# Edit server.xml, change httpEndpoint port
```

### Issue: Maven Build Fails

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

### Issue: Database Connection Failed (Lab 3+)

**Solution:**
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql  # Linux
brew services list  # macOS

# Verify credentials
psql -U bankuser -d bankingdb -h localhost
```

### Issue: Liberty Won't Start

**Solution:**
```bash
# Check Java version (must be 17 or 21)
java -version

# Check JAVA_HOME
echo $JAVA_HOME

# Check logs
tail -f target/liberty/wlp/usr/servers/defaultServer/logs/messages.log

# Clean and rebuild
mvn clean package
```

### Issue: Podman/Docker Container Won't Start

**Solution:**
```bash
# Check container logs
podman logs <container-name>
# or
docker logs <container-name>

# Check if port is available
lsof -i :9080

# Remove old containers
podman ps -a
podman rm <container-name>

# Rebuild image
podman build --no-cache -t banking-app .
```

---

## 📚 Additional Resources

### Documentation
- [Open Liberty Documentation](https://openliberty.io/docs/)
- [Jakarta EE Specifications](https://jakarta.ee/specifications/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- [Podman Documentation](https://docs.podman.io/)
- [Docker Documentation](https://docs.docker.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Video Tutorials
- Jakarta EE Setup: [YouTube playlist link]
- WildFly Configuration: [YouTube playlist link]

---

## ✅ Setup Checklist

Before starting the course, ensure:

- [ ] JDK 17+ installed and verified
- [ ] Maven 3.8+ installed and configured
- [ ] WildFly 27+ installed and running
- [ ] PostgreSQL installed and database created
- [ ] IDE installed with required plugins
- [ ] Git installed and configured
- [ ] Course repository cloned
- [ ] Sample application builds and deploys
- [ ] Admin console accessible
- [ ] Database connection working

---

## 🎓 Ready to Start!

Once all items are checked, you're ready to begin the course!

**Next Steps:**
1. Review Lecture 1: Introduction to Jakarta EE
2. Complete Lab 1: First Servlet Application
3. Join course discussion forum
4. Attend first class session

**Need Help?**
- Check troubleshooting section
- Consult course forum
- Contact instructor
- Review official documentation

---

**Good luck with your Jakarta EE journey! 🚀**