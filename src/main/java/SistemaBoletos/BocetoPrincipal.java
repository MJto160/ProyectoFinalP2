/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package SistemaBoletos;

import Guardar.SessionManager;
import Modelos.Detalle;
import Modelos.Inventario;
import Modelos.Localidad;
import Modelos.Partido;
import Modelos.Solicitud;
import Modelos.Usuario;
import Modelos.Venta;
import Servicios.ServicioInventario;
import Servicios.ServicioLocalidade;
import Servicios.ServicioPartido;
import Servicios.ServicioUsuario;
import Util.GeneradorFacturaPDF;
import conexion.Conexion;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.List; 
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author EDY
 */
public class BocetoPrincipal extends javax.swing.JFrame {
    
    private javax.swing.table.DefaultTableModel modeloTableSolicitudes;
    private int xMouse,yMouse;
    private javax.swing.JComboBox<String> cbEstadio;
    private javax.swing.JLabel lblEstadio;
    private final java.util.Map<String, String> mapaEstadios = new java.util.HashMap<>();
    private javax.swing.JPanel mainContentPanel;
    private java.awt.CardLayout cardLayout;
    
    
{
    mapaEstadios.put("DOROTEO GUAMUCH", "/images/Cementos Progres.png");
    mapaEstadios.put("MATEO FLORES", "/images/Doroteo guamuch.png");
    mapaEstadios.put("CEMENTOS PROGRESO", "/images/Israel Barrios.png");
    mapaEstadios.put("CARLOS SALAZAR", "/images/Mateo flores.png");
}
private List<Partido> partidosList;
private List<Localidad> localidadesList;
private List<Inventario> inventarioList;
private List<Partido> partidosListDisponibilidad;
private List<Localidad> localidadesListDisponibilidad;
private List<Inventario> inventarioListDisponibilidad;
private List<Partido> partidosListGeneral;
private List<Usuario> usuariosList;
private DefaultTableModel modelUsuarios;
private List<Localidad> listaLocalidades;
private DefaultTableModel modeloLocalidades;
private ServicioInventario servicioInventario = new ServicioInventario();

private java.util.Map<String, Integer> mapaLocalidades = new java.util.HashMap<>();
private ServicioPartido servicioPartidos = new ServicioPartido();
private int idInventarioSeleccionado = -1;
private double totalVenta = 0.0;
private void actualizarImagenEstadio() {
    String seleccionado = (String) cbEstadio.getSelectedItem(); 
    String ruta = mapaEstadios.get(seleccionado.toUpperCase());

    if (ruta != null) {
        java.net.URL url = getClass().getResource(ruta);
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(
                lblEstadio.getWidth(),
                lblEstadio.getHeight(),
                Image.SCALE_SMOOTH
        );
        lblEstadio.setIcon(new ImageIcon(img));
    } else {
        lblEstadio.setIcon(null);
        lblEstadio.setText("Sin imagen");
    }
}


private void mostrarPanelVentas() {
    jLabel101.setText("DISPONIBLES: ");
    jLabel101.setForeground(new Color(204, 204, 204));
    
    if (jComboBox4.getSelectedItem() != null) {
        validarStockDisponible();
    }
}
private void inicializarSistemaVentas() {
    cargarDatosDesdeBD();
    
    agregarListenersVenta();
    configurarListenerSpinner();
    configurarTablaVentas();
    

    jLabel100.setText("0.00");
    userTxt7.setText("0.00");
    jLabel101.setText("0");
    jLabel101.setForeground(new Color(220, 53, 69));
    
    if (jComboBox3.getItemCount() > 0) {
        jComboBox3.setSelectedIndex(0);
    }
    
    SwingUtilities.invokeLater(() -> {
        if (jComboBox4.getItemCount() > 0) {
            actualizarPrecioYStock();
            jSpinnerCantidad.setValue(1);
        }
    });
}
private void limpiarInterfazDuplicados() {
    try {
        System.out.println("=== VERIFICANDO COMPONENTES ===");
      
        if (jLabel101 != null) {
            jLabel101.setText("0");
            System.out.println("jLabel101 configurado");
        }
        
        if (jLabel100 != null) {
            jLabel100.setText("0.00");
            System.out.println("jLabel100 configurado");
        }
        
        System.out.println("=== VERIFICACIÓN COMPLETADA ===");
        
    } catch (Exception e) {
        System.err.println("Error limpiando interfaz: " + e.getMessage());
    }
}
private void configurarTablaVentas() {
    String[] columnNames = {"PARTIDO", "LOCALIDAD", "PRECIO U", "CANTIDAD", "SUBTOTAL"};
    
    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
    };
    jTable1.setModel(model);
    jTable1.getColumnModel().getColumn(0).setPreferredWidth(180);
    jTable1.getColumnModel().getColumn(1).setPreferredWidth(120); 
    jTable1.getColumnModel().getColumn(2).setPreferredWidth(80); 
    jTable1.getColumnModel().getColumn(3).setPreferredWidth(60);  
    jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
    
    jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
}
private void configurarListenerSpinner() {
    jSpinnerCantidad.addChangeListener(new javax.swing.event.ChangeListener() {
        @Override
        public void stateChanged(javax.swing.event.ChangeEvent e) {
            actualizarTotal();
        }
    });
}
private void cargarDatosDesdeBD() {
    try {
        System.out.println("Cargando datos desde la base de datos...");
        Servicios.ServicioPartido servicioPartido = new Servicios.ServicioPartido();
        partidosList = servicioPartido.getPartidos();
        System.out.println("Partidos cargados: " + (partidosList != null ? partidosList.size() : 0));
        cargarPartidosEnComboBox();
        
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        localidadesList = servicioLocalidades.getLocalidades();
        System.out.println("Localidades cargadas: " + (localidadesList != null ? localidadesList.size() : 0));
        cargarLocalidadesEnComboBox();
        
        Servicios.ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        inventarioList = servicioInventario.getInventario();
        System.out.println("Items en inventario: " + (inventarioList != null ? inventarioList.size() : 0));

        if (localidadesList != null && inventarioList != null) {
            for (int i = 0; i < Math.min(3, localidadesList.size()); i++) {
                Localidad loc = localidadesList.get(i);
                int stock = obtenerStockActual(loc.getNombre());
                System.out.println("DEBUG - " + loc.getNombre() + ": stock = " + stock);
            }
        }
        
    } catch (Exception e) {
        System.err.println("Error cargando datos desde BD: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cargando datos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void configurarSpinnerCantidad() {  
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(jSpinnerCantidad, "#");
    jSpinnerCantidad.setEditor(editor);
    
    System.out.println("Spinner configurado - Valor actual: " + jSpinnerCantidad.getValue());
}
private void cargarPartidosEnComboBox() {
    try {
        java.util.List<Partido> listaPartidos = servicioPartidos.getPartidos();
        jComboBox3.removeAllItems();
        
        for (Partido partido : listaPartidos) {
            String partidoStr = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
            jComboBox3.addItem(partidoStr);
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cargar partidos: " + e.getMessage());
    }
}
private Partido obtenerPartidoSeleccionado() {
    try {
        if (jComboBox3.getSelectedItem() == null) {
            return null;
        }
        
        String partidoSeleccionadoStr = jComboBox3.getSelectedItem().toString();
        java.util.List<Partido> todosPartidos = servicioPartidos.getPartidos();
        
        for (Partido partido : todosPartidos) {
            String partidoStr = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
            if (partidoStr.equals(partidoSeleccionadoStr)) {
                return partido;
            }
        }
        return null;
    } catch (Exception e) {
        System.err.println("Error obteniendo partido seleccionado: " + e.getMessage());
        return null;
    }
}
private void cargarLocalidadesEnComboBox() {
   
    jComboBox4.removeAllItems();
    if (localidadesList != null) {
        for (Localidad localidad : localidadesList) {
            jComboBox4.addItem(localidad.getNombre());
        }
    }
    if (jComboBox4.getItemCount() > 0) {
        jComboBox4.setSelectedIndex(0);

        SwingUtilities.invokeLater(() -> {
            actualizarPrecioUnitario();
            validarStockDisponible();

            jSpinnerCantidad.setValue(1);
            System.out.println("Spinner forzado a 1 después de cargar localidades");
        });
    }
}
private void inicializarPanelPartidos() {
    try {
        System.out.println("INICIALIZANDO PANEL PARTIDOS");

        cargarDatosPartidos();

        configurarTablaPartidos();

        cargarPartidosEnTabla();
        
        System.out.println("Panel de partidos inicializado correctamente");
        
    } catch (Exception e) {
        System.err.println("Error inicializando panel partidos: " + e.getMessage());
       
        
        JOptionPane.showMessageDialog(this, 
            "Error cargando partidos. Se mostrarán datos de ejemplo.\n\n" + e.getMessage(), 
            "Advertencia", 
            JOptionPane.WARNING_MESSAGE);
    }
}
    
    public BocetoPrincipal() {
        initComponents();  
       
        btnMostrarPass1.setText("👁 Mostrar");
        passTxt2.setEchoChar('•');
        inicializarSpinnerPorDefecto();
        configurarTodaLaNavegacion();
        
        configurarPlaceholders();
        configurarBotonesMostrarContrasena();
        
       
        configurarNavegacionRestablecerContraseña();
        configurarNavegacionDesdeLobby();
        configurarNavegacionEntrePanelesAdmin();
        configurarNavegacionAReportesDesdeAdmin();
        configurarNavegacionDesdeReportes();
        configurarCerrarSesionEnPanelesAdmin();
        configurarNavegacionAReportesDesdeAdmin();
        
         
         jTable2 = new javax.swing.JTable();
        
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.setIcon(Recursos.Iconos.getSolIcon());
        jButton1.setText("🌙");
        
        jButtonVendedor.setBorderPainted(false);
        jButtonVendedor.setContentAreaFilled(false);
        jButtonVendedor.setFocusPainted(false);
        jButtonVendedor.setIcon(Recursos.Iconos.getSolIcon());
        jButtonVendedor.setText("🌙");
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainPanel = new javax.swing.JPanel();
        INICIO = new javax.swing.JPanel();
        logo3 = new javax.swing.JLabel();
        header4 = new javax.swing.JPanel();
        exitBtn4 = new javax.swing.JPanel();
        favicon3 = new javax.swing.JLabel();
        title2 = new javax.swing.JLabel();
        userLabel5 = new javax.swing.JLabel();
        userTxt4 = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        passLabel2 = new javax.swing.JLabel();
        passTxt2 = new javax.swing.JPasswordField();
        jSeparator8 = new javax.swing.JSeparator();
        loginBtn2 = new javax.swing.JPanel();
        loginBtnTxt2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        userLabel6 = new javax.swing.JLabel();
        loginBtnTxt3 = new javax.swing.JLabel();
        loginBtn3 = new javax.swing.JPanel();
        btnMostrarPass1 = new javax.swing.JButton();
        RESTABLECER = new javax.swing.JPanel();
        logo4 = new javax.swing.JLabel();
        header6 = new javax.swing.JPanel();
        exitBtn6 = new javax.swing.JPanel();
        favicon8 = new javax.swing.JLabel();
        title5 = new javax.swing.JLabel();
        txtUsuarioR = new javax.swing.JTextField();
        jSeparator12 = new javax.swing.JSeparator();
        loginBtn10 = new javax.swing.JPanel();
        loginBtnTxt5 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        userLabel13 = new javax.swing.JLabel();
        jPanel89 = new javax.swing.JPanel();
        loginBtnTxt4 = new javax.swing.JLabel();
        userLabel14 = new javax.swing.JLabel();
        txtUsuarioR1 = new javax.swing.JTextField();
        jSeparator13 = new javax.swing.JSeparator();
        Reportes = new javax.swing.JPanel();
        jPanel65 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        favicon5 = new javax.swing.JLabel();
        jPanel66 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel67 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jPanel68 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jPanel69 = new javax.swing.JPanel();
        jLabel123 = new javax.swing.JLabel();
        jPanel70 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jPanel71 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        favicon109 = new javax.swing.JLabel();
        favicon110 = new javax.swing.JLabel();
        favicon111 = new javax.swing.JLabel();
        favicon112 = new javax.swing.JLabel();
        favicon113 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        LOCALIDADES = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        favicon4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        jPanel44 = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        jPanel45 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel64 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        favicon94 = new javax.swing.JLabel();
        favicon95 = new javax.swing.JLabel();
        favicon96 = new javax.swing.JLabel();
        favicon97 = new javax.swing.JLabel();
        favicon98 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jButtonVendedor = new javax.swing.JButton();
        lblBienvenido = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtNombreaparece = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        PARTIDOS = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        favicon35 = new javax.swing.JLabel();
        jPanel46 = new javax.swing.JPanel();
        jLabel104 = new javax.swing.JLabel();
        jPanel47 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        jPanel48 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel49 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel50 = new javax.swing.JPanel();
        jLabel103 = new javax.swing.JLabel();
        jPanel51 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        favicon99 = new javax.swing.JLabel();
        favicon100 = new javax.swing.JLabel();
        favicon101 = new javax.swing.JLabel();
        favicon102 = new javax.swing.JLabel();
        favicon103 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable9 = new javax.swing.JTable();
        VENTA = new javax.swing.JPanel();
        title1 = new javax.swing.JLabel();
        passLabel1 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        userLabel4 = new javax.swing.JLabel();
        userLabel7 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        userLabel8 = new javax.swing.JLabel();
        favicon1 = new javax.swing.JLabel();
        userTxt5 = new javax.swing.JTextField();
        passLabel3 = new javax.swing.JLabel();
        userTxt6 = new javax.swing.JTextField();
        userTxt7 = new javax.swing.JTextField();
        jSeparator9 = new javax.swing.JSeparator();
        userLabel9 = new javax.swing.JLabel();
        userLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        loginBtn1 = new javax.swing.JPanel();
        loginBtnTxt1 = new javax.swing.JLabel();
        btnCerrarVentaVendedor = new javax.swing.JButton();
        jLabel100 = new javax.swing.JLabel();
        jSpinnerCantidad = new javax.swing.JSpinner();
        jLabel101 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jLabel107 = new javax.swing.JLabel();
        favicon36 = new javax.swing.JLabel();
        jPanel54 = new javax.swing.JPanel();
        jLabel106 = new javax.swing.JLabel();
        jPanel55 = new javax.swing.JPanel();
        jLabel105 = new javax.swing.JLabel();
        jPanel56 = new javax.swing.JPanel();
        jLabel108 = new javax.swing.JLabel();
        jPanel52 = new javax.swing.JPanel();
        jLabel109 = new javax.swing.JLabel();
        jPanel53 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jPanel57 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        favicon104 = new javax.swing.JLabel();
        favicon105 = new javax.swing.JLabel();
        favicon106 = new javax.swing.JLabel();
        favicon107 = new javax.swing.JLabel();
        favicon108 = new javax.swing.JLabel();
        DISPONIBLE2 = new javax.swing.JPanel();
        header3 = new javax.swing.JPanel();
        exitBtn3 = new javax.swing.JPanel();
        favicon = new javax.swing.JLabel();
        loginBtn = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        userLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableDisponibilidad = new javax.swing.JTable();
        loginBtn5 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        loginBtn6 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        jLabel112 = new javax.swing.JLabel();
        favicon37 = new javax.swing.JLabel();
        jPanel60 = new javax.swing.JPanel();
        jLabel111 = new javax.swing.JLabel();
        jPanel61 = new javax.swing.JPanel();
        jLabel110 = new javax.swing.JLabel();
        jPanel62 = new javax.swing.JPanel();
        jLabel113 = new javax.swing.JLabel();
        jPanel63 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jPanel59 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jPanel58 = new javax.swing.JPanel();
        jLabel114 = new javax.swing.JLabel();
        favicon89 = new javax.swing.JLabel();
        favicon93 = new javax.swing.JLabel();
        favicon92 = new javax.swing.JLabel();
        favicon90 = new javax.swing.JLabel();
        favicon91 = new javax.swing.JLabel();
        title = new javax.swing.JLabel();
        ADMINISTRACION = new javax.swing.JPanel();
        LOBBY = new javax.swing.JPanel();
        loginBtn12 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        favicon7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        favicon9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        favicon10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        favicon11 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        favicon114 = new javax.swing.JLabel();
        jPanel72 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jPanel73 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        favicon115 = new javax.swing.JLabel();
        favicon12 = new javax.swing.JLabel();
        favicon14 = new javax.swing.JLabel();
        favicon15 = new javax.swing.JLabel();
        favicon16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        Inventario = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        favicon13 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        favicon17 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        favicon18 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        favicon19 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jPanel74 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jPanel75 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        favicon116 = new javax.swing.JLabel();
        favicon117 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel46 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jComboBox19 = new javax.swing.JComboBox<>();
        jPanel33 = new javax.swing.JPanel();
        jLabel99 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jPanel41 = new javax.swing.JPanel();
        jLabel121 = new javax.swing.JLabel();
        jPanel42 = new javax.swing.JPanel();
        jLabel122 = new javax.swing.JLabel();
        NewPartido = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        favicon20 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        favicon21 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        favicon22 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        favicon23 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jPanel87 = new javax.swing.JPanel();
        jLabel127 = new javax.swing.JLabel();
        jPanel88 = new javax.swing.JPanel();
        jLabel128 = new javax.swing.JLabel();
        favicon118 = new javax.swing.JLabel();
        favicon119 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox<>();
        jLabel75 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jComboBox10 = new javax.swing.JComboBox<>();
        jComboBox11 = new javax.swing.JComboBox<>();
        jLabel77 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        NewLocalidades = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        favicon27 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        favicon28 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        favicon29 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        favicon30 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jPanel85 = new javax.swing.JPanel();
        jLabel125 = new javax.swing.JLabel();
        jPanel86 = new javax.swing.JPanel();
        jLabel126 = new javax.swing.JLabel();
        favicon120 = new javax.swing.JLabel();
        favicon121 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jTextField7 = new javax.swing.JTextField();
        jPanel39 = new javax.swing.JPanel();
        jLabel119 = new javax.swing.JLabel();
        Solicitud = new javax.swing.JPanel();
        jPanel90 = new javax.swing.JPanel();
        jLabel129 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        jPanel91 = new javax.swing.JPanel();
        favicon42 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        jPanel92 = new javax.swing.JPanel();
        favicon43 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jPanel93 = new javax.swing.JPanel();
        favicon44 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        jPanel94 = new javax.swing.JPanel();
        favicon45 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        jPanel95 = new javax.swing.JPanel();
        jLabel135 = new javax.swing.JLabel();
        jPanel96 = new javax.swing.JPanel();
        jLabel136 = new javax.swing.JLabel();
        favicon126 = new javax.swing.JLabel();
        favicon127 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel137 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel138 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        Usuario = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        favicon31 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        favicon32 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        favicon33 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        favicon34 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jPanel78 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        jPanel84 = new javax.swing.JPanel();
        jLabel124 = new javax.swing.JLabel();
        favicon122 = new javax.swing.JLabel();
        favicon123 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel96 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox<>();
        jLabel97 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jLabel116 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel115 = new javax.swing.JLabel();
        jComboBox13 = new javax.swing.JComboBox<>();
        jPanel38 = new javax.swing.JPanel();
        jLabel118 = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        jLabel98 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        jLabel117 = new javax.swing.JLabel();
        jPanel40 = new javax.swing.JPanel();
        jLabel120 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        REPORTEA = new javax.swing.JPanel();
        jPanel79 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jPanel80 = new javax.swing.JPanel();
        favicon38 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jPanel81 = new javax.swing.JPanel();
        favicon39 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jPanel82 = new javax.swing.JPanel();
        favicon40 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jPanel83 = new javax.swing.JPanel();
        favicon41 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jPanel76 = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        jPanel77 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        favicon124 = new javax.swing.JLabel();
        favicon125 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainPanel.setLayout(new java.awt.CardLayout());

        INICIO.setBackground(new java.awt.Color(255, 255, 255));
        INICIO.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/Ticketmaster-Emblem.png"))); // NOI18N
        INICIO.add(logo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 70, 290, 410));

        header4.setBackground(new java.awt.Color(255, 255, 255));

        exitBtn4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout exitBtn4Layout = new javax.swing.GroupLayout(exitBtn4);
        exitBtn4.setLayout(exitBtn4Layout);
        exitBtn4Layout.setHorizontalGroup(
            exitBtn4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        exitBtn4Layout.setVerticalGroup(
            exitBtn4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout header4Layout = new javax.swing.GroupLayout(header4);
        header4.setLayout(header4Layout);
        header4Layout.setHorizontalGroup(
            header4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header4Layout.createSequentialGroup()
                .addComponent(exitBtn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(800, Short.MAX_VALUE))
        );
        header4Layout.setVerticalGroup(
            header4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitBtn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        INICIO.add(header4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, 40));

        favicon3.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N
        INICIO.add(favicon3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        title2.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title2.setText("INICIAR SESIÓN");
        INICIO.add(title2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, -1, -1));

        userLabel5.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        userLabel5.setForeground(new java.awt.Color(51, 102, 255));
        userLabel5.setText("Restablecer Contraseña");
        userLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userLabel5MouseClicked(evt);
            }
        });
        INICIO.add(userLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 460, 250, -1));

        userTxt4.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt4.setForeground(new java.awt.Color(204, 204, 204));
        userTxt4.setText("Ingrese su nombre de usuario");
        userTxt4.setBorder(null);
        INICIO.add(userTxt4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 410, 30));

        jSeparator7.setForeground(new java.awt.Color(0, 0, 0));
        INICIO.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 410, 20));

        passLabel2.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        passLabel2.setText("CONTRASEÑA");
        INICIO.add(passLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 120, -1));

        passTxt2.setForeground(new java.awt.Color(204, 204, 204));
        passTxt2.setText("********");
        passTxt2.setBorder(null);
        INICIO.add(passTxt2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, 410, 30));

        jSeparator8.setForeground(new java.awt.Color(0, 0, 0));
        INICIO.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 350, 410, 20));

        loginBtn2.setBackground(new java.awt.Color(0, 134, 190));

        loginBtnTxt2.setFont(new java.awt.Font("Roboto Condensed", 1, 14)); // NOI18N
        loginBtnTxt2.setForeground(new java.awt.Color(255, 255, 255));
        loginBtnTxt2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginBtnTxt2.setText("ENTRAR");
        loginBtnTxt2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        loginBtnTxt2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginBtnTxt2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout loginBtn2Layout = new javax.swing.GroupLayout(loginBtn2);
        loginBtn2.setLayout(loginBtn2Layout);
        loginBtn2Layout.setHorizontalGroup(
            loginBtn2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginBtnTxt2, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        loginBtn2Layout.setVerticalGroup(
            loginBtn2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginBtnTxt2, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        INICIO.add(loginBtn2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, 130, 40));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N
        INICIO.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 130, 60));

        userLabel6.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel6.setText("USUARIO");
        INICIO.add(userLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 70, -1));

        loginBtnTxt3.setFont(new java.awt.Font("Roboto Condensed", 1, 14)); // NOI18N
        loginBtnTxt3.setForeground(new java.awt.Color(255, 255, 255));
        loginBtnTxt3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginBtnTxt3.setText("ENTRAR");
        loginBtnTxt3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        loginBtnTxt3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtnTxt3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtnTxt3MouseExited(evt);
            }
        });
        INICIO.add(loginBtnTxt3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        loginBtn3.setBackground(new java.awt.Color(0, 134, 190));

        javax.swing.GroupLayout loginBtn3Layout = new javax.swing.GroupLayout(loginBtn3);
        loginBtn3.setLayout(loginBtn3Layout);
        loginBtn3Layout.setHorizontalGroup(
            loginBtn3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        loginBtn3Layout.setVerticalGroup(
            loginBtn3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        INICIO.add(loginBtn3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, 130, 40));

        btnMostrarPass1.setText(".");
        INICIO.add(btnMostrarPass1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 320, 90, 30));

        MainPanel.add(INICIO, "card2");

        RESTABLECER.setBackground(new java.awt.Color(255, 255, 255));
        RESTABLECER.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logo4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/Ticketmaster-Emblem.png"))); // NOI18N
        RESTABLECER.add(logo4, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 0, 290, 500));

        header6.setBackground(new java.awt.Color(255, 255, 255));
        header6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                header6MouseDragged(evt);
            }
        });
        header6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                header6MousePressed(evt);
            }
        });

        exitBtn6.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout exitBtn6Layout = new javax.swing.GroupLayout(exitBtn6);
        exitBtn6.setLayout(exitBtn6Layout);
        exitBtn6Layout.setHorizontalGroup(
            exitBtn6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        exitBtn6Layout.setVerticalGroup(
            exitBtn6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout header6Layout = new javax.swing.GroupLayout(header6);
        header6.setLayout(header6Layout);
        header6Layout.setHorizontalGroup(
            header6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header6Layout.createSequentialGroup()
                .addComponent(exitBtn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 800, Short.MAX_VALUE))
        );
        header6Layout.setVerticalGroup(
            header6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitBtn6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        RESTABLECER.add(header6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, 40));

        favicon8.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N
        RESTABLECER.add(favicon8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        title5.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title5.setText("RESTABLECE TU CONTRASEÑA");
        RESTABLECER.add(title5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, -1, -1));

        txtUsuarioR.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        txtUsuarioR.setForeground(new java.awt.Color(204, 204, 204));
        txtUsuarioR.setText("Ingrese el motivo");
        txtUsuarioR.setBorder(null);
        txtUsuarioR.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUsuarioRFocusGained(evt);
            }
        });
        txtUsuarioR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtUsuarioRMousePressed(evt);
            }
        });
        RESTABLECER.add(txtUsuarioR, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 310, 410, 30));

        jSeparator12.setForeground(new java.awt.Color(0, 0, 0));
        RESTABLECER.add(jSeparator12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 410, 20));

        loginBtn10.setBackground(new java.awt.Color(0, 134, 190));
        loginBtn10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginBtn10MouseClicked(evt);
            }
        });

        loginBtnTxt5.setFont(new java.awt.Font("Roboto Condensed", 1, 14)); // NOI18N
        loginBtnTxt5.setForeground(new java.awt.Color(255, 255, 255));
        loginBtnTxt5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginBtnTxt5.setText("ENVIAR");
        loginBtnTxt5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        loginBtnTxt5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginBtnTxt5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtnTxt5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtnTxt5MouseExited(evt);
            }
        });

        javax.swing.GroupLayout loginBtn10Layout = new javax.swing.GroupLayout(loginBtn10);
        loginBtn10.setLayout(loginBtn10Layout);
        loginBtn10Layout.setHorizontalGroup(
            loginBtn10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtn10Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(loginBtnTxt5, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        loginBtn10Layout.setVerticalGroup(
            loginBtn10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginBtnTxt5, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        RESTABLECER.add(loginBtn10, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 360, 130, 40));

        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N
        RESTABLECER.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 130, 60));

        userLabel13.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel13.setText("Motivo");
        RESTABLECER.add(userLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 70, -1));

        jPanel89.setBackground(new java.awt.Color(0, 134, 190));
        jPanel89.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel89MouseClicked(evt);
            }
        });

        loginBtnTxt4.setFont(new java.awt.Font("Roboto Condensed", 1, 14)); // NOI18N
        loginBtnTxt4.setForeground(new java.awt.Color(255, 255, 255));
        loginBtnTxt4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginBtnTxt4.setText("REGRESAR");
        loginBtnTxt4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        loginBtnTxt4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginBtnTxt4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtnTxt4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtnTxt4MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel89Layout = new javax.swing.GroupLayout(jPanel89);
        jPanel89.setLayout(jPanel89Layout);
        jPanel89Layout.setHorizontalGroup(
            jPanel89Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel89Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginBtnTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel89Layout.setVerticalGroup(
            jPanel89Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel89Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginBtnTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        RESTABLECER.add(jPanel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 360, 110, 40));

        userLabel14.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel14.setText("USUARIO");
        RESTABLECER.add(userLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 70, -1));

        txtUsuarioR1.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        txtUsuarioR1.setForeground(new java.awt.Color(204, 204, 204));
        txtUsuarioR1.setText("Ingrese su nombre de usuario");
        txtUsuarioR1.setBorder(null);
        txtUsuarioR1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUsuarioR1FocusGained(evt);
            }
        });
        txtUsuarioR1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtUsuarioR1MousePressed(evt);
            }
        });
        RESTABLECER.add(txtUsuarioR1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 410, 30));

        jSeparator13.setForeground(new java.awt.Color(0, 0, 0));
        RESTABLECER.add(jSeparator13, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 410, 20));

        MainPanel.add(RESTABLECER, "contraseña");

        Reportes.setBackground(new java.awt.Color(255, 255, 255));

        jPanel65.setBackground(new java.awt.Color(194, 226, 250));
        jPanel65.setForeground(new java.awt.Color(27, 86, 253));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon5.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jPanel66.setBackground(new java.awt.Color(255, 255, 255));

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel23.setText("VENTAS");
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel23MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel66Layout = new javax.swing.GroupLayout(jPanel66);
        jPanel66.setLayout(jPanel66Layout);
        jPanel66Layout.setHorizontalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel23)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel66Layout.setVerticalGroup(
            jPanel66Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel66Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel67.setBackground(new java.awt.Color(255, 255, 255));

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel32.setText("LOBBY");

        javax.swing.GroupLayout jPanel67Layout = new javax.swing.GroupLayout(jPanel67);
        jPanel67.setLayout(jPanel67Layout);
        jPanel67Layout.setHorizontalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel32)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel67Layout.setVerticalGroup(
            jPanel67Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel67Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel68.setBackground(new java.awt.Color(255, 255, 255));

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel63.setText("DISPONIBILIDAD");
        jLabel63.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel63MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel68Layout = new javax.swing.GroupLayout(jPanel68);
        jPanel68.setLayout(jPanel68Layout);
        jPanel68Layout.setHorizontalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 161, Short.MAX_VALUE)
            .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel68Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel63)
                    .addContainerGap(8, Short.MAX_VALUE)))
        );
        jPanel68Layout.setVerticalGroup(
            jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
            .addGroup(jPanel68Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel68Layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jLabel63)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel69.setBackground(new java.awt.Color(255, 255, 255));

        jLabel123.setBackground(new java.awt.Color(255, 255, 255));
        jLabel123.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel123.setText("PARTIDOS");
        jLabel123.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel123MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel69Layout = new javax.swing.GroupLayout(jPanel69);
        jPanel69.setLayout(jPanel69Layout);
        jPanel69Layout.setHorizontalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel69Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel123)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel69Layout.setVerticalGroup(
            jPanel69Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel69Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel123)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel70.setBackground(new java.awt.Color(255, 255, 255));

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel33.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel70Layout = new javax.swing.GroupLayout(jPanel70);
        jPanel70.setLayout(jPanel70Layout);
        jPanel70Layout.setHorizontalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 165, Short.MAX_VALUE)
            .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel70Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel70Layout.setVerticalGroup(
            jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
            .addGroup(jPanel70Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel70Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel33)
                    .addContainerGap(7, Short.MAX_VALUE)))
        );

        jPanel71.setBackground(new java.awt.Color(255, 255, 255));

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel34.setText("DASHBOARD");

        javax.swing.GroupLayout jPanel71Layout = new javax.swing.GroupLayout(jPanel71);
        jPanel71.setLayout(jPanel71Layout);
        jPanel71Layout.setHorizontalGroup(
            jPanel71Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel71Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel71Layout.setVerticalGroup(
            jPanel71Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel71Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel34)
                .addContainerGap())
        );

        favicon109.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon109.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        favicon110.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon110.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/partidos.png"))); // NOI18N

        favicon111.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/venta.png"))); // NOI18N
        favicon111.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon111MouseClicked(evt);
            }
        });

        favicon112.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/disponiblidad.png"))); // NOI18N
        favicon112.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon112MouseClicked(evt);
            }
        });

        favicon113.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon113.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        javax.swing.GroupLayout jPanel65Layout = new javax.swing.GroupLayout(jPanel65);
        jPanel65.setLayout(jPanel65Layout);
        jPanel65Layout.setHorizontalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel65Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
            .addGroup(jPanel65Layout.createSequentialGroup()
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(favicon111, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel65Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(favicon112, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(favicon110, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(favicon109))))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel65Layout.createSequentialGroup()
                        .addComponent(favicon113)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel67, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel69, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel66, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel65Layout.setVerticalGroup(
            jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel65Layout.createSequentialGroup()
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(favicon5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favicon113, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addComponent(jPanel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(favicon112, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel65Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addComponent(favicon111, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(favicon110, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(17, 17, 17)
                        .addComponent(favicon109))
                    .addGroup(jPanel65Layout.createSequentialGroup()
                        .addComponent(jPanel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jPanel69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jPanel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jButton2.setBackground(new java.awt.Color(0, 51, 255));
        jButton2.setText("Generar Reporte");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ReportesLayout = new javax.swing.GroupLayout(Reportes);
        Reportes.setLayout(ReportesLayout);
        ReportesLayout.setHorizontalGroup(
            ReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReportesLayout.createSequentialGroup()
                .addComponent(jPanel65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(195, 195, 195)
                .addComponent(jButton2)
                .addGap(0, 1549, Short.MAX_VALUE))
        );
        ReportesLayout.setVerticalGroup(
            ReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReportesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReportesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(232, 232, 232))
        );

        MainPanel.add(Reportes, "reportes");

        LOCALIDADES.setBackground(new java.awt.Color(255, 255, 255));
        LOCALIDADES.setPreferredSize(new java.awt.Dimension(800, 500));
        LOCALIDADES.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(194, 226, 250));
        jPanel1.setForeground(new java.awt.Color(27, 86, 253));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon4.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel12.setText("VENTAS");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel12)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel43.setBackground(new java.awt.Color(255, 255, 255));

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel31.setText("LOBBY");

        javax.swing.GroupLayout jPanel43Layout = new javax.swing.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel43Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel31)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel43Layout.setVerticalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel43Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel57.setText("DISPONIBILIDAD");
        jLabel57.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel57MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 161, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel57)
                    .addContainerGap(8, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jLabel57)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel44.setBackground(new java.awt.Color(255, 255, 255));

        jLabel102.setBackground(new java.awt.Color(255, 255, 255));
        jLabel102.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel102.setText("PARTIDOS");
        jLabel102.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel102MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel102)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel102)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel45.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel11.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 165, Short.MAX_VALUE)
            .addGroup(jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel45Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
            .addGroup(jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel45Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel11)
                    .addContainerGap(7, Short.MAX_VALUE)))
        );

        jPanel64.setBackground(new java.awt.Color(255, 255, 255));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel22.setText("DASHBOARD");

        javax.swing.GroupLayout jPanel64Layout = new javax.swing.GroupLayout(jPanel64);
        jPanel64.setLayout(jPanel64Layout);
        jPanel64Layout.setHorizontalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel64Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel64Layout.setVerticalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel64Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addContainerGap())
        );

        favicon94.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon94.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        favicon95.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon95.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/partidos.png"))); // NOI18N

        favicon96.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon96.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/venta.png"))); // NOI18N
        favicon96.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon96MouseClicked(evt);
            }
        });

        favicon97.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon97.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/disponiblidad.png"))); // NOI18N
        favicon97.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon97MouseClicked(evt);
            }
        });

        favicon98.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon98.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(favicon96, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(favicon97, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(favicon95, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(favicon94))))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(favicon98)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel43, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel44, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(favicon4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favicon98, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(favicon97, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(favicon96, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(favicon95, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(17, 17, 17)
                        .addComponent(favicon94))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jPanel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jPanel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        LOCALIDADES.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 500));

        jLabel14.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel14.setText("INICIO");
        LOCALIDADES.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel15.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel15.setText("¡Bienvenido a TicketMaster!");
        LOCALIDADES.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 30, 260, 60));

        jButtonVendedor.setText("texto");
        jButtonVendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVendedorActionPerformed(evt);
            }
        });
        LOCALIDADES.add(jButtonVendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 0, 60, 33));

        lblBienvenido.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBienvenido.setText(".");
        LOCALIDADES.add(lblBienvenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 90, 170, -1));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setText("Tu puerta de entrada a los mejores eventos");
        jLabel28.setToolTipText("");
        LOCALIDADES.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 170, 300, 50));

        txtNombreaparece.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        txtNombreaparece.setText("asd");
        LOCALIDADES.add(txtNombreaparece, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 110, 230, 30));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setText("Compra tus boletos de forma rápida, segura y sin complicaciones");
        LOCALIDADES.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 230, -1, -1));

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel30.setText("¡Descubre, elige y disfruta de la experiencia!");
        LOCALIDADES.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 270, -1, -1));

        MainPanel.add(LOCALIDADES, "card4");

        PARTIDOS.setBackground(new java.awt.Color(255, 255, 255));
        PARTIDOS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel16.setText("PARTIDOS");
        PARTIDOS.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 30, -1, -1));

        jPanel34.setBackground(new java.awt.Color(194, 226, 250));
        jPanel34.setForeground(new java.awt.Color(27, 86, 253));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon35.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jPanel46.setBackground(new java.awt.Color(255, 255, 255));

        jLabel104.setBackground(new java.awt.Color(255, 255, 255));
        jLabel104.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel104.setText("DASHBOARD");

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel46Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel104)
                .addGap(37, 37, 37))
        );

        jPanel47.setBackground(new java.awt.Color(255, 255, 255));

        jLabel59.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel59.setText("DISPONIBILIDAD");
        jLabel59.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel59MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel59, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel47Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel59)
                .addGap(34, 34, 34))
        );

        jPanel48.setBackground(new java.awt.Color(255, 255, 255));

        jLabel19.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel19.setText("LOBBY");
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel19MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel48Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel19)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel19)
                .addContainerGap())
        );

        jPanel49.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel18.setText("VENTAS");
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel18MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel18)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel49Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addGap(37, 37, 37))
        );

        jPanel50.setBackground(new java.awt.Color(255, 255, 255));

        jLabel103.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel103.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel50Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jLabel103)
                .addGap(26, 26, 26))
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel50Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel103)
                .addContainerGap())
        );

        jPanel51.setBackground(new java.awt.Color(255, 255, 255));

        jLabel17.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel17.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addContainerGap())
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        favicon99.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon99.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        favicon100.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon100.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/partidos.png"))); // NOI18N

        favicon101.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon101.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/venta.png"))); // NOI18N
        favicon101.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon101MouseClicked(evt);
            }
        });

        favicon102.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon102.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/disponiblidad.png"))); // NOI18N
        favicon102.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon102MouseClicked(evt);
            }
        });

        favicon103.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon103.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel34Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel34Layout.createSequentialGroup()
                                    .addGap(8, 8, 8)
                                    .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(favicon102)
                                        .addComponent(favicon103)))
                                .addComponent(favicon100, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(favicon99, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(favicon101))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(favicon35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(favicon35)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(favicon103, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(favicon102)
                        .addGap(113, 113, 113)
                        .addComponent(favicon101, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(favicon100)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(favicon99))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jPanel47, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26))
        );

        PARTIDOS.add(jPanel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 500));

        jTable9.setBackground(new java.awt.Color(255, 253, 246));
        jTable9.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Equipo Local", "Equipo Visitante", "Fecha de partido", "Estadio", "Estado"
            }
        ));
        jScrollPane9.setViewportView(jTable9);

        PARTIDOS.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 500, 200));

        MainPanel.add(PARTIDOS, "card10");

        VENTA.setBackground(new java.awt.Color(255, 255, 255));
        VENTA.setPreferredSize(new java.awt.Dimension(800, 500));
        VENTA.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        title1.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title1.setText("VENTA DE BOLETOS");
        VENTA.add(title1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 20, -1, -1));

        passLabel1.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        passLabel1.setText("TOTAL");
        VENTA.add(passLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 310, 150, -1));

        jSeparator4.setForeground(new java.awt.Color(0, 0, 0));
        VENTA.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 360, 110, 20));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N
        VENTA.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 30, 130, 60));

        userLabel4.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel4.setText("CANTIDAD");
        VENTA.add(userLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 140, 90, -1));

        userLabel7.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel7.setText("CARRITO");
        VENTA.add(userLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 180, 120, 20));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        VENTA.add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 110, 240, -1));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        VENTA.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 180, 80, -1));

        userLabel8.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel8.setText("LOCALIDAD");
        VENTA.add(userLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, 90, -1));

        favicon1.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N
        VENTA.add(favicon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 40, -1, -1));

        userTxt5.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt5.setForeground(new java.awt.Color(204, 204, 204));
        userTxt5.setText("Q");
        userTxt5.setBorder(null);
        VENTA.add(userTxt5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 330, 20, 30));

        passLabel3.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        passLabel3.setText("PRECIO UNITARIO");
        VENTA.add(passLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 230, 150, -1));

        userTxt6.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt6.setForeground(new java.awt.Color(204, 204, 204));
        userTxt6.setText("Q");
        userTxt6.setBorder(null);
        VENTA.add(userTxt6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, 40, 30));

        userTxt7.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt7.setForeground(new java.awt.Color(204, 204, 204));
        userTxt7.setText("asd");
        userTxt7.setBorder(null);
        VENTA.add(userTxt7, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 260, 90, 30));

        jSeparator9.setForeground(new java.awt.Color(0, 0, 0));
        VENTA.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 300, 110, 20));

        userLabel9.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel9.setText("PARTIDO");
        VENTA.add(userLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 70, 70, 20));

        userLabel10.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel10.setText("DISPONIBLES :");
        VENTA.add(userLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 140, 120, 20));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "LOCALIDAD", "CANTIDAD", "PRECIO U", "SUBTOTAL"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        VENTA.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 230, 410, 220));

        loginBtn1.setBackground(new java.awt.Color(0, 134, 190));

        loginBtnTxt1.setFont(new java.awt.Font("Roboto Condensed", 1, 14)); // NOI18N
        loginBtnTxt1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginBtnTxt1.setText("AÑADIR ");
        loginBtnTxt1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        loginBtnTxt1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginBtnTxt1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtnTxt1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtnTxt1MouseExited(evt);
            }
        });

        javax.swing.GroupLayout loginBtn1Layout = new javax.swing.GroupLayout(loginBtn1);
        loginBtn1.setLayout(loginBtn1Layout);
        loginBtn1Layout.setHorizontalGroup(
            loginBtn1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(loginBtnTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        loginBtn1Layout.setVerticalGroup(
            loginBtn1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(loginBtnTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        VENTA.add(loginBtn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 400, 130, 40));

        btnCerrarVentaVendedor.setBackground(new java.awt.Color(0, 0, 153));
        btnCerrarVentaVendedor.setText("Cerrar Venta");
        btnCerrarVentaVendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarVentaVendedorActionPerformed(evt);
            }
        });
        VENTA.add(btnCerrarVentaVendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 460, -1, -1));

        jLabel100.setText("asd");
        VENTA.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 340, -1, -1));
        VENTA.add(jSpinnerCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 180, -1, -1));

        jLabel101.setText("asd");
        VENTA.add(jLabel101, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 140, 70, -1));

        jPanel35.setBackground(new java.awt.Color(194, 226, 250));
        jPanel35.setForeground(new java.awt.Color(27, 86, 253));

        jLabel107.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon36.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jPanel54.setBackground(new java.awt.Color(255, 255, 255));

        jLabel106.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel106.setText("LOBBY");

        javax.swing.GroupLayout jPanel54Layout = new javax.swing.GroupLayout(jPanel54);
        jPanel54.setLayout(jPanel54Layout);
        jPanel54Layout.setHorizontalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel106)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel54Layout.setVerticalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel54Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel106)
                .addContainerGap())
        );

        jPanel55.setBackground(new java.awt.Color(255, 255, 255));

        jLabel105.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel105.setText("VENTAS");

        javax.swing.GroupLayout jPanel55Layout = new javax.swing.GroupLayout(jPanel55);
        jPanel55.setLayout(jPanel55Layout);
        jPanel55Layout.setHorizontalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel55Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel105)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel55Layout.setVerticalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel105)
                .addGap(17, 17, 17))
        );

        jPanel56.setBackground(new java.awt.Color(255, 255, 255));

        jLabel108.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel108.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel56Layout = new javax.swing.GroupLayout(jPanel56);
        jPanel56.setLayout(jPanel56Layout);
        jPanel56Layout.setHorizontalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel56Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel108)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel56Layout.setVerticalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel56Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel108)
                .addGap(29, 29, 29))
        );

        jPanel52.setBackground(new java.awt.Color(255, 255, 255));

        jLabel109.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel109.setText("DASHBOARD");

        javax.swing.GroupLayout jPanel52Layout = new javax.swing.GroupLayout(jPanel52);
        jPanel52.setLayout(jPanel52Layout);
        jPanel52Layout.setHorizontalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel52Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel109)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel52Layout.setVerticalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel52Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel109)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel53.setBackground(new java.awt.Color(255, 255, 255));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel58.setText("DISPONIBILIDAD");

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel58)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel53Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel58)
                .addGap(30, 30, 30))
        );

        jPanel57.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel21.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel57Layout = new javax.swing.GroupLayout(jPanel57);
        jPanel57.setLayout(jPanel57Layout);
        jPanel57Layout.setHorizontalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel57Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel57Layout.setVerticalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel57Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addContainerGap())
        );

        favicon104.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon104.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        favicon105.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon105.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/partidos.png"))); // NOI18N

        favicon106.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon106.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/venta.png"))); // NOI18N
        favicon106.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon106MouseClicked(evt);
            }
        });

        favicon107.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon107.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/disponiblidad.png"))); // NOI18N
        favicon107.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon107MouseClicked(evt);
            }
        });

        favicon108.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon108.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel35Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(favicon106))
                            .addGroup(jPanel35Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel35Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(favicon108, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(favicon105, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(jPanel35Layout.createSequentialGroup()
                                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(favicon104)
                                            .addComponent(favicon107))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel35Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(favicon36)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel53, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(favicon108, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon107)))
                .addGap(31, 31, 31)
                .addComponent(jPanel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(jPanel56, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(favicon106, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(favicon105)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(favicon104, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37))
        );

        VENTA.add(jPanel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 500));

        MainPanel.add(VENTA, "card5");

        DISPONIBLE2.setBackground(new java.awt.Color(255, 255, 255));
        DISPONIBLE2.setMinimumSize(new java.awt.Dimension(800, 500));
        DISPONIBLE2.setPreferredSize(new java.awt.Dimension(800, 500));
        DISPONIBLE2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        header3.setBackground(new java.awt.Color(255, 255, 255));
        header3.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                header3MouseDragged(evt);
            }
        });
        header3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                header3MousePressed(evt);
            }
        });

        exitBtn3.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout exitBtn3Layout = new javax.swing.GroupLayout(exitBtn3);
        exitBtn3.setLayout(exitBtn3Layout);
        exitBtn3Layout.setHorizontalGroup(
            exitBtn3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        exitBtn3Layout.setVerticalGroup(
            exitBtn3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout header3Layout = new javax.swing.GroupLayout(header3);
        header3.setLayout(header3Layout);
        header3Layout.setHorizontalGroup(
            header3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header3Layout.createSequentialGroup()
                .addComponent(exitBtn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        header3Layout.setVerticalGroup(
            header3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitBtn3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        DISPONIBLE2.add(header3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, -1));

        favicon.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N
        DISPONIBLE2.add(favicon, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, -1, -1));

        loginBtn.setBackground(new java.awt.Color(0, 134, 190));

        jLabel24.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("EXPORTAR");

        javax.swing.GroupLayout loginBtnLayout = new javax.swing.GroupLayout(loginBtn);
        loginBtn.setLayout(loginBtnLayout);
        loginBtnLayout.setHorizontalGroup(
            loginBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtnLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel24)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        loginBtnLayout.setVerticalGroup(
            loginBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtnLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addContainerGap())
        );

        DISPONIBLE2.add(loginBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 440, 110, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N
        DISPONIBLE2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, 130, 60));

        userLabel2.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel2.setText("PARTIDO");
        DISPONIBLE2.add(userLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, 70, -1));

        jComboBox1.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        DISPONIBLE2.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 410, 30));

        jTableDisponibilidad.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "LOCALIDAD", "AFORO TOTAL", "DISPONIBLE", "PRECIO"
            }
        ));
        jScrollPane3.setViewportView(jTableDisponibilidad);

        DISPONIBLE2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 210, 540, 220));

        loginBtn5.setBackground(new java.awt.Color(0, 134, 190));

        jLabel25.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("BUSCAR");

        javax.swing.GroupLayout loginBtn5Layout = new javax.swing.GroupLayout(loginBtn5);
        loginBtn5.setLayout(loginBtn5Layout);
        loginBtn5Layout.setHorizontalGroup(
            loginBtn5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtn5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        loginBtn5Layout.setVerticalGroup(
            loginBtn5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addContainerGap())
        );

        DISPONIBLE2.add(loginBtn5, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 160, 90, 30));

        loginBtn6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setBackground(new java.awt.Color(0, 0, 0));
        jLabel26.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel26.setText("ACTUALIZAR");

        javax.swing.GroupLayout loginBtn6Layout = new javax.swing.GroupLayout(loginBtn6);
        loginBtn6.setLayout(loginBtn6Layout);
        loginBtn6Layout.setHorizontalGroup(
            loginBtn6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtn6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel26)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        loginBtn6Layout.setVerticalGroup(
            loginBtn6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26)
                .addContainerGap())
        );

        DISPONIBLE2.add(loginBtn6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 440, 110, 30));

        jPanel36.setBackground(new java.awt.Color(194, 226, 250));
        jPanel36.setForeground(new java.awt.Color(27, 86, 253));

        jLabel112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon37.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jPanel60.setBackground(new java.awt.Color(255, 255, 255));

        jLabel111.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel111.setText("LOBBY");

        javax.swing.GroupLayout jPanel60Layout = new javax.swing.GroupLayout(jPanel60);
        jPanel60.setLayout(jPanel60Layout);
        jPanel60Layout.setHorizontalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel60Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel111)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel60Layout.setVerticalGroup(
            jPanel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel60Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel111)
                .addContainerGap())
        );

        jPanel61.setBackground(new java.awt.Color(255, 255, 255));

        jLabel110.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel110.setText("VENTAS");

        javax.swing.GroupLayout jPanel61Layout = new javax.swing.GroupLayout(jPanel61);
        jPanel61.setLayout(jPanel61Layout);
        jPanel61Layout.setHorizontalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel61Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel110)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel61Layout.setVerticalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel61Layout.createSequentialGroup()
                .addGap(0, 8, Short.MAX_VALUE)
                .addComponent(jLabel110))
        );

        jPanel62.setBackground(new java.awt.Color(255, 255, 255));

        jLabel113.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel113.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel62Layout = new javax.swing.GroupLayout(jPanel62);
        jPanel62.setLayout(jPanel62Layout);
        jPanel62Layout.setHorizontalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel62Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel113)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel62Layout.setVerticalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel62Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel113)
                .addContainerGap())
        );

        jPanel63.setBackground(new java.awt.Color(255, 255, 255));

        jLabel53.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel53.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel63Layout = new javax.swing.GroupLayout(jPanel63);
        jPanel63.setLayout(jPanel63Layout);
        jPanel63Layout.setHorizontalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel63Layout.setVerticalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel53))
        );

        jPanel59.setBackground(new java.awt.Color(255, 255, 255));

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel56.setText("DISPONIBILIDAD");

        javax.swing.GroupLayout jPanel59Layout = new javax.swing.GroupLayout(jPanel59);
        jPanel59.setLayout(jPanel59Layout);
        jPanel59Layout.setHorizontalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel59Layout.setVerticalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel58.setBackground(new java.awt.Color(255, 255, 255));

        jLabel114.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel114.setText("DASHBOARD");

        javax.swing.GroupLayout jPanel58Layout = new javax.swing.GroupLayout(jPanel58);
        jPanel58.setLayout(jPanel58Layout);
        jPanel58Layout.setHorizontalGroup(
            jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel58Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel114)
                .addGap(19, 19, 19))
        );
        jPanel58Layout.setVerticalGroup(
            jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel58Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel114)
                .addContainerGap())
        );

        favicon89.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon89.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        favicon93.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon93.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/partidos.png"))); // NOI18N

        favicon92.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/venta.png"))); // NOI18N
        favicon92.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon92MouseClicked(evt);
            }
        });

        favicon90.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        favicon91.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon91.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/disponiblidad.png"))); // NOI18N
        favicon91.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                favicon91MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel36Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel36Layout.createSequentialGroup()
                                    .addComponent(favicon89)
                                    .addGap(1, 1, 1))
                                .addComponent(favicon93, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(favicon91, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(favicon92, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(favicon90, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel58, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel59, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel60, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel61, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel63, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel62, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel36Layout.createSequentialGroup()
                        .addComponent(favicon37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75))))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(favicon37)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favicon90, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favicon91, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jPanel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favicon92, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(favicon93, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(jPanel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favicon89))
                .addGap(32, 32, 32))
        );

        DISPONIBLE2.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 500));

        title.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title.setText("DISPONIBILIDAD");
        DISPONIBLE2.add(title, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, -1, -1));

        MainPanel.add(DISPONIBLE2, "card6");

        ADMINISTRACION.setLayout(new java.awt.CardLayout());

        LOBBY.setBackground(new java.awt.Color(255, 255, 255));
        LOBBY.setMinimumSize(new java.awt.Dimension(800, 500));
        LOBBY.setPreferredSize(new java.awt.Dimension(800, 500));
        LOBBY.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        loginBtn12.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout loginBtn12Layout = new javax.swing.GroupLayout(loginBtn12);
        loginBtn12.setLayout(loginBtn12Layout);
        loginBtn12Layout.setHorizontalGroup(
            loginBtn12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        loginBtn12Layout.setVerticalGroup(
            loginBtn12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        LOBBY.add(loginBtn12, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 440, 110, 30));

        jPanel2.setBackground(new java.awt.Color(243, 243, 243));
        jPanel2.setMinimumSize(new java.awt.Dimension(230, 500));
        jPanel2.setPreferredSize(new java.awt.Dimension(230, 500));

        jLabel3.setBackground(new java.awt.Color(153, 204, 255));
        jLabel3.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 204, 255));
        jLabel3.setText("BIENVENIDO ");

        jLabel4.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 153, 255));
        jLabel4.setText("ADMINISTRADOR");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon7.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user_avatar_people_man_person_icon_262044.png"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel5.setText("USUARIOS");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(favicon7))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel5)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(188, 60));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        favicon9.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/building_home_furniture_house_construction_icon_262050.png"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel6.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(17, 17, 17))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon10.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/navigation_pin_location_map_point_icon_262049.png"))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel7.setText("LOCALIDADES");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(favicon10)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18))))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon11.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/finance_chart_diagram_business_report_bar_icon_262056.png"))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel9.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon11)
                .addContainerGap())
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        favicon114.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon114.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        jPanel72.setBackground(new java.awt.Color(255, 255, 255));
        jPanel72.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel72MouseClicked(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel35.setText("REPORTES");

        javax.swing.GroupLayout jPanel72Layout = new javax.swing.GroupLayout(jPanel72);
        jPanel72.setLayout(jPanel72Layout);
        jPanel72Layout.setHorizontalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel72Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel35)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel72Layout.setVerticalGroup(
            jPanel72Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel72Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel35)
                .addContainerGap())
        );

        jPanel73.setBackground(new java.awt.Color(255, 255, 255));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel36.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel73Layout = new javax.swing.GroupLayout(jPanel73);
        jPanel73.setLayout(jPanel73Layout);
        jPanel73Layout.setHorizontalGroup(
            jPanel73Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel73Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel36)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel73Layout.setVerticalGroup(
            jPanel73Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel73Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel36)
                .addContainerGap())
        );

        favicon115.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon115.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(favicon114)
                    .addComponent(favicon115))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel73, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(favicon114, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favicon115))
                .addContainerGap())
        );

        LOBBY.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        favicon12.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/BIENVENIDA 3.png"))); // NOI18N
        LOBBY.add(favicon12, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 40, -1, -1));

        favicon14.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/BIENVENIDA1.png"))); // NOI18N
        LOBBY.add(favicon14, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, -1, -1));

        favicon15.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/BIENVENIDA 4.png"))); // NOI18N
        LOBBY.add(favicon15, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 260, -1, -1));

        favicon16.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/BIENVENIDA 2.png"))); // NOI18N
        LOBBY.add(favicon16, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 260, -1, -1));

        jButton1.setText("texto");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        LOBBY.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 0, 60, 33));

        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setText("Restableces contraseña");
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });
        LOBBY.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 470, -1, -1));

        ADMINISTRACION.add(LOBBY, "card7");

        Inventario.setBackground(new java.awt.Color(255, 255, 255));
        Inventario.setMinimumSize(new java.awt.Dimension(800, 500));
        Inventario.setPreferredSize(new java.awt.Dimension(800, 500));
        Inventario.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(243, 243, 243));
        jPanel9.setMinimumSize(new java.awt.Dimension(230, 500));

        jLabel37.setBackground(new java.awt.Color(153, 204, 255));
        jLabel37.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(102, 204, 255));
        jLabel37.setText("BIENVENIDO ");

        jLabel38.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 153, 255));
        jLabel38.setText("ADMINISTRADOR");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon13.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user_avatar_people_man_person_icon_262044.png"))); // NOI18N

        jLabel39.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel39.setText("USUARIOS");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(favicon13))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel39)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon17.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/building_home_furniture_house_construction_icon_262050.png"))); // NOI18N

        jLabel40.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel40.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel40)
                .addGap(17, 17, 17))
        );

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon18.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/navigation_pin_location_map_point_icon_262049.png"))); // NOI18N

        jLabel41.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel41.setText("LOCALIDADES");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(favicon18)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addGap(18, 18, 18))))
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon19.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/finance_chart_diagram_business_report_bar_icon_262056.png"))); // NOI18N

        jLabel42.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel42.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon19)
                .addContainerGap())
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel42)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel74.setBackground(new java.awt.Color(255, 255, 255));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel47.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel74Layout = new javax.swing.GroupLayout(jPanel74);
        jPanel74.setLayout(jPanel74Layout);
        jPanel74Layout.setHorizontalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel74Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel47)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel74Layout.setVerticalGroup(
            jPanel74Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel74Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel47)
                .addContainerGap())
        );

        jPanel75.setBackground(new java.awt.Color(255, 255, 255));

        jLabel64.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel64.setText("REPORTES");

        javax.swing.GroupLayout jPanel75Layout = new javax.swing.GroupLayout(jPanel75);
        jPanel75.setLayout(jPanel75Layout);
        jPanel75Layout.setHorizontalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel75Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel64)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel75Layout.setVerticalGroup(
            jPanel75Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel75Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel64)
                .addContainerGap())
        );

        favicon116.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon116.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        favicon117.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon117.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38)
                            .addComponent(jLabel37)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(favicon116)
                            .addComponent(favicon117))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel74, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel75, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(favicon116, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon117)))
                .addContainerGap())
        );

        Inventario.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel43.setBackground(new java.awt.Color(204, 204, 204));
        jLabel43.setFont(new java.awt.Font("Roboto Medium", 1, 48)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(204, 204, 204));
        jLabel43.setText("INVENTARIO");
        Inventario.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));

        jLabel44.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel44.setText("PRECIO");
        Inventario.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 200, -1, -1));

        jLabel45.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel45.setText("LOCALIDAD");
        Inventario.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 200, -1, -1));

        Inventario.add(jComboBox6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, 180, -1));

        jLabel46.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel46.setText("PARTIDO");
        Inventario.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, -1));
        Inventario.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 230, 170, -1));

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "PARTIDO", "LOCALIDAD", "TOTAL", "DISPONIBLE"
            }
        ));
        jScrollPane5.setViewportView(jTable5);

        Inventario.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 290, 410, 170));

        Inventario.add(jComboBox19, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 180, -1));

        jPanel33.setBackground(new java.awt.Color(51, 153, 255));
        jPanel33.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel33MouseClicked(evt);
            }
        });

        jLabel99.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel99.setForeground(new java.awt.Color(255, 255, 255));
        jLabel99.setText("GUARDAR");

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jLabel99)
                .addGap(16, 16, 16))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel99)
                .addContainerGap())
        );

        Inventario.add(jPanel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 190, -1, 30));

        jLabel61.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel61.setText("CANTIDAD");
        Inventario.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 130, -1, -1));
        Inventario.add(jTextField11, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, 170, -1));

        jPanel41.setBackground(new java.awt.Color(51, 153, 255));
        jPanel41.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel41MouseClicked(evt);
            }
        });

        jLabel121.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel121.setForeground(new java.awt.Color(255, 255, 255));
        jLabel121.setText("CAMBIAR PRECIO");

        javax.swing.GroupLayout jPanel41Layout = new javax.swing.GroupLayout(jPanel41);
        jPanel41.setLayout(jPanel41Layout);
        jPanel41Layout.setHorizontalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel41Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel121)
                .addGap(16, 16, 16))
        );
        jPanel41Layout.setVerticalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel121)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        Inventario.add(jPanel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(691, 150, 130, 30));

        jPanel42.setBackground(new java.awt.Color(51, 153, 255));
        jPanel42.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel42MouseClicked(evt);
            }
        });

        jLabel122.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel122.setForeground(new java.awt.Color(255, 255, 255));
        jLabel122.setText("ACTUALIZAR");

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel42Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel122)
                .addGap(16, 16, 16))
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel122)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        Inventario.add(jPanel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 230, 100, 30));

        ADMINISTRACION.add(Inventario, "inventario");

        NewPartido.setBackground(new java.awt.Color(255, 255, 255));
        NewPartido.setMinimumSize(new java.awt.Dimension(800, 500));
        NewPartido.setPreferredSize(new java.awt.Dimension(800, 500));
        NewPartido.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBackground(new java.awt.Color(243, 243, 243));
        jPanel15.setMinimumSize(new java.awt.Dimension(230, 500));

        jLabel48.setBackground(new java.awt.Color(153, 204, 255));
        jLabel48.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(102, 204, 255));
        jLabel48.setText("BIENVENIDO ");

        jLabel49.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(0, 153, 255));
        jLabel49.setText("ADMINISTRADOR");

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon20.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user_avatar_people_man_person_icon_262044.png"))); // NOI18N

        jLabel50.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel50.setText("USUARIOS");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel50)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(favicon20))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel50)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon21.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/building_home_furniture_house_construction_icon_262050.png"))); // NOI18N

        jLabel51.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel51.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel51)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon21)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel51)
                .addGap(17, 17, 17))
        );

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon22.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/navigation_pin_location_map_point_icon_262049.png"))); // NOI18N

        jLabel52.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel52.setText("LOCALIDADES");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel52)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addComponent(favicon22)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addGap(18, 18, 18))))
        );

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon23.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/finance_chart_diagram_business_report_bar_icon_262056.png"))); // NOI18N

        jLabel54.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel54.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel54)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon23)
                .addContainerGap())
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel54)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel87.setBackground(new java.awt.Color(255, 255, 255));

        jLabel127.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel127.setText("REPORTES");

        javax.swing.GroupLayout jPanel87Layout = new javax.swing.GroupLayout(jPanel87);
        jPanel87.setLayout(jPanel87Layout);
        jPanel87Layout.setHorizontalGroup(
            jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel87Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel127)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel87Layout.setVerticalGroup(
            jPanel87Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel87Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel127)
                .addContainerGap())
        );

        jPanel88.setBackground(new java.awt.Color(255, 255, 255));

        jLabel128.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel128.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel88Layout = new javax.swing.GroupLayout(jPanel88);
        jPanel88.setLayout(jPanel88Layout);
        jPanel88Layout.setHorizontalGroup(
            jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel88Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel128)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel88Layout.setVerticalGroup(
            jPanel88Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel88Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel128)
                .addContainerGap())
        );

        favicon118.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon118.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        favicon119.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon119.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel48)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel15Layout.createSequentialGroup()
                            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(favicon118)
                                .addComponent(favicon119))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel88, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jPanel87, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jPanel87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(favicon118, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon119)))
                .addContainerGap())
        );

        NewPartido.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel55.setBackground(new java.awt.Color(204, 204, 204));
        jLabel55.setFont(new java.awt.Font("Roboto Medium", 1, 48)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(204, 204, 204));
        jLabel55.setText("PARTIDOS");
        NewPartido.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, -1, -1));

        jLabel73.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel73.setText("EQUIPO VISITANTE");
        NewPartido.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 120, -1, -1));

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DOROTEO GUAMUCH", "CEMENTOS PROGRESO", "MATEO FLORES", "ISRAEL FLORES" }));
        NewPartido.add(jComboBox9, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 200, 180, -1));

        jLabel75.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel75.setText("EQUIPO LOCAL");
        NewPartido.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 120, -1, -1));

        jPanel20.setBackground(new java.awt.Color(51, 153, 255));
        jPanel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel20MouseClicked(evt);
            }
        });

        jLabel76.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(255, 255, 255));
        jLabel76.setText("GUARDAR");
        jLabel76.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel76MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel76)
                .addGap(17, 17, 17))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel76)
                .addContainerGap())
        );

        NewPartido.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 250, -1, 30));

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "LOCAL", "VISITANTE", "FECHA", "ESTADIO"
            }
        ));
        jScrollPane6.setViewportView(jTable6);

        NewPartido.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 310, 530, 170));

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ANTIGUA", "COMUNICACIONES", "COMUNICACIONES", "XELAJU" }));
        NewPartido.add(jComboBox10, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 140, 180, -1));

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ANTIGUA", "COMUNICACIONES", "COMUNICACIONES", "XELAJU" }));
        NewPartido.add(jComboBox11, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 180, -1));

        jLabel77.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel77.setText("FECHA");
        NewPartido.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 180, -1, -1));

        jLabel86.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel86.setText("ESTADIO");
        NewPartido.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 180, -1, -1));
        NewPartido.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 170, -1));

        ADMINISTRACION.add(NewPartido, "partido");

        NewLocalidades.setBackground(new java.awt.Color(255, 255, 255));
        NewLocalidades.setMinimumSize(new java.awt.Dimension(800, 500));
        NewLocalidades.setPreferredSize(new java.awt.Dimension(800, 500));
        NewLocalidades.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel21.setBackground(new java.awt.Color(243, 243, 243));
        jPanel21.setMinimumSize(new java.awt.Dimension(230, 500));

        jLabel78.setBackground(new java.awt.Color(153, 204, 255));
        jLabel78.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel78.setForeground(new java.awt.Color(102, 204, 255));
        jLabel78.setText("BIENVENIDO ");

        jLabel79.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(0, 153, 255));
        jLabel79.setText("ADMINISTRADOR");

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));
        jPanel22.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon27.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user_avatar_people_man_person_icon_262044.png"))); // NOI18N

        jLabel80.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel80.setText("USUARIOS");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel80)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(favicon27))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel80)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon28.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/building_home_furniture_house_construction_icon_262050.png"))); // NOI18N

        jLabel81.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel81.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel81)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel81)
                .addGap(17, 17, 17))
        );

        jPanel24.setBackground(new java.awt.Color(255, 255, 255));
        jPanel24.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon29.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/navigation_pin_location_map_point_icon_262049.png"))); // NOI18N

        jLabel82.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel82.setText("LOCALIDADES");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel82)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                        .addComponent(favicon29)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addGap(18, 18, 18))))
        );

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));
        jPanel25.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon30.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/finance_chart_diagram_business_report_bar_icon_262056.png"))); // NOI18N

        jLabel83.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel83.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel83)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon30)
                .addContainerGap())
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel83)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel85.setBackground(new java.awt.Color(255, 255, 255));

        jLabel125.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel125.setText("REPORTES");

        javax.swing.GroupLayout jPanel85Layout = new javax.swing.GroupLayout(jPanel85);
        jPanel85.setLayout(jPanel85Layout);
        jPanel85Layout.setHorizontalGroup(
            jPanel85Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel85Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel125)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel85Layout.setVerticalGroup(
            jPanel85Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel85Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel125)
                .addContainerGap())
        );

        jPanel86.setBackground(new java.awt.Color(255, 255, 255));

        jLabel126.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel126.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel86Layout = new javax.swing.GroupLayout(jPanel86);
        jPanel86.setLayout(jPanel86Layout);
        jPanel86Layout.setHorizontalGroup(
            jPanel86Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel86Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel126)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel86Layout.setVerticalGroup(
            jPanel86Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel86Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel126)
                .addContainerGap())
        );

        favicon120.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon120.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        favicon121.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon121.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel79)
                            .addComponent(jLabel78)
                            .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel21Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(favicon120)
                            .addComponent(favicon121))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel86, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel85, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel78)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel79)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jPanel85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(favicon120, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon121)))
                .addContainerGap())
        );

        NewLocalidades.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel84.setBackground(new java.awt.Color(204, 204, 204));
        jLabel84.setFont(new java.awt.Font("Roboto Medium", 1, 48)); // NOI18N
        jLabel84.setForeground(new java.awt.Color(204, 204, 204));
        jLabel84.setText("LOCALIDADES");
        NewLocalidades.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));

        jLabel87.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel87.setText("LOCALIDAD");
        NewLocalidades.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, -1));

        jPanel26.setBackground(new java.awt.Color(51, 153, 255));
        jPanel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel26MouseClicked(evt);
            }
        });

        jLabel88.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(255, 255, 255));
        jLabel88.setText("Actualizar");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jLabel88)
                .addGap(16, 16, 16))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel88)
                .addContainerGap())
        );

        NewLocalidades.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 170, -1, 30));

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "TIPO DE LOCALIDAD", "PRECIO", "ESTADO"
            }
        ));
        jScrollPane7.setViewportView(jTable7);

        NewLocalidades.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 290, 400, 170));
        NewLocalidades.add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 120, -1));

        jPanel39.setBackground(new java.awt.Color(51, 153, 255));
        jPanel39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel39MouseClicked(evt);
            }
        });

        jLabel119.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel119.setForeground(new java.awt.Color(255, 255, 255));
        jLabel119.setText("GUARDAR");

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel119)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel119)
                .addContainerGap())
        );

        NewLocalidades.add(jPanel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 220, -1, 30));

        ADMINISTRACION.add(NewLocalidades, "localidades");

        Solicitud.setBackground(new java.awt.Color(255, 255, 255));

        jPanel90.setBackground(new java.awt.Color(243, 243, 243));
        jPanel90.setMinimumSize(new java.awt.Dimension(230, 500));

        jLabel129.setBackground(new java.awt.Color(153, 204, 255));
        jLabel129.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel129.setForeground(new java.awt.Color(102, 204, 255));
        jLabel129.setText("BIENVENIDO ");

        jLabel130.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel130.setForeground(new java.awt.Color(0, 153, 255));
        jLabel130.setText("ADMINISTRADOR");

        jPanel91.setBackground(new java.awt.Color(255, 255, 255));
        jPanel91.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon42.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user_avatar_people_man_person_icon_262044.png"))); // NOI18N

        jLabel131.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel131.setText("USUARIOS");

        javax.swing.GroupLayout jPanel91Layout = new javax.swing.GroupLayout(jPanel91);
        jPanel91.setLayout(jPanel91Layout);
        jPanel91Layout.setHorizontalGroup(
            jPanel91Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel91Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel131)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel91Layout.setVerticalGroup(
            jPanel91Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel91Layout.createSequentialGroup()
                .addGroup(jPanel91Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel91Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(favicon42))
                    .addGroup(jPanel91Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel131)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel92.setBackground(new java.awt.Color(255, 255, 255));
        jPanel92.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon43.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/building_home_furniture_house_construction_icon_262050.png"))); // NOI18N

        jLabel132.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel132.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel92Layout = new javax.swing.GroupLayout(jPanel92);
        jPanel92.setLayout(jPanel92Layout);
        jPanel92Layout.setHorizontalGroup(
            jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel92Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel132)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel92Layout.setVerticalGroup(
            jPanel92Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel92Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon43)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel92Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel132)
                .addGap(17, 17, 17))
        );

        jPanel93.setBackground(new java.awt.Color(255, 255, 255));
        jPanel93.setPreferredSize(new java.awt.Dimension(188, 60));
        jPanel93.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel93MouseClicked(evt);
            }
        });

        favicon44.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/navigation_pin_location_map_point_icon_262049.png"))); // NOI18N

        jLabel133.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel133.setText("LOCALIDADES");

        javax.swing.GroupLayout jPanel93Layout = new javax.swing.GroupLayout(jPanel93);
        jPanel93.setLayout(jPanel93Layout);
        jPanel93Layout.setHorizontalGroup(
            jPanel93Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel93Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon44)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel133)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel93Layout.setVerticalGroup(
            jPanel93Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel93Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel93Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel93Layout.createSequentialGroup()
                        .addComponent(favicon44)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel93Layout.createSequentialGroup()
                        .addComponent(jLabel133)
                        .addGap(18, 18, 18))))
        );

        jPanel94.setBackground(new java.awt.Color(255, 255, 255));
        jPanel94.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon45.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/finance_chart_diagram_business_report_bar_icon_262056.png"))); // NOI18N

        jLabel134.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel134.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel94Layout = new javax.swing.GroupLayout(jPanel94);
        jPanel94.setLayout(jPanel94Layout);
        jPanel94Layout.setHorizontalGroup(
            jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel94Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel134)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel94Layout.setVerticalGroup(
            jPanel94Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel94Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon45)
                .addContainerGap())
            .addGroup(jPanel94Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel134)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel95.setBackground(new java.awt.Color(255, 255, 255));

        jLabel135.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel135.setText("REPORTES");

        javax.swing.GroupLayout jPanel95Layout = new javax.swing.GroupLayout(jPanel95);
        jPanel95.setLayout(jPanel95Layout);
        jPanel95Layout.setHorizontalGroup(
            jPanel95Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel95Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel135)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel95Layout.setVerticalGroup(
            jPanel95Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel95Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel135)
                .addContainerGap())
        );

        jPanel96.setBackground(new java.awt.Color(255, 255, 255));

        jLabel136.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel136.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel96Layout = new javax.swing.GroupLayout(jPanel96);
        jPanel96.setLayout(jPanel96Layout);
        jPanel96Layout.setHorizontalGroup(
            jPanel96Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel96Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel136)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel96Layout.setVerticalGroup(
            jPanel96Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel96Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel136)
                .addContainerGap())
        );

        favicon126.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon126.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        favicon127.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon127.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        javax.swing.GroupLayout jPanel90Layout = new javax.swing.GroupLayout(jPanel90);
        jPanel90.setLayout(jPanel90Layout);
        jPanel90Layout.setHorizontalGroup(
            jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel90Layout.createSequentialGroup()
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel130)
                            .addComponent(jLabel129)
                            .addComponent(jPanel94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel90Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(favicon126)
                            .addComponent(favicon127))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel96, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel95, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel90Layout.setVerticalGroup(
            jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel90Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel129)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel130)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel90Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addComponent(jPanel95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel96, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel90Layout.createSequentialGroup()
                        .addComponent(favicon126, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon127)))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel137.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel137.setText("SOLICUTUDES DE RECUPERACION DE CONTRASEÑA");

        jButton5.setText("Actualizar");

        jLabel138.setForeground(new java.awt.Color(0, 0, 0));
        jLabel138.setText("Nueva Contraseña");

        javax.swing.GroupLayout SolicitudLayout = new javax.swing.GroupLayout(Solicitud);
        Solicitud.setLayout(SolicitudLayout);
        SolicitudLayout.setHorizontalGroup(
            SolicitudLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SolicitudLayout.createSequentialGroup()
                .addComponent(jPanel90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(SolicitudLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SolicitudLayout.createSequentialGroup()
                        .addGap(144, 144, 144)
                        .addComponent(jLabel137))
                    .addGroup(SolicitudLayout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(SolicitudLayout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(jLabel138, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(jButton5)))
                .addContainerGap(1270, Short.MAX_VALUE))
        );
        SolicitudLayout.setVerticalGroup(
            SolicitudLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel90, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SolicitudLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel137, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addGroup(SolicitudLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jLabel138)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ADMINISTRACION.add(Solicitud, "card8");

        Usuario.setBackground(new java.awt.Color(255, 255, 255));
        Usuario.setMinimumSize(new java.awt.Dimension(800, 500));
        Usuario.setPreferredSize(new java.awt.Dimension(800, 500));
        Usuario.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel27.setBackground(new java.awt.Color(243, 243, 243));
        jPanel27.setMinimumSize(new java.awt.Dimension(230, 500));

        jLabel85.setBackground(new java.awt.Color(153, 204, 255));
        jLabel85.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(102, 204, 255));
        jLabel85.setText("BIENVENIDO ");

        jLabel89.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel89.setForeground(new java.awt.Color(0, 153, 255));
        jLabel89.setText("ADMINISTRADOR");

        jPanel28.setBackground(new java.awt.Color(255, 255, 255));
        jPanel28.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon31.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user_avatar_people_man_person_icon_262044.png"))); // NOI18N

        jLabel90.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel90.setText("USUARIOS");

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel90)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(favicon31))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel90)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel29.setBackground(new java.awt.Color(255, 255, 255));
        jPanel29.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon32.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/building_home_furniture_house_construction_icon_262050.png"))); // NOI18N

        jLabel91.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel91.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel91)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon32)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel91)
                .addGap(17, 17, 17))
        );

        jPanel30.setBackground(new java.awt.Color(255, 255, 255));
        jPanel30.setPreferredSize(new java.awt.Dimension(188, 60));
        jPanel30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel30MouseClicked(evt);
            }
        });

        favicon33.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/navigation_pin_location_map_point_icon_262049.png"))); // NOI18N

        jLabel92.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel92.setText("LOCALIDADES");

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel92)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                        .addComponent(favicon33)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel30Layout.createSequentialGroup()
                        .addComponent(jLabel92)
                        .addGap(18, 18, 18))))
        );

        jPanel31.setBackground(new java.awt.Color(255, 255, 255));
        jPanel31.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon34.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/finance_chart_diagram_business_report_bar_icon_262056.png"))); // NOI18N

        jLabel93.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel93.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel93)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon34)
                .addContainerGap())
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel93)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel78.setBackground(new java.awt.Color(255, 255, 255));

        jLabel74.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel74.setText("REPORTES");

        javax.swing.GroupLayout jPanel78Layout = new javax.swing.GroupLayout(jPanel78);
        jPanel78.setLayout(jPanel78Layout);
        jPanel78Layout.setHorizontalGroup(
            jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel78Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel74)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel78Layout.setVerticalGroup(
            jPanel78Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel78Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel74)
                .addContainerGap())
        );

        jPanel84.setBackground(new java.awt.Color(255, 255, 255));

        jLabel124.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel124.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel84Layout = new javax.swing.GroupLayout(jPanel84);
        jPanel84.setLayout(jPanel84Layout);
        jPanel84Layout.setHorizontalGroup(
            jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel84Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel124)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel84Layout.setVerticalGroup(
            jPanel84Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel84Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel124)
                .addContainerGap())
        );

        favicon122.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon122.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        favicon123.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon123.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel89)
                            .addComponent(jLabel85)
                            .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel27Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(favicon122)
                            .addComponent(favicon123))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel84, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel78, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel85)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel89)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jPanel78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(favicon122, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon123)))
                .addContainerGap())
        );

        Usuario.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel94.setBackground(new java.awt.Color(204, 204, 204));
        jLabel94.setFont(new java.awt.Font("Roboto Medium", 1, 48)); // NOI18N
        jLabel94.setForeground(new java.awt.Color(204, 204, 204));
        jLabel94.setText("USUARIO");
        Usuario.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, -1, -1));

        jLabel95.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel95.setText("ROL");
        Usuario.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 130, -1, -1));
        Usuario.add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 210, 170, -1));

        jLabel96.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel96.setText("CONTRASEÑA");
        Usuario.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 190, -1, -1));

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMINISTRADOR", "COMPRADOR", "VENDEDOR" }));
        Usuario.add(jComboBox12, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 150, 180, -1));

        jLabel97.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel97.setText("USUARIO");
        Usuario.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, -1));
        Usuario.add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 150, 170, -1));

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "USUARIO", "CONTRASEÑA", "ROL"
            }
        ));
        jScrollPane8.setViewportView(jTable8);

        Usuario.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 320, 540, 170));

        jLabel116.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel116.setText("NOMBRE");
        Usuario.add(jLabel116, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 190, -1, -1));
        Usuario.add(jTextField10, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 210, 170, -1));

        jLabel115.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel115.setText("ESTADO");
        Usuario.add(jLabel115, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 70, -1, -1));

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMINISTRADOR", "COMPRADOR", "VENDEDOR" }));
        Usuario.add(jComboBox13, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 90, 180, -1));

        jPanel38.setBackground(new java.awt.Color(51, 153, 255));

        jLabel118.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel118.setForeground(new java.awt.Color(255, 255, 255));
        jLabel118.setText("BUSCAR");
        jLabel118.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel118MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel38Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel118)
                .addGap(17, 17, 17))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel118)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        Usuario.add(jPanel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 260, -1, 30));

        jPanel32.setBackground(new java.awt.Color(51, 153, 255));

        jLabel98.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(255, 255, 255));
        jLabel98.setText("CREAR");
        jLabel98.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel98MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel32Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel98)
                .addGap(17, 17, 17))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel98)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        Usuario.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 260, -1, 30));

        jPanel37.setBackground(new java.awt.Color(51, 153, 255));

        jLabel117.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel117.setForeground(new java.awt.Color(255, 255, 255));
        jLabel117.setText("ACTUALIZAR");
        jLabel117.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel117MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel117)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel37Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel117)
                .addContainerGap())
        );

        Usuario.add(jPanel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 260, 100, -1));

        jPanel40.setBackground(new java.awt.Color(51, 153, 255));
        jPanel40.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel40MouseClicked(evt);
            }
        });

        jLabel120.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel120.setForeground(new java.awt.Color(255, 255, 255));
        jLabel120.setText("ESTADO");
        jLabel120.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel120MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel40Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel120)
                .addGap(17, 17, 17))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel120)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        Usuario.add(jPanel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 260, -1, 30));

        jLabel60.setText("ID");
        Usuario.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, -1, -1));
        Usuario.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 120, -1));

        ADMINISTRACION.add(Usuario, "usuario");

        REPORTEA.setBackground(new java.awt.Color(255, 255, 255));

        jPanel79.setBackground(new java.awt.Color(243, 243, 243));
        jPanel79.setForeground(new java.awt.Color(153, 153, 153));
        jPanel79.setMinimumSize(new java.awt.Dimension(230, 500));

        jLabel67.setBackground(new java.awt.Color(153, 204, 255));
        jLabel67.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(102, 204, 255));
        jLabel67.setText("BIENVENIDO ");

        jLabel68.setFont(new java.awt.Font("Roboto Light", 1, 24)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 153, 255));
        jLabel68.setText("ADMINISTRADOR");

        jPanel80.setBackground(new java.awt.Color(255, 255, 255));
        jPanel80.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon38.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user_avatar_people_man_person_icon_262044.png"))); // NOI18N

        jLabel69.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel69.setText("USUARIOS");
        jLabel69.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel69MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel80Layout = new javax.swing.GroupLayout(jPanel80);
        jPanel80.setLayout(jPanel80Layout);
        jPanel80Layout.setHorizontalGroup(
            jPanel80Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel80Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel69)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel80Layout.setVerticalGroup(
            jPanel80Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel80Layout.createSequentialGroup()
                .addGroup(jPanel80Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel80Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(favicon38))
                    .addGroup(jPanel80Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel69)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel81.setBackground(new java.awt.Color(255, 255, 255));
        jPanel81.setPreferredSize(new java.awt.Dimension(188, 60));
        jPanel81.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel81MouseClicked(evt);
            }
        });

        favicon39.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/building_home_furniture_house_construction_icon_262050.png"))); // NOI18N

        jLabel70.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel70.setText("PARTIDOS");

        javax.swing.GroupLayout jPanel81Layout = new javax.swing.GroupLayout(jPanel81);
        jPanel81.setLayout(jPanel81Layout);
        jPanel81Layout.setHorizontalGroup(
            jPanel81Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel81Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel70)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel81Layout.setVerticalGroup(
            jPanel81Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel81Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon39)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel81Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel70)
                .addGap(17, 17, 17))
        );

        jPanel82.setBackground(new java.awt.Color(255, 255, 255));
        jPanel82.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon40.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/navigation_pin_location_map_point_icon_262049.png"))); // NOI18N

        jLabel71.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel71.setText("LOCALIDADES");

        javax.swing.GroupLayout jPanel82Layout = new javax.swing.GroupLayout(jPanel82);
        jPanel82.setLayout(jPanel82Layout);
        jPanel82Layout.setHorizontalGroup(
            jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel82Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel71)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel82Layout.setVerticalGroup(
            jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel82Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel82Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel82Layout.createSequentialGroup()
                        .addComponent(favicon40)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel82Layout.createSequentialGroup()
                        .addComponent(jLabel71)
                        .addGap(18, 18, 18))))
        );

        jPanel83.setBackground(new java.awt.Color(255, 255, 255));
        jPanel83.setPreferredSize(new java.awt.Dimension(188, 60));

        favicon41.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/finance_chart_diagram_business_report_bar_icon_262056.png"))); // NOI18N

        jLabel72.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel72.setText("INVENTARIO");

        javax.swing.GroupLayout jPanel83Layout = new javax.swing.GroupLayout(jPanel83);
        jPanel83.setLayout(jPanel83Layout);
        jPanel83Layout.setHorizontalGroup(
            jPanel83Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel83Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel72)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel83Layout.setVerticalGroup(
            jPanel83Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel83Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon41)
                .addContainerGap())
            .addGroup(jPanel83Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel72)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel76.setBackground(new java.awt.Color(255, 255, 255));

        jLabel65.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel65.setText("REPORTES");

        javax.swing.GroupLayout jPanel76Layout = new javax.swing.GroupLayout(jPanel76);
        jPanel76.setLayout(jPanel76Layout);
        jPanel76Layout.setHorizontalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel76Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel65)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel76Layout.setVerticalGroup(
            jPanel76Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel76Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel65)
                .addContainerGap())
        );

        jPanel77.setBackground(new java.awt.Color(255, 255, 255));

        jLabel66.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel66.setText("CERRAR SESIÓN");

        javax.swing.GroupLayout jPanel77Layout = new javax.swing.GroupLayout(jPanel77);
        jPanel77.setLayout(jPanel77Layout);
        jPanel77Layout.setHorizontalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel77Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel66)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel77Layout.setVerticalGroup(
            jPanel77Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel77Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel66)
                .addContainerGap())
        );

        favicon124.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon124.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/dashboardd.png"))); // NOI18N

        favicon125.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon125.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cerrar sesionnnnnnn.png"))); // NOI18N

        javax.swing.GroupLayout jPanel79Layout = new javax.swing.GroupLayout(jPanel79);
        jPanel79.setLayout(jPanel79Layout);
        jPanel79Layout.setHorizontalGroup(
            jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel79Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel68)
                    .addComponent(jLabel67)
                    .addGroup(jPanel79Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel79Layout.createSequentialGroup()
                        .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(favicon124)
                            .addComponent(favicon125))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel77, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel76, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel79Layout.setVerticalGroup(
            jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel79Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel79Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel79Layout.createSequentialGroup()
                        .addComponent(jPanel76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel79Layout.createSequentialGroup()
                        .addComponent(favicon124, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(favicon125)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jButton3.setBackground(new java.awt.Color(0, 51, 255));
        jButton3.setText("Generar Reporte");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("REPORTE PARTIDOS");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout REPORTEALayout = new javax.swing.GroupLayout(REPORTEA);
        REPORTEA.setLayout(REPORTEALayout);
        REPORTEALayout.setHorizontalGroup(
            REPORTEALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(REPORTEALayout.createSequentialGroup()
                .addComponent(jPanel79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(REPORTEALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(REPORTEALayout.createSequentialGroup()
                        .addGap(234, 234, 234)
                        .addComponent(jButton3))
                    .addGroup(REPORTEALayout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addComponent(jButton4)))
                .addGap(0, 1510, Short.MAX_VALUE))
        );
        REPORTEALayout.setVerticalGroup(
            REPORTEALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, REPORTEALayout.createSequentialGroup()
                .addGap(0, 53, Short.MAX_VALUE)
                .addComponent(jPanel79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, REPORTEALayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(28, 28, 28)
                .addComponent(jButton3)
                .addGap(230, 230, 230))
        );

        ADMINISTRACION.add(REPORTEA, "reportea");

        MainPanel.add(ADMINISTRACION, "card9");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2091, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(MainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(MainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void configurarTodaLaNavegacion() {
    jLabel106.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card4");
        }
    });

    jLabel109.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "reportes"); 
        }
    });

    jLabel108.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card10");
        }
    });

    jLabel58.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card6");
        }
    });

    jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            cerrarSesion();
        }
    });

    jLabel12.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card5");
        }
    });

    jLabel22.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "reportes");
        }
    });

    jLabel102.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card10");
        }
    });

    jLabel57.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card6");
        }
    });

    jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            cerrarSesion();
        }
    });

    jLabel18.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card5");
        }
    });

    jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card4");
        }
    });

    jLabel104.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "reportes");
        }
    });

    jLabel59.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card6");
        }
    });

    jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            cerrarSesion();
        }
    });

    jLabel110.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card5");
        }
    });

    jLabel111.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card4");
        }
    });

    jLabel113.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card10");
        }
    });

    jLabel114.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "reportes");
        }
    });

    jLabel53.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            cerrarSesion();
        }
    });

    jLabel23.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card5");
        }
    });

    jLabel63.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card6");
        }
    });

    jLabel123.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card10");
        }
    });

    jLabel32.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card4"); // LOCALIDADES/DASHBOARD
        }
    });

    jLabel33.addMouseListener(new java.awt.event.MouseAdapter() { 
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            cerrarSesion();
        }
    });
 
}
    private void cerrarSesion() {
    int respuesta = JOptionPane.showConfirmDialog(
        this, 
        "¿Estás seguro de que deseas cerrar sesión?", 
        "Cerrar Sesión", 
        JOptionPane.YES_NO_OPTION
    );
    
    if (respuesta == JOptionPane.YES_OPTION) {
        try {
            ServicioUsuario servicio = new ServicioUsuario();
            servicio.logout();

            datosCargados = false;
            
            limpiarCarrito();
            
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card2"); 
            
            JOptionPane.showMessageDialog(this, "Sesión cerrada correctamente");
            
        } catch (Exception e) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card2");
            JOptionPane.showMessageDialog(this, "Sesión cerrada");
        }
    }
}
    
    private void inicializarSpinnerPorDefecto() {
    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
    jSpinnerCantidad.setModel(spinnerModel);
    jSpinnerCantidad.setValue(1);
    
    System.out.println("Spinner inicializado con valor: " + jSpinnerCantidad.getValue());
}
    private void configurarTablaCarrito() {
    String[] columnNames = {"PARTIDO", "LOCALIDAD", "CANTIDAD", "PRECIO U", "SUBTOTAL"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    jTable1.setModel(model);
    jTable1.getColumnModel().getColumn(0).setPreferredWidth(200);
    jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
    jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
    jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
    jTable1.getColumnModel().getColumn(4).setPreferredWidth(100); 
}
    private void cargarDatosPartidos() {
    try {
        System.out.println("Cargando datos de partidos...");
        
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        partidosListGeneral = servicioPartidos.getPartidos();
        
        System.out.println("Partidos cargados: " + partidosListGeneral.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando datos de partidos: " + e.getMessage());
        throw new RuntimeException("Error cargando datos de partidos: " + e.getMessage(), e);
    }
}
    private void configurarTablaPartidos() {
    try {
        String[] columnNames = {"EQUIPO LOCAL", "EQUIPO VISITANTE", "FECHA DEL PARTIDO", "ESTADO"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 1: return String.class; 
                    case 2: return String.class; 
                    case 3: return String.class; 
                    default: return Object.class;
                }
            }
        };
        
        jTable9.setModel(model);

        configurarFormatoTablaPartidos();
        
        System.out.println("Tabla de partidos configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla partidos: " + e.getMessage());
    }
}
    
    private void mostrarPanelAdministracion() {
    inicializarPanelUsuarios();
    
}
private void configurarTablaUsuarios() {
    try {
        String[] columnNames = {"ID", "NOMBRE USUARIO", "CONTRASEÑA", "NOMBRE COMPLETO", "ROL", "ESTADO"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return String.class;
                    case 4: return String.class;
                    case 5: return String.class;
                    default: return Object.class;
                }
            }
        };

        jTable8.setModel(model);
        
        configurarFormatoTablaUsuarios();
        
        jTable8.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable8.getSelectedRow() != -1) {
                llenarCamposDesdeTabla();
            }
        });
        
        System.out.println("Tabla de usuarios configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla usuarios: " + e.getMessage());
    }
}

