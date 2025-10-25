package Modelos;
import java.util.List;


public class LocalidadResponse {
    private List<Localidad> data;
    private String message;
    private boolean success;
    
    public List<Localidad> getData() { return data; }
    public void setData(List<Localidad> data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}