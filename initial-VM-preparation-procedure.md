# Inventory SaaS – Ubuntu VM Deployment Guide

## 1. Overview

This document describes how to prepare an Ubuntu VM to deploy and run the Inventory SaaS Spring Boot application as a Linux system service using `systemd`.

**Deployment architecture:**

- **Java:** OpenJDK 25
- **Application directory:** `/opt/inventory-saas`
- **Service user:** `inventory`
- **Service manager:** `systemd`
- **Configuration:** `.env.spring`
- **Application logs:** `/opt/inventory-saas/logs/application.log`

## 2. Prepare the Ubuntu VM

### Step 1 – Update the system and install Java 25

```bash
sudo apt update
sudo apt install -y openjdk-25-jdk
```

Verify the Java installation:

```bash
java -version
```

Check the Java executable path:

```bash
readlink -f "$(which java)"
```

### Step 2 – Create the application directory

```bash
sudo mkdir -p /opt/inventory-saas
```

Create the logs and secrets directories:

```bash
sudo mkdir -p /opt/inventory-saas/logs
sudo mkdir -p /opt/inventory-saas/secrets
```

### Step 3 – Create a dedicated system user

Create a dedicated Linux account to run the application.

```bash
sudo useradd \
  --system \
  --no-create-home \
  --shell /usr/sbin/nologin \
  inventory
```

If the user already exists, this command may report that it already exists; in that case, continue.

Set directory ownership and permissions:

```bash
sudo chown -R inventory:inventory /opt/inventory-saas
sudo chmod 750 /opt/inventory-saas
sudo chmod 750 /opt/inventory-saas/logs
sudo chmod 700 /opt/inventory-saas/secrets
```

The application will run under the `inventory` account, not under a personal Linux account.

## 3. Configure the systemd service

### Step 1 – Create the service definition

```bash
sudo nano /etc/systemd/system/inventory-saas.service
```

Add the following configuration:

```ini
[Unit]
Description=Inventory SaaS Spring Boot Application
Wants=network-online.target
After=network-online.target

[Service]
Type=simple
User=inventory
Group=inventory
WorkingDirectory=/opt/inventory-saas
Environment="JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64"
Environment="OTEL_SERVICE_NAME=inventory-saas"
Environment="OTEL_EXPORTER_OTLP_ENDPOINT=http://127.0.0.1:4318"
Environment="OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf"
Environment="OTEL_TRACES_EXPORTER=otlp"
Environment="OTEL_TRACES_SAMPLER=parentbased_traceidratio"
Environment="OTEL_TRACES_SAMPLER_ARG=1.0"

# --- Addition to capture SQL queries ---
Environment="OTEL_INSTRUMENTATION_JDBC_ENABLED=true"
#Environment="OTEL_INSTRUMENTATION_JDBC_STATEMENT_SANITIZER_ENABLED=true"
# Optional: to view the settings (use with caution)
Environment="OTEL_INSTRUMENTATION_COMMON_DB_STATEMENT_SANITIZER_ENABLED=false"
Environment="OTEL_INSTRUMENTATION_JDBC_EXPERIMENTAL_CAPTURE_QUERY_PARAMETERS=true"

EnvironmentFile=/opt/inventory-saas/.env.spring
ExecStart=/usr/lib/jvm/java-25-openjdk-amd64/bin/java -javaagent:/opt/inventory-saas/opentelemetry-javaagent.jar -jar /opt/inventory-saas/inventory-saas-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Important:**

- The JAR must be deployed as `/opt/inventory-saas/inventory-saas.jar`.
- The `.env.spring` file must exist before starting the service.
- `/usr/bin/java` must point to the installed Java 25 executable. Verify this with `java -version` and `readlink -f "$(which java)"`.

### Step 2 – Enable the service

Reload the systemd configuration:

```bash
sudo systemctl daemon-reload
```

Enable automatic startup at VM boot:

```bash
sudo systemctl enable inventory-saas.service
```

## 4. Deploy the application and configuration

The following files must be available in `/opt/inventory-saas/`:

```text
/opt/inventory-saas/
├── inventory-saas.jar
├── .env.spring
├── secrets/
└── logs/
```

### Step 1 – Deploy the Spring Boot JAR

Copy the generated JAR to the VM and use a stable filename:

```bash
sudo install \
  -o inventory \
  -g inventory \
  -m 640 \
  ./target/inventory-saas-0.0.1-SNAPSHOT.jar \
  /opt/inventory-saas/inventory-saas.jar
