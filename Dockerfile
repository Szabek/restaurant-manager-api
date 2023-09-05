# Używamy oficjalnego obrazu OpenJDK z zainstalowaną wersją 17 na systemie Alpine
FROM openjdk:17-alpine

# Otwieramy port 8090, aby pozwolić na połączenia przychodzące na ten port
EXPOSE 8090

# Tworzymy katalog roboczy w kontenerze o nazwie /app i przechodzimy do niego
WORKDIR /app

# Kopiujemy plik jar z aplikacją z lokalnej maszyny do katalogu /app w kontenerze
COPY target/RestaurantManager-0.0.1-SNAPSHOT.jar app.jar

# Uruchamiamy aplikację, wykorzystując komendę "java -jar" i plik app.jar
CMD ["java", "-jar", "app.jar"]