private void llenarCamposDesdeTabla() {
    try {
        int selectedRow = jTable8.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) jTable8.getValueAt(selectedRow, 0);
            String usuario = (String) jTable8.getValueAt(selectedRow, 1);
            String contrasena = (String) jTable8.getValueAt(selectedRow, 2);
            String nombreCompleto = (String) jTable8.getValueAt(selectedRow, 3);
            String rol = (String) jTable8.getValueAt(selectedRow, 4);
            String estado = (String) jTable8.getValueAt(selectedRow, 5);
            
            jTextField4.setText(String.valueOf(id));
            jTextField9.setText(usuario);
            jTextField8.setText(contrasena);
            jTextField10.setText(nombreCompleto);
            jComboBox12.setSelectedItem(rol);
            jComboBox13.setSelectedItem(estado.equals("ACTIVO") ? "true" : "false");
            
            System.out.println("Campos llenados con datos del usuario ID: " + id);
        }
    } catch (Exception e) {
        System.err.println("Error llenando campos desde tabla: " + e.getMessage());
    }
}

private void configurarComboBoxesUsuarios() {
    try {

        jComboBox12.removeAllItems();
        jComboBox12.addItem("admin");
        jComboBox12.addItem("vendedor");

        jComboBox13.removeAllItems();
        jComboBox13.addItem("Activo");
        jComboBox13.addItem("Inactivo");

        jComboBox13.putClientProperty("Activo", true);
        jComboBox13.putClientProperty("Inactivo", false);

        System.out.println("ComboBox13 configurado correctamente con valores lógicos.");
    } catch (Exception e) {
        System.err.println("Error configurando combo boxes usuarios: " + e.getMessage());
    }
}

