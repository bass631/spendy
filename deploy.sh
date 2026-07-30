#!/bin/bash
set -e

REMOTE=sandra
REMOTE_DIR=/opt/spendy

echo "Синхронизация файлов на сервере..."
rsync -avz --exclude='node_modules' \
           --exclude='dist' \
           --exclude='target' \
           --exclude='build' \
           --exclude='ai' \
           --exclude='docs' \
           --exclude='.git' \
           --exclude='.idea' \
           --exclude='.vscode' \
           --exclude='*.iml' \
           --exclude='.DS_Store' \
           --exclude='*.log' \
           --exclude='*.jar' \
           --exclude='*.war' \
           --exclude='*.zip' \
           --exclude='.env' \
           --exclude='.env.*' \
           --exclude='spendy-backend/src/main/resources/application-dev.yml' \
  ./ $REMOTE:$REMOTE_DIR/

echo "Запуск docker compose на сервере..."
ssh $REMOTE "cd $REMOTE_DIR && docker compose down --rmi all && docker compose up --build -d"

echo "✓ Деплой завершён: " + ssh $REMOTE