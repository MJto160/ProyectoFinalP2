package Servicios;

import Guardar.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import Modelos.Venta;
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
import java.util.List;

public class ServicioVenta {
    private static final String BASE_URL = "http://localhost:8081/api/ventas";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final SessionManager sessionManager;

    public ServicioVenta() {
        this.sessionManager = SessionManager.getInstance();
    }
    
    public List<Venta> getVentas() throws Exception {
        verificarSesion();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, new TypeReference<List<Venta>>() {});
        }
    }
    
public Venta createVenta(Venta v) throws Exception {
    verificarSesion();
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
        String json = mapper.writeValueAsString(v);
        
        System.out.println("=== JSON ENVIADO ===");
        System.out.println(json);
        
        request.setEntity(EntityBuilder.create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());
        
        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
        
        // Leer la respuesta como string primero para debug
        String responseBody = convertInputStreamToString(response.getEntity().getContent());
        System.out.println("=== RESPUESTA DEL API ===");
        System.out.println("Código: " + response.getCode());
        System.out.println("Respuesta: " + responseBody);
        
        // Reiniciar el stream para leerlo como objeto
        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                Venta ventaCreada = mapper.readValue(responseBody, Venta.class);
                System.out.println("Venta deserializada - ID: " + ventaCreada.getId_venta());
                return ventaCreada;
            } catch (Exception e) {
                System.err.println("Error deserializando venta: " + e.getMessage());
                // Si no se puede deserializar como Venta, crear una vacía
                Venta ventaVacia = new Venta();
                ventaVacia.setId_venta(0); // Indicar que no se obtuvo ID
                return ventaVacia;
            }
        }
        
        // Si no hay respuesta, retornar venta vacía
        Venta ventaVacia = new Venta();
        ventaVacia.setId_venta(0);
        return ventaVacia;
        
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
/*    
    public Venta updateVenta(int id, Venta v) throws Exception {
        verificarSesion();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/" + id);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            String json = mapper.writeValueAsString(v);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, Venta.class);
        }
    }
*/    
    private void verificarSesion() throws Exception {
        if (!sessionManager.isSessionActiva()) {
            throw new Exception("Debe iniciar sesión primero");
        }
    }
}