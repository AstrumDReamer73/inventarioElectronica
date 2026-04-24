#!/bin/bash

echo "🚀 Iniciando SQL Server..."
/opt/mssql/bin/sqlservr &

echo "⏳ Esperando SQL Server (health real)..."

# espera real a que responda
for i in {1..60}; do
  /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Sql#1234pass" -C -Q "SELECT 1" > /dev/null 2>&1
  if [ $? -eq 0 ]; then
    echo "✅ SQL Server listo"
    break
  fi
  sleep 2
done

echo "📦 Verificando base de datos..."

DB_EXISTS=$(/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Sql#1234pass" -C -Q "SET NOCOUNT ON; SELECT DB_ID('ProyectoResidencias')" -h -1 | tr -d ' ')

if [ "$DB_EXISTS" = "" ] || [ "$DB_EXISTS" = "NULL" ]; then
  echo "📦 Restaurando base de datos..."

  /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Sql#1234pass" -C -Q "
  RESTORE DATABASE ProyectoResidencias
  FROM DISK = '/var/opt/mssql/backup/backup.bak'
  WITH MOVE 'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\ProyectoResidencias.mdf' TO '/var/opt/mssql/data/ProyectoResidencias.mdf',
       MOVE 'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\ProyectoResidencias_log.ldf' TO '/var/opt/mssql/data/ProyectoResidencias_log.ldf',
       REPLACE;
  "

  echo "✅ Restore completado"
else
  echo "✅ DB ya existe, no se restaura"
fi

wait