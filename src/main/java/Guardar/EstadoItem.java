/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Guardar;

/**
 *
 * @author josth
 */
public class EstadoItem {
    private String texto;
    private boolean valor;

    public EstadoItem(String texto, boolean valor) {
        this.texto = texto;
        this.valor = valor;
    }

    public boolean getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return texto; // lo que se mostrará en el JComboBox
    }
}

