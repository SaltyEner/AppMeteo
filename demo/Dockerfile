# Usa l'immagine ufficiale di Java 25 (la stessa che hai usato tu)
FROM eclipse-temurin:25-jdk

# Imposta la cartella di lavoro dentro il container
WORKDIR /app

# Copia tutti i file del tuo progetto all'interno del container
COPY . .

# Permetti l'esecuzione del file Gradle
RUN chmod +x ./gradlew

# Costruisci l'applicazione (crea il file .jar)
RUN ./gradlew bootJar --no-daemon

# Esponi la porta 8081 che abbiamo configurato
EXPOSE 8080

# Comando per avviare l'applicazione quando il container parte
ENTRYPOINT ["sh", "-c", "java -jar build/libs/*.jar"]