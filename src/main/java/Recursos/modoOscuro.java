package Recursos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Marcos Tomas
 */
public class modoOscuro {
    private static boolean darkMode = false;
    
    public static void toggleDarkMode(Container container) {
        darkMode = !darkMode;
        applyThemeToContainer(container);
    }
    
    private static void applyThemeToContainer(Container container) {
        if (container == null) return;
        
        try {
            // Aplicar tema a todos los componentes recursivamente
            applyThemeToComponent(container);
            
            for (Component comp : container.getComponents()) {
                if (comp instanceof Container) {
                    applyThemeToContainer((Container) comp);
                } else {
                    applyThemeToComponent(comp);
                }
            }
            
            if (container instanceof JFrame) {
                container.revalidate();
                container.repaint();
            }
        } catch (Exception e) {
            System.out.println("Error en applyThemeToContainer: " + e.getMessage());
        }
    }
    
    private static void applyThemeToComponent(Component comp) {
        if (comp == null) return;
        if (!comp.isEnabled()) return; // Saltar componentes deshabilitados
        
        try {
            if (darkMode) {
                // MODO OSCURO
                if (comp.isBackgroundSet()) {
                    comp.setBackground(Color.DARK_GRAY);
                }
                if (comp.isForegroundSet()) {
                    comp.setForeground(Color.WHITE);
                }
                
                if (comp instanceof JLabel) {
                    comp.setForeground(Color.WHITE);
                }
                if (comp instanceof JPanel) {
                    comp.setBackground(new Color(45, 45, 45));
                }
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    if (!btn.isContentAreaFilled()) {
                        // Si el botón tiene contentAreaFilled=false, no cambiar fondo
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(new Color(70, 70, 70));
                        btn.setForeground(Color.WHITE);
                    }
                }
                
            } else {
                // MODO CLARO
                if (comp.isBackgroundSet()) {
                    comp.setBackground(Color.WHITE);
                }
                if (comp.isForegroundSet()) {
                    comp.setForeground(Color.BLACK);
                }
                
                if (comp instanceof JLabel) {
                    comp.setForeground(Color.BLACK);
                }
                if (comp instanceof JPanel) {
                    comp.setBackground(Color.WHITE);
                }
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    if (!btn.isContentAreaFilled()) {
                        // Si el botón tiene contentAreaFilled=false, no cambiar fondo
                        btn.setForeground(Color.BLACK);
                    } else {
                        btn.setBackground(Color.LIGHT_GRAY);
                        btn.setForeground(Color.BLACK);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error aplicando tema a " + comp.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    public static boolean isDarkMode() {
        return darkMode;
    }
}