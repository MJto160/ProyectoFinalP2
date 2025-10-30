package Servicios;

import Guardar.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import Modelos.Localidad;
import java.io.BufferedReader;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ServicioLocalidade {
    private static final String BASE_URL = "http://localhost:8081/api/localidades";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final SessionManager sessionManager;

    public ServicioLocalidade() {
        this.sessionManager = SessionManager.getInstance();
    }
    
public List<Localidad> getLocalidades() throws Exception {
    verificarSesion();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpGet request = new HttpGet(BASE_URL);
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        
        int statusCode = response.getCode();
        System.out.println("Localidades - Código de respuesta: " + statusCode);
        
        if (statusCode == 500) {
            throw new Exception("Error interno del servidor (500). Contacte al administrador.");
        } else if (statusCode != 200) {
            throw new Exception("Error en la API: " + statusCode);
        }
        
        // Leer la respuesta una sola vez y guardarla en String
        String responseBody;
        try (InputStream is = response.getEntity().getContent();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            responseBody = stringBuilder.toString();
        }
        
        System.out.println("Respuesta cruda de localidades: " + responseBody);
        
        // Procesar la respuesta desde el String
        return parseLocalidadesResponse(responseBody);
        
    }
}

private List<Localidad> parseLocalidadesResponse(String responseBody) throws Exception {
    try {
        return mapper.readValue(responseBody, new TypeReference<List<Localidad>>() {});
    } catch (Exception e) {
        System.err.println("Error parseando respuesta de localidades: " + e.getMessage());
        throw new Exception("Error procesando respuesta del servidor: " + e.getMessage());
    }
}
    private String convertInputStreamToString(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }
public Localidad createLocalidad(Localidad l) throws Exception {
    verificarSesion();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        
        String json = mapper.writeValueAsString(l);
        
        System.out.println("🔍 CREANDO localidad - URL: " + BASE_URL);
        System.out.println("🔍 JSON enviado: " + json);
        System.out.println("🔍 Token presente: " + (sessionManager.getAuthToken() != null ? "SI" : "NO"));
        
        request.setEntity(EntityBuilder.create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        int statusCode = response.getCode();
        String responseBody = convertInputStreamToString(response.getEntity().getContent());
        
        System.out.println("🔍 Respuesta CREAR localidad: " + statusCode + " - " + responseBody);
        
        if (statusCode == 200 || statusCode == 201) {
            return mapper.readValue(responseBody, Localidad.class);
        } else {
            throw new Exception("Error del servidor al crear: " + statusCode + " - " + responseBody);
        }
    }
}
    
public Localidad updateLocalidad(int id, Localidad l) throws Exception {
    verificarSesion();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPut request = new HttpPut(BASE_URL + "/" + id);
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        
        String json = mapper.writeValueAsString(l);
        
        System.out.println("🔍 Actualizando localidad - ID: " + id);
        System.out.println("🔍 JSON enviado: " + json);
        
        request.setEntity(EntityBuilder.create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        int statusCode = response.getCode();
        String responseBody = convertInputStreamToString(response.getEntity().getContent());
        
        System.out.println("🔍 Respuesta actualizar localidad: " + statusCode + " - " + responseBody);
        
        if (statusCode == 200) {
            return mapper.readValue(responseBody, Localidad.class);
        } else {
            throw new Exception("Error del servidor: " + statusCode + " - " + responseBody);
        }
    }
}

public boolean actualizarNombreLocalidad(int idLocalidad, String nuevoNombre) throws Exception {
    verificarSesion();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPut request = new HttpPut(BASE_URL + "/" + idLocalidad + "/nombre");
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        
        // Crear JSON con el nuevo nombre
        String json = String.format("{\"nombre\":\"%s\"}", nuevoNombre);
        
        System.out.println("🔍 Actualizando nombre localidad - ID: " + idLocalidad + ", Nuevo nombre: " + nuevoNombre);
        System.out.println("🔍 JSON enviado: " + json);
        
        request.setEntity(EntityBuilder.create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        int statusCode = response.getCode();
        String responseBody = convertInputStreamToString(response.getEntity().getContent());
        
        System.out.println("🔍 Respuesta actualizar nombre: " + statusCode + " - " + responseBody);
        
        return statusCode == 200;
    }
}    
private void verificarSesion() throws Exception {
    if (!sessionManager.isSessionActiva()) {
        throw new Exception("Debe iniciar sesión primero");
    }
    
    // Verificar que el token no esté expirado
    if (sessionManager.getAuthToken() == null || sessionManager.getAuthToken().isEmpty()) {
        throw new Exception("Token de autenticación inválido o expirado");
    }
    
    // Verificar que la sesión no haya expirado
    if (sessionManager.isTokenExpirado()) {
        throw new Exception("La sesión ha expirado. Por favor, inicie sesión nuevamente.");
    }
}
    public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;
    
    // Getters y setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}

// Clase específica para localidades si la necesitas
public class LocalidadResponse {
    private List<Localidad> data;
    public List<Localidad> getData() { return data; }
    public void setData(List<Localidad> data) { this.data = data; }
}
}