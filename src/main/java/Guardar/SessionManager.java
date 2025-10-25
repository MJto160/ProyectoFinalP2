package Guardar;

import Modelos.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final String SESSION_FILE = "session.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static SessionManager instance;
    private String authToken;
    private Usuario usuarioLogueado;
    // private String AuthToken; // ← ELIMINAR - duplicado
    // private boolean TokenExpirado; // ← ELIMINAR - no se usa
    
    // Patrón Singleton
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
            instance.cargarSession();
        }
        return instance;
    }
    
    private SessionManager() {}
    
    // ==================== MÉTODOS PÚBLICOS ====================
    
    public void iniciarSession(String token, Usuario usuario) {
        this.authToken = token;
        this.usuarioLogueado = usuario;
        guardarSession();
    }
    
    public void cerrarSession() {
        this.authToken = null;
        this.usuarioLogueado = null;
        eliminarSession();
    }
    
    public boolean isSessionActiva() {
        return authToken != null && !authToken.isEmpty() && usuarioLogueado != null;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }
    
    public int getUsuarioId() {
        return usuarioLogueado != null ? usuarioLogueado.getId_usuario() : -1;
    }
    
    public String getUsuarioRol() {
        return usuarioLogueado != null ? usuarioLogueado.getRol() : "";
    }
    
    public String getNombreUsuario() {
        return usuarioLogueado != null ? usuarioLogueado.getNombre_completo() : "";
    }
    
    // ==================== PERSISTENCIA ====================
    
    private void guardarSession() {
        try {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("authToken", authToken);
            sessionData.put("usuario", usuarioLogueado);
            
            mapper.writeValue(new File(SESSION_FILE), sessionData);
            System.out.println("Sesión guardada correctamente");
        } catch (IOException e) {
            System.err.println("Error guardando sesión: " + e.getMessage());
        }
    }
    
    private void cargarSession() {
        try {
            File sessionFile = new File(SESSION_FILE);
            if (sessionFile.exists()) {
                Map<String, Object> sessionData = mapper.readValue(sessionFile, Map.class);
                
                // Convertir el Map de usuario back a objeto Usuario
                Map<String, Object> usuarioMap = (Map<String, Object>) sessionData.get("usuario");
                if (usuarioMap != null) {
                    this.usuarioLogueado = mapper.convertValue(usuarioMap, Usuario.class);
                }
                
                this.authToken = (String) sessionData.get("authToken");
                System.out.println("Sesión cargada - Usuario: " + (usuarioLogueado != null ? usuarioLogueado.getNombre_completo() : "null"));
            } else {
                System.out.println("No existe archivo de sesión previa");
            }
        } catch (IOException e) {
            System.err.println("Error cargando sesión: " + e.getMessage());
        }
    }
    
    private void eliminarSession() {
        try {
            File sessionFile = new File(SESSION_FILE);
            if (sessionFile.exists()) {
                boolean eliminado = sessionFile.delete();
                System.out.println("Sesión eliminada: " + eliminado);
            }
        } catch (Exception e) {
            System.err.println("Error eliminando sesión: " + e.getMessage());
        }
    }
    
    // ==================== VERIFICACIONES ====================
    
    public boolean esAdmin() {
        return isSessionActiva() && "admin".equalsIgnoreCase(usuarioLogueado.getRol());
    }
    
    public boolean esVendedor() {
        return isSessionActiva() && "vendedor".equalsIgnoreCase(usuarioLogueado.getRol());
    }
    
    public boolean tienePermiso(String rolRequerido) {
        if (!isSessionActiva()) return false;
        return rolRequerido.equalsIgnoreCase(usuarioLogueado.getRol());
    }
    
    public boolean isTokenExpirado() {
        // Implementación básica - puedes mejorarla con timestamp
        if (!isSessionActiva()) {
            return true;
        }
        
        // Verificar si el archivo de sesión es muy antiguo (más de 24 horas)
        File sessionFile = new File(SESSION_FILE);
        if (sessionFile.exists()) {
            long lastModified = sessionFile.lastModified();
            long now = System.currentTimeMillis();
            long hoursDiff = (now - lastModified) / (1000 * 60 * 60);
            
            // Considerar expirado después de 24 horas
            return hoursDiff > 24;
        }
        
        return true;
    }
    
    // Método para verificar estado completo de la sesión (para debug)
    public void debugSession() {
        System.out.println("=== DEBUG SESSION ===");
        System.out.println("Sesión activa: " + isSessionActiva());
        System.out.println("Token presente: " + (authToken != null && !authToken.isEmpty()));
        System.out.println("Usuario: " + (usuarioLogueado != null ? usuarioLogueado.getNombre_completo() : "null"));
        System.out.println("Rol: " + getUsuarioRol());
        System.out.println("Token expirado: " + isTokenExpirado());
        System.out.println("Archivo sesión existe: " + new File(SESSION_FILE).exists());
        System.out.println("=== END DEBUG ===");
    }
}