private void cargarUsuariosEnTabla() {
    try {
        Servicios.ServicioUsuario servicioUsuario = new Servicios.ServicioUsuario();
        List<Usuario> usuarios = servicioUsuario.getUsuarios();
        
        DefaultTableModel model = (DefaultTableModel) jTable8.getModel();
        model.setRowCount(0);
        
        for (Usuario usuario : usuarios) {
            model.addRow(new Object[]{
                usuario.getId_usuario(),
                usuario.getNombre_usuario(),
                "********", 
                usuario.getNombre_completo(),
                usuario.getRol(),
                usuario.isEstado() ? "ACTIVO" : "INACTIVO"
            });
        }
        
        System.out.println("Usuarios cargados en tabla: " + usuarios.size());
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error cargando usuarios: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void crearUsuario() {
    try {
        String usuario = jTextField9.getText().trim();
        String contrasena = jTextField8.getText().trim();
        String nombreCompleto = jTextField10.getText().trim();
        String rol = jComboBox12.getSelectedItem().toString();
        String estadoStr = jComboBox13.getSelectedItem().toString();
        
        if (usuario.isEmpty() || contrasena.isEmpty() || nombreCompleto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos obligatorios", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean estado = estadoStr.equals("true");
        
        Servicios.ServicioUsuario servicioUsuario = new Servicios.ServicioUsuario();
        
        boolean exito = servicioUsuario.signup(usuario, contrasena, nombreCompleto, rol);
        
        if (exito) {
            JOptionPane.showMessageDialog(this, 
                "Usuario creado correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            cargarUsuariosEnTabla();
            limpiarCamposUsuario();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al crear el usuario", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error creando usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error creando usuario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void actualizarUsuario() {
    try {
        if (jTextField4.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario de la tabla para actualizar", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(jTextField4.getText().trim());
        String usuario = jTextField9.getText().trim();
        String contrasena = jTextField8.getText().trim();
        String nombreCompleto = jTextField10.getText().trim();
        String rol = jComboBox12.getSelectedItem().toString();
        boolean estado = jComboBox13.getSelectedItem().equals("true");
        
        if (usuario.isEmpty() || nombreCompleto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos obligatorios", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId_usuario(id);
        usuarioActualizado.setNombre_usuario(usuario);
        usuarioActualizado.setNombre_completo(nombreCompleto);
        usuarioActualizado.setRol(rol);
        usuarioActualizado.setEstado(estado);
        
        if (!contrasena.equals("********") && !contrasena.isEmpty()) {
            usuarioActualizado.setContrasena_hash(contrasena);
        }
        
        Servicios.ServicioUsuario servicioUsuario = new Servicios.ServicioUsuario();
        
        Usuario resultado = servicioUsuario.updateUsuario(id, usuarioActualizado);
        
        if (resultado != null) {
            JOptionPane.showMessageDialog(this, 
                "Usuario actualizado correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            cargarUsuariosEnTabla();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar el usuario", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error actualizando usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando usuario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void cambiarEstadoUsuario() {
    try {
        if (jTextField4.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario de la tabla para cambiar estado", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(jTextField4.getText().trim());
        String nombreUsuario = jTextField9.getText().trim();

        int filaSeleccionada = jTable8.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una fila en la tabla.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String estadoTabla = jTable8.getValueAt(filaSeleccionada, 5).toString();
        boolean estadoActual = estadoTabla.equalsIgnoreCase("Activo");

        String seleccionCombo = jComboBox13.getSelectedItem().toString();
        boolean nuevoEstado = seleccionCombo.equals("Activo");

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de cambiar el estado del usuario?\n\n" +
            "Usuario: " + nombreUsuario + "\n" +
            "Estado actual: " + (estadoActual ? "ACTIVO" : "INACTIVO") + "\n" +
            "Nuevo estado: " + (nuevoEstado ? "ACTIVO" : "INACTIVO"),
            "Confirmar cambio de estado",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        Usuario usuarioEstado = new Usuario();
        usuarioEstado.setId_usuario(id);
        usuarioEstado.setNombre_usuario(nombreUsuario);
        usuarioEstado.setEstado(nuevoEstado);

        Servicios.ServicioUsuario servicioUsuario = new Servicios.ServicioUsuario();

        Usuario resultado = servicioUsuario.updateUsuario(id, usuarioEstado);

        if (resultado != null) {
            JOptionPane.showMessageDialog(this, 
                "Estado del usuario actualizado correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            jComboBox13.setSelectedItem(nuevoEstado ? "Activo" : "Inactivo");
            cargarUsuariosEnTabla();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al cambiar el estado del usuario", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        System.err.println("Error cambiando estado de usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cambiando estado: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void buscarUsuarioPorId() {
    try {
        String idTexto = jTextField4.getText().trim();
        
        if (idTexto.isEmpty()) {
            cargarUsuariosEnTabla();
            return;
        }
        
        int id = Integer.parseInt(idTexto);
        
        DefaultTableModel model = (DefaultTableModel) jTable8.getModel();
        boolean encontrado = false;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            int idFila = (int) model.getValueAt(i, 0);
            if (idFila == id) {
                jTable8.setRowSelectionInterval(i, i);
                jTable8.scrollRectToVisible(jTable8.getCellRect(i, 0, true));
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró usuario con ID: " + id, 
                "Usuario No Encontrado", 
                JOptionPane.WARNING_MESSAGE);
        }
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, 
            "Por favor ingresa un ID válido (solo números).", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error buscando usuario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void limpiarCamposUsuario() {
    jTextField4.setText("");
    jTextField9.setText("");
    jTextField8.setText("");
    jTextField10.setText("");
    jComboBox12.setSelectedIndex(0);
    jComboBox13.setSelectedIndex(0);
}

private void inicializarPanelUsuarios() {
    try {
        System.out.println("INICIALIZANDO PANEL DE USUARIOS");
        
        configurarComboBoxesUsuarios();
        cargarPartidosEnTabla();
        
        configurarTablaUsuarios();
        
        cargarUsuariosEnTabla();
        
        System.out.println("Panel de usuarios inicializado correctamente");
        
    } catch (Exception e) {
        System.err.println("Error inicializando panel de usuarios: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error inicializando panel de usuarios: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void configurarFormatoTablaPartidos() {
    try {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < jTable9.getColumnCount(); i++) {
            jTable9.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        jTable9.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof String) {
                    String estado = ((String) value).toLowerCase();
                    switch (estado) {
                        case "activo":
                        case "programado":
                            label.setForeground(new Color(0, 128, 0)); // Verde
                            label.setFont(label.getFont().deriveFont(Font.BOLD));
                            break;
                        case "finalizado":
                        case "completado":
                            label.setForeground(Color.BLUE);
                            break;
                        case "cancelado":
                        case "suspendido":
                            label.setForeground(Color.RED);
                            label.setFont(label.getFont().deriveFont(Font.BOLD));
                            break;
                        case "en juego":
                        case "en curso":
                            label.setForeground(Color.ORANGE);
                            label.setFont(label.getFont().deriveFont(Font.BOLD));
                            break;
                        default:
                            label.setForeground(Color.BLACK);
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });
        
        jTable9.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                if (value instanceof java.util.Date) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    value = sdf.format((java.util.Date) value);
                }
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });
        
        jTable9.getColumnModel().getColumn(0).setPreferredWidth(150);
        jTable9.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable9.getColumnModel().getColumn(2).setPreferredWidth(180);
        jTable9.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        jTable9.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252)); // Color gris muy claro
                    }
                }
                
                return c;
            }
        });
        
        jTable9.setAutoCreateRowSorter(true);
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla partidos: " + e.getMessage());
    }
}
private void cargarPartidosEnTabla() {
    try {
        System.out.println("Cargando partidos en la tabla...");
        
        DefaultTableModel model = (DefaultTableModel) jTable9.getModel();
        model.setRowCount(0);
        
        if (partidosListGeneral != null && !partidosListGeneral.isEmpty()) {
            for (Partido partido : partidosListGeneral) {
                String fechaFormateada = formatearFecha(partido.getFecha_partido());

                String estado = obtenerEstadoPartido(partido);
                
                model.addRow(new Object[]{
                    partido.getEquipo_local(),
                    partido.getEquipo_visitante(),
                    fechaFormateada,
                    estado
                });
            }
            
            System.out.println("Partidos cargados en tabla: " + partidosListGeneral.size());
            
        } else {
            System.out.println("No hay partidos para mostrar");
            model.addRow(new Object[]{"No hay datos", "No hay datos", "No hay datos", "No hay datos"});
        }
        
    } catch (Exception e) {
        System.err.println("Error cargando partidos en tabla: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cargando partidos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private String obtenerEstadoPartido(Partido partido) {
    try {

        if (partido.getEstado() != null && !partido.getEstado().isEmpty()) {
            return partido.getEstado();
        }

        if (partido.getFecha_partido() != null) {
            Date fechaPartido = convertirFecha(partido.getFecha_partido());
            
            if (fechaPartido != null) {
                Date ahora = new Date();
                
                if (fechaPartido.after(ahora)) {
                    return "Programado";
                } else {

                    return "Finalizado";
                }
            }
        }
        
        return "Estado no definido";
        
    } catch (Exception e) {
        System.err.println("Error obteniendo estado del partido: " + e.getMessage());
        return "Error";
    }
}
private void actualizarDatosPartidos() {
    try {
        System.out.println("Actualizando datos de partidos...");
        
        cargarDatosPartidos();
        
        cargarPartidosEnTabla();
        
        JOptionPane.showMessageDialog(this, 
            "Datos de partidos actualizados correctamente", 
            "Actualización Exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
        
    } catch (Exception e) {
        System.err.println("Error actualizando datos de partidos: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando partidos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private Date convertirFecha(Object fecha) {
    try {
        if (fecha == null) {
            return null;
        }
        
        if (fecha instanceof String) {
            String fechaStr = (String) fecha;
            if (fechaStr.contains("T")) {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                return isoFormat.parse(fechaStr);
            } else {
                SimpleDateFormat[] formatos = {
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                    new SimpleDateFormat("yyyy-MM-dd"),
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"),
                    new SimpleDateFormat("dd/MM/yyyy")
                };
                
                for (SimpleDateFormat formato : formatos) {
                    try {
                        return formato.parse(fechaStr);
                    } catch (Exception e) {
                    }
                }
                return null;
            }
        } else if (fecha instanceof java.util.Date) {
            return (Date) fecha;
        } else if (fecha instanceof java.sql.Date) {
            return new Date(((java.sql.Date) fecha).getTime());
        } else if (fecha instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) fecha).getTime());
        }
        
        return null;
        
    } catch (Exception e) {
        System.err.println("Error convirtiendo fecha: " + e.getMessage());
        return null;
    }
}
private String formatearFecha(Object fecha) {
    try {
        if (fecha == null) {
            return "Fecha no definida";
        }
        
        Date fechaDate = convertirFecha(fecha);
        
        if (fechaDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format(fechaDate);
        }

        return fecha.toString();
        
    } catch (Exception e) {
        System.err.println("Error formateando fecha: " + e.getMessage());
        return fecha != null ? fecha.toString() : "Fecha inválida";
    }
}
private void exportarPartidosACSV() {
    try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte de partidos");
        fileChooser.setSelectedFile(new File("reporte_partidos.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            exportarTablaACSV(jTable9, fileToSave);
            
            JOptionPane.showMessageDialog(this, 
                "Reporte de partidos exportado correctamente a: " + fileToSave.getAbsolutePath(), 
                "Exportación Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error exportando partidos: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error exportando partidos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void exportarTablaACSV(JTable tabla, File archivo) {
    try (PrintWriter pw = new PrintWriter(archivo)) {

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            pw.print(tabla.getColumnName(i));
            if (i < tabla.getColumnCount() - 1) {
                pw.print(",");
            }
        }
        pw.println();

        for (int i = 0; i < tabla.getRowCount(); i++) {
            for (int j = 0; j < tabla.getColumnCount(); j++) {
                Object value = tabla.getValueAt(i, j);
                String valueStr = value != null ? value.toString().replace(",", ";") : "";
                pw.print(valueStr);
                if (j < tabla.getColumnCount() - 1) {
                    pw.print(",");
                }
            }
            pw.println();
        }
    } catch (Exception e) {
        throw new RuntimeException("Error exportando a CSV: " + e.getMessage(), e);
    }
}private void cargarDatosParaAdmin() {
    try {
        System.out.println("Cargando datos para administrador...");

        cargarLocalidades();
        
    } catch (Exception e) {
        System.err.println("Error cargando datos para admin: " + e.getMessage());
        throw new RuntimeException("Error cargando datos de administrador: " + e.getMessage(), e);
    }
}
    private void loginBtnTxt2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt2MouseClicked
    try {
        String usuario = userTxt4.getText();
        String contrasena = new String(passTxt2.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos");
            return;
        }

        ServicioUsuario servicio = new ServicioUsuario();

        boolean loginExitoso = servicio.login(usuario, contrasena);
        
        if (loginExitoso) {
            SessionManager session = servicio.getSessionManager();
            String rol = session.getUsuarioRol();
            String nombreCompleto = session.getNombreUsuario();
        
            ServicioUsuario servicioUsuario = new ServicioUsuario();
            String nombre = servicioUsuario.obtenerNombreUsuarioLogueado();
            txtNombreaparece.setText("Vendedor: " + nombre);
            
            switch (rol.toLowerCase()) {
case "admin" -> {
    JOptionPane.showMessageDialog(this, 
        " Login exitoso como ADMINISTRADOR\nBienvenido: " + nombreCompleto);
    CardLayout cl = (CardLayout) MainPanel.getLayout();
    cl.show(MainPanel, "card9"); 
    
    cargarDatosDespuesDeLogin();

    cargarFuncionesAdminBasicas();
    inicializarPanelAdmin();
    inicializarGestionInventario();
    inicializarPanelUsuarios();
    prepararModuloLocalidades();
    inicializarModulosAdministrador();
    adminInicializarMapaLocalidades();
    
    inicializarTablaSolicitudes();
    
    System.out.println("Módulos de administrador inicializados correctamente");
}
                case "vendedor" -> {
                    JOptionPane.showMessageDialog(this, 
                        "Login exitoso como VENDEDOR\nBienvenido: " + nombreCompleto);
                    CardLayout cl = (CardLayout) MainPanel.getLayout();
                    cl.show(MainPanel, "card4");
                    cargarDatosDespuesDeLogin();
                    inicializarSistemaVentas();
                    inicializarPanelDisponibilidad();
                    mostrarPanelVentas();
                    cargarLocalidadesEnTabla();
                    inicializarPanelPartidos();
                    
                }
                default -> {
                    JOptionPane.showMessageDialog(this, "Rol no reconocido: " + rol);
                    servicio.logout();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
        }
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error en login: " + ex.getMessage());
        ex.printStackTrace();
    } finally {
        cleanFields();
    }
    }//GEN-LAST:event_loginBtnTxt2MouseClicked

    private void cleanFields() {
    userTxt4.setText("Ingrese su nombre de usuario");
    userTxt4.setForeground(Color.GRAY);
    
    passTxt2.setText("********");
    passTxt2.setForeground(Color.GRAY);
    passTxt2.setEchoChar((char) 0);
}

private void cargarLocalidades() {
    try {
        ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        
        System.out.println("=== CARGANDO LOCALIDADES CON STOCK ===");
        
        List<Localidad> localidades = servicioLocalidades.getLocalidades();
        List<Inventario> inventarios = servicioInventario.getInventario();
        
        Map<Integer, Integer> stockPorLocalidad = new HashMap<>();
        Map<Integer, Double> precioPromedioPorLocalidad = new HashMap<>();
        Map<Integer, Integer> contadorPreciosPorLocalidad = new HashMap<>();
        
        for (Inventario inventario : inventarios) {
            int idLocalidad = inventario.getId_localidad();
   
            int stockActual = stockPorLocalidad.getOrDefault(idLocalidad, 0);
            stockPorLocalidad.put(idLocalidad, stockActual + inventario.getCantidad_disponible());

            double precioActual = precioPromedioPorLocalidad.getOrDefault(idLocalidad, 0.0);
            int contadorActual = contadorPreciosPorLocalidad.getOrDefault(idLocalidad, 0);
            
            precioPromedioPorLocalidad.put(idLocalidad, precioActual + inventario.getPrecio());
            contadorPreciosPorLocalidad.put(idLocalidad, contadorActual + 1);
        }

        for (Integer idLocalidad : precioPromedioPorLocalidad.keySet()) {
            double sumaPrecios = precioPromedioPorLocalidad.get(idLocalidad);
            int contador = contadorPreciosPorLocalidad.get(idLocalidad);
            precioPromedioPorLocalidad.put(idLocalidad, sumaPrecios / contador);
        }

        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"NOMBRE", "PRECIO", "DISPONIBLES"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 1: return Double.class;
                    case 2: return Integer.class;
                    default: return Object.class;
                }
            }
        };
        
        for (Localidad localidad : localidades) {
            Integer stockDisponible = stockPorLocalidad.getOrDefault(localidad.getId_localidad(), 0);
            Double precioPromedio = precioPromedioPorLocalidad.getOrDefault(localidad.getId_localidad(), 0.0);
            
            model.addRow(new Object[]{
                localidad.getNombre(),
                precioPromedio,
                stockDisponible
            });
        }
        
        System.out.println("Localidades cargadas: " + localidades.size());
        
    } catch (Exception e) {
        System.err.println("Error en cargarLocalidades: " + e.getMessage());
        throw new RuntimeException("Error cargando localidades: " + e.getMessage(), e);
    }
}
private void actualizarOpcionesCantidad(int stockDisponible) {
   
    System.out.println("Stock recibido: " + stockDisponible);
    System.out.println("Valor actual del spinner ANTES de actualizar: " + jSpinnerCantidad.getValue());
    
    if (stockDisponible <= 0) {
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 0, 1);
        jSpinnerCantidad.setModel(model);
        jSpinnerCantidad.setEnabled(false);
        System.out.println("Caso: Sin stock - Spinner en 0");
    } else if (stockDisponible == 1) {
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 1, 1);
        jSpinnerCantidad.setModel(model);
        jSpinnerCantidad.setValue(1);
        jSpinnerCantidad.setEnabled(false);
        System.out.println("Caso: 1 único boleto - Spinner en 1");
    } else {
        int maxCantidad = Math.min(stockDisponible, 10);
        System.out.println("Máxima cantidad permitida: " + maxCantidad);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, maxCantidad, 1);
        jSpinnerCantidad.setModel(spinnerModel);
        jSpinnerCantidad.setValue(1);
        jSpinnerCantidad.setEnabled(true);
        
        System.out.println("Nuevo valor del spinner: " + jSpinnerCantidad.getValue());
        System.out.println("Caso: Stock normal - Spinner en 1");
    }
    jSpinnerCantidad.revalidate();
    jSpinnerCantidad.repaint();
}


private boolean datosCargados = false;
public void cargarDatosDespuesDeLogin() {
    if (datosCargados) {
        System.out.println("Los datos ya fueron cargados anteriormente");
        return;
    }
    
    if (!SessionManager.getInstance().isSessionActiva()) {
        System.out.println("No hay sesión activa, no se pueden cargar datos");
        return;
    }
    
    try {
        System.out.println("Cargando datos después del login...");

        String rol = SessionManager.getInstance().getUsuarioRol();
        
        if ("vendedor".equalsIgnoreCase(rol)) {
            cargarLocalidades();
        } else if ("admin".equalsIgnoreCase(rol)) {
            cargarDatosParaAdmin();
        }
        
        datosCargados = true;
        System.out.println("Datos cargados exitosamente para rol: " + rol);
        
    } catch (Exception e) {
        System.err.println("Error cargando datos después del login: " + e.getMessage());
        datosCargados = false;

        String mensajeError;
        if (e.getMessage().contains("403") || e.getMessage().contains("Forbidden")) {
            mensajeError = "Error de autenticación. La sesión puede haber expirado.\nPor favor, inicie sesión nuevamente.";
        } else if (e.getMessage().contains("Stream already closed")) {
            mensajeError = "Error de conexión con el servidor.\nPor favor, intente nuevamente.";
        } else {
            mensajeError = "Error cargando datos: " + e.getMessage();
        }
        
        JOptionPane.showMessageDialog(this, 
            mensajeError, 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}


private void manejarErrorCarga(Exception e) {
    String mensaje = "Error al cargar localidades:\n";
    
    if (e.getMessage().contains("Debe iniciar sesión primero")) {
        mensaje += "Sesión expirada. Por favor, inicie sesión nuevamente.";

    } else if (e.getMessage().contains("Connection refused")) {
        mensaje += "No se puede conectar al servidor. Verifique que esté ejecutándose.";
    } else {
        mensaje += e.getMessage();
    }
    
    JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    e.printStackTrace();
}


private void validarStockDisponible() {
    if (jComboBox4.getSelectedItem() == null) {
        System.out.println("No hay localidad seleccionada");
        return;
    }
    
    String localidadSeleccionada = jComboBox4.getSelectedItem().toString();
    System.out.println("Validando stock para: " + localidadSeleccionada); 
    
    int stockDisponible = obtenerStockActual(localidadSeleccionada);
    int cantidadSolicitada = (int) jSpinnerCantidad.getValue();
    
    System.out.println("Stock disponible: " + stockDisponible);
    
    jLabel101.setText("DISPONIBLES: " + stockDisponible);

    if (cantidadSolicitada > stockDisponible) {
        System.out.println("Stock insuficiente");
        jLabel101.setForeground(Color.RED);
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.BOLD));

        if (stockDisponible > 0) {
            jSpinnerCantidad.setValue(stockDisponible);
        } else {
            jSpinnerCantidad.setValue(0);
        }
    } else {
        System.out.println("Stock suficiente");
        jLabel101.setForeground(new Color(204, 204, 204));
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));
    }

    actualizarLimitesSpinner(stockDisponible);

    actualizarPrecioTotal();
}
private void actualizarPrecioTotal() {
    try {
        if (jComboBox4.getSelectedItem() != null && jComboBox3.getSelectedItem() != null && userTxt7.getText() != null) {
            String localidad = jComboBox4.getSelectedItem().toString();
            Object partido = jComboBox3.getSelectedItem();
            int cantidad = (int) jSpinnerCantidad.getValue();
            
            double precioUnitario;
            try {
                precioUnitario = Double.parseDouble(userTxt7.getText().trim());
            } catch (NumberFormatException e) {
                precioUnitario = obtenerPrecioLocalidad(localidad, partido);
                userTxt7.setText(String.format("%.2f", precioUnitario));
            }
            
            double precioTotal = precioUnitario * cantidad;

            jLabel100.setText(String.format("%.2f", precioTotal));
        }
    } catch (Exception e) {
        jLabel100.setText("0.00");
        System.err.println("Error actualizando precio total: " + e.getMessage());
    }
}
private void actualizarLimitesSpinner(int stockDisponible) {
    SpinnerNumberModel model = (SpinnerNumberModel) jSpinnerCantidad.getModel();
    
    if (stockDisponible == 0) {
        model.setMinimum(0);
        model.setMaximum(0);
        jSpinnerCantidad.setValue(0);
        jSpinnerCantidad.setEnabled(false);
    } else {
        model.setMinimum(1);
        model.setMaximum(stockDisponible);
        jSpinnerCantidad.setEnabled(true);
        
        int valorActual = (int) jSpinnerCantidad.getValue();
        if (valorActual > stockDisponible) {
            jSpinnerCantidad.setValue(stockDisponible);
        }
    }
}

private int obtenerStockActual(String nombreLocalidad) {
    try {
        System.out.println("Buscando stock para: " + nombreLocalidad);
        
        if (localidadesList == null || inventarioList == null) {
            System.out.println("Listas nulas");
            return 0;
        }
        
        Integer localidadId = null;
        for (Localidad localidad : localidadesList) {
            if (localidad.getNombre().equals(nombreLocalidad)) {
                localidadId = localidad.getId_localidad();
                System.out.println("ID encontrado: " + localidadId);
                break;
            }
        }
        
        if (localidadId == null) {
            System.out.println("ID no encontrado para: " + nombreLocalidad);
            return 0;
        }

        for (Inventario inventario : inventarioList) {
            if (inventario.getId_localidad() == localidadId) {
                System.out.println("Stock encontrado: " + inventario.getCantidad_disponible());
                return inventario.getCantidad_disponible();
            }
        }
        
        System.out.println("Stock no encontrado en inventario");
        return 0;
        
    } catch (Exception e) {
        System.err.println("Error obteniendo stock: " + e.getMessage());
        return 0;
    }
}
private double obtenerPrecioLocalidad(String nombreLocalidad, Object partidoSeleccionado) {
    try {
        int idLocalidad = obtenerIdLocalidad(nombreLocalidad);
        if (idLocalidad == -1) {
            throw new Exception("No se encontró la localidad: " + nombreLocalidad);
        }
        
        int idPartido = obtenerIdPartido(partidoSeleccionado);
        if (idPartido == -1) {
            throw new Exception("No se encontró el partido seleccionado");
        }

        if (inventarioList != null) {
            for (Inventario inventario : inventarioList) {
                if (inventario.getId_localidad() == idLocalidad && 
                    inventario.getId_partido() == idPartido) {
                    return inventario.getPrecio();
                }
            }
        }
        return consultarPrecioDirecto(idLocalidad, idPartido);
        
    } catch (Exception e) {
        System.err.println("Error obteniendo precio: " + e.getMessage());
        throw new RuntimeException("Error obteniendo precio de localidad: " + e.getMessage());
    }
}
private void actualizarPrecioYStock() {
    try {
        if (jComboBox3.getSelectedItem() != null && jComboBox4.getSelectedItem() != null) {
            String localidad = (String) jComboBox4.getSelectedItem();
            Object partidoSeleccionado = jComboBox3.getSelectedItem();

            double precio = obtenerPrecioLocalidad(localidad, partidoSeleccionado);
            userTxt7.setText(String.format("%.2f", precio));

            int stock = obtenerStockDisponible(localidad, partidoSeleccionado);
            actualizarColorStock(stock);
            
            actualizarSpinnerStock(stock);
            actualizarTotal();
        }
    } catch (Exception e) {
        System.err.println("Error actualizando precio unitario: " + e.getMessage());
        userTxt7.setText("0.00");
        jLabel101.setText("0");
        jLabel101.setForeground(Color.RED);
        actualizarSpinnerStock(0);
    }
}
private void actualizarSpinnerStock(int stockDisponible) {
    try {
        System.out.println("Stock recibido: " + stockDisponible);
        System.out.println("Valor actual del spinner ANTES de actualizar: " + jSpinnerCantidad.getValue());
        
        if (stockDisponible <= 0) {
            jSpinnerCantidad.setModel(new SpinnerNumberModel(0, 0, 0, 1));
            System.out.println("Caso: Sin stock - Spinner en 0");
        } else {
            jSpinnerCantidad.setModel(new SpinnerNumberModel(1, 1, stockDisponible, 1));
            System.out.println("Caso: Con stock - Spinner en 1, máximo: " + stockDisponible);
        }
        
        actualizarTotal();
        
    } catch (Exception e) {
        System.err.println("Error actualizando spinner: " + e.getMessage());
        jSpinnerCantidad.setModel(new SpinnerNumberModel(0, 0, 0, 1));
    }
}
private void actualizarTotal() {
    try {
        int cantidad = (int) jSpinnerCantidad.getValue();

        String precioTexto = userTxt7.getText().trim();
        double precioUnitario = 0.0;
        
        if (!precioTexto.isEmpty() && !precioTexto.equals("0.00")) {
            precioUnitario = Double.parseDouble(precioTexto);
        }

        double total = cantidad * precioUnitario;

        jLabel100.setText(String.format("%.2f", total));
        
    } catch (NumberFormatException e) {
        System.err.println("Error en formato de precio: " + e.getMessage());
        jLabel100.setText("0.00");
    } catch (Exception e) {
        System.err.println("Error actualizando total: " + e.getMessage());
        jLabel100.setText("0.00");
    }
}
private double consultarPrecioDirecto(int idLocalidad, int idPartido) {
    try {
        ServicioInventario servicio = new ServicioInventario();
        List<Inventario> inventarioActual = servicio.getInventario();
        
        for (Inventario inv : inventarioActual) {
            if (inv.getId_localidad() == idLocalidad && inv.getId_partido() == idPartido) {
                return inv.getPrecio();
            }
        }
        return 0.0;
    } catch (Exception e) {
        System.err.println("Error consultando precio directo: " + e.getMessage());
        return 0.0;
    }
}
private void añadirFilaAlCarrito(String partido, String localidad, int cantidad, double precioUnitario, double subtotal) {
    try {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        String partidoLimpio = limpiarTextoPartido(partido);

        model.addRow(new Object[]{
            partidoLimpio,                   
            localidad,                        
            String.format("%.2f", precioUnitario), 
            cantidad,                         
            String.format("%.2f", subtotal)   
        });
        
        jTable1.revalidate();
        jTable1.repaint();
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error al añadir fila: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
private void limpiarTablaVentas() {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0); 
    configurarTablaVentas();
}
private void actualizarTotales() {
    userTxt5.setText(String.format("%.2f", totalVenta));
}
private void limpiarCamposVenta() {

    jComboBox4.setSelectedIndex(0);

    jSpinnerCantidad.setValue(1);

    jLabel100.setText("0.00");

    jLabel101.setText("DISPONIBLES: ");
    jLabel101.setForeground(new Color(204, 204, 204));
    jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));

    jComboBox4.requestFocus();
}

private void inicializarPanelDisponibilidad() {
    try {
        System.out.println("=== INICIALIZANDO PANEL DISPONIBILIDAD ===");

        cargarDatosParaDisponibilidad();
  
        configurarComboBoxPartidos();

        configurarTablaDisponibilidad();

        agregarListenersDisponibilidad();

        actualizarTablaDisponibilidad();
        
        System.out.println("Panel de disponibilidad inicializado correctamente");
        
    } catch (Exception e) {
        System.err.println("Error inicializando panel disponibilidad: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error inicializando panel de disponibilidad: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void cargarDatosParaDisponibilidad() {
    try {
        System.out.println("Cargando datos para disponibilidad...");

        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        partidosListDisponibilidad = servicioPartidos.getPartidos();
        partidosList = servicioPartidos.getPartidos();

        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        localidadesListDisponibilidad = servicioLocalidades.getLocalidades();

        ServicioInventario servicioInventario = new ServicioInventario();
        inventarioListDisponibilidad = servicioInventario.getInventario();
        
        System.out.println("Partidos cargados: " + partidosList.size());
        System.out.println("Localidades cargadas: " + localidadesListDisponibilidad.size());
        System.out.println("Inventario cargado: " + inventarioListDisponibilidad.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando datos para disponibilidad: " + e.getMessage());
        throw new RuntimeException("Error cargando datos de disponibilidad: " + e.getMessage(), e);
    }
}
private void cargarDatosParaVenta() {
    try {
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        partidosList = servicioPartidos.getPartidos();

        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        localidadesList = servicioLocalidades.getLocalidades();

        jComboBox3.removeAllItems();
        if (partidosList != null && !partidosList.isEmpty()) {
            for (Partido partido : partidosList) {
                String display = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
                jComboBox3.addItem(display);
            }
        } else {
            jComboBox3.addItem("No hay partidos disponibles");
        }
        
    } catch (Exception e) {
        System.err.println("Error cargando datos para venta: " + e.getMessage());
    }
}
private void configurarComboBoxPartidos() {
    try {
        jComboBox1.removeAllItems();
        
        if (partidosListDisponibilidad != null && !partidosListDisponibilidad.isEmpty()) {
            jComboBox1.addItem("Todos los partidos");

            for (Partido partido : partidosListDisponibilidad) {
                String descripcionPartido = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
                jComboBox1.addItem(descripcionPartido);
            }
        } else {
            jComboBox1.addItem("No hay partidos disponibles");
        }
        
        System.out.println("ComboBox de partidos configurado con " + partidosListDisponibilidad.size() + " partidos");
        
    } catch (Exception e) {
        System.err.println("Error configurando combo box partidos: " + e.getMessage());
    }
}

private void configurarFormatoTablaDisponibilidad() {
    try {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < jTableDisponibilidad.getColumnCount(); i++) {
            jTableDisponibilidad.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        jTableDisponibilidad.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                if (value instanceof Double) {
                    value = String.format("Q%,.2f", (Double) value);
                }
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.RIGHT);
                return label;
            }
        });

        jTableDisponibilidad.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

                if (value instanceof Integer) {
                    int disponibles = (Integer) value;
                    if (disponibles == 0) {
                        label.setForeground(Color.RED);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                        label.setText("AGOTADO");
                    } else if (disponibles < 10) {
                        label.setForeground(Color.RED);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    } else if (disponibles < 50) {
                        label.setForeground(Color.ORANGE);
                    } else {
                        label.setForeground(new Color(0, 128, 0));
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        jTableDisponibilidad.getColumnModel().getColumn(0).setPreferredWidth(200);
        jTableDisponibilidad.getColumnModel().getColumn(1).setPreferredWidth(120);
        jTableDisponibilidad.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTableDisponibilidad.getColumnModel().getColumn(3).setPreferredWidth(100);
  
        jTableDisponibilidad.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() { 
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                }
                
                return c;
            }
        });
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla disponibilidad: " + e.getMessage());
    }
}

private void agregarListenersDisponibilidad() {
    jComboBox1.addActionListener(e -> actualizarTablaDisponibilidad());
}
private void actualizarTablaDisponibilidad() {
    try {
        System.out.println("Actualizando tabla de disponibilidad...");
        
        Object partidoSeleccionado = jComboBox1.getSelectedItem();
        
        if (partidoSeleccionado == null || "No hay partidos disponibles".equals(partidoSeleccionado.toString())) {
            limpiarTablaDisponibilidad();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTableDisponibilidad.getModel();
        model.setRowCount(0);

        boolean mostrarTodos = "Todos los partidos".equals(partidoSeleccionado.toString());
        
        if (mostrarTodos) {
            cargarDisponibilidadConsolidada();
        } else {
            cargarDisponibilidadPorPartido(partidoSeleccionado.toString());
        }
        
        System.out.println("Tabla de disponibilidad actualizada. Filas: " + model.getRowCount());
        
    } catch (Exception e) {
        System.err.println("Error actualizando tabla disponibilidad: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando disponibilidad: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void cargarDisponibilidadPorPartido(String descripcionPartido) {
    try {
        DefaultTableModel model = (DefaultTableModel) jTableDisponibilidad.getModel();

        Partido partidoSeleccionado = buscarPartidoPorDescripcion(descripcionPartido);
        
        if (partidoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró el partido seleccionado", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Localidad localidad : localidadesListDisponibilidad) {
            int idPartido = partidoSeleccionado.getId_partido();
            int idLocalidad = localidad.getId_localidad();
            
            int disponible = obtenerDisponibilidadPorPartidoYLocalidad(idPartido, idLocalidad);

            double precio = obtenerPrecioPorPartidoYLocalidad(idPartido, idLocalidad);
            
            int aforoTotal = obtenerAforoTotalPorPartidoYLocalidad(idPartido, idLocalidad);
            
            model.addRow(new Object[]{
                localidad.getNombre(),
                aforoTotal,
                disponible,
                precio
            });
        }
        
    } catch (Exception e) {
        System.err.println("Error cargando disponibilidad por partido: " + e.getMessage());
        throw e;
    }
}
private double calcularPrecioPromedioPorLocalidad(int idLocalidad) {
    double sumaPrecios = 0.0;
    int contador = 0;
    for (Inventario inventario : inventarioListDisponibilidad) {
        if (inventario.getId_localidad() == idLocalidad) {
            sumaPrecios += inventario.getPrecio();
            contador++;
        }
    }
    return contador > 0 ? sumaPrecios / contador : 0.0;
}
private double obtenerPrecioPorPartidoYLocalidad(int idPartido, int idLocalidad) {
    for (Inventario inventario : inventarioListDisponibilidad) {
        if (inventario.getId_partido() == idPartido && inventario.getId_localidad() == idLocalidad) {
            return inventario.getPrecio();
        }
    }
    return 0.0;
}
private void cargarDisponibilidadConsolidada() {
    try {
        DefaultTableModel model = (DefaultTableModel) jTableDisponibilidad.getModel();

        for (Localidad localidad : localidadesListDisponibilidad) {
            int totalDisponible = calcularTotalDisponiblePorLocalidad(localidad.getId_localidad());

            double precioPromedio = calcularPrecioPromedioPorLocalidad(localidad.getId_localidad());
            
            int aforoTotal = calcularAforoTotalPorLocalidad(localidad.getId_localidad());
            
            model.addRow(new Object[]{
                localidad.getNombre(),
                aforoTotal,
                totalDisponible,
                precioPromedio
            });
        }
        
    } catch (Exception e) {
        System.err.println("Error cargando disponibilidad consolidada: " + e.getMessage());
        throw e;
    }
}

private int calcularTotalDisponiblePorLocalidad(int idLocalidad) {
    int total = 0;
    for (Inventario inventario : inventarioListDisponibilidad) {
        if (inventario.getId_localidad() == idLocalidad) {
            total += inventario.getCantidad_disponible();
        }
    }
    return total;
}

private int calcularAforoTotalPorLocalidad(int idLocalidad) {
    int total = 0;
    for (Inventario inventario : inventarioListDisponibilidad) {
        if (inventario.getId_localidad() == idLocalidad) {
            total += inventario.getCantidad_disponible();
        }
    }
    return total;
}

private Partido buscarPartidoPorDescripcion(String descripcion) {
    for (Partido partido : partidosListDisponibilidad) {
        String descPartido = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
        if (descPartido.equals(descripcion)) {
            return partido;
        }
    }
    return null;
}

private int obtenerDisponibilidadPorPartidoYLocalidad(int idPartido, int idLocalidad) {
    for (Inventario inventario : inventarioListDisponibilidad) {
        if (inventario.getId_partido() == idPartido && inventario.getId_localidad() == idLocalidad) {
            return inventario.getCantidad_disponible();
        }
    }
    return 0;
}

private int obtenerAforoTotalPorPartidoYLocalidad(int idPartido, int idLocalidad) {
    for (Inventario inventario : inventarioListDisponibilidad) {
        if (inventario.getId_partido() == idPartido && inventario.getId_localidad() == idLocalidad) {
            return inventario.getCantidad_disponible();
        }
    }
    return 0;
}

private void limpiarTablaDisponibilidad() {
    DefaultTableModel model = (DefaultTableModel) jTableDisponibilidad.getModel(); // ← CAMBIAR
    model.setRowCount(0);
}

private void actualizarDatosDisponibilidad() {
    try {
        JOptionPane.showMessageDialog(this, 
            "Actualizando datos de disponibilidad...", 
            "Actualizando", 
            JOptionPane.INFORMATION_MESSAGE);

        cargarDatosParaDisponibilidad();

        actualizarTablaDisponibilidad();
        
        JOptionPane.showMessageDialog(this, 
            "Datos de disponibilidad actualizados correctamente", 
            "Actualización Exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
        
    } catch (Exception e) {
        System.err.println("Error actualizando datos disponibilidad: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando datos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void exportarDatosDisponibilidad() {
    try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte de disponibilidad");
        fileChooser.setSelectedFile(new File("reporte_disponibilidad.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            exportarTablaACSV(jTableDisponibilidad, fileToSave);
            
            JOptionPane.showMessageDialog(this, 
                "Reporte exportado correctamente a: " + fileToSave.getAbsolutePath(), 
                "Exportación Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error exportando datos: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error exportando datos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void agregarListenersVenta() {
    jComboBox4.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            actualizarPrecioYStock();
        }
    });
    jComboBox3.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            actualizarPrecioYStock();
        }
    });

    jSpinnerCantidad.addChangeListener(e -> {
        actualizarPrecioTotal();    
        validarCantidadConStock();   
    });
    configurarListenerSpinner();
}
private void configurarTablaDisponibilidad() {
    try {
        String[] columnNames = {"LOCALIDAD", "AFORO TOTAL", "DISPONIBLE", "PRECIO"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;  
                    case 1: return Integer.class; 
                    case 2: return Integer.class; 
                    case 3: return Double.class; 
                    default: return Object.class;
                }
            }
        };

        jTableDisponibilidad.setModel(model);

        configurarFormatoTablaDisponibilidad();
        
        System.out.println("Tabla de disponibilidad configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla disponibilidad: " + e.getMessage());
    }
}
private void actualizarStockDisponible() {
    if (jComboBox3.getSelectedItem() != null && jComboBox4.getSelectedItem() != null) {
        Object partido = jComboBox3.getSelectedItem();
        String localidad = jComboBox4.getSelectedItem().toString();
        
        int stockDisponible = obtenerStockDisponible(localidad, partido);
        jLabel101.setText(String.valueOf(stockDisponible));

        actualizarColorStock(stockDisponible);
        actualizarOpcionesCantidad(stockDisponible);
    }
}
private void actualizarColorStock(int stock) {
    jLabel101.setText(String.valueOf(stock));

    if (stock <= 0) {
        jLabel101.setForeground(new Color(220, 53, 69)); // Rojo
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.BOLD));
    } else if (stock <= 10) {
        jLabel101.setForeground(new Color(255, 153, 0)); // Naranja
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.BOLD));
    } else if (stock <= 50) {
        jLabel101.setForeground(new Color(255, 193, 7)); // Amarillo
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));
    } else {
        jLabel101.setForeground(new Color(40, 167, 69)); // Verde
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));
    }
}
private void validarCantidadConStock() {
    if (jComboBox3.getSelectedItem() != null && 
        jComboBox4.getSelectedItem() != null) {
        
        Object partido = jComboBox3.getSelectedItem();
        String localidad = jComboBox4.getSelectedItem().toString();
        int cantidadSeleccionada = (int) jSpinnerCantidad.getValue();
        int stockDisponible = obtenerStockDisponible(localidad, partido);
        
        if (cantidadSeleccionada > stockDisponible) {
            jSpinnerCantidad.setValue(stockDisponible);
        }
    }
}
private int obtenerStockDisponible(String nombreLocalidad, Object partidoSeleccionado) {
    try {
        int idLocalidad = obtenerIdLocalidad(nombreLocalidad);
        if (idLocalidad == -1) {
            throw new Exception("No se encontró la localidad: " + nombreLocalidad);
        }

        int idPartido = obtenerIdPartido(partidoSeleccionado);
        if (idPartido == -1) {
            throw new Exception("No se encontró el partido seleccionado");
        }
        
        if (inventarioList != null) {
            for (Inventario inventario : inventarioList) {
                if (inventario.getId_localidad() == idLocalidad && 
                    inventario.getId_partido() == idPartido) {
                    return inventario.getCantidad_disponible();
                }
            }
        }

        return consultarStockDirecto(idLocalidad, idPartido);
        
    } catch (Exception e) {
        System.err.println("Error obteniendo stock: " + e.getMessage());
        return 0;
    }
}
private int consultarStockDirecto(int idLocalidad, int idPartido) {
    try {
        ServicioInventario servicio = new ServicioInventario();
        List<Inventario> inventarioActual = servicio.getInventario();
        
        for (Inventario inv : inventarioActual) {
            if (inv.getId_localidad() == idLocalidad && inv.getId_partido() == idPartido) {
                return inv.getCantidad_disponible();
            }
        }
        return 0;
    } catch (Exception e) {
        System.err.println("Error consultando stock directo: " + e.getMessage());
        return 0;
    }
}

