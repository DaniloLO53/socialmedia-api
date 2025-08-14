# ESTÁGIO 1: Build com o JDK completo
# Usamos a imagem com o JDK completo e a nomeamos como 'build'
FROM amazoncorretto:21-al2023-jdk AS build

# Define o diretório de trabalho
WORKDIR /socialmedia-api

# INSTALA O 'TAR' e 'GZIP' ANTES DE USAR O MVNW
RUN dnf install -y tar gzip

# Copia o wrapper do Maven/Gradle e o arquivo de dependências
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Baixa as dependências
RUN ./mvnw dependency:go-offline

# Copia o resto do código-fonte
COPY src src

# Compila o projeto e gera o .jar
RUN ./mvnw clean package -DskipTests


# ESTÁGIO 2: Produção com a imagem headless (enxuta)
# Começamos uma nova imagem do zero, muito menor
FROM amazoncorretto:21-al2023-headless

# Define o diretório de trabalho
WORKDIR /app

# Copia APENAS o arquivo .jar gerado no estágio anterior
COPY --from=build /socialmedia-api/target/*.jar app.jar

# Expõe a porta que a aplicação usa
EXPOSE 8090

# Comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]