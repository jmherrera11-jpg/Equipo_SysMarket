package config;

public class Config {
    // Configuración de MongoDB Atlas
    public static final String MONGODB_URI = "mongodb+srv://usuario:password@cluster0.mongodb.net/minimarket_db?retryWrites=true&w=majority";
    public static final String DATABASE_NAME = "minimarket_db";
    
    // Configuración de la aplicación
    public static final String APP_NAME = "Sistema Minimarket";
    public static final String VERSION = "2.0.0";
    
    // Configuración de seguridad
    public static final int MAX_LOGIN_ATTEMPTS = 3;
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    
    // Configuración de reportes
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    // Método para validar conexión
    public static boolean isValidConnection() {
        return MONGODB_URI != null && !MONGODB_URI.contains("usuario:password");
    }
    
    // Método para obtener información de configuración
    public static String getConfigInfo() {
        return String.format(
            "%s v%s\n" +
            "Base de datos: %s\n" +
            "URI: %s",
            APP_NAME, VERSION, DATABASE_NAME, 
            MONGODB_URI.replaceAll(":([^@]+)@", ":****@")
        );
    }
}