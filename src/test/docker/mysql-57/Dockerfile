FROM zanox/mysql:5.7

COPY database.sql /

RUN start-mysql && \
    mysql < /database.sql && \
    stop-mysql
