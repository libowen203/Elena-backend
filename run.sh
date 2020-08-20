./mvnw package
docker build -t punkninja/elena .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE='dev' punkninja/elena