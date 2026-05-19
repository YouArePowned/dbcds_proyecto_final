#!/bin/bash
set -e

echo "[INFO] Levantando RabbitMQ..."
docker-compose up -d

echo "[INFO] Esperando RabbitMQ..."
sleep 10

echo "[INFO] Compilando microservicios..."
mvn clean install -DskipTests

echo "[INFO] Iniciando Config Server (puerto 8888)..."
java -jar config-server/target/config-server-0.0.1-SNAPSHOT.jar &
sleep 12

echo "[INFO] Iniciando Eureka Server (puerto 8761)..."
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar &
sleep 12

echo "[INFO] Iniciando Gateway (puerto 8080)..."
java -jar gateway/target/gateway-0.0.1-SNAPSHOT.jar &
sleep 8

echo "[INFO] Iniciando API 1 (puerto 8081)..."
java -jar api1-triaje-camas/target/api1-triaje-camas-0.0.1-SNAPSHOT.jar --spring.profiles.active=local &
sleep 5

echo "[INFO] Iniciando API 2 (puerto 8082)..."
java -jar api2-sensores-vitales/target/api2-sensores-vitales-0.0.1-SNAPSHOT.jar --spring.profiles.active=local &
sleep 3

echo "[INFO] Iniciando API 3 (puerto 8083)..."
java -jar api3-supervisor-medico/target/api3-supervisor-medico-0.0.1-SNAPSHOT.jar --spring.profiles.active=local &
sleep 5

echo ""
echo "[INFO] Sistema iniciado. Servicios disponibles:"
echo "  Dashboard:        http://localhost:8080/static/index.html"
echo "  Swagger UI:       http://localhost:8080/swagger-ui.html"
echo "  RabbitMQ Mgmt:    http://localhost:15672 (guest/guest)"
echo "  Eureka:           http://localhost:8761"
echo "  Config Server:    http://localhost:8888"
echo ""
echo "[INFO] Presiona Ctrl+C para detener todos los servicios."

wait
