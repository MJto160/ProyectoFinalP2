/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import java.util.List;

/**
 *
 * @author josth
 */
public class InventarioResponse {
    private List<Inventario> data;
    private String message;
    private boolean success;
    
    // Getters y Setters
    public List<Inventario> getData() { return data; }
    public void setData(List<Inventario> data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
