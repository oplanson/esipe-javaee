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

## 3️⃣ WildFly Application Server

### Download and Installation

1. **Download WildFly 27+:**
   - Visit: https://www.wildfly.org/downloads/
   - Download: WildFly 27.x.x Final (ZIP)

2. **Extract:**

**macOS/Linux:**
```bash
cd ~/Downloads
unzip wildfly-27.*.zip
sudo mv wildfly-27.* /opt/wildfly
```

**Windows:**
- Extract to: `C:\wildfly`

3. **Set Environment Variable:**

**macOS/Linux (add to ~/.bashrc or ~/.zshrc):**
```bash
export WILDFLY_HOME=/opt/wildfly
export PATH=$WILDFLY_HOME/bin:$PATH
```

**Windows:**
- Add system variable: `WILDFLY_HOME` = `C:\wildfly`
- Add to PATH: `%WILDFLY_HOME%\bin`

### Start WildFly

**macOS/Linux:**
```bash
$WILDFLY_HOME/bin/standalone.sh
```

**Windows:**
```cmd
%WILDFLY_HOME%\bin\standalone.bat
```

### Verification

1. Wait for startup message:
   ```
   WildFly 27.x.x.Final started in XXXXms
   ```

2. Open browser: http://localhost:8080

3. You should see WildFly welcome page

### Create Admin User

```bash
# macOS/Linux
$WILDFLY_HOME/bin/add-user.sh

# Windows
%WILDFLY_HOME%\bin\add-user.bat
```

Follow prompts:
- Type: Management User
- Username: `admin`
- Password: `admin` (or your choice)
- Groups: (leave empty)

### Access Admin Console

- URL: http://localhost:9990
- Login with admin credentials

---

## 4️⃣ PostgreSQL Database

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

### Create Database and User

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

### Install JDBC Driver in WildFly

1. **Download PostgreSQL JDBC Driver:**
   ```bash
   cd $WILDFLY_HOME/standalone/deployments
   curl -O https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
   ```

2. **Configure DataSource:**
   
   Edit `$WILDFLY_HOME/standalone/configuration/standalone.xml`
   
   Add inside `<datasources>` section:
   ```xml
   <datasource jndi-name="java:jboss/datasources/BankingDS" 
               pool-name="BankingDS" 
               enabled="true">
       <connection-url>jdbc:postgresql://localhost:5432/bankingdb</connection-url>
       <driver>postgresql</driver>
       <security>
           <user-name>bankuser</user-name>
           <password>bankpass</password>
       </security>
   </datasource>
   ```

---

## 5️⃣ Integrated Development Environment (IDE)

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

### Option B: Eclipse IDE

1. **Download Eclipse IDE for Enterprise Java:**
   https://www.eclipse.org/downloads/packages/

2. **Install Plugins:**
   - WildFly Tools
   - Maven Integration

### Option C: Visual Studio Code

1. **Download:** https://code.visualstudio.com/

2. **Install Extensions:**
   - Extension Pack for Java
   - Maven for Java
   - Community Server Connectors
   - Database Client

---

## 6️⃣ Git Version Control

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

## 7️⃣ Additional Tools (Optional but Recommended)

### Postman (API Testing)

1. Download: https://www.postman.com/downloads/
2. Install and create free account
3. Import course API collections (provided later)

### Docker (For Containerization)

**macOS/Windows:**
- Download Docker Desktop: https://www.docker.com/products/docker-desktop/

**Linux:**
```bash
sudo apt install docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
```

### DBeaver (Database Management)

1. Download: https://dbeaver.io/download/
2. Install and configure PostgreSQL connection

---

## 8️⃣ Verify Complete Setup

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

echo "3. WildFly Home:"
echo $WILDFLY_HOME
echo ""

echo "4. PostgreSQL:"
psql --version
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

## 9️⃣ Clone Course Repository

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

## 🔟 Test First Application

### Build and Deploy Sample App

```bash
cd 03-Labs/Lab01-FirstServlet/solution

# Build
mvn clean package

# Start WildFly (if not running)
$WILDFLY_HOME/bin/standalone.sh &

# Deploy
mvn wildfly:deploy

# Test
curl http://localhost:8080/banking-app/welcome
```

**Expected:** HTML response with welcome message

---

## 🆘 Troubleshooting

### Issue: Port 8080 Already in Use

**Solution:**
```bash
# Find process
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process or change WildFly port
# Edit standalone.xml, change socket-binding port
```

### Issue: Maven Build Fails

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

### Issue: Database Connection Failed

**Solution:**
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql  # Linux
brew services list  # macOS

# Verify credentials
psql -U bankuser -d bankingdb -h localhost
```

### Issue: WildFly Won't Start

**Solution:**
```bash
# Check Java version
java -version

# Check JAVA_HOME
echo $JAVA_HOME

# Check logs
tail -f $WILDFLY_HOME/standalone/log/server.log
```

---

## 📚 Additional Resources

### Documentation
- [WildFly Documentation](https://docs.wildfly.org/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
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