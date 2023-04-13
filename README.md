# demo-saga
```
$ git clone https://github.com/wurstmeister/kafka-docker
$ cd kafka-docker
$ docker-compose up

$ git clone https://github.com/tchiotludo/akhq
$ cd akhq


$ vim docker-compose.yml
```

M1에서도 실행 가능하도록 platform 옵션 추가
로컬환경에서도 Kafka 서버 접속되도록 ports 설정 추가

```
...생략...
services:
  akhq:
    platform: linux/x86_64
    image: tchiotludo/akhq
...생략...

  zookeeper:
    platform: linux/x86_64
...생략...

  kafka:
    platform: linux/x86_64
    image: confluentinc/cp-kafka
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
    KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
...생략...
    ports:
      - "29092:29092"
    links:
      - zookeeper

  schema-registry:
    platform: linux/x86_64
    image: confluentinc/cp-schema-registry
...생략...

  connect:
    platform: linux/x86_64
    image: confluentinc/cp-kafka-connect
...생략...

  test-data:
    platform: linux/x86_64
...생략...

  kafkacat:
    platform: linux/x86_64
...생략...

$ docker-compose up
localhost:8080 
```



출처 : https://blog.advenoh.pe.kr/cloud/%EB%A1%9C%EC%BB%AC%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-Kafka-%EC%8B%A4%ED%96%89%ED%95%98%EA%B8%B0-with-AKHQ/
