# 기반 이미지 설정
FROM adoptopenjdk:17-jdk-hotspot

# 작업 디렉토리 설정
WORKDIR /app

# 호스트의 빌드 결과물을 Docker 내부로 복사
COPY ./build/libs/*.jar app.jar

# 컨테이너 시작 시 실행될 명령어 설정
ENTRYPOINT ["java", "-jar", "app.jar"]