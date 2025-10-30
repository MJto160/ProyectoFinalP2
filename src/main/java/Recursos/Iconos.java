/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Recursos;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Marcos Tomas
 */
public class Iconos {
     public static ImageIcon getSolIcon() {
        // Crear ícono de sol (puedes reemplazar con imagen real)
        return crearIcono(Color.YELLOW, "Sol");
    }
    
    public static ImageIcon getLunaIcon() {
        // Crear ícono de luna (puedes reemplazar con imagen real)
        return crearIcono(Color.LIGHT_GRAY, "Luna");
    }
    
    private static ImageIcon crearIcono(Color color, String texto) {
        // Método temporal - reemplaza con tus imágenes reales
        JLabel label = new JLabel(texto);
        label.setOpaque(true);
        label.setBackground(color);
        label.setPreferredSize(new Dimension(30, 30));
        
        // Convertir JLabel a ImageIcon (simplificado)
        return new ImageIcon(); // Reemplaza con imagen real
    }
}
