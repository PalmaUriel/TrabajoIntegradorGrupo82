📚 Descripción

Sistema para gestionar libros y sus fichas bibliográficas con relación 1→1. Cada libro puede tener una única ficha con información especializada (ISBN, clasificación, etc.).

⚙️ Requisitos

Java: JDK 11+

MySQL: 8.0+

Driver: MySQL Connector/J


🗄️ Base de Datos
1. Crear base de datos:
sql
SOURCE 1_schema_biblioteca.sql

2. Datos de prueba:
sql
SOURCE 2_datos_prueba.sql

3. Configurar conexión (db.properties):
properties
db.url=jdbc:mysql://localhost:3306/biblioteca_tfi
db.user=root
db.password=tu_password


🚀 Ejecución

Compilar:
bash
javac -cp ".:mysql-connector-java.jar" main/Main.java

Ejecutar:
bash
java -cp ".:mysql-connector-java.jar" main.Main


Enlace de Video: 
