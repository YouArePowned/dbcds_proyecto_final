#!/bin/bash
# Script de parada del ecosistema completo.
# Detiene todos los procesos Java de los microservicios y el contenedor RabbitMQ.
# Autores: Victor Sanz, Carlos Marques, Sara Cardenas
set -e

echo "[INFO] Parando servicios Java..."
pkill -f "config-server-0.0.1-SNAPSHOT.jar" || true
pkill -f "eureka-server-0.0.1-SNAPSHOT.jar" || true
pkill -f "gateway-0.0.1-SNAPSHOT.jar" || true
pkill -f "api1-triaje-camas-0.0.1-SNAPSHOT.jar" || true
pkill -f "api2-sensores-vitales-0.0.1-SNAPSHOT.jar" || true
pkill -f "api3-supervisor-medico-0.0.1-SNAPSHOT.jar" || true

echo "[INFO] Parando RabbitMQ..."
docker-compose down || true

echo "[INFO] Verificando estado..."
jps | grep -E "config-server|eureka-server|gateway|api1|api2|api3" || echo "[INFO] No quedan procesos del sistema activos."

echo "[INFO] Sistema detenido."