private int obtenerIdPartido(Object partidoSeleccionado) {
    try {
        if (partidoSeleccionado instanceof Partido) {
            return ((Partido) partidoSeleccionado).getId_partido();
        } else if (partidosList != null) {
            String nombrePartidoBuscado = partidoSeleccionado.toString();
            
            for (Partido partido : partidosList) {
                String nombrePartidoEnLista = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
                
                if (nombrePartidoEnLista.equals(nombrePartidoBuscado)) {
                    return partido.getId_partido();
                }
            }
        }
        return -1;
    } catch (Exception e) {
        System.err.println("Error obteniendo ID del partido: " + e.getMessage());
        return -1;
    }
}
private int obtenerIdLocalidad(String nombreLocalidad) {
    try {
        if (localidadesList != null) {
            for (Localidad localidad : localidadesList) {
                if (localidad.getNombre().equals(nombreLocalidad)) {
                    return localidad.getId_localidad();
                }
            }
        }
        return -1;
    } catch (Exception e) {
        System.err.println("Error obteniendo ID de localidad: " + e.getMessage());
        return -1;
    }
}
private void actualizarPrecioUnitario() {
    if (jComboBox4.getSelectedItem() != null && jComboBox3.getSelectedItem() != null) {
        String localidad = jComboBox4.getSelectedItem().toString();
        Object partido = jComboBox3.getSelectedItem();
        
        try {
            double precio = obtenerPrecioLocalidad(localidad, partido);
            userTxt7.setText(String.format("%.2f", precio));
            actualizarPrecioTotal();
        } catch (Exception e) {
            userTxt7.setText("0.00");
            System.err.println("Error actualizando precio unitario: " + e.getMessage());
        }
    }
}
private double obtenerPrecioMinimoPorLocalidad(int idLocalidad) {
    double precioMinimo = Double.MAX_VALUE;
    for (Inventario inventario : inventarioList) {
        if (inventario.getId_localidad() == idLocalidad) {
            if (inventario.getPrecio() < precioMinimo) {
                precioMinimo = inventario.getPrecio();
            }
        }
    }
    return precioMinimo == Double.MAX_VALUE ? 0.0 : precioMinimo;
}
private double obtenerPrecioMasComunPorLocalidad(int idLocalidad) {
    Map<Double, Integer> frecuenciaPrecios = new HashMap<>();
    
    for (Inventario inventario : inventarioList) {
        if (inventario.getId_localidad() == idLocalidad) {
            double precio = inventario.getPrecio();
            frecuenciaPrecios.put(precio, frecuenciaPrecios.getOrDefault(precio, 0) + 1);
        }
    }
    
    double precioMasComun = 0.0;
    int maxFrecuencia = 0;
    
    for (Map.Entry<Double, Integer> entry : frecuenciaPrecios.entrySet()) {
        if (entry.getValue() > maxFrecuencia) {
            maxFrecuencia = entry.getValue();
            precioMasComun = entry.getKey();
        }
    }
    
    return precioMasComun;
}
private void actualizarLocalidadesEnTabla() {
    try {
        cargarLocalidadesEnTabla();
        JOptionPane.showMessageDialog(this, 
            "Datos actualizados correctamente", 
            "Actualización", 
            JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error actualizando datos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    private void header3MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header3MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse, y - yMouse);
    }//GEN-LAST:event_header3MouseDragged

    private void header3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header3MousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_header3MousePressed

    private void userLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userLabel5MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card3"); 
    }//GEN-LAST:event_userLabel5MouseClicked

    private void jLabel18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseClicked
    CardLayout cl = (CardLayout) MainPanel.getLayout();
    cl.show(MainPanel, "card5"); // VENTAS
    }//GEN-LAST:event_jLabel18MouseClicked

    private void loginBtnTxt3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt3MouseExited
        loginBtn.setBackground(new Color(0,134,190));
    }//GEN-LAST:event_loginBtnTxt3MouseExited

    private void loginBtnTxt3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt3MouseEntered
        loginBtn.setBackground(new Color(0, 156, 223));
    }//GEN-LAST:event_loginBtnTxt3MouseEntered

    private void btnCerrarVentaVendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarVentaVendedorActionPerformed
    try {
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String partidoInfo = "";
        if (jComboBox3.getSelectedItem() != null) {
            partidoInfo = jComboBox3.getSelectedItem().toString();
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Confirmar venta?\n\n" +
            "Total: Q" + String.format("%.2f", totalVenta) + "\n" +
            "Items: " + jTable1.getRowCount(),
            "Confirmar Venta",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            procesarVenta();
            boolean facturaGenerada = GeneradorFacturaPDF.generarFacturaVenta(
                jTable1, totalVenta, partidoInfo
            );

            JOptionPane.showMessageDialog(this,
                "✅ Venta procesada exitosamente!\n" +
                "Total: Q" + String.format("%.2f", totalVenta),
                "Venta Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
        }
                    limpiarCarrito();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "❌ Error al procesar venta: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_btnCerrarVentaVendedorActionPerformed

    private void loginBtnTxt1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt1MouseExited
        loginBtn.setBackground(new Color(0,134,190));
    }//GEN-LAST:event_loginBtnTxt1MouseExited

    private void loginBtnTxt1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt1MouseEntered
        loginBtn.setBackground(new Color(0, 156, 223));
    }//GEN-LAST:event_loginBtnTxt1MouseEntered

    private void loginBtnTxt1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt1MouseClicked
    try {
        if (jComboBox3.getSelectedItem() == null ||
            jComboBox4.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object partidoObj = jComboBox3.getSelectedItem();
        String partido = partidoObj.toString();
        String localidad = jComboBox4.getSelectedItem().toString();
        int cantidad = (int) jSpinnerCantidad.getValue();

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this,
                "La cantidad debe ser mayor a 0",
                "Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stockDisponible = obtenerStockDisponible(localidad, partidoObj);
        if (cantidad > stockDisponible) {
            JOptionPane.showMessageDialog(this,
                "Stock insuficiente\n\n" +
                "Cantidad solicitada: " + cantidad + "\n" +
                "Stock disponible: " + stockDisponible + "\n\n" +
                "Por favor seleccione una cantidad menor",
                "Stock Insuficiente",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (stockDisponible == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay boletos disponibles para esta localidad y partido",
                "Stock Agotado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precioUnitario;
        try {
            precioUnitario = Double.parseDouble(userTxt7.getText().trim());
        } catch (NumberFormatException e) {

            precioUnitario = obtenerPrecioLocalidad(localidad, partidoObj);
            userTxt7.setText(String.format("%.2f", precioUnitario));
        }

        double precioTotal;
        try {
            precioTotal = Double.parseDouble(jLabel100.getText().trim());
        } catch (NumberFormatException e) {
            precioTotal = precioUnitario * cantidad;
            jLabel100.setText(String.format("%.2f", precioTotal));
        }
        
        double subtotal = precioUnitario * cantidad;
 
        añadirFilaAlCarrito(partido, localidad, cantidad, precioUnitario, subtotal);
        
        totalVenta += subtotal;
        actualizarTotales();

        limpiarCamposVenta();

        JOptionPane.showMessageDialog(this,
            "Item añadido al carrito:\n" +
            "Partido: " + partido + "\n" +
            "Localidad: " + localidad + "\n" +
            "Cantidad: " + cantidad + "\n" +
            "Precio Unitario: Q" + String.format("%.2f", precioUnitario) + "\n" +
            "Subtotal: Q" + String.format("%.2f", subtotal),
            "Item Añadido",
            JOptionPane.INFORMATION_MESSAGE);
            
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error al añadir item: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_loginBtnTxt1MouseClicked
private String limpiarTextoPartido(String partidoCompleto) {
    if (partidoCompleto == null) return "";

    if (partidoCompleto.contains(" - ")) {
        return partidoCompleto.split(" - ")[0];
    }
    
    return partidoCompleto;
}
    private void jLabel59MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel59MouseClicked
     // PARTIDOS - Ir al panel de disponibilidad
    CardLayout cl = (CardLayout) MainPanel.getLayout();
    cl.show(MainPanel, "card6");  // DISPONIBILIDAD
    }//GEN-LAST:event_jLabel59MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked

    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel118MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel118MouseClicked
        buscarUsuarioPorId();
    }//GEN-LAST:event_jLabel118MouseClicked
private void guardarUsuario() {
    try {
        String nombreUsuario = jTextField9.getText();
        String nombreCompleto = jTextField10.getText();
        String contrasena = jTextField8.getText();
        String rol = jComboBox13.getSelectedItem().toString();
        String estadoStr = jComboBox12.getSelectedItem().toString();
        boolean estado = estadoStr.equalsIgnoreCase("ACTIVO");

        if (nombreUsuario.isEmpty() || nombreCompleto.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos obligatorios", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre_usuario(nombreUsuario);
        nuevoUsuario.setNombre_completo(nombreCompleto);
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setContrasena_hash(contrasena);
        nuevoUsuario.setEstado(estado);

        Servicios.ServicioUsuario servicio = new Servicios.ServicioUsuario();
        boolean exito = servicio.signup(nombreUsuario, contrasena, nombreCompleto, rol);
        
        if (exito) {
            JOptionPane.showMessageDialog(this, 
                "Usuario creado correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            cargarDatosUsuarios();
            cargarUsuariosEnTabla();

            limpiarCamposUsuario();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al crear el usuario", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error creando usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error creando usuario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void cargarDatosUsuarios() {
    try {
        System.out.println("Cargando datos de usuarios...");
        
        // Cargar usuarios desde el servicio
        Servicios.ServicioUsuario servicioUsuario = new Servicios.ServicioUsuario();
        usuariosList = servicioUsuario.getUsuarios();
        
        System.out.println("Usuarios cargados: " + usuariosList.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando datos de usuarios: " + e.getMessage());
        throw new RuntimeException("Error cargando datos de usuarios: " + e.getMessage(), e);
    }
}
private Usuario buscarUsuarioPorId(int id) {
    if (usuariosList != null) {
        for (Usuario usuario : usuariosList) {
            if (usuario.getId_usuario() == id) {
                return usuario;
            }
        }
    }
    return null;
}
    private void jLabel98MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel98MouseClicked
        crearUsuario();
    }//GEN-LAST:event_jLabel98MouseClicked

    private void jLabel117MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel117MouseClicked
        actualizarUsuario();
    }//GEN-LAST:event_jLabel117MouseClicked

    private void jLabel120MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel120MouseClicked
        cambiarEstadoUsuario();
    }//GEN-LAST:event_jLabel120MouseClicked

    private void jPanel40MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel40MouseClicked
        cambiarEstadoUsuario();
    }//GEN-LAST:event_jPanel40MouseClicked

    private void jPanel30MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel30MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card7");
    }//GEN-LAST:event_jPanel30MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
    try {
        CardLayout cl = (CardLayout) MainPanel.getLayout();

        cl.show(MainPanel, "ADMINISTRACION");

        SwingUtilities.invokeLater(() -> {
            try {

                JPanel panelAdministracion = (JPanel) MainPanel.getComponent(7); 

                CardLayout clAnidado = (CardLayout) panelAdministracion.getLayout();
                clAnidado.show(panelAdministracion, "usuario");
                
                System.out.println("Navegado a NewUser correctamente");
                
            } catch (Exception e) {
                System.err.println("Error navegando a NewUser: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error al cargar el panel de usuarios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
    } catch (Exception e) {
        System.err.println("Error en navegación: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
            "Error de navegación: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jPanel39MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel39MouseClicked
        crearLocalidadAdmin();
    }//GEN-LAST:event_jPanel39MouseClicked

    private void jPanel26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel26MouseClicked
       actualizarLocalidadAdmin();
    }//GEN-LAST:event_jPanel26MouseClicked

    private void jLabel76MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel76MouseClicked
        crearPartidoAdmin();
    }//GEN-LAST:event_jLabel76MouseClicked

    private void jPanel20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel20MouseClicked
        crearPartidoAdmin();
    }//GEN-LAST:event_jPanel20MouseClicked

    private void jPanel33MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel33MouseClicked
        adminGuardarInventario();
    }//GEN-LAST:event_jPanel33MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    boolean esModoOscuro = getContentPane().getBackground().equals(Color.DARK_GRAY);
    
    if (esModoOscuro) {

        aplicarModoClaroCompleto();
        jButton1.setText("🌙");
        jButton1.setToolTipText("Cambiar a modo oscuro");
    } else {
        aplicarModoOscuroCompleto();
        jButton1.setText("☀️");
        jButton1.setToolTipText("Cambiar a modo claro");
    }

    jButton1.revalidate();
    jButton1.repaint();
}

private void aplicarModoOscuroCompleto() {
    getContentPane().setBackground(Color.DARK_GRAY);

    cambiarColorPanel(MainPanel, Color.DARK_GRAY, Color.WHITE);

    cambiarColorPanel(INICIO, Color.DARK_GRAY, Color.WHITE);
    cambiarColorPanel(LOCALIDADES, Color.DARK_GRAY, Color.WHITE);
    cambiarColorPanel(PARTIDOS, Color.DARK_GRAY, Color.WHITE);
    cambiarColorPanel(VENTA, Color.DARK_GRAY, Color.WHITE);
    cambiarColorPanel(DISPONIBLE2, Color.DARK_GRAY, Color.WHITE);
    cambiarColorPanel(LOBBY, Color.DARK_GRAY, Color.WHITE);
    cambiarColorPanel(ADMINISTRACION, new Color(45, 45, 45), Color.WHITE);
    cambiarColorPanel(Inventario, new Color(45, 45, 45), Color.WHITE);
    cambiarColorPanel(NewPartido, new Color(45, 45, 45), Color.WHITE);
    cambiarColorPanel(NewLocalidades, new Color(45, 45, 45), Color.WHITE);
    cambiarColorPanel(Usuario, new Color(45, 45, 45), Color.WHITE);
}

private void aplicarModoClaroCompleto() {
    getContentPane().setBackground(Color.WHITE);

    cambiarColorPanel(MainPanel, Color.WHITE, Color.BLACK);
    cambiarColorPanel(INICIO, Color.WHITE, Color.BLACK);
    cambiarColorPanel(LOCALIDADES, Color.WHITE, Color.BLACK);
    cambiarColorPanel(PARTIDOS, Color.WHITE, Color.BLACK);
    cambiarColorPanel(VENTA, Color.WHITE, Color.BLACK);
    cambiarColorPanel(DISPONIBLE2, Color.WHITE, Color.BLACK);
    cambiarColorPanel(LOBBY, Color.WHITE, Color.BLACK);
    cambiarColorPanel(ADMINISTRACION, Color.WHITE, Color.BLACK);
    cambiarColorPanel(Inventario, Color.WHITE, Color.BLACK);
    cambiarColorPanel(NewPartido, Color.WHITE, Color.BLACK);
    cambiarColorPanel(NewLocalidades, Color.WHITE, Color.BLACK);
    cambiarColorPanel(Usuario, Color.WHITE, Color.BLACK);
}

private void cambiarColorPanel(JPanel panel, Color colorFondo, Color colorTexto) {
    if (panel != null) {
        panel.setBackground(colorFondo);
        panel.setForeground(colorTexto);
        for (Component comp : panel.getComponents()) {
            if (comp == jPanel26 || comp == jPanel39 || comp == jPanel38|| 
                comp == jPanel32|| comp == jPanel37 || comp == jPanel40 ||
                comp == jPanel20 || comp == jPanel41 || comp == jPanel33 || comp == jPanel42  || comp == loginBtn5 || comp == loginBtn || comp == loginBtnTxt1 || comp == btnCerrarVentaVendedor ) {
                continue;
            }
            
            if (comp instanceof JLabel) {
                comp.setForeground(colorTexto);
            } else if (comp instanceof JPanel) {
                cambiarColorPanel((JPanel) comp, colorFondo, colorTexto);
            } else if (comp instanceof JButton) {
                comp.setBackground(colorFondo);
                comp.setForeground(colorTexto);
            } else if (comp instanceof JTextField) {
                comp.setBackground(colorFondo);
                comp.setForeground(colorTexto);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(colorFondo);
                comp.setForeground(colorTexto);
            } else if (comp instanceof JTable) {
                comp.setBackground(colorFondo);
                comp.setForeground(colorTexto);
            }
        }
    }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jPanel41MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel41MouseClicked
    try {
        int filaSeleccionada = jTable5.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un registro de la tabla para actualizar el precio", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idInventario = (int) jTable5.getValueAt(filaSeleccionada, 0);
        String partido = jTable5.getValueAt(filaSeleccionada, 1).toString();
        String localidad = jTable5.getValueAt(filaSeleccionada, 2).toString();
        double precioActual = (double) jTable5.getValueAt(filaSeleccionada, 3);

        String precioStr = jTextField5.getText().trim();
        if (precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el nuevo precio", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double nuevoPrecio;
        try {
            nuevoPrecio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Precio debe ser un número decimal válido", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nuevoPrecio <= 0) {
            JOptionPane.showMessageDialog(this, 
                "El precio debe ser mayor a cero", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de actualizar SOLO el precio?\n\n" +
            "Partido: " + partido + "\n" +
            "Localidad: " + localidad + "\n" +
            "Precio actual: Q" + String.format("%.2f", precioActual) + "\n" +
            "Nuevo precio: Q" + String.format("%.2f", nuevoPrecio),
            "Confirmar cambio de precio",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        Servicios.ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        Inventario resultado = servicioInventario.updatePrecioInventario(idInventario, nuevoPrecio);
        
        if (resultado != null) {
            JOptionPane.showMessageDialog(this, 
                "Precio actualizado correctamente\n\n" +
                "Nuevo precio: Q" + String.format("%.2f", nuevoPrecio), 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            adminCargarInventarioEnTablaDesdeBD();

            jTextField5.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar el precio", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error actualizando solo precio: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando precio: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jPanel41MouseClicked

    private void jButtonVendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVendedorActionPerformed

        boolean esModoOscuro = getContentPane().getBackground().equals(Color.DARK_GRAY);

        if (esModoOscuro) {
            aplicarModoClaroCompleto();
            jButtonVendedor.setText("🌙");
            jButtonVendedor.setToolTipText("Cambiar a modo oscuro");
        } else {
            aplicarModoOscuroCompleto();
            jButtonVendedor.setText("☀️");
            jButtonVendedor.setToolTipText("Cambiar a modo claro");
        }

        jButtonVendedor.revalidate();
        jButtonVendedor.repaint();

    }//GEN-LAST:event_jButtonVendedorActionPerformed

    private void jLabel57MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel57MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card6");
    }//GEN-LAST:event_jLabel57MouseClicked

    private void jLabel102MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel102MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card10");
    }//GEN-LAST:event_jLabel102MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card5");
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jPanel42MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel42MouseClicked
        adminActualizarInventario();
    }//GEN-LAST:event_jPanel42MouseClicked

    private void jLabel19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card4"); // LOCALIDADES
    }//GEN-LAST:event_jLabel19MouseClicked

    private void favicon92MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon92MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon92MouseClicked

    private void favicon91MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon91MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon91MouseClicked

    private void favicon96MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon96MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon96MouseClicked

    private void favicon97MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon97MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon97MouseClicked

    private void favicon101MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon101MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon101MouseClicked

    private void favicon102MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon102MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon102MouseClicked

    private void favicon106MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon106MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon106MouseClicked

    private void favicon107MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon107MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon107MouseClicked

    private void header6MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header6MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_header6MouseDragged

    private void header6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header6MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_header6MousePressed

    private void txtUsuarioRFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsuarioRFocusGained
        txtUsuarioR.setText("");
        txtUsuarioR.setForeground(Color.black);
    }//GEN-LAST:event_txtUsuarioRFocusGained

    private void txtUsuarioRMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUsuarioRMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioRMousePressed

    private void loginBtnTxt5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt5MouseClicked

    private void loginBtnTxt5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt5MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt5MouseEntered

    private void loginBtnTxt5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt5MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt5MouseExited

    private void jLabel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel23MouseClicked

    private void jLabel63MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel63MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel63MouseClicked

    private void jLabel123MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel123MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel123MouseClicked

    private void favicon111MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon111MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon111MouseClicked

    private void favicon112MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favicon112MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_favicon112MouseClicked

    private void jLabel69MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel69MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel69MouseClicked

    private void jPanel81MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel81MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel81MouseClicked

    private void loginBtnTxt4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt4MouseClicked

    private void loginBtnTxt4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt4MouseEntered

    private void loginBtnTxt4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt4MouseExited

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
try {
    Conexion cn = new Conexion();
    Connection cnn = cn.getConnection();
    String reportPath = "C:\\Users\\josth\\JaspersoftWorkspace\\MyReports\\Vendedor2.jrxml";

    JasperReport jr = JasperCompileManager.compileReport(reportPath);
    JasperPrint jp = JasperFillManager.fillReport(jr, null, cnn);
    JasperViewer.viewReport(jp, false);
    
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "Error cargando el reporte por partido: " + ex.getMessage());
    ex.printStackTrace();
}
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jPanel72MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel72MouseClicked

    }//GEN-LAST:event_jPanel72MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
try {
    Conexion cn = new Conexion();
    Connection cnn = cn.getConnection();
    String reportPath = "C:\\Users\\josth\\JaspersoftWorkspace\\MyReports\\VentasPartido.jrxml";

    JasperReport jr = JasperCompileManager.compileReport(reportPath);
    JasperPrint jp = JasperFillManager.fillReport(jr, null, cnn);
    JasperViewer.viewReport(jp, false);
    
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "Error cargando el reporte por partido: " + ex.getMessage());
    ex.printStackTrace();
}
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    try {
        Conexion cn = new Conexion();
        Connection cnn = cn.getConnection();
        String reportPath = "C:\\Users\\josth\\JaspersoftWorkspace\\MyReports\\VentaTotal.jrxml";

        Map<String, Object> parameters = new HashMap<>();
        String reportDir = "C:\\Users\\josth\\JaspersoftWorkspace\\MyReports\\";
        parameters.put("REPORT_DIR", reportDir);

        JasperReport jr = JasperCompileManager.compileReport(reportPath);
        JasperPrint jp = JasperFillManager.fillReport(jr, parameters, cnn);

        JasperViewer.viewReport(jp, false);
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error cargando el reporte: " + ex.getMessage());
        ex.printStackTrace();
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
    try {
        Guardar.SessionManager sessionManager = Guardar.SessionManager.getInstance();

        if (!sessionManager.isSessionActiva()) {
            JOptionPane.showMessageDialog(this, 
                "Debe iniciar sesión primero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!sessionManager.esAdmin()) {
            JOptionPane.showMessageDialog(this, 
                "Solo los administradores pueden ver las solicitudes", 
                "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        System.out.println("=== ABRIENDO MÓDULO DE SOLICITUDES ===");
        System.out.println("Usuario: " + sessionManager.getNombreUsuario());
        System.out.println("Rol: " + sessionManager.getUsuarioRol());

        crearVentanaSolicitudes();
        
    } catch (Exception ex) {
        System.err.println("Error abriendo módulo de solicitudes: " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jLabel27MouseClicked

    private void loginBtn10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtn10MouseClicked
    try {
        String nombreUsuario = txtUsuarioR1.getText().trim();
        String motivo = txtUsuarioR.getText().trim();
        
        if (nombreUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su nombre de usuario");
            txtUsuarioR1.requestFocus();
            return;
        }
        
        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el motivo de la solicitud");
            txtUsuarioR.requestFocus();
            return;
        }

        ServicioUsuario servicio = new ServicioUsuario();
        boolean exito = servicio.crearSolicitudRecuperacion(nombreUsuario, motivo);
        
        if (exito) {
            JOptionPane.showMessageDialog(this, 
                "Solicitud enviada exitosamente\nEl administrador revisará su solicitud",
                "Solicitud Enviada", 
                JOptionPane.INFORMATION_MESSAGE);

            txtUsuarioR1.setText("");
            txtUsuarioR.setText("");

            System.out.println("🔄 Actualizando tabla después de enviar solicitud...");
            cargarDatosEnTabla();
            
        } else {
            JOptionPane.showMessageDialog(this, 
                " Error al enviar la solicitud\nPor favor intente nuevamente",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            " Error: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_loginBtn10MouseClicked

    private void txtUsuarioR1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsuarioR1FocusGained
        txtUsuarioR1.setText("");
        txtUsuarioR1.setForeground(Color.black);
    }//GEN-LAST:event_txtUsuarioR1FocusGained

    private void txtUsuarioR1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUsuarioR1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioR1MousePressed

    private void jPanel89MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel89MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel89MouseClicked

    private void jPanel93MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel93MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel93MouseClicked
public void abrirModuloSolicitudes() {
    Guardar.SessionManager sessionManager = Guardar.SessionManager.getInstance();
    if (!sessionManager.isSessionActiva()) {
        JOptionPane.showMessageDialog(this, 
            "Debe iniciar sesión primero", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    if (!"admin".equalsIgnoreCase(sessionManager.getUsuarioRol())) {
        JOptionPane.showMessageDialog(this, 
            "Solo los administradores pueden ver las solicitudes", 
            "Acceso denegado", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    crearVentanaSolicitudes();
}
    
    private JTable buscarTablaEnCard8() {
    Component[] cards = ADMINISTRACION.getComponents();
    if (cards.length >= 8) { 
        Component card8 = cards[7]; 
        if (card8 instanceof Container) {
            System.out.println(" Buscando tabla en card8...");
            return buscarTablaEnContainer((Container) card8);
        }
    }
    return null;
}
private void crearTablaEnCard8Activo() {
    try {
        System.out.println("CREANDO TABLA EN CARD8");

        Component[] cards = ADMINISTRACION.getComponents();
        if (cards.length >= 8) {
            JPanel card8 = (JPanel) cards[7];

            card8.removeAll();
            JLabel titulo = new JLabel("SOLICITUDES DE RECUPERACIÓN DE CONTRASEÑA", JLabel.CENTER);
            titulo.setFont(new Font("Arial", Font.BOLD, 18));
            titulo.setBounds(50, 20, 700, 30);
            card8.add(titulo);
            jTable2 = new JTable();
            JScrollPane scrollPane = new JScrollPane(jTable2);
            scrollPane.setBounds(50, 70, 900, 400);
            
            card8.add(scrollPane);
            card8.revalidate();
            card8.repaint();
            
            System.out.println(" Nueva tabla creada en card8");
            
            // Inicializar y cargar datos
            inicializarTablaSolicitudes();
            cargarSolicitudesEnTable();
        }
        
    } catch (Exception e) {
        System.err.println("Error creando tabla en card8: " + e.getMessage());
        e.printStackTrace();
    }
}
    private void mostrarPanelSolicitudesCorrecto() {
    try {
        System.out.println("CREANDO TABLA EN PANEL CORRECTO");
        Container panelActivo = null;
        for (Component comp : ADMINISTRACION.getComponents()) {
            if (comp.isShowing() && comp instanceof Container) {
                panelActivo = (Container) comp;
                System.out.println(" Panel activo encontrado: " + comp.getClass().getSimpleName());
                break;
            }
        }
        
        if (panelActivo != null) {
            JTable nuevaTabla = new JTable();
            JScrollPane scrollPane = new JScrollPane(nuevaTabla);
            scrollPane.setBounds(300, 150, 600, 300);
            
            panelActivo.add(scrollPane);
            panelActivo.revalidate();
            panelActivo.repaint();
            
            jTable2 = nuevaTabla;
            System.out.println(" Nueva tabla creada en panel activo");

            inicializarTablaSolicitudes();
            cargarSolicitudesEnTable();
        }
        
    } catch (Exception e) {
        System.err.println("Error creando tabla en panel activo: " + e.getMessage());
    }
}
    private JTable buscarTablaEnPanelActivo() {
    for (Component comp : ADMINISTRACION.getComponents()) {
        if (comp.isShowing()) {
            System.out.println("🔍 Buscando en panel ACTIVO: " + comp.getClass().getSimpleName());
            JTable tabla = buscarTablaEnContainerShowing((Container) comp);
            if (tabla != null) {
                return tabla;
            }
        }
    }
    return null;
}
private JTable buscarTablaEnContainerShowing(Container container) {
    for (Component comp : container.getComponents()) {
        if (comp.isShowing()) {
            System.out.println("  🔍 En: " + comp.getClass().getSimpleName() + " - Showing: " + comp.isShowing());
            
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTable) {
                    System.out.println("  TABLA ENCONTRADA EN SCROLLPANE ACTIVO");
                    return (JTable) view;
                }
            }
            
            if (comp instanceof JTable) {
                System.out.println("  TABLA DIRECTA ENCONTRADA");
                return (JTable) comp;
            }
            
            if (comp instanceof Container) {
                JTable tabla = buscarTablaEnContainerShowing((Container) comp);
                if (tabla != null) {
                    return tabla;
                }
            }
        }
    }
    return null;
}
    private void printComponentHierarchy(java.awt.Container container, int level) {
    String indent = "  ".repeat(level);
    for (java.awt.Component comp : container.getComponents()) {
        System.out.println(indent + comp.getClass().getSimpleName() + 
                         " - Name: " + comp.getName() +
                         " - Visible: " + comp.isVisible() +
                         " - Showing: " + comp.isShowing() +
                         " - Size: " + comp.getSize());
        
        if (comp instanceof javax.swing.JPanel) {
            if (containsJTable2((javax.swing.JPanel) comp)) {
                System.out.println(indent + "  ⭐ ESTE PANEL CONTIENE jTable2!");
            }
        }
        
        if (comp instanceof java.awt.Container) {
            printComponentHierarchy((java.awt.Container) comp, level + 1);
        }
    }
}
private JTable crearTablaSolicitudes() {
    try {
        Container card8Panel = null;
        for (Component comp : ADMINISTRACION.getComponents()) {
            if (comp instanceof JPanel && comp.isVisible() && comp.isShowing()) {
                card8Panel = (Container) comp;
                System.out.println(" Panel card8 encontrado: " + comp.getSize());
                break;
            }
        }
        
        if (card8Panel != null) {
            JTable nuevaTabla = new JTable();
            JScrollPane scrollPane = new JScrollPane(nuevaTabla);
            scrollPane.setBounds(50, 100, 600, 300);
            
            card8Panel.add(scrollPane);
            card8Panel.revalidate();
            card8Panel.repaint();
            
            System.out.println(" Nueva tabla creada en card8");
            return nuevaTabla;
        }
    } catch (Exception e) {
        System.err.println("Error creando tabla: " + e.getMessage());
    }
    return null;
}
private boolean containsJTable2(javax.swing.JPanel panel) {
    for (java.awt.Component comp : panel.getComponents()) {
        if (comp == jTable2) {
            return true;
        }
        if (comp instanceof java.awt.Container) {
            if (containsJTable2InContainer((java.awt.Container) comp)) {
                return true;
            }
        }
    }
    return false;
}
private JTable buscarTablaEnContainer(Container container) {
    for (Component comp : container.getComponents()) {
        System.out.println("Buscando en: " + comp.getClass().getSimpleName() + 
                         " - Visible: " + comp.isVisible() + 
                         " - Showing: " + comp.isShowing());

        if (comp instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) comp;
            Component view = scrollPane.getViewport().getView();
            if (view instanceof JTable) {
                System.out.println(" ENCONTRADA TABLA EN SCROLLPANE: " + view.getSize());
                return (JTable) view;
            }
        }

        if (comp instanceof JTable) {
            System.out.println(" ENCONTRADA TABLA DIRECTA: " + comp.getSize());
            return (JTable) comp;
        }

        if (comp instanceof Container) {
            JTable tabla = buscarTablaEnContainer((Container) comp);
            if (tabla != null) {
                return tabla;
            }
        }
    }
    return null;
}
private JTable encontrarTablaSolicitudes() {
    return buscarTablaEnContainer(ADMINISTRACION);
}
private boolean containsJTable2InContainer(java.awt.Container container) {
    for (java.awt.Component comp : container.getComponents()) {
        if (comp == jTable2) {
            return true;
        }
        if (comp instanceof java.awt.Container) {
            if (containsJTable2InContainer((java.awt.Container) comp)) {
                return true;
            }
        }
    }
    return false;
}
    private void inicializarPanelAdmin() {
    try {
        System.out.println("INICIALIZANDO PANEL ADMINISTRADOR");

        Servicios.ServicioUsuario servicioUsuario = new Servicios.ServicioUsuario();

        configurarComboBoxesAdmin();

        configurarTablaUsuarios();

        cargarUsuariosEnTabla();
        prepararModuloLocalidades();
        
        System.out.println("Panel de administrador inicializado correctamente");
        
    } catch (Exception e) {
        System.err.println("Error inicializando panel administrador: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error inicializando panel de administrador: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void prepararModuloLocalidades() {
    try {
        System.out.println("PREPARANDO MÓDULO DE LOCALIDADES ");
        
        obtenerTodasLasLocalidades();
        configurarTablaLocalidadesAdmin();
        mostrarLocalidadesEnTablaAdmin();
        configurarEventosLocalidadesAdmin();
        
        System.out.println(" Módulo de localidades listo");
        
    } catch (Exception e) {
        System.err.println(" Error preparando módulo localidades: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error configurando localidades: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void obtenerTodasLasLocalidades() {
    try {
        System.out.println("Obteniendo lista de localidades...");
        
        Servicios.ServicioLocalidade servicio = new Servicios.ServicioLocalidade();
        listaLocalidades = servicio.getLocalidades();
        
        if (listaLocalidades != null) {
            System.out.println("Localidades obtenidas: " + listaLocalidades.size());
        } else {
            System.out.println(" No se pudieron cargar las localidades");
            listaLocalidades = new ArrayList<>();
        }
        
    } catch (Exception e) {
        System.err.println("Error obteniendo localidades: " + e.getMessage());
        listaLocalidades = new ArrayList<>();
    }
}

private void aplicarEstiloTablaLocalidades() {
    try {
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < jTable7.getColumnCount(); i++) {
            jTable7.getColumnModel().getColumn(i).setCellRenderer(centro);
        }
        
        jTable7.getColumnModel().getColumn(0).setPreferredWidth(300);
        
        jTable7.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                
                return c;
            }
        });
        
    } catch (Exception e) {
        System.err.println(" Error aplicando estilo: " + e.getMessage());
    }
}

private void mostrarLocalidadesEnTablaAdmin() {
    try {
        modeloLocalidades.setRowCount(0);
        
        if (listaLocalidades != null && !listaLocalidades.isEmpty()) {
            for (Localidad localidad : listaLocalidades) {
                modeloLocalidades.addRow(new Object[]{localidad.getNombre()});
            }
            System.out.println(" " + listaLocalidades.size() + " localidades mostradas");
        } else {
            modeloLocalidades.addRow(new Object[]{"No hay localidades registradas"});
            System.out.println("ℹ️ No hay localidades para mostrar");
        }
        
    } catch (Exception e) {
        System.err.println(" Error mostrando localidades: " + e.getMessage());
    }
}

private void configurarEventosLocalidadesAdmin() {
    jTable7.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            manejarSeleccionLocalidad();
        }
    });

    jPanel26.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            ejecutarCreacionLocalidad();
        }
    });

    jTextField7.addActionListener(e -> {
        ejecutarCreacionLocalidad();
    });
}

private void manejarSeleccionLocalidad() {
    try {
        int fila = jTable7.getSelectedRow();
        if (fila >= 0) {
            String nombre = (String) modeloLocalidades.getValueAt(
                jTable7.convertRowIndexToModel(fila), 0);
            jTextField7.setText(nombre);
            System.out.println(" Localidad seleccionada: " + nombre);
        }
    } catch (Exception e) {
        System.err.println(" Error en selección: " + e.getMessage());
    }
}

private void ejecutarCreacionLocalidad() {
    try {
        String nombre = jTextField7.getText().trim();
        
        if (!esNombreValido(nombre)) return;
        if (existeLocalidad(nombre)) return;
        if (!confirmarCreacion(nombre)) return;
        
        crearNuevaLocalidad(nombre);
        
    } catch (Exception e) {
        System.err.println(" Error creando localidad: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private boolean esNombreValido(String nombre) {
    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Ingrese un nombre de localidad", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    if (nombre.length() < 2) {
        JOptionPane.showMessageDialog(this, 
            "El nombre debe tener al menos 2 caracteres", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    return true;
}

private boolean existeLocalidad(String nombre) {
    if (listaLocalidades != null) {
        for (Localidad loc : listaLocalidades) {
            if (loc.getNombre().equalsIgnoreCase(nombre)) {
                JOptionPane.showMessageDialog(this, 
                    "La localidad '" + nombre + "' ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        }
    }
    return false;
}

private boolean confirmarCreacion(String nombre) {
    int respuesta = JOptionPane.showConfirmDialog(this,
        "¿Crear localidad?\n\nNombre: " + nombre,
        "Confirmar", JOptionPane.YES_NO_OPTION);
    return respuesta == JOptionPane.YES_OPTION;
}

private void crearNuevaLocalidad(String nombre) throws Exception {
    Localidad nueva = new Localidad();
    nueva.setNombre(nombre);
    
    Servicios.ServicioLocalidade servicio = new Servicios.ServicioLocalidade();
    Localidad resultado = servicio.createLocalidad(nueva);
    
    if (resultado != null) {
        JOptionPane.showMessageDialog(this, 
            "Localidad creada: " + nombre, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        actualizarVistaLocalidades();
        limpiarFormulario();
    } else {
        JOptionPane.showMessageDialog(this, 
            " Error al crear localidad", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void actualizarVistaLocalidades() {
    obtenerTodasLasLocalidades();
    mostrarLocalidadesEnTablaAdmin();
}

private void limpiarFormulario() {
    jTextField7.setText("");
    jTable7.clearSelection();
}
private void cargarFuncionesAdminBasicas() {
    try {
        System.out.println("CARGA BÁSICA PARA ADMIN");
        
        // Solo cargar localidades por ahora
        prepararModuloLocalidades();
        
        System.out.println("Carga básica completada - Módulo de localidades activo");
        
    } catch (Exception e) {
        System.err.println("Error en carga básica: " + e.getMessage());
    }
}
private void actualizarListaUsuarios() {
    try {
        cargarUsuariosEnTabla();
        JOptionPane.showMessageDialog(this, 
            "Lista de usuarios actualizada correctamente", 
            "Actualización Exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
        
    } catch (Exception e) {
        System.err.println("Error actualizando lista de usuarios: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando usuarios: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void configurarComboBoxesAdmin() {
    try {
        jComboBox13.removeAllItems();
        jComboBox13.addItem("admin");
        jComboBox13.addItem("vendedor");

        jComboBox12.removeAllItems();
        jComboBox12.addItem("true");
        jComboBox12.addItem("false");
        
        System.out.println("ComboBoxes de administrador configurados");
        
    } catch (Exception e) {
        System.err.println("Error configurando combo boxes admin: " + e.getMessage());
    }
}

private void configurarFormatoTablaUsuarios() {
    try {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < jTable8.getColumnCount(); i++) {
            jTable8.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        jTable8.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

                if (value instanceof String) {
                    String estado = ((String) value).toLowerCase();
                    if (estado.equals("true") || estado.equals("activo")) {
                        label.setForeground(new Color(0, 128, 0)); // Verde
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                        label.setText("ACTIVO");
                    } else {
                        label.setForeground(Color.RED);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                        label.setText("INACTIVO");
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        jTable8.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

                if (value instanceof String) {
                    String rol = ((String) value).toLowerCase();
                    if (rol.equals("admin")) {
                        label.setForeground(Color.RED);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    } else if (rol.equals("vendedor")) {
                        label.setForeground(Color.BLUE);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        jTable8.getColumnModel().getColumn(0).setPreferredWidth(50);  
        jTable8.getColumnModel().getColumn(1).setPreferredWidth(120); 
        jTable8.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable8.getColumnModel().getColumn(3).setPreferredWidth(200); 
        jTable8.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable8.getColumnModel().getColumn(5).setPreferredWidth(80);

        jTable8.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252)); 
                    }
                }
                
                return c;
            }
        });
        jTable8.setAutoCreateRowSorter(true);
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla usuarios: " + e.getMessage());
    }
}
private void procesarVenta() {
    try {
        System.out.println("PROCESANDO VENTA");
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        String partidoNombre = jComboBox3.getSelectedItem().toString();
        
        java.util.List<Detalle> detalles = new java.util.ArrayList<>();
        double totalVentaCalculado = 0.0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            int colLocalidad = -1, colPrecio = -1, colCantidad = -1, colSubtotal = -1;
            
            for (int j = 0; j < model.getColumnCount(); j++) {
                String colName = model.getColumnName(j).toLowerCase();
                if (colName.contains("localidad")) colLocalidad = j;
                else if (colName.contains("precio")) colPrecio = j;
                else if (colName.contains("canti")) colCantidad = j;
                else if (colName.contains("subtotal")) colSubtotal = j;
            }
            
            if (colLocalidad == -1 || colPrecio == -1 || colCantidad == -1) {
                throw new Exception("No se pudieron encontrar todas las columnas necesarias");
            }
            
            String localidadNombre = model.getValueAt(i, colLocalidad).toString();
            double precioUnitario = Double.parseDouble(model.getValueAt(i, colPrecio).toString());
            int cantidad = Integer.parseInt(model.getValueAt(i, colCantidad).toString());

            double subtotal = precioUnitario * cantidad;
            totalVentaCalculado += subtotal;

            if (colSubtotal != -1) {
                subtotal = Double.parseDouble(model.getValueAt(i, colSubtotal).toString());
                totalVentaCalculado += subtotal;
            }
            
            int idPartido = obtenerIdPartidoPorNombre(partidoNombre);
            int idLocalidad = obtenerIdLocalidadPorNombre(localidadNombre);
            
            if (idPartido == -1 || idLocalidad == -1) {
                throw new Exception("No se pudo encontrar IDs para: " + partidoNombre + " - " + localidadNombre);
            }
            
            Detalle detalle = new Detalle();
            detalle.setId_partido(idPartido);
            detalle.setId_localidad(idLocalidad);
            detalle.setCantidad(cantidad);
            detalle.setPrecio_unitario(precioUnitario);
            detalles.add(detalle);
            
            System.out.println("" + localidadNombre + " x" + cantidad + " Q" + precioUnitario + " = Q" + subtotal);
        }
        
        System.out.println(" Total final: Q" + totalVentaCalculado);

        Venta venta = new Venta();
        venta.setId_vendedor(SessionManager.getInstance().getUsuarioId());
        venta.setTotal_venta(totalVentaCalculado);
        venta.setDetalles(detalles);
        
        Servicios.ServicioVenta servicioVentas = new Servicios.ServicioVenta();
        servicioVentas.createVenta(venta);
        
        System.out.println(" Venta procesada exitosamente - Total: Q" + totalVentaCalculado);
        
    } catch (Exception e) {
        System.err.println("Error procesando venta: " + e.getMessage());
        throw new RuntimeException("Error al procesar venta: " + e.getMessage(), e);
    }
}

private int obtenerIdPartidoPorNombre(String nombrePartidoCompleto) {
    try {
        System.out.println("Búsqueda robusta para: '" + nombrePartidoCompleto + "'");
        
        if (partidosList == null) return -1;

        for (Partido partido : partidosList) {
            String partidoSimple = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();

            if (nombrePartidoCompleto.contains(partido.getEquipo_local()) && 
                nombrePartidoCompleto.contains(partido.getEquipo_visitante())) {
                
                System.out.println(" Partido encontrado por coincidencia: " + partidoSimple + " -> ID: " + partido.getId_partido());
                return partido.getId_partido();
            }

            if (partidoSimple.contains(nombrePartidoCompleto) || 
                nombrePartidoCompleto.contains(partidoSimple)) {
                
                System.out.println(" Partido encontrado por contenido: " + partidoSimple + " -> ID: " + partido.getId_partido());
                return partido.getId_partido();
            }
        }
        
        System.err.println(" No se pudo encontrar partido para: " + nombrePartidoCompleto);
        return -1;
        
    } catch (Exception e) {
        System.err.println("Error en búsqueda robusta: " + e.getMessage());
        return -1;
    }
}
private List<String> generarVariacionesBusqueda(String partidoCompleto) {
    List<String> variaciones = new ArrayList<>();

    variaciones.add(partidoCompleto);

    if (partidoCompleto.contains(" - ")) {
        String[] partes = partidoCompleto.split(" - ");
        if (partes.length > 0) {
            variaciones.add(partes[0].trim());
        }
    }

    if (partidoCompleto.contains("T")) {
        String[] partes = partidoCompleto.split("T");
        if (partes.length > 0) {
            variaciones.add(partes[0].trim());
        }
    }
    if (partidoCompleto.contains("vs")) {
        String[] equipos = partidoCompleto.split("vs");
        if (equipos.length == 2) {
            String soloEquipos = equipos[0].trim() + " " + equipos[1].trim();
            // Remover fecha si existe
            if (soloEquipos.contains("-")) {
                String[] temp = soloEquipos.split("-");
                if (temp.length > 0) {
                    variaciones.add(temp[0].trim());
                }
            } else {
                variaciones.add(soloEquipos);
            }
        }
    }
    
    System.out.println("Variaciones generadas: " + variaciones);
    return variaciones;
}

private void mostrarPartidosDisponibles() {
    System.err.println("Partidos disponibles en la lista:");
    if (partidosList != null) {
        for (int i = 0; i < partidosList.size(); i++) {
            Partido p = partidosList.get(i);
            System.err.println("  " + (i+1) + ". " + p.getEquipo_local() + " vs " + p.getEquipo_visitante() + " (ID: " + p.getId_partido() + ")");
        }
    } else {
        System.err.println("  Lista de partidos es nula");
    }
}
private String extraerNombresEquipos(String partidoCompleto) {
    try {

        if (partidoCompleto.contains(" - ")) {
            String[] partes = partidoCompleto.split(" - ");
            if (partes.length > 0) {
                return partes[0].trim();
            }
        }

        return partidoCompleto;
        
    } catch (Exception e) {
        System.err.println("Error extrayendo nombres de equipos: " + e.getMessage());
        return partidoCompleto;
    }
}

private int obtenerIdLocalidadPorNombre(String nombreLocalidad) {
    try {
        if (localidadesList != null) {
            for (Localidad localidad : localidadesList) {
                if (localidad.getNombre().equals(nombreLocalidad)) {
                    System.out.println("Localidad encontrada: " + nombreLocalidad + " -> ID: " + localidad.getId_localidad());
                    return localidad.getId_localidad();
                }
            }
        }
        System.err.println("No se encontró localidad: '" + nombreLocalidad + "'");
        System.err.println("Localidades disponibles: " + (localidadesList != null ? localidadesList.size() : 0));
        return -1;
    } catch (Exception e) {
        System.err.println("Error obteniendo ID localidad: " + e.getMessage());
        return -1;
    }
}
private void limpiarCarrito() {
    try {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        totalVenta = 0.0;
        actualizarTotales();

        jComboBox3.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0); 
        jSpinnerCantidad.setValue(1);
        jLabel100.setText("0.00");
        jLabel101.setText("DISPONIBLES: ");
        jLabel101.setForeground(new Color(204, 204, 204)); 
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));
        jSpinnerCantidad.setEnabled(true);

        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) jSpinnerCantidad.getModel();
        spinnerModel.setMinimum(1);
        spinnerModel.setMaximum(100); 
        spinnerModel.setValue(1);
        
        System.out.println("Carrito limpiado exitosamente");
        
    } catch (Exception e) {
        System.err.println("Error limpiando carrito: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error al limpiar el carrito: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void cargarLocalidadesEnTabla() {
    try {
        System.out.println("CARGANDO LOCALIDADES EN TABLA");

        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        List<Localidad> localidades = servicioLocalidades.getLocalidades();

        ServicioInventario servicioInventario = new ServicioInventario();
        List<Inventario> inventarios = servicioInventario.getInventario();
        
        System.out.println("Localidades obtenidas: " + localidades.size());
        System.out.println("Registros de inventario: " + inventarios.size());

        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"NOMBRE", "PRECIO", "DISPONIBLES"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;  
                    case 1: return Double.class; 
                    case 2: return Integer.class; 
                    default: return Object.class;
                }
            }
        };

        for (Localidad localidad : localidades) {

            int totalDisponible = calcularTotalDisponible(localidad.getId_localidad(), inventarios);

            double precioPromedio = calcularPrecioPromedio(localidad.getId_localidad(), inventarios);
            
            model.addRow(new Object[]{
                localidad.getNombre(),
                precioPromedio,
                totalDisponible
            });
        }
        

        
        System.out.println("Localidades cargadas en tabla: " + localidades.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando localidades en tabla: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cargando localidades: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
private void configurarTablaLocalidadesAdmin() {
    try {
        String[] columnNames = {"ID", "TIPO DE LOCALIDAD"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;
                    case 1: return String.class;  
                    default: return Object.class;
                }
            }
        };

        jTable7.setModel(model);
        configurarFormatoTablaLocalidadesAdmin();

        jTable7.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable7.getSelectedRow() != -1) {
                llenarCampoLocalidadDesdeTablaAdmin();
            }
        });
        
        System.out.println("Tabla de localidades ADMIN configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla localidades ADMIN: " + e.getMessage());
    }
}

private void configurarFormatoTablaLocalidadesAdmin() {
    try {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < jTable7.getColumnCount(); i++) {
            jTable7.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        jTable7.getColumnModel().getColumn(0).setPreferredWidth(50);   
        jTable7.getColumnModel().getColumn(1).setPreferredWidth(250);  

        jTable7.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252)); 
                    }
                }
                
                return c;
            }
        });
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla localidades ADMIN: " + e.getMessage());
    }
}

