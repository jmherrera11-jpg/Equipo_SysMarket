
package Ejecutable;

import database.MongoDBConnection;
import view.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SISTEMA MINIMARKET - INICIANDO");
        System.out.println("========================================");
        
        try {
            // Conectar a MongoDB
            System.out.println("🔄 Conectando a MongoDB...");
            MongoDBConnection.connect();
            
            // Iniciar la interfaz gráfica
            System.out.println("🚀 Iniciando interfaz gráfica...");
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                System.out.println("✅ Interfaz de login cargada");
            });
            
            // Cerrar conexión al salir
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🔒 Cerrando aplicación...");
                MongoDBConnection.close();
            }));
            
        } catch (Exception e) {
            System.err.println("❌ Error fatal: " + e.getMessage());
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error al iniciar: " + e.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}