
package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de configuración para obtener conexiones a la base de datos.
 * Lee parámetros desde db.properties (ubicado en src/ o classpath raíz).
 */
public class DatabaseConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException(" Archivo '" + PROPERTIES_FILE + "' no encontrado en el classpath.");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar '" + PROPERTIES_FILE + "'", e);
        }
        return props;
    }

    /**
     * Obtiene una nueva conexión a la base de datos.
     * 
     * @return Connection abierta y lista para usar.
     * @throws RuntimeException si falla la conexión.
     */
    public static Connection getConnection() {
        Properties props = loadProperties();
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // Validaciones básicas
        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException(" Propiedad 'db.url' no definida en " + PROPERTIES_FILE);
        }

        try {
            // Cargar driver explícitamente
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(" Driver de MySQL no encontrado. Asegurate de tener mysql-connector-java en el classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException(" Error al conectar a la base de datos. Verifica URL, usuario y contrasenia.", e);
        }
    }
    
    public static void main(String[] args) {
        try (InputStream is = DatabaseConnection.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                System.out.println(" db.properties NO encontrado en el classpath");
            } else {
                System.out.println(" db.properties encontrado");
                Properties p = new Properties();
                p.load(is);
                System.out.println("URL: " + p.getProperty("db.url"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
    
}