private void cargarLocalidadesEnTablaAdmin() {
    try {
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        List<Localidad> localidades = servicioLocalidades.getLocalidades();
        
        DefaultTableModel model = (DefaultTableModel) jTable7.getModel();
        model.setRowCount(0);
        
        for (Localidad localidad : localidades) {
            model.addRow(new Object[]{
                localidad.getId_localidad(),
                localidad.getNombre()
            });
        }
        
        System.out.println("Localidades cargadas en tabla ADMIN: " + localidades.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando localidades en tabla ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
            "Error cargando localidades: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void llenarCampoLocalidadDesdeTablaAdmin() {
    try {
        int selectedRow = jTable7.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) jTable7.getValueAt(selectedRow, 0);
            String nombreLocalidad = (String) jTable7.getValueAt(selectedRow, 1);
            
            // Llenar el campo de texto
            jTextField7.setText(nombreLocalidad);
            
            System.out.println("Campo localidad llenado con datos - ID: " + id + ", Nombre: " + nombreLocalidad);
        }
    } catch (Exception e) {
        System.err.println("Error llenando campo localidad desde tabla ADMIN: " + e.getMessage());
    }
}

private void crearLocalidadAdmin() {
    try {
        String nombreLocalidad = jTextField7.getText().trim();

        if (nombreLocalidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese el nombre de la localidad", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        System.out.println(" Iniciando creación de localidad: " + nombreLocalidad);

        Localidad nuevaLocalidad = new Localidad();
        nuevaLocalidad.setNombre(nombreLocalidad);
        // NO establecer precio ya que no existe en el modelo
        
        System.out.println(" Objeto Localidad creado: " + nuevaLocalidad.getNombre());

        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        Localidad resultado = servicioLocalidades.createLocalidad(nuevaLocalidad);
        
        System.out.println(" Resultado recibido: " + (resultado != null ? resultado.getId_localidad() : "NULL"));
        
        if (resultado != null && resultado.getId_localidad() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Localidad creada correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            cargarLocalidadesEnTablaAdmin();
            limpiarCampoLocalidadAdmin();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al crear la localidad (resultado nulo)", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println(" Error creando localidad ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            " Error creando localidad: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void actualizarLocalidadAdmin() {
    try {
        int selectedRow = jTable7.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una localidad de la tabla para actualizar", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String nuevoNombre = jTextField7.getText().trim();

        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese el nombre de la localidad", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idLocalidad = (int) jTable7.getValueAt(selectedRow, 0);
        String nombreActual = (String) jTable7.getValueAt(selectedRow, 1);
        
        System.out.println("Actualizando localidad:");
        System.out.println("ID: " + idLocalidad);
        System.out.println("Nombre actual: " + nombreActual);
        System.out.println("Nuevo nombre: " + nuevoNombre);
        Localidad localidadActualizada = new Localidad();
        localidadActualizada.setId_localidad(idLocalidad);
        localidadActualizada.setNombre(nuevoNombre);
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        Localidad resultado = servicioLocalidades.updateLocalidad(idLocalidad, localidadActualizada);
        
        if (resultado != null) {
            JOptionPane.showMessageDialog(this, 
                " Localidad actualizada correctamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Recargar tabla
            cargarLocalidadesEnTablaAdmin();
        } else {
            JOptionPane.showMessageDialog(this, 
                " Error al actualizar la localidad", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println(" Error actualizando localidad ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            " Error actualizando localidad: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void limpiarCampoLocalidadAdmin() {
    jTextField7.setText("");
    jTable7.clearSelection();
}

private void actualizarListaLocalidadesAdmin() {
    try {
        cargarLocalidadesEnTablaAdmin();
        JOptionPane.showMessageDialog(this, 
            "Lista de localidades actualizada correctamente", 
            "Actualización Exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
        
    } catch (Exception e) {
        System.err.println("Error actualizando lista de localidades ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando localidades: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void inicializarPanelLocalidadesAdmin() {
    try {
        System.out.println("INICIALIZANDO PANEL DE LOCALIDADES ADMIN");

        configurarTablaLocalidadesAdmin();

        cargarLocalidadesEnTablaAdmin();

        configurarEventosLocalidadesAdmin();
        
        System.out.println("Panel de localidades ADMIN inicializado correctamente");
        
    } catch (Exception e) {
        System.err.println("Error inicializando panel de localidades ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error inicializando panel de localidades: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private double calcularPrecioPromedio(int idLocalidad, List<Inventario> inventarios) {
    try {
        double sumaPrecios = 0.0;
        int contador = 0;
        
        for (Inventario inventario : inventarios) {
            if (inventario.getId_localidad() == idLocalidad) {
                sumaPrecios += inventario.getPrecio();
                contador++;
            }
        }
        
        return contador > 0 ? sumaPrecios / contador : 0.0;
        
    } catch (Exception e) {
        System.err.println("Error calculando precio promedio: " + e.getMessage());
        return 0.0;
    }
}
private int calcularTotalDisponible(int idLocalidad, List<Inventario> inventarios) {
    try {
        int total = 0;
        for (Inventario inventario : inventarios) {
            if (inventario.getId_localidad() == idLocalidad) {
                total += inventario.getCantidad_disponible();
            }
        }
        return total;
    } catch (Exception e) {
        System.err.println("Error calculando total disponible: " + e.getMessage());
        return 0;
    }
}

private void configurarPlaceholders() {
    configurarPlaceholder(userTxt4, "Ingrese su nombre de usuario");
    configurarPlaceholderPassword(passTxt2, "********");
    MainPanel.requestFocusInWindow();
}

private void configurarPlaceholder(JTextField field, String placeholder) {
    field.setText(placeholder);
    field.setForeground(Color.GRAY);
    
    field.setFocusable(true);
    
    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (field.getText().equals(placeholder)) {
                field.setText("");
                field.setForeground(Color.BLACK);
            }
        }
        
        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (field.getText().isEmpty()) {
                field.setText(placeholder);
                field.setForeground(Color.GRAY);
            }
        }
    });
}

private void configurarPlaceholderPassword(JPasswordField field, String placeholder) {
    field.setText(placeholder);
    field.setForeground(Color.GRAY);
    field.setEchoChar((char) 0);
    
    field.setFocusable(true);   
    
    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (String.valueOf(field.getPassword()).equals(placeholder)) {
                field.setText("");
                field.setForeground(Color.BLACK);
                field.setEchoChar('•');
            }
        }
        
        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (field.getPassword().length == 0) {
                field.setText(placeholder);
                field.setForeground(Color.GRAY);
                field.setEchoChar((char) 0);
            }
        }
    });
}
private void configurarBotonesMostrarContrasena() {
    btnMostrarPass1.addActionListener(e -> toggleContrasena(passTxt2, btnMostrarPass1));
}

private void toggleContrasena(JPasswordField passwordField, JButton boton) {
    if (passwordField.getEchoChar() == '•' || passwordField.getEchoChar() == '\u2022') {
        passwordField.setEchoChar((char) 0);
        boton.setText("🚫 Ocultar");
    } else {
        passwordField.setEchoChar('•');
        boton.setText("👁 Mostrar");
    }

     passwordField.repaint();
}

private void configurarTablaPartidosAdmin() {
    try {
        String[] columnNames = {"ID", "EQUIPO LOCAL", "EQUIPO VISITANTE", "FECHA", "ESTADIO", "ESTADO"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; 
                    case 1: return String.class; 
                    case 2: return String.class;
                    case 3: return String.class;
                    case 4: return String.class; 
                    case 5: return String.class;
                    default: return Object.class;
                }
            }
        };

        jTable6.setModel(model);

        configurarFormatoTablaPartidosAdmin();
        
        System.out.println("Tabla de partidos ADMIN configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla partidos ADMIN: " + e.getMessage());
    }
}

private void configurarFormatoTablaPartidosAdmin() {
    try {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jTable6.getColumnCount(); i++) {
            jTable6.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        jTable6.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                if (value instanceof String) {
                    String estado = ((String) value).toLowerCase();
                    if (estado.equals("activo") || estado.equals("programado")) {
                        label.setForeground(new Color(0, 128, 0)); // Verde
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    } else if (estado.equals("finalizado")) {
                        label.setForeground(Color.BLUE);
                    } else if (estado.equals("cancelado")) {
                        label.setForeground(Color.RED);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        jTable6.getColumnModel().getColumn(0).setPreferredWidth(50); 
        jTable6.getColumnModel().getColumn(1).setPreferredWidth(120); 
        jTable6.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTable6.getColumnModel().getColumn(3).setPreferredWidth(150);
        jTable6.getColumnModel().getColumn(4).setPreferredWidth(150); 
        jTable6.getColumnModel().getColumn(5).setPreferredWidth(100); 
        jTable6.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                }
                
                return c;
            }
        });
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla partidos ADMIN: " + e.getMessage());
    }
}

private void configurarComboBoxesPartidosAdmin() {
    try {
        jComboBox11.removeAllItems();
        String[] equipos = {"ANTIGUA", "COMUNICACIONES", "XELAJU", "MUNICIPAL"};
        for (String equipo : equipos) {
            jComboBox11.addItem(equipo);
        }

        jComboBox10.removeAllItems();
        for (String equipo : equipos) {
            jComboBox10.addItem(equipo);
        }

        jComboBox9.removeAllItems();
        String[] estadios = {"DOROTEO GUAMUCH", "CEMENTOS PROGRESO", "MATEO FLORES", "ISRAEL FLORES"};
        for (String estadio : estadios) {
            jComboBox9.addItem(estadio);
        }
        
        System.out.println("ComboBoxes de partidos ADMIN configurados");
        
    } catch (Exception e) {
        System.err.println("Error configurando combo boxes partidos ADMIN: " + e.getMessage());
    }
}

private void cargarPartidosEnTablaAdmin() {
    try {
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        List<Partido> partidos = servicioPartidos.getPartidos();
        
        DefaultTableModel model = (DefaultTableModel) jTable6.getModel();
        model.setRowCount(0);
        
        for (Partido partido : partidos) {

            String fechaFormateada = formatearFechaPartidoAdmin(partido.getFecha_partido());
            
            model.addRow(new Object[]{
                partido.getId_partido(),
                partido.getEquipo_local(),
                partido.getEquipo_visitante(),
                fechaFormateada,
                partido.getEstadio(),
                partido.getEstado()
            });
        }
        
        System.out.println("Partidos cargados en tabla ADMIN: " + partidos.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando partidos en tabla ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
            "Error cargando partidos: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private String formatearFechaPartidoAdmin(String fechaOriginal) {
    try {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no definida";
        }

        if (fechaOriginal.contains("-") && fechaOriginal.contains("T")) {
            String[] partes = fechaOriginal.split("T");
            String fecha = partes[0];
            String hora = partes[1].substring(0, 5);
            
            String[] fechaParts = fecha.split("-");
            return fechaParts[2] + "/" + fechaParts[1] + "/" + fechaParts[0] + " " + hora;
        }
        
        return fechaOriginal;
        
    } catch (Exception e) {
        System.err.println("Error formateando fecha del partido: " + e.getMessage());
        return fechaOriginal;
    }
}

private void crearPartidoAdmin() {
    try {
        String equipoLocal = jComboBox11.getSelectedItem().toString();
        String equipoVisitante = jComboBox10.getSelectedItem().toString();
        String estadio = jComboBox9.getSelectedItem().toString();
        String fecha = jTextField6.getText().trim();
        
        System.out.println("Datos del partido a crear:");
        System.out.println("Equipo Local: " + equipoLocal);
        System.out.println("Equipo Visitante: " + equipoVisitante);
        System.out.println("Estadio: " + estadio);
        System.out.println("Fecha: " + fecha);

        if (equipoLocal.equals(equipoVisitante)) {
            JOptionPane.showMessageDialog(this, 
                "El equipo local y visitante no pueden ser el mismo", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (equipoLocal.isEmpty() || equipoVisitante.isEmpty() || estadio.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validarFormatoFechaPartidoAdmin(fecha)) {
            JOptionPane.showMessageDialog(this, 
                "Formato de fecha inválido. Use formato: YYYY-MM-DDTHH:MM:SS\n\n" +
                "Ejemplo: 2024-12-25T20:30:00", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Partido nuevoPartido = new Partido();
        nuevoPartido.setEquipo_local(equipoLocal);
        nuevoPartido.setEquipo_visitante(equipoVisitante);
        nuevoPartido.setEstadio(estadio);
        nuevoPartido.setFecha_partido(fecha);
        nuevoPartido.setEstado("programado");

        System.out.println("Objeto Partido creado:");
        System.out.println("Equipo Local: " + nuevoPartido.getEquipo_local());
        System.out.println("Equipo Visitante: " + nuevoPartido.getEquipo_visitante());
        System.out.println("Estadio: " + nuevoPartido.getEstadio());
        System.out.println("Fecha: " + nuevoPartido.getFecha_partido());
        System.out.println("Estado: " + nuevoPartido.getEstado());

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Crear nuevo partido?\n\n" +
            "Equipo Local: " + equipoLocal + "\n" +
            "Equipo Visitante: " + equipoVisitante + "\n" +
            "Estadio: " + estadio + "\n" +
            "Fecha: " + fecha + "\n" +
            "Estado: PROGRAMADO",
            "Confirmar creación de partido",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        System.out.println("Llamando al servicio para crear partido...");
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        Partido resultado = servicioPartidos.createPartido(nuevoPartido);
        
        System.out.println("Resultado del servicio:");
        System.out.println("Resultado: " + resultado);
        if (resultado != null) {
            System.out.println("ID del partido: " + resultado.getId_partido());
            System.out.println("Fecha del partido: " + resultado.getFecha_partido());
            System.out.println("Estado del partido: " + resultado.getEstado());
        }
        
        if (resultado != null && resultado.getId_partido() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Partido creado correctamente\n\n" +
                "ID: " + resultado.getId_partido() + "\n" +
                "Fecha programada: " + formatearFechaPartidoAdmin(resultado.getFecha_partido()) + "\n" +
                "Estado: " + resultado.getEstado(),
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            cargarPartidosEnTablaAdmin();
            limpiarCamposPartidoAdmin();
        } else {
            String mensajeError = "Error al crear el partido";
            if (resultado != null && resultado.getId_partido() == 0) {
                mensajeError += "\nEl partido se creó pero no se asignó un ID válido";
            } else if (resultado == null) {
                mensajeError += "\nEl servicio retornó null";
            }
            
            JOptionPane.showMessageDialog(this, 
                mensajeError, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error creando partido ADMIN: " + e.getMessage());
        e.printStackTrace(); 
        
        JOptionPane.showMessageDialog(this, 
            "Error creando partido: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private boolean validarFormatoFechaPartidoAdmin(String fecha) {
    try {
        String regex = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$";
        boolean formatoValido = fecha.matches(regex);
        
        if (!formatoValido) {
            return false;
        }
        
        String[] partes = fecha.split("T");
        String fechaPart = partes[0];
        String horaPart = partes[1];
        
        String[] fechaParts = fechaPart.split("-");
        int año = Integer.parseInt(fechaParts[0]);
        int mes = Integer.parseInt(fechaParts[1]);
        int dia = Integer.parseInt(fechaParts[2]);
        
        String[] horaParts = horaPart.split(":");
        int horas = Integer.parseInt(horaParts[0]);
        int minutos = Integer.parseInt(horaParts[1]);
        int segundos = Integer.parseInt(horaParts[2]);

        return mes >= 1 && mes <= 12 && 
               dia >= 1 && dia <= 31 && 
               horas >= 0 && horas <= 23 && 
               minutos >= 0 && minutos <= 59 && 
               segundos >= 0 && segundos <= 59;
        
    } catch (Exception e) {
        return false;
    }
}

private void configurarCampoFechaPartidoAdmin() {
    jTextField6.setText("YYYY-MM-DDTHH:MM:SS");
    jTextField6.setForeground(Color.GRAY);
    
    jTextField6.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (jTextField6.getText().equals("YYYY-MM-DDTHH:MM:SS")) {
                jTextField6.setText("");
                jTextField6.setForeground(Color.BLACK);
            }
        }
        
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (jTextField6.getText().isEmpty()) {
                jTextField6.setText("YYYY-MM-DDTHH:MM:SS");
                jTextField6.setForeground(Color.GRAY);
            }
        }
    });
}

private void limpiarCamposPartidoAdmin() {
    jComboBox11.setSelectedIndex(0);
    jComboBox10.setSelectedIndex(0);
    jComboBox9.setSelectedIndex(0);
    jTextField6.setText("YYYY-MM-DDTHH:MM:SS"); // Restaurar placeholder
    jTextField6.setForeground(Color.GRAY);
}

private boolean validarFormatoFechaAdmin(String fecha) {
    try {
        String regex = "^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}$";
        return fecha.matches(regex);
    } catch (Exception e) {
        return false;
    }
}

private String convertirFechaAPFormatoAPI(String fechaUsuario) {
    try {
        String[] partes = fechaUsuario.split(" ");
        String fecha = partes[0]; 
        String hora = partes[1];
        
        String[] fechaParts = fecha.split("-");
        String dia = fechaParts[0];
        String mes = fechaParts[1];
        String año = fechaParts[2];

        return año + "-" + mes + "-" + dia + "T" + hora + ":00";
        
    } catch (Exception e) {
        System.err.println("Error convirtiendo fecha: " + e.getMessage());
        return fechaUsuario;
    }
}

private void actualizarListaPartidosAdmin() {
    try {
        cargarPartidosEnTablaAdmin();
        JOptionPane.showMessageDialog(this, 
            "Lista de partidos actualizada correctamente", 
            "Actualización Exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
        
    } catch (Exception e) {
        System.err.println("Error actualizando lista de partidos ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando partidos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void inicializarPanelPartidosAdmin() {
    try {
        System.out.println("=== INICIALIZANDO PANEL DE PARTIDOS ADMIN ===");

        configurarComboBoxesPartidosAdmin();

        configurarCampoFechaPartidoAdmin();

        configurarTablaPartidosAdmin();

        cargarPartidosEnTablaAdmin();
   
        System.out.println("Panel de partidos ADMIN inicializado correctamente");
        
    } catch (Exception e) {
        System.err.println("Error inicializando panel de partidos ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error inicializando panel de partidos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void inicializarGestionInventario() {
    configurarMapaLocalidades();
    cargarComboPartidos();
    cargarComboLocalidades();
    cargarTablaGestionInventario();
    configurarListenerTablaInventario();
}

private void configurarMapaLocalidades() {
    mapaLocalidades = new java.util.HashMap<>();
    mapaLocalidades.put("Platea VIP", 1);
    mapaLocalidades.put("General Sur", 2);
    mapaLocalidades.put("General Norte", 3);
    mapaLocalidades.put("Palco", 4);
    mapaLocalidades.put("Tribuna", 5);
    mapaLocalidades.put("Preferencia", 6);
}

private String obtenerNombreLocalidadPorId(int id) {
    for (java.util.Map.Entry<String, Integer> entry : mapaLocalidades.entrySet()) {
        if (entry.getValue() == id) {
            return entry.getKey();
        }
    }
    return "";
}

private void cargarComboPartidos() {
    try {
        java.util.List<Partido> listaPartidos = servicioPartidos.getPartidos();
        jComboBox19.removeAllItems();
        for (Partido partido : listaPartidos) {
            String partidoStr = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
            jComboBox19.addItem(partidoStr);
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar partidos: " + e.getMessage());
    }
}

private void cargarComboLocalidades() {
    String[] localidades = {"Platea VIP", "General Sur", "General Norte", "Palco", "Tribuna", "Preferencia"};
    for (String localidad : localidades) {
        jComboBox6.addItem(localidad);
    }
}

private void configurarListenerTablaInventario() {
    jTable5.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        @Override
        public void valueChanged(javax.swing.event.ListSelectionEvent event) {
            if (!event.getValueIsAdjusting() && jTable5.getSelectedRow() != -1) {
                cargarDatosInventarioDesdeTabla();
            }
        }
    });
}

private void cargarDatosInventarioDesdeTabla() {
    int filaSeleccionada = jTable5.getSelectedRow();
    if (filaSeleccionada != -1) {
        try {
            idInventarioSeleccionado = (int) jTable5.getValueAt(filaSeleccionada, 0);
            
            java.util.List<Inventario> listaInventario = servicioInventario.getInventario();
            Inventario inventarioSeleccionado = null;
            
            for (Inventario inv : listaInventario) {
                if (inv.getId_inventario() == idInventarioSeleccionado) {
                    inventarioSeleccionado = inv;
                    break;
                }
            }
            
            if (inventarioSeleccionado != null) {
                java.util.List<Partido> todosPartidos = servicioPartidos.getPartidos();
                for (Partido partido : todosPartidos) {
                    if (partido.getId_partido() == inventarioSeleccionado.getId_partido()) {
                        String partidoStr = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
                        jComboBox19.setSelectedItem(partidoStr);
                        break;
                    }
                }

                jComboBox6.setSelectedItem(obtenerNombreLocalidadPorId(inventarioSeleccionado.getId_localidad()));

                jTextField11.setText(String.valueOf(inventarioSeleccionado.getCantidad_total()));

                jTextField5.setText(String.valueOf(inventarioSeleccionado.getPrecio()));
                
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar datos del inventario: " + e.getMessage());
        }
    }
}

private int obtenerCantidadDisponibleActual() throws Exception {
    java.util.List<Inventario> listaInventario = servicioInventario.getInventario();
    for (Inventario inv : listaInventario) {
        if (inv.getId_inventario() == idInventarioSeleccionado) {
            return inv.getCantidad_disponible();
        }
    }
    return 0;
}

private void cargarTablaGestionInventario() {
    try {
        java.util.List<Inventario> listaInventario = servicioInventario.getInventario();
        javax.swing.table.DefaultTableModel modeloTabla = (javax.swing.table.DefaultTableModel) jTable5.getModel();
        modeloTabla.setRowCount(0);
        
        for (Inventario itemInventario : listaInventario) {
            String nombrePartido = obtenerNombrePartidoPorId(itemInventario.getId_partido());
            String nombreLocalidad = obtenerNombreLocalidadPorId(itemInventario.getId_localidad());
            
            modeloTabla.addRow(new Object[]{
                itemInventario.getId_inventario(),
                nombrePartido,
                nombreLocalidad,
                itemInventario.getPrecio(),
                itemInventario.getCantidad_total(),
                itemInventario.getCantidad_disponible()
            });
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar tabla de inventario: " + e.getMessage());
    }
}



private void limpiarCamposInventario() {
    if (jComboBox19.getItemCount() > 0) jComboBox19.setSelectedIndex(0);
    if (jComboBox6.getItemCount() > 0) jComboBox6.setSelectedIndex(0);
    jTextField11.setText("");
    jTextField5.setText("");
    idInventarioSeleccionado = -1;
    jTable5.clearSelection();
}

private String obtenerNombrePartidoPorId(int idPartido) {
    try {
        java.util.List<Partido> todosPartidos = servicioPartidos.getPartidos();
        for (Partido partido : todosPartidos) {
            if (partido.getId_partido() == idPartido) {
                return partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
            }
        }
        return "Partido no encontrado";
    } catch (Exception e) {
        return "Error al cargar partidos";
    }
}

private void configurarNavegacionDesdeLobby() {
    try {
        System.out.println("CONFIGURANDO NAVEGACIÓN DESDE LOBBY");

        JPanel panelAdministracion = ADMINISTRACION;
        CardLayout clAnidado = (CardLayout) panelAdministracion.getLayout();

        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    System.out.println("Navegando a USUARIOS desde LOBBY");

                    CardLayout clPrincipal = (CardLayout) MainPanel.getLayout();
                    clPrincipal.show(MainPanel, "card9");

                    SwingUtilities.invokeLater(() -> {
                        try {
                            clAnidado.show(panelAdministracion, "usuario");
                            inicializarPanelUsuarios();
                            System.out.println("Panel USUARIOS mostrado correctamente");
                        } catch (Exception e) {
                            System.err.println(" Error mostrando panel usuarios: " + e.getMessage());
                        }
                    });
                    
                } catch (Exception e) {
                    System.err.println(" Error navegando a usuarios: " + e.getMessage());
                }
            }
        });

        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    System.out.println("Navegando a PARTIDOS desde LOBBY");

                    CardLayout clPrincipal = (CardLayout) MainPanel.getLayout();
                    clPrincipal.show(MainPanel, "card9");

                    SwingUtilities.invokeLater(() -> {
                        try {
                            clAnidado.show(panelAdministracion, "partido");
                            inicializarPanelPartidosAdmin();
                            System.out.println(" Panel PARTIDOS mostrado correctamente");
                        } catch (Exception e) {
                            System.err.println(" Error mostrando panel partidos: " + e.getMessage());
                        }
                    });
                    
                } catch (Exception e) {
                    System.err.println(" Error navegando a partidos: " + e.getMessage());
                }
            }
        });

        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    System.out.println("Navegando a LOCALIDADES desde LOBBY");

                    CardLayout clPrincipal = (CardLayout) MainPanel.getLayout();
                    clPrincipal.show(MainPanel, "card9");

                    SwingUtilities.invokeLater(() -> {
                        try {
                            clAnidado.show(panelAdministracion, "localidades");
                            inicializarPanelLocalidadesAdmin();
                            System.out.println("✅ Panel LOCALIDADES mostrado correctamente");
                        } catch (Exception e) {
                            System.err.println("❌ Error mostrando panel localidades: " + e.getMessage());
                        }
                    });
                    
                } catch (Exception e) {
                    System.err.println("❌ Error navegando a localidades: " + e.getMessage());
                }
            }
        });

        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    System.out.println("Navegando a INVENTARIO desde LOBBY");

                    CardLayout clPrincipal = (CardLayout) MainPanel.getLayout();
                    clPrincipal.show(MainPanel, "card9");
                    SwingUtilities.invokeLater(() -> {
                        try {
                            clAnidado.show(panelAdministracion, "inventario");
                            inicializarGestionInventario();
                            System.out.println("Panel INVENTARIO mostrado correctamente");
                        } catch (Exception e) {
                            System.err.println(" Error mostrando panel inventario: " + e.getMessage());
                        }
                    });
                    
                } catch (Exception e) {
                    System.err.println(" Error navegando a inventario: " + e.getMessage());
                }
            }
        });

        jLabel35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    System.out.println("Navegando a REPORTESA desde LOBBY");

                    CardLayout clPrincipal = (CardLayout) MainPanel.getLayout();
                    clPrincipal.show(MainPanel, "card9");

                    SwingUtilities.invokeLater(() -> {
                        try {
                            clAnidado.show(panelAdministracion, "reportea");
                            System.out.println(" Panel REPORTESA mostrado correctamente");
                        } catch (Exception e) {
                            System.err.println(" Error mostrando panel reportes: " + e.getMessage());
                        }
                    });
                    
                } catch (Exception e) {
                    System.err.println(" Error navegando a reportes: " + e.getMessage());
                }
            }
        });

        System.out.println(" Navegación desde LOBBY configurada correctamente");
        
    } catch (Exception e) {
        System.err.println(" Error configurando navegación desde LOBBY: " + e.getMessage());
    }
}

