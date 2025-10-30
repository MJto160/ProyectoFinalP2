package Servicios;

import Guardar.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import Modelos.Usuario;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;


import Modelos.Solicitud;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.util.List;
import java.io.InputStream;
import java.util.List;

public class ServicioUsuario {
    private static final String BASE_URL = "http://localhost:8081/api";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final SessionManager sessionManager;

public ServicioUsuario() {
    this.sessionManager = SessionManager.getInstance();
    
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
}
    
    // Logeo y Token
    
    public boolean login(String usuario, String contrasena) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/auth/login");

            String json = String.format(
                "{\"nombre_usuario\":\"%s\",\"contrasena\":\"%s\"}",
                usuario, contrasena
            );

            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());

            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode == 200) {
                JsonNode node = mapper.readTree(responseBody);

                if (node.has("token") && node.has("rol") && node.has("nombre_completo")) {
                    String token = node.get("token").asText();
                    String rol = node.get("rol").asText();
                    String nombreCompleto = node.get("nombre_completo").asText();

                    Usuario usuarioLogueado = new Usuario();
                    usuarioLogueado.setNombre_usuario(usuario);
                    usuarioLogueado.setNombre_completo(nombreCompleto);
                    usuarioLogueado.setRol(rol);

                    sessionManager.iniciarSession(token, usuarioLogueado);
                    return true;
                } else {
                    System.err.println("⚠️ Respuesta incompleta: " + responseBody);
                }
            } else {
                System.err.println("⚠️ Error HTTP: " + statusCode);
            }
        } catch (IOException e) {
            System.err.println("❌ Error al conectar con el servidor: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
        }
        return false;
    }

    
    public boolean signup(String usuario, String contrasena, String nombreCompleto, String rol) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/auth/signup");
            String json = String.format(
                "{\"nombre_usuario\":\"%s\",\"contrasena\":\"%s\",\"nombre_completo\":\"%s\",\"rol\":\"%s\"}", 
                usuario, contrasena, nombreCompleto, rol
            );
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            return response.getCode() == 201;
        }
    }

    // Creacion y Extraccion
    
    public List<Usuario> getUsuarios() throws Exception {
        if (!sessionManager.isSessionActiva()) {
            throw new Exception("Debe iniciar sesión primero");
        }
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/usuarios");
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, new TypeReference<List<Usuario>>() {});
        }
    }
    
    public Usuario createUsuario(Usuario u) throws Exception {
        if (!sessionManager.isSessionActiva()) {
            throw new Exception("Debe iniciar sesión primero");
        }
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/usuarios");
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            String json = mapper.writeValueAsString(u);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, Usuario.class);
        }
    }
    
    public Usuario updateUsuario(int id, Usuario u) throws Exception {
        if (!sessionManager.isSessionActiva()) {
            throw new Exception("Debe iniciar sesión primero");
        }
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/usuarios/" + id);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            String json = mapper.writeValueAsString(u);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, Usuario.class);
        }
    }
    
    public String obtenerNombreUsuarioLogueado() {
    if (sessionManager.isSessionActiva()) {
        Usuario usuario = sessionManager.getUsuarioLogueado();
        return usuario != null ? usuario.getNombre_completo() : "Usuario";
    }
    return "Invitado";
}
    
public List<Solicitud> getSolicitudes() throws Exception {
    if (!sessionManager.isSessionActiva()) {
        throw new Exception("Debe iniciar sesión primero");
    }
    
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpGet request = new HttpGet(BASE_URL + "/solicitudes");
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        int statusCode = response.getCode();
        
        if (statusCode == 200) {
            // DEBUG: Leer la respuesta como string primero
            String jsonResponse = EntityUtils.toString(response.getEntity());
            System.out.println("=== DEBUG JSON RESPONSE ===");
            System.out.println(jsonResponse);
            System.out.println("===========================");
            
            // Convertir de nuevo a InputStream para el mapper
            InputStream is = new java.io.ByteArrayInputStream(jsonResponse.getBytes());
            List<Solicitud> solicitudes = mapper.readValue(is, new TypeReference<List<Solicitud>>() {});
            
            // DEBUG: Ver qué se cargó
            System.out.println("Solicitudes cargadas: " + solicitudes.size());
            for (Solicitud s : solicitudes) {
                System.out.println("Solicitud ID: " + s.getId_solicitud() + 
                                 ", Usuario: " + (s.getUsuario() != null ? s.getUsuario().getNombre_completo() : "null"));
            }
            
            return solicitudes;
        } else if (statusCode == 403) {
            throw new Exception("No tiene permisos de administrador para ver las solicitudes");
        } else {
            throw new Exception("Error al obtener solicitudes: " + statusCode);
        }
    }
}

    // Método para responder a una solicitud (cambiar contraseña)
    public boolean responderSolicitud(int idSolicitud, String nuevaContrasena, String estado) throws Exception {
        if (!sessionManager.isSessionActiva()) {
            throw new Exception("Debe iniciar sesión primero");
        }
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/solicitudes/" + idSolicitud);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            
            String json = String.format(
                "{\"nuevaContrasena\":\"%s\",\"estado\":\"%s\"}",
                nuevaContrasena, estado
            );
            
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            return response.getCode() == 200;
        }
    }

    // Método para crear una solicitud (para vendedores)
public boolean crearSolicitud(String nombreUsuario, String motivo) throws Exception {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPost request = new HttpPost(BASE_URL + "/solicitudes");
        
        // Incluir el nombre de usuario en el JSON
        String json = String.format(
            "{\"nombre_usuario\":\"%s\",\"motivo\":\"%s\"}", 
            nombreUsuario, motivo
        );
        
        System.out.println("🔍 JSON enviado: " + json); // Para debug
        
        request.setEntity(EntityBuilder.create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        int statusCode = response.getCode();
        System.out.println("🔍 Código de respuesta: " + statusCode); // Para debug
        
        return statusCode == 200 || statusCode == 201;
    }
}

public boolean crearSolicitudRecuperacion(String nombreUsuario, String motivo) throws Exception {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPost request = new HttpPost(BASE_URL + "/solicitudes/recuperacion");
        
        String json = String.format(
            "{\"nombre_usuario\":\"%s\",\"motivo\":\"%s\"}", 
            nombreUsuario, motivo
        );
        
        System.out.println("🔍 DEBUG JSON: " + json);
        
        request.setEntity(EntityBuilder.create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        int statusCode = response.getCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        
        System.out.println("🔍 DEBUG Status: " + statusCode);
        System.out.println("🔍 DEBUG Response: " + responseBody);
        
        // Verifica si la respuesta es exitosa
        if (statusCode == 200 || statusCode == 201) {
            System.out.println("✅ Solicitud creada exitosamente en el servidor");
            return true;
        } else {
            System.out.println("❌ Error del servidor: " + responseBody);
            return false;
        }
    }
}
    
    
    public void logout() {
        sessionManager.cerrarSession();
    }
    
    public boolean isLoggedIn() {
        return sessionManager.isSessionActiva();
    }
    
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}

