#!/usr/bin/env sh

export DB_MEMORANDUM_API_PW=$(echo $DB_MEMORANDUM_API_SECRET | grep -o '"password": *"[^"]*"' | cut -d'"' -f4)

java -jar /app/application.jar