private void inicializarGestionInventarioAdmin() {
    try {
        System.out.println(" INICIALIZANDO MÓDULO DE INVENTARIO ADMIN ");
 
        adminConfigurarTablaInventario();
        adminCargarPartidosEnComboBox19DesdeBD();
        adminCargarLocalidadesEnComboBox6DesdeBD();
        adminCargarInventarioEnTablaDesdeBD();
        adminConfigurarListenerTablaInventario();
        
        System.out.println("Módulo de inventario ADMIN inicializado correctamente");
        
    } catch (Exception e) {
        System.err.println(" Error inicializando módulo de inventario ADMIN: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error inicializando inventario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    private void adminInicializarMapaLocalidades() {
        try {
            mapaLocalidades = new java.util.HashMap<>();
            
            Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
            List<Localidad> localidades = servicioLocalidades.getLocalidades();
            
            for (Localidad localidad : localidades) {
                mapaLocalidades.put(localidad.getNombre(), localidad.getId_localidad());
            }
            
            System.out.println("Mapa de localidades inicializado: " + mapaLocalidades.size() + " localidades");
            
        } catch (Exception e) {
            System.err.println("Error inicializando mapa de localidades: " + e.getMessage());
        }
    }
private void adminConfigurarTablaInventario() {
    try {
        String[] columnNames = {"ID", "PARTIDO", "LOCALIDAD", "PRECIO", "STOCK TOTAL", "STOCK DISPONIBLE"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; 
                    case 1: return String.class;
                    case 2: return String.class; 
                    case 3: return Double.class; 
                    case 4: return Integer.class;
                    case 5: return Integer.class; 
                    default: return Object.class;
                }
            }
        };
        
        jTable5.setModel(model);
        adminConfigurarFormatoTablaInventario();
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla de inventario ADMIN: " + e.getMessage());
    }
}

private void adminConfigurarFormatoTablaInventario() {
    try {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < jTable5.getColumnCount(); i++) {
            jTable5.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        jTable5.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof Double) {
                    label.setText(String.format("Q%.2f", (Double) value));
                    label.setForeground(new Color(0, 100, 0)); // Verde para precios
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
                
                label.setHorizontalAlignment(JLabel.RIGHT);
                return label;
            }
        });

        jTable5.getColumnModel().getColumn(0).setPreferredWidth(50); 
        jTable5.getColumnModel().getColumn(1).setPreferredWidth(200);  
        jTable5.getColumnModel().getColumn(2).setPreferredWidth(120); 
        jTable5.getColumnModel().getColumn(3).setPreferredWidth(80); 
        jTable5.getColumnModel().getColumn(4).setPreferredWidth(80);  
        jTable5.getColumnModel().getColumn(5).setPreferredWidth(100);
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla inventario ADMIN: " + e.getMessage());
    }
}

