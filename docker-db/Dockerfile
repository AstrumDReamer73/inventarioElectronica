FROM mcr.microsoft.com/mssql/server:2022-latest

ENV ACCEPT_EULA=Y
ENV MSSQL_SA_PASSWORD=Sql#1234pass

USER root

# tools oficiales (sqlcmd)
RUN apt-get update && \
    apt-get install -y curl gnupg2 apt-transport-https && \
    curl https://packages.microsoft.com/keys/microsoft.asc | apt-key add - && \
    curl https://packages.microsoft.com/config/ubuntu/22.04/prod.list > /etc/apt/sources.list.d/mssql-release.list && \
    apt-get update && \
    ACCEPT_EULA=Y apt-get install -y mssql-tools18 unixodbc-dev

RUN ln -s /opt/mssql-tools18/bin/sqlcmd /usr/bin/sqlcmd || true

RUN mkdir -p /var/opt/mssql/backup /var/opt/mssql/data

COPY backup.bak /var/opt/mssql/backup/backup.bak
COPY init.sh /init.sh

RUN chmod +x /init.sh

USER mssql

CMD ["/bin/bash", "/init.sh"]