FROM zanox/mysql:5.6

COPY database.sql /

RUN start-mysql && \
    mysql < /database.sql && \
    stop-mysql