```

Adjust the source JAR filename according to the Maven project version.

### Step 2 – Install the environment configuration

Copy `.env.spring` into the application directory:

```bash
sudo install \
  -o inventory \
  -g inventory \
  -m 600 \
  ~/.env.spring \
  /opt/inventory-saas/.env.spring
```

Once the configuration has been installed and verified, remove the temporary copy if it is no longer needed:

```bash
rm ~/.env.spring
```

**Security note:** `.env.spring` contains sensitive configuration values. Do not commit it to Git or expose its contents in logs.

### Step 3 – Configure RSA keys

If the application uses RSA keys for JWT signing and verification, place the required key files in:

```text
/opt/inventory-saas/secrets/
```

Set ownership and restrictive permissions:

```bash
sudo chown -R inventory:inventory /opt/inventory-saas/secrets
sudo chmod 700 /opt/inventory-saas/secrets
sudo chmod 600 /opt/inventory-saas/secrets/*
```

Never commit private keys to the Git repository.

## 5. Start and verify the application

Start the application:

```bash
sudo systemctl start inventory-saas.service
```

Check its status:

```bash
sudo systemctl status inventory-saas.service
```

Expected status:

```text
Active: active (running)
```

Check the application logs to verify successful startup.

## 6. Application logs

Spring Boot logs are configured in `application.yml`.

The application log file is stored at:

```text
/opt/inventory-saas/logs/application.log
```

Make sure the directory is writable by the `inventory` user:

```bash
sudo chown -R inventory:inventory /opt/inventory-saas/logs
sudo chmod 750 /opt/inventory-saas/logs
```

Follow application logs in real time:

```bash
sudo tail -f /opt/inventory-saas/logs/application.log
```

List log files and their sizes:

```bash
ls -lh /opt/inventory-saas/logs/
```

The application also writes its standard output and error streams to the systemd journal.

## 7. Useful systemd commands

| Command | Description |
|---|---|
| `sudo systemctl start inventory-saas` | Start the application |
| `sudo systemctl stop inventory-saas` | Stop the application |
| `sudo systemctl restart inventory-saas` | Restart the application |
| `sudo systemctl status inventory-saas` | Check service status |
| `sudo systemctl enable inventory-saas` | Enable startup at boot |
| `sudo systemctl disable inventory-saas` | Disable startup at boot |
| `sudo systemctl daemon-reload` | Reload systemd configuration |

### Journal logs

Follow systemd logs in real time:

```bash
sudo journalctl -u inventory-saas.service -f
```

Display the last 100 log entries:

```bash
sudo journalctl -u inventory-saas.service -n 100 --no-pager
```

Display logs from the current boot:

```bash
sudo journalctl -u inventory-saas.service -b
```

## 8. Deployment checklist

Before considering the VM ready, verify the following:

- [ ] Java 25 is installed and available.
- [ ] The `inventory` system user exists.
- [ ] The application directory has the correct permissions.
- [ ] The logs and secrets directories exist.
- [ ] The `.env.spring` configuration file is installed.
- [ ] RSA keys are installed if required by the application.
- [ ] The application JAR is deployed as `inventory-saas.jar`.
- [ ] The systemd service is enabled.
- [ ] The application service is running.
- [ ] Application logs are being generated.

---

**Deployment directory:** `/opt/inventory-saas`
**Systemd service:** `inventory-saas.service`
**Application user:** `inventory`
**Application logs:** `/opt/inventory-saas/logs/application.log`