private void adminCargarPartidosEnComboBox19DesdeBD() {
    try {
        jComboBox19.removeAllItems();
        
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        List<Partido> partidos = servicioPartidos.getPartidos();
        
        for (Partido partido : partidos) {
            String partidoStr = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
            jComboBox19.addItem(partidoStr);
        }
        
        System.out.println("Partidos cargados en ComboBox19: " + partidos.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando partidos en ComboBox19: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cargando partidos: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void adminCargarLocalidadesEnComboBox6DesdeBD() {
    try {
        jComboBox6.removeAllItems();
        
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        List<Localidad> localidades = servicioLocalidades.getLocalidades();
        
        for (Localidad localidad : localidades) {
            jComboBox6.addItem(localidad.getNombre());
        }
        
        System.out.println("Localidades cargadas en ComboBox6: " + localidades.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando localidades en ComboBox6: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cargando localidades: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void adminCargarInventarioEnTablaDesdeBD() {
    try {
        Servicios.ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        List<Inventario> inventarios = servicioInventario.getInventario();
        
        DefaultTableModel model = (DefaultTableModel) jTable5.getModel();
        model.setRowCount(0);
        
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        List<Partido> partidos = servicioPartidos.getPartidos();
        
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        List<Localidad> localidades = servicioLocalidades.getLocalidades();
        
        for (Inventario inventario : inventarios) {
            String nombrePartido = "Partido no encontrado";
            for (Partido partido : partidos) {
                if (partido.getId_partido() == inventario.getId_partido()) {
                    nombrePartido = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
                    break;
                }
            }

            String nombreLocalidad = "Localidad no encontrada";
            for (Localidad localidad : localidades) {
                if (localidad.getId_localidad() == inventario.getId_localidad()) {
                    nombreLocalidad = localidad.getNombre();
                    break;
                }
            }
            
            model.addRow(new Object[]{
                inventario.getId_inventario(),
                nombrePartido,
                nombreLocalidad,
                inventario.getPrecio(),
                inventario.getCantidad_total(),
                inventario.getCantidad_disponible()
            });
        }
        
        System.out.println("Inventario cargado en tabla: " + inventarios.size() + " registros");
        
    } catch (Exception e) {
        System.err.println("Error cargando inventario en tabla: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cargando inventario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void adminConfigurarListenerTablaInventario() {
    jTable5.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() && jTable5.getSelectedRow() != -1) {
            adminCargarDatosInventarioSeleccionado();
        }
    });
}

private void adminCargarDatosInventarioSeleccionado() {
    try {
        int filaSeleccionada = jTable5.getSelectedRow();
        if (filaSeleccionada != -1) {
            String partido = jTable5.getValueAt(filaSeleccionada, 1).toString();
            String localidad = jTable5.getValueAt(filaSeleccionada, 2).toString();
            String precio = jTable5.getValueAt(filaSeleccionada, 3).toString();
            String stock = jTable5.getValueAt(filaSeleccionada, 4).toString();
            
            // Llenar los campos
            jComboBox19.setSelectedItem(partido);
            jComboBox6.setSelectedItem(localidad);
            jTextField5.setText(precio.replace("Q", ""));
            jTextField11.setText(stock);
            
            System.out.println("Datos cargados para edición - Partido: " + partido + ", Localidad: " + localidad);
        }
    } catch (Exception e) {
        System.err.println("Error cargando datos del inventario seleccionado: " + e.getMessage());
    }
}

private void adminGuardarInventario() {
    try {
        if (jComboBox19.getSelectedItem() == null || jComboBox6.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un partido y una localidad", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String precioStr = jTextField5.getText().trim();
        String stockStr = jTextField11.getText().trim();
        
        if (precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese precio y stock", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        double precio;
        int stock;
        try {
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Precio debe ser número decimal y stock debe ser número entero", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idPartido = adminObtenerIdPartidoSeleccionado();
        int idLocalidad = adminObtenerIdLocalidadSeleccionada();
        
        if (idPartido == -1 || idLocalidad == -1) {
            JOptionPane.showMessageDialog(this, 
                "Error obteniendo IDs de partido o localidad", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Inventario nuevoInventario = new Inventario();
        nuevoInventario.setId_partido(idPartido);
        nuevoInventario.setId_localidad(idLocalidad);
        nuevoInventario.setPrecio(precio);
        nuevoInventario.setCantidad_total(stock);
        nuevoInventario.setCantidad_disponible(stock);

        Servicios.ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        Inventario resultado = servicioInventario.createInventario(nuevoInventario);
        
        if (resultado != null && resultado.getId_inventario() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Inventario guardado correctamente\nID: " + resultado.getId_inventario(), 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

            adminCargarInventarioEnTablaDesdeBD();
            adminLimpiarCamposInventario();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar el inventario", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error guardando inventario: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error guardando inventario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void adminActualizarInventario() {
    try {
        int filaSeleccionada = jTable5.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un registro de la tabla para actualizar", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idInventario = (int) jTable5.getValueAt(filaSeleccionada, 0);

        String precioStr = jTextField5.getText().trim();
        String stockStr = jTextField11.getText().trim();
        
        if (precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el nuevo precio", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double nuevoPrecio;
        try {
            nuevoPrecio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Precio debe ser un número decimal válido", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer nuevoStock = null;
        if (!stockStr.isEmpty()) {
            try {
                nuevoStock = Integer.parseInt(stockStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Stock debe ser un número entero válido", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de actualizar el inventario?\n\n" +
            "ID: " + idInventario + "\n" +
            "Nuevo precio: Q" + String.format("%.2f", nuevoPrecio) +
            (nuevoStock != null ? "\nNuevo stock: " + nuevoStock : ""),
            "Confirmar actualización",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        Servicios.ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        
        if (nuevoStock != null) {
            Inventario inventarioActualizado = new Inventario();
            inventarioActualizado.setPrecio(nuevoPrecio);
            inventarioActualizado.setCantidad_total(nuevoStock);
            
            servicioInventario.updateInventario(idInventario, inventarioActualizado);
        } else {

            servicioInventario.updatePrecioInventario(idInventario, nuevoPrecio);
        }
        
        JOptionPane.showMessageDialog(this, 
            "Inventario actualizado correctamente", 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);

        adminCargarInventarioEnTablaDesdeBD();
        
    } catch (Exception e) {
        System.err.println("Error actualizando inventario: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error actualizando inventario: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private int adminObtenerIdPartidoSeleccionado() {
    try {
        String partidoSeleccionado = jComboBox19.getSelectedItem().toString();
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        List<Partido> partidos = servicioPartidos.getPartidos();
        
        for (Partido partido : partidos) {
            String partidoStr = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
            if (partidoStr.equals(partidoSeleccionado)) {
                return partido.getId_partido();
            }
        }
        return -1;
    } catch (Exception e) {
        System.err.println("Error obteniendo ID del partido: " + e.getMessage());
        return -1;
    }
}

private int adminObtenerIdLocalidadSeleccionada() {
    try {
        String localidadSeleccionada = jComboBox6.getSelectedItem().toString();
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        List<Localidad> localidades = servicioLocalidades.getLocalidades();
        
        for (Localidad localidad : localidades) {
            if (localidad.getNombre().equals(localidadSeleccionada)) {
                return localidad.getId_localidad();
            }
        }
        return -1;
    } catch (Exception e) {
        System.err.println("Error obteniendo ID de la localidad: " + e.getMessage());
        return -1;
    }
}

private void adminLimpiarCamposInventario() {
    if (jComboBox19.getItemCount() > 0) jComboBox19.setSelectedIndex(0);
    if (jComboBox6.getItemCount() > 0) jComboBox6.setSelectedIndex(0);
    jTextField5.setText("");
    jTextField11.setText("");
    jTable5.clearSelection();
}
    private void inicializarModulosAdministrador() {
        try {
            System.out.println("INICIALIZANDO MÓDULOS ADMINISTRADOR");

            inicializarPanelPartidosAdmin();

            inicializarPanelLocalidadesAdmin();

            inicializarGestionInventarioAdmin();
            
            System.out.println(" Módulos de administrador inicializados correctamente");
            
        } catch (Exception e) {
            System.err.println(" Error inicializando módulos administrador: " + e.getMessage());
        }
    }
    

private void configurarNavegacionEntrePanelesAdmin() {
    try {
        System.out.println(" CONFIGURANDO NAVEGACIÓN ENTRE PANELES ADMIN ");

        JPanel panelAdministracion = ADMINISTRACION;
        CardLayout clAnidado = (CardLayout) panelAdministracion.getLayout();

        jPanel28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "usuario");
            }
        });
        
        jPanel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "partido");
            }
        });
        
        jPanel30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "localidades");
            }
        });
        
        jPanel31.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "inventario");
            }
        });

        jPanel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "usuario");
            }
        });
        
        jPanel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "partido");
            }
        });
        
        jPanel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "localidades");
            }
        });
        
        jPanel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "inventario");
            }
        });

        // Desde LOCALIDADES a otros paneles
        jPanel22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "usuario");
            }
        });
        
        jPanel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "partido");
            }
        });
        
        jPanel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "localidades");
            }
        });
        
        jPanel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "inventario");
            }
        });

        jPanel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "usuario");
            }
        });
        
        jPanel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "partido");
            }
        });
        
        jPanel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "localidades");
            }
        });
        
        jPanel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "inventario");
            }
        });
 
        jPanel80.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "usuario");
            }
        });
        
        jPanel81.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "partido");
            }
        });
        
        jPanel82.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "localidades");
            }
        });
        
        jPanel83.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "inventario");
            }
        });

        System.out.println(" Navegación entre paneles ADMIN configurada correctamente");
        
    } catch (Exception e) {
        System.err.println(" Error configurando navegación entre paneles ADMIN: " + e.getMessage());
    }
}

private void configurarNavegacionRestablecerContraseña() {
    userLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "contraseña");
        }
    });
    
    
    loginBtnTxt4.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            CardLayout cl = (CardLayout) MainPanel.getLayout();
            cl.show(MainPanel, "card2");
        }
    });
}

private void configurarNavegacionAReportesDesdeAdmin() {
    try {
        System.out.println("CONFIGURANDO NAVEGACIÓN A REPORTES DESDE MÓDULOS ADMIN ");
        
        JPanel panelAdministracion = ADMINISTRACION;
        CardLayout clAnidado = (CardLayout) panelAdministracion.getLayout();

        jPanel78.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "reportea");
                System.out.println("Navegando a REPORTES desde USUARIOS");
            }
        });

        jPanel87.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "reportea");
                System.out.println("Navegando a REPORTES desde PARTIDOS");
            }
        });

        jPanel85.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "reportea");
                System.out.println("Navegando a REPORTES desde LOCALIDADES");
            }
        });

        jPanel75.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "reportea");
                System.out.println("Navegando a REPORTES desde INVENTARIO");
            }
        });
        
        System.out.println("Navegación a REPORTES configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando navegación a REPORTES: " + e.getMessage());
    }
}

private void configurarNavegacionDesdeReportes() {
    try {
        System.out.println("CONFIGURANDO NAVEGACIÓN DESDE REPORTES");
        
        JPanel panelAdministracion = ADMINISTRACION;
        CardLayout clAnidado = (CardLayout) panelAdministracion.getLayout();

        jLabel69.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "usuario");
                System.out.println("Navegando a USUARIOS desde REPORTES");
            }
        });

        jPanel81.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "partido");
                System.out.println("Navegando a PARTIDOS desde REPORTES");
            }
        });

        jPanel82.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "localidades");
                System.out.println("Navegando a LOCALIDADES desde REPORTES");
            }
        });

        jPanel83.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clAnidado.show(panelAdministracion, "inventario");
                System.out.println("Navegando a INVENTARIO desde REPORTES");
            }
        });
        
        System.out.println(" Navegación desde REPORTES configurada correctamente");
        
    } catch (Exception e) {
        System.err.println(" Error configurando navegación desde REPORTES: " + e.getMessage());
    }
}

private void configurarCerrarSesionEnPanelesAdmin() {
    try {
        System.out.println(" CONFIGURANDO CIERRE DE SESIÓN EN PANELES ADMIN ");

        jPanel84.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });
        
        jLabel124.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });

        jPanel88.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });
        
        jLabel128.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });

        jPanel86.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });
        
        jLabel126.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });

        jPanel74.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });
        
        jLabel47.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });

        jPanel77.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });
        
        jLabel66.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });

        jPanel73.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });
        
        jLabel36.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cerrarSesion();
            }
        });

        System.out.println(" Cierre de sesión configurado en todos los paneles ADMIN");
        
    } catch (Exception e) {
        System.err.println("Error configurando cierre de sesión: " + e.getMessage());
    }
}
// Comenta temporalmente el configurarFormatoTablaSolicitudes() y usa:
private void inicializarTablaSolicitudes() {
    try {
        System.out.println("INICIALIZANDO TABLA DE SOLICITUDES ");

        if (jTable2 == null) {
            System.err.println(" jTable2 es null en inicializarTablaSolicitudes");
            return;
        }
        
        String[] columnNames = {"ID", "USUARIO", "MOTIVO", "ESTADO", "FECHA CREACIÓN"};
        modeloTableSolicitudes = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        jTable2.setModel(modeloTableSolicitudes);

        jTable2.setFillsViewportHeight(true);
        jTable2.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable2.setRowHeight(25);
        jTable2.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        System.out.println(" Tabla de solicitudes configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla de solicitudes: " + e.getMessage());
        e.printStackTrace();
    }
}

private void configurarFormatoTablaSolicitudes() {
    try {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jTable2.getColumnCount(); i++) {
            jTable2.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        jTable2.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

                if (value instanceof String) {
                    String estado = ((String) value).toLowerCase();
                    if (estado.equals("pendiente")) {
                        label.setForeground(Color.ORANGE);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    } else if (estado.equals("resuelta")) {
                        label.setForeground(new Color(0, 128, 0)); // Verde
                    } else if (estado.equals("rechazada")) {
                        label.setForeground(Color.RED);
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        jTable2.getColumnModel().getColumn(0).setPreferredWidth(50);   
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(150);  
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(200); 
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(100);  
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(150); 
        
        jTable2.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                }
                
                return c;
            }
        });
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla solicitudes: " + e.getMessage());
    }
}
private JTable buscarTablaVisible() {
    for (Component comp : ADMINISTRACION.getComponents()) {
        if (comp.isShowing() && comp instanceof Container) {
            JTable tabla = buscarTablaEnContainer((Container) comp);
            if (tabla != null && tabla.isShowing()) {
                return tabla;
            }
        }
    }
    return null;
}

private void mostrarSolicitudesEnDialogo() {
    try {
        System.out.println("USANDO FALLBACK - MOSTRANDO EN DIÁLOGO ");
        
        ServicioUsuario servicio = new ServicioUsuario();
        java.util.List<Solicitud> solicitudes = servicio.getSolicitudes();
        
        if (solicitudes != null && !solicitudes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("SOLICITUDES DE RECUPERACIÓN DE CONTRASEÑA\n\n");
            
            for (Solicitud solicitud : solicitudes) {
                String nombreUsuario = "N/A";
                if (solicitud.getUsuario() != null) {
                    nombreUsuario = solicitud.getUsuario().getNombre_completo();
                    if (nombreUsuario == null || nombreUsuario.isEmpty()) {
                        nombreUsuario = solicitud.getUsuario().getNombre_usuario();
                    }
                }
                
                String fechaFormateada = formatearFechaSolicitud(solicitud.getFecha_creacion());
                
                sb.append("ID: ").append(solicitud.getId_solicitud()).append("\n")
                  .append("Usuario: ").append(nombreUsuario).append("\n")
                  .append("Motivo: ").append(solicitud.getMotivo()).append("\n")
                  .append("Estado: ").append(solicitud.getEstado()).append("\n")
                  .append("Fecha: ").append(fechaFormateada).append("\n")
                  .append("----------------------------------------\n");
            }
            
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "Solicitudes de Recuperación de Contraseña", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } else {
            JOptionPane.showMessageDialog(this, 
                "No hay solicitudes para mostrar", 
                "Solicitudes", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch (Exception e) {
        System.err.println("Error mostrando solicitudes en diálogo: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "Error cargando solicitudes: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void cargarSolicitudesEnTable() {
    try {
        System.out.println(" CARGANDO SOLICITUDES EN TABLA DINÁMICA ");

        Guardar.SessionManager sessionManager = Guardar.SessionManager.getInstance();
        if (!sessionManager.isSessionActiva()) {
            JOptionPane.showMessageDialog(this, 
                "Debe iniciar sesión primero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!sessionManager.esAdmin()) {
            JOptionPane.showMessageDialog(this, 
                "Solo los administradores pueden ver las solicitudes", 
                "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        crearVentanaSolicitudes();
        
    } catch (Exception e) {
        System.err.println("Error cargando solicitudes: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error cargando solicitudes: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void crearVentanaSolicitudes() {
    try {
        JFrame frameSolicitudes = new JFrame("Solicitudes de Recuperación de Contraseña");
        frameSolicitudes.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameSolicitudes.setSize(1000, 400); // Reducido la altura
        frameSolicitudes.setLocationRelativeTo(this);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("SOLICITUDES DE RECUPERACIÓN DE CONTRASEÑA", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(0, 70, 140));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        jTable2 = new JTable();
        inicializarTablaSolicitudes();

        JScrollPane scrollPane = new JScrollPane(jTable2);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Lista de Solicitudes Pendientes",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            Color.BLUE
        ));

        JPanel panelContraseña = new JPanel(new FlowLayout());
        panelContraseña.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel lblContraseña = new JLabel("Nueva Contraseña:");
        lblContraseña.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField txtContraseña = new JTextField(20);
        txtContraseña.setEnabled(false); // Inicialmente deshabilitado
        
        JButton btnActualizarContraseña = new JButton("🔑 Actualizar Contraseña");
        btnActualizarContraseña.setFont(new Font("Arial", Font.BOLD, 12));
        btnActualizarContraseña.setEnabled(false); // Inicialmente deshabilitado
        
        panelContraseña.add(lblContraseña);
        panelContraseña.add(txtContraseña);
        panelContraseña.add(btnActualizarContraseña);

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton btnActualizar = new JButton("🔄 Actualizar Lista");
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 12));
        btnActualizar.addActionListener(e -> cargarDatosEnTabla());
        
        JButton btnCerrar = new JButton("❌ Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrar.addActionListener(e -> frameSolicitudes.dispose());
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        panelCentral.add(panelContraseña, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        frameSolicitudes.add(panelPrincipal);

        jTable2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && jTable2.getSelectedRow() != -1) {
                txtContraseña.setEnabled(true);
                btnActualizarContraseña.setEnabled(true);
                txtContraseña.setText("");
                txtContraseña.requestFocus();
            } else {
                txtContraseña.setEnabled(false);
                btnActualizarContraseña.setEnabled(false);
                txtContraseña.setText("");
            }
        });

        btnActualizarContraseña.addActionListener(e -> {
            int filaSeleccionada = jTable2.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(frameSolicitudes, "Por favor selecciona una solicitud de la tabla");
                return;
            }
            
            String nuevaContraseña = txtContraseña.getText().trim();
            if (nuevaContraseña.isEmpty()) {
                JOptionPane.showMessageDialog(frameSolicitudes, "Por favor ingresa la nueva contraseña");
                return;
            }

            int idSolicitud = (int) jTable2.getValueAt(filaSeleccionada, 0);
            
            try {
                ServicioUsuario servicio = new ServicioUsuario();
                boolean exito = servicio.responderSolicitud(idSolicitud, nuevaContraseña, "RESUELTA");
                
                if (exito) {
                    JOptionPane.showMessageDialog(frameSolicitudes, " Contraseña actualizada exitosamente");
                    jTable2.setValueAt("RESUELTA", filaSeleccionada, 3);
                    txtContraseña.setEnabled(false);
                    btnActualizarContraseña.setEnabled(false);
                    txtContraseña.setText("");
                } else {
                    JOptionPane.showMessageDialog(frameSolicitudes, " Error al actualizar la contraseña");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frameSolicitudes, " Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        frameSolicitudes.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            cargarDatosEnTabla();
        });
        
        System.out.println(" Ventana de solicitudes creada exitosamente");
        
    } catch (Exception e) {
        System.err.println("Error creando ventana de solicitudes: " + e.getMessage());
        e.printStackTrace();
    }
}
private void responderSolicitudSeleccionada() {
    try {
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, 
                "Por favor seleccione una solicitud de la tabla", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idSolicitud = (int) jTable2.getValueAt(selectedRow, 0);
        String usuario = (String) jTable2.getValueAt(selectedRow, 1);
        String motivo = (String) jTable2.getValueAt(selectedRow, 2);

        String nuevaContrasena = JOptionPane.showInputDialog(
            null, 
            "Responder solicitud para: " + usuario + 
            "\nMotivo: " + motivo + 
            "\n\nIngrese la nueva contraseña:",
            "Responder Solicitud",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
            ServicioUsuario servicio = new ServicioUsuario();
            boolean exito = servicio.responderSolicitud(idSolicitud, nuevaContrasena, "resuelta");
            
            if (exito) {
                JOptionPane.showMessageDialog(null, 
                    "Solicitud respondida exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarDatosEnTabla(); // Actualizar tabla
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Error al responder la solicitud", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
    } catch (Exception e) {
        System.err.println("Error respondiendo solicitud: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Error: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
private void cargarDatosEnTabla() {
    try {
        ServicioUsuario servicio = new ServicioUsuario();
        List<Solicitud> solicitudes = servicio.getSolicitudes();
        
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        
        for (Solicitud solicitud : solicitudes) {
            String estado = solicitud.getEstado();
            if (estado != null && 
                (estado.equalsIgnoreCase("PENDIENTE") || 
                 estado.equalsIgnoreCase("ACTIVA"))) {
                
                model.addRow(new Object[]{
                    solicitud.getId_solicitud(),
                    solicitud.getUsuario() != null ? solicitud.getUsuario().getNombre_completo() : "N/A",
                    solicitud.getMotivo(),
                    estado,
                    solicitud.getFecha_creacion()
                });
            }
        }
        
        System.out.println(" Tabla actualizada con solicitudes pendientes");
        
    } catch (Exception e) {
        System.err.println(" Error cargando datos: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "Error al cargar solicitudes: " + e.getMessage());
        e.printStackTrace();
    }
}
private String formatearFechaSolicitud(Object fecha) {
    try {
        if (fecha == null) {
            return "Fecha no disponible";
        }
        
        if (fecha instanceof String) {
            String fechaStr = (String) fecha;
            if (fechaStr.contains("T")) {
                SimpleDateFormat sdf;
                if (fechaStr.contains(".")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                } else {
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                }
                Date date = sdf.parse(fechaStr);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                return outputFormat.format(date);
            }
        } else if (fecha instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format((java.util.Date) fecha);
        }
        
        return fecha.toString();
        
    } catch (Exception e) {
        System.err.println("Error formateando fecha de solicitud: " + e.getMessage());
        System.err.println("Fecha original: " + fecha);
        return "Fecha inválida";
    }
}


    public static void main(String args[])  {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BocetoPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BocetoPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BocetoPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BocetoPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BocetoPrincipal().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ADMINISTRACION;
    private javax.swing.JPanel DISPONIBLE2;
    private javax.swing.JPanel INICIO;
    private javax.swing.JPanel Inventario;
    private javax.swing.JPanel LOBBY;
    private javax.swing.JPanel LOCALIDADES;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel NewLocalidades;
    private javax.swing.JPanel NewPartido;
    private javax.swing.JPanel PARTIDOS;
    private javax.swing.JPanel REPORTEA;
    private javax.swing.JPanel RESTABLECER;
    private javax.swing.JPanel Reportes;
    private javax.swing.JPanel Solicitud;
    private javax.swing.JPanel Usuario;
    private javax.swing.JPanel VENTA;
    private javax.swing.JButton btnCerrarVentaVendedor;
    private javax.swing.JButton btnMostrarPass1;
    private javax.swing.JPanel exitBtn3;
    private javax.swing.JPanel exitBtn4;
    private javax.swing.JPanel exitBtn6;
    private javax.swing.JLabel favicon;
    private javax.swing.JLabel favicon1;
    private javax.swing.JLabel favicon10;
    private javax.swing.JLabel favicon100;
    private javax.swing.JLabel favicon101;
    private javax.swing.JLabel favicon102;
    private javax.swing.JLabel favicon103;
    private javax.swing.JLabel favicon104;
    private javax.swing.JLabel favicon105;
    private javax.swing.JLabel favicon106;
    private javax.swing.JLabel favicon107;
    private javax.swing.JLabel favicon108;
    private javax.swing.JLabel favicon109;
    private javax.swing.JLabel favicon11;
    private javax.swing.JLabel favicon110;
    private javax.swing.JLabel favicon111;
    private javax.swing.JLabel favicon112;
    private javax.swing.JLabel favicon113;
    private javax.swing.JLabel favicon114;
    private javax.swing.JLabel favicon115;
    private javax.swing.JLabel favicon116;
    private javax.swing.JLabel favicon117;
    private javax.swing.JLabel favicon118;
    private javax.swing.JLabel favicon119;
    private javax.swing.JLabel favicon12;
    private javax.swing.JLabel favicon120;
    private javax.swing.JLabel favicon121;
    private javax.swing.JLabel favicon122;
    private javax.swing.JLabel favicon123;
    private javax.swing.JLabel favicon124;
    private javax.swing.JLabel favicon125;
    private javax.swing.JLabel favicon126;
    private javax.swing.JLabel favicon127;
    private javax.swing.JLabel favicon13;
    private javax.swing.JLabel favicon14;
    private javax.swing.JLabel favicon15;
    private javax.swing.JLabel favicon16;
    private javax.swing.JLabel favicon17;
    private javax.swing.JLabel favicon18;
    private javax.swing.JLabel favicon19;
    private javax.swing.JLabel favicon20;
    private javax.swing.JLabel favicon21;
    private javax.swing.JLabel favicon22;
    private javax.swing.JLabel favicon23;
    private javax.swing.JLabel favicon27;
    private javax.swing.JLabel favicon28;
    private javax.swing.JLabel favicon29;
    private javax.swing.JLabel favicon3;
    private javax.swing.JLabel favicon30;
    private javax.swing.JLabel favicon31;
    private javax.swing.JLabel favicon32;
    private javax.swing.JLabel favicon33;
    private javax.swing.JLabel favicon34;
    private javax.swing.JLabel favicon35;
    private javax.swing.JLabel favicon36;
    private javax.swing.JLabel favicon37;
    private javax.swing.JLabel favicon38;
    private javax.swing.JLabel favicon39;
    private javax.swing.JLabel favicon4;
    private javax.swing.JLabel favicon40;
    private javax.swing.JLabel favicon41;
    private javax.swing.JLabel favicon42;
    private javax.swing.JLabel favicon43;
    private javax.swing.JLabel favicon44;
    private javax.swing.JLabel favicon45;
    private javax.swing.JLabel favicon5;
    private javax.swing.JLabel favicon7;
    private javax.swing.JLabel favicon8;
    private javax.swing.JLabel favicon89;
    private javax.swing.JLabel favicon9;
    private javax.swing.JLabel favicon90;
    private javax.swing.JLabel favicon91;
    private javax.swing.JLabel favicon92;
    private javax.swing.JLabel favicon93;
    private javax.swing.JLabel favicon94;
    private javax.swing.JLabel favicon95;
    private javax.swing.JLabel favicon96;
    private javax.swing.JLabel favicon97;
    private javax.swing.JLabel favicon98;
    private javax.swing.JLabel favicon99;
    private javax.swing.JPanel header3;
    private javax.swing.JPanel header4;
    private javax.swing.JPanel header6;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButtonVendedor;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox12;
    private javax.swing.JComboBox<String> jComboBox13;
    private javax.swing.JComboBox<String> jComboBox19;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel60;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel65;
    private javax.swing.JPanel jPanel66;
    private javax.swing.JPanel jPanel67;
    private javax.swing.JPanel jPanel68;
    private javax.swing.JPanel jPanel69;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel70;
    private javax.swing.JPanel jPanel71;
    private javax.swing.JPanel jPanel72;
    private javax.swing.JPanel jPanel73;
    private javax.swing.JPanel jPanel74;
    private javax.swing.JPanel jPanel75;
    private javax.swing.JPanel jPanel76;
    private javax.swing.JPanel jPanel77;
    private javax.swing.JPanel jPanel78;
    private javax.swing.JPanel jPanel79;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel80;
    private javax.swing.JPanel jPanel81;
    private javax.swing.JPanel jPanel82;
    private javax.swing.JPanel jPanel83;
    private javax.swing.JPanel jPanel84;
    private javax.swing.JPanel jPanel85;
    private javax.swing.JPanel jPanel86;
    private javax.swing.JPanel jPanel87;
    private javax.swing.JPanel jPanel88;
    private javax.swing.JPanel jPanel89;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel90;
    private javax.swing.JPanel jPanel91;
    private javax.swing.JPanel jPanel92;
    private javax.swing.JPanel jPanel93;
    private javax.swing.JPanel jPanel94;
    private javax.swing.JPanel jPanel95;
    private javax.swing.JPanel jPanel96;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JSpinner jSpinnerCantidad;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTable9;
    private javax.swing.JTable jTableDisponibilidad;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JLabel lblBienvenido;
    private javax.swing.JPanel loginBtn;
    private javax.swing.JPanel loginBtn1;
    private javax.swing.JPanel loginBtn10;
    private javax.swing.JPanel loginBtn12;
    private javax.swing.JPanel loginBtn2;
    private javax.swing.JPanel loginBtn3;
    private javax.swing.JPanel loginBtn5;
    private javax.swing.JPanel loginBtn6;
    private javax.swing.JLabel loginBtnTxt1;
    private javax.swing.JLabel loginBtnTxt2;
    private javax.swing.JLabel loginBtnTxt3;
    private javax.swing.JLabel loginBtnTxt4;
    private javax.swing.JLabel loginBtnTxt5;
    private javax.swing.JLabel logo3;
    private javax.swing.JLabel logo4;
    private javax.swing.JLabel passLabel1;
    private javax.swing.JLabel passLabel2;
    private javax.swing.JLabel passLabel3;
    private javax.swing.JPasswordField passTxt2;
    private javax.swing.JLabel title;
    private javax.swing.JLabel title1;
    private javax.swing.JLabel title2;
    private javax.swing.JLabel title5;
    private javax.swing.JLabel txtNombreaparece;
    private javax.swing.JTextField txtUsuarioR;
    private javax.swing.JTextField txtUsuarioR1;
    private javax.swing.JLabel userLabel10;
    private javax.swing.JLabel userLabel13;
    private javax.swing.JLabel userLabel14;
    private javax.swing.JLabel userLabel2;
    private javax.swing.JLabel userLabel4;
    private javax.swing.JLabel userLabel5;
    private javax.swing.JLabel userLabel6;
    private javax.swing.JLabel userLabel7;
    private javax.swing.JLabel userLabel8;
    private javax.swing.JLabel userLabel9;
    private javax.swing.JTextField userTxt4;
    private javax.swing.JTextField userTxt5;
    private javax.swing.JTextField userTxt6;
    private javax.swing.JTextField userTxt7;
    // End of variables declaration//GEN-END:variables
}
