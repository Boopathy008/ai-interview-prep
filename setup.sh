#!/bin/bash
# ============================================================
# AI Interview Prep — Quick Setup Script
# ============================================================

set -e

echo ""
echo "🎯 AI Interview Preparation System — Setup"
echo "==========================================="
echo ""

# Check Java 21
if ! java -version 2>&1 | grep -q "21\|22\|23"; then
    echo "❌ Java 21+ is required. Install from https://adoptium.net"
    exit 1
fi
echo "✅ Java: $(java -version 2>&1 | head -1)"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Install from https://maven.apache.org"
    exit 1
fi
echo "✅ Maven: $(mvn -version 2>&1 | head -1)"

# Check MySQL
if ! command -v mysql &> /dev/null; then
    echo "⚠️  MySQL CLI not found. Make sure MySQL 8 is running."
else
    echo "✅ MySQL available"
fi

echo ""
echo "📋 Configuration required:"
echo ""
echo "1. Edit src/main/resources/application.properties:"
echo "   - spring.datasource.password=YOUR_MYSQL_PASSWORD"
echo "   - spring.ai.vertex.ai.gemini.project-id=YOUR_GCP_PROJECT_ID"
echo ""
echo "2. Set Google credentials environment variable:"
echo "   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account-key.json"
echo ""

read -p "Have you configured application.properties? (y/n): " CONFIGURED
if [[ "$CONFIGURED" != "y" ]]; then
    echo "Please configure application.properties first, then re-run this script."
    exit 0
fi

# Database setup
read -p "MySQL root password: " -s MYSQL_PASS
echo ""

echo "🗄️  Setting up database..."
mysql -u root -p"$MYSQL_PASS" -e "CREATE DATABASE IF NOT EXISTS ai_interview_prep CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null && echo "✅ Database created" || echo "⚠️  Database may already exist"

# Build
echo ""
echo "🔨 Building project..."
mvn clean install -q -DskipTests
echo "✅ Build successful"

echo ""
echo "🚀 Starting application..."
echo "   Access at: http://localhost:8080"
echo ""
mvn spring-boot:run
