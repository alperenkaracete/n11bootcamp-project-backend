#!/bin/bash
set -e

# Tüm servislerin veritabanlarını oluşturur
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE user_db;
    CREATE DATABASE product_db;
    CREATE DATABASE stock_db;
    CREATE DATABASE order_db;
    CREATE DATABASE payment_db;
    CREATE DATABASE log_db;
EOSQL
