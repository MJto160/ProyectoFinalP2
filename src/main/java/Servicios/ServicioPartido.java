package Servicios;

import Guardar.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import Modelos.Partido;
import java.io.BufferedReader;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioPartido {
    private static final String BASE_URL = "http://localhost:8081/api/partidos";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final SessionManager sessionManager;

    public ServicioPartido() {
        this.sessionManager = SessionManager.getInstance();
    }
    
public List<Partido> getPartidos() throws Exception {
    verificarSesion();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpGet request = new HttpGet(BASE_URL);
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        
        int statusCode = response.getCode();
        System.out.println("Partidos - Código de respuesta: " + statusCode);
        
        if (statusCode != 200) {
            throw new Exception("Error en la API: " + statusCode);
        }
        
        // Leer la respuesta una sola vez
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
        
        System.out.println("Respuesta cruda de partidos: " + responseBody);
        
        // Procesar desde el String
        return parsePartidosResponse(responseBody);
    }
}

private List<Partido> parsePartidosResponse(String responseBody) throws Exception {
    try {
        return mapper.readValue(responseBody, new TypeReference<List<Partido>>() {});
    } catch (Exception e) {
        System.err.println("Error parseando respuesta de partidos: " + e.getMessage());
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
public Partido createPartido(Partido p) throws Exception {
    verificarSesion();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        
        // Crear un mapa solo con los campos necesarios, sin el ID
        Map<String, Object> partidoMap = new HashMap<>();
        partidoMap.put("equipo_local", p.getEquipo_local());
        partidoMap.put("equipo_visitante", p.getEquipo_visitante());
        partidoMap.put("fecha_partido", p.getFecha_partido());
        partidoMap.put("estadio", p.getEstadio());
        partidoMap.put("estado", p.getEstado());
        
        String json = mapper.writeValueAsString(partidoMap);
        
        System.out.println("JSON enviado a la API:");
        System.out.println(json);
        
        request.setEntity(EntityBuilder.create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        
        int statusCode = response.getCode();
        System.out.println("Código de respuesta: " + statusCode);
        
        if (statusCode != 200 && statusCode != 201) {
            String errorBody = convertInputStreamToString(response.getEntity().getContent());
            System.err.println("Error del servidor: " + errorBody);
            throw new Exception("Error en la API: " + statusCode + " - " + errorBody);
        }
        
        InputStream is = response.getEntity().getContent();
        Partido partidoCreado = mapper.readValue(is, Partido.class);
        
        System.out.println("Partido creado desde API:");
        System.out.println("ID: " + partidoCreado.getId_partido());
        
        return partidoCreado;
    }
}
/*    
    public Partido updatePartido(int id, Partido p) throws Exception {
        verificarSesion();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/" + id);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            String json = mapper.writeValueAsString(p);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, Partido.class);
        }
    }
*/    
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
    private List<Partido> data;
    public List<Partido> getData() { return data; }
    public void setData(List<Partido> data) { this.data = data; }
}
}