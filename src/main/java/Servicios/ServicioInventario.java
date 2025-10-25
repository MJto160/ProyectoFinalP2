package Servicios;

import Guardar.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import Modelos.Inventario;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;

import java.io.InputStream;
import java.util.List;

public class ServicioInventario {
    private static final String BASE_URL = "http://localhost:8081/api/inventario";
    private static final ObjectMapper mapper = new ObjectMapper();
    private final SessionManager sessionManager;

    public ServicioInventario() {
        this.sessionManager = SessionManager.getInstance();
    }
    
    public List<Inventario> getInventario() throws Exception {
        verificarSesion();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            
            if (response.getCode() != 200) {
                throw new Exception("Error en la API: " + response.getCode());
            }
            
            // Primero intentar como array directo
            try {
                return mapper.readValue(is, new TypeReference<List<Inventario>>() {});
            } catch (Exception e) {
                // Si falla, intentar como objeto wrapped
                is = response.getEntity().getContent(); // Resetear el stream
                Modelos.InventarioResponse wrapper = mapper.readValue(is, Modelos.InventarioResponse.class);
                if (wrapper != null && wrapper.getData() != null) {
                    return wrapper.getData();
                } else {
                    throw new Exception("Formato de respuesta no reconocido");
                }
            }
        }
    }
    
    public Inventario createInventario(Inventario i) throws Exception {
        verificarSesion();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            String json = mapper.writeValueAsString(i);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, Inventario.class);
        }
    }
    
    /*public Inventario updateInventario(int id, Inventario i) throws Exception {
        verificarSesion();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + "/" + id);
            request.setHeader("Authorization", "Bearer " + sessionManager.getAuthToken());
            String json = mapper.writeValueAsString(i);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);
            InputStream is = response.getEntity().getContent();
            return mapper.readValue(is, Inventario.class);
        }
    }*/
    
    private void verificarSesion() throws Exception {
        if (!sessionManager.isSessionActiva()) {
            throw new Exception("Debe iniciar sesión primero");
        }
    }
}