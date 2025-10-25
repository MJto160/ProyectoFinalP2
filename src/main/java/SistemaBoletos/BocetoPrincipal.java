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
import Modelos.Venta;
import Servicios.ServicioInventario;
import Servicios.ServicioLocalidade;
import Servicios.ServicioUsuario;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.PrintWriter;
import java.security.Provider.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author EDY
 */
public class BocetoPrincipal extends javax.swing.JFrame {
    private int xMouse,yMouse;
    private javax.swing.JComboBox<String> cbEstadio;
    private javax.swing.JLabel lblEstadio;
    private final java.util.Map<String, String> mapaEstadios = new java.util.HashMap<>();
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
    // ... otro código de inicialización
    
    // Inicializar el label de disponibles
    jLabel101.setText("DISPONIBLES: ");
    jLabel101.setForeground(new Color(204, 204, 204));
    
    // Si hay una localidad seleccionada por defecto, validar su stock
    if (jComboBox4.getSelectedItem() != null) {
        validarStockDisponible();
    }
}
private void inicializarSistemaVentas() {
    // Configurar tabla del carrito
    configurarTablaCarrito();
    
    // El spinner ya está configurado en el constructor
    
    // Cargar datos desde la base de datos
    cargarDatosDesdeBD();
    
    // Agregar listeners
    agregarListenersVenta();
    
    // Inicializar labels
    jLabel100.setText("0.00"); // Precio total
    jLabel101.setText("DISPONIBLES: "); // Stock disponible
    userTxt7.setText("0.00"); // Precio unitario
    
    // ✅ FORZAR la primera validación después de cargar datos
    SwingUtilities.invokeLater(() -> {
        if (jComboBox4.getItemCount() > 0) {
            validarStockDisponible();
            // ✅ Asegurar que el spinner empiece en 1
            jSpinnerCantidad.setValue(1);
            System.out.println("Sistema de ventas inicializado - Spinner en 1");
        }
    });
}

private void cargarDatosDesdeBD() {
    try {
        System.out.println("Cargando datos desde la base de datos...");
        
        // Cargar partidos
        Servicios.ServicioPartido servicioPartido = new Servicios.ServicioPartido();
        partidosList = servicioPartido.getPartidos();
        System.out.println("Partidos cargados: " + (partidosList != null ? partidosList.size() : 0));
        cargarPartidosEnComboBox();
        
        // Cargar localidades
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        localidadesList = servicioLocalidades.getLocalidades();
        System.out.println("Localidades cargadas: " + (localidadesList != null ? localidadesList.size() : 0));
        cargarLocalidadesEnComboBox();
        
        // Cargar inventario
        Servicios.ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        inventarioList = servicioInventario.getInventario();
        System.out.println("Items en inventario: " + (inventarioList != null ? inventarioList.size() : 0));
        
        // ✅ DEBUG: Mostrar algunas localidades y su stock
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
    // Ya no necesitamos crear un nuevo modelo aquí, solo usar el existente
    // El spinner ya fue inicializado en el constructor
    
    // Solo configurar el editor si es necesario
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(jSpinnerCantidad, "#");
    jSpinnerCantidad.setEditor(editor);
    
    System.out.println("Spinner configurado - Valor actual: " + jSpinnerCantidad.getValue());
}
private void cargarPartidosEnComboBox() {
    jComboBox3.removeAllItems();
    if (partidosList != null) {
        for (Partido partido : partidosList) {
            String textoPartido = partido.getEquipo_local() + " vs " + 
                                partido.getEquipo_visitante() + " - " + 
                                partido.getFecha_partido();
            jComboBox3.addItem(textoPartido);
        }
    }
    if (jComboBox3.getItemCount() > 0) {
        jComboBox3.setSelectedIndex(0);
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
        // ✅ FORZAR la actualización del spinner después de cargar datos
        SwingUtilities.invokeLater(() -> {
            actualizarPrecioUnitario();
            validarStockDisponible();
            
            // ✅ Asegurar que el spinner tenga valor 1
            jSpinnerCantidad.setValue(1);
            System.out.println("Spinner forzado a 1 después de cargar localidades");
        });
    }
}
private void inicializarPanelPartidos() {
    try {
        System.out.println("=== INICIALIZANDO PANEL PARTIDOS ===");
        
        // Cargar datos de partidos
        cargarDatosPartidos();
        
        // Configurar tabla de partidos
        configurarTablaPartidos();
        
        // Cargar datos en la tabla
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
        inicializarSpinnerPorDefecto(); // <- AÑADE ESTA LÍNEA
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
        CREARCUENTA = new javax.swing.JPanel();
        logo2 = new javax.swing.JLabel();
        favicon5 = new javax.swing.JLabel();
        title3 = new javax.swing.JLabel();
        userLabel1 = new javax.swing.JLabel();
        userTxt1 = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        passLabel4 = new javax.swing.JLabel();
        passTxt1 = new javax.swing.JPasswordField();
        jSeparator10 = new javax.swing.JSeparator();
        loginBtn4 = new javax.swing.JPanel();
        loginBtnTxt4 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        userLabel11 = new javax.swing.JLabel();
        userTxt8 = new javax.swing.JTextField();
        jSeparator11 = new javax.swing.JSeparator();
        btnBack1 = new javax.swing.JButton();
        LOCALIDADES = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        favicon4 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        PARTIDOS = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        favicon35 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
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
        jLabel21 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        favicon36 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
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
        jLabel53 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        favicon37 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        title = new javax.swing.JLabel();
        HISTORIAL = new javax.swing.JPanel();
        header5 = new javax.swing.JPanel();
        exitBtn5 = new javax.swing.JPanel();
        favicon6 = new javax.swing.JLabel();
        title4 = new javax.swing.JLabel();
        loginBtn7 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        loginBtn8 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        loginBtn9 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
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
        favicon12 = new javax.swing.JLabel();
        favicon14 = new javax.swing.JLabel();
        favicon15 = new javax.swing.JLabel();
        favicon16 = new javax.swing.JLabel();
        NewUser = new javax.swing.JPanel();
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
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel46 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jComboBox19 = new javax.swing.JComboBox<>();
        jPanel33 = new javax.swing.JPanel();
        jLabel99 = new javax.swing.JLabel();
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
        jLabel55 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox<>();
        jLabel75 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jComboBox10 = new javax.swing.JComboBox<>();
        favicon24 = new javax.swing.JLabel();
        favicon25 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox<>();
        jLabel77 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        favicon26 = new javax.swing.JLabel();
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
        jLabel84 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jComboBox18 = new javax.swing.JComboBox<>();
        INVENTARIO = new javax.swing.JPanel();
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
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel96 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox<>();
        jLabel97 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jPanel32 = new javax.swing.JPanel();
        jLabel98 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainPanel.setLayout(new java.awt.CardLayout());

        INICIO.setBackground(new java.awt.Color(255, 255, 255));
        INICIO.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/Ticketmaster-Emblem.png"))); // NOI18N
        INICIO.add(logo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 0, 290, 500));

        header4.setBackground(new java.awt.Color(255, 255, 255));
        header4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                header4MouseDragged(evt);
            }
        });
        header4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                header4MousePressed(evt);
            }
        });

        exitBtn4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout exitBtn4Layout = new javax.swing.GroupLayout(exitBtn4);
        exitBtn4.setLayout(exitBtn4Layout);
        exitBtn4Layout.setHorizontalGroup(
            exitBtn4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        exitBtn4Layout.setVerticalGroup(
            exitBtn4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout header4Layout = new javax.swing.GroupLayout(header4);
        header4.setLayout(header4Layout);
        header4Layout.setHorizontalGroup(
            header4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header4Layout.createSequentialGroup()
                .addComponent(exitBtn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 800, Short.MAX_VALUE))
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
        userLabel5.setText("No tienes una cuenta?");
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
        userTxt4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTxt4MousePressed(evt);
            }
        });
        userTxt4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTxt4ActionPerformed(evt);
            }
        });
        INICIO.add(userTxt4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 410, 30));

        jSeparator7.setForeground(new java.awt.Color(0, 0, 0));
        INICIO.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 410, 20));

        passLabel2.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        passLabel2.setText("CONTRASEÑA");
        INICIO.add(passLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 120, -1));

        passTxt2.setForeground(new java.awt.Color(204, 204, 204));
        passTxt2.setText("********");
        passTxt2.setBorder(null);
        passTxt2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                passTxt2MousePressed(evt);
            }
        });
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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtnTxt2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtnTxt2MouseExited(evt);
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
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginBtnTxt3MouseClicked(evt);
            }
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
            .addGap(0, 130, Short.MAX_VALUE)
        );
        loginBtn3Layout.setVerticalGroup(
            loginBtn3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        INICIO.add(loginBtn3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, 130, 40));

        MainPanel.add(INICIO, "card2");

        CREARCUENTA.setBackground(new java.awt.Color(255, 255, 255));
        CREARCUENTA.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/Ticketmaster-Emblem.png"))); // NOI18N
        CREARCUENTA.add(logo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 0, 290, 500));

        favicon5.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N
        CREARCUENTA.add(favicon5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        title3.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title3.setText("CREAR CUENTA");
        CREARCUENTA.add(title3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, -1, -1));

        userLabel1.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel1.setText("NOMBRE");
        CREARCUENTA.add(userLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 290, 70, -1));

        userTxt1.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt1.setForeground(new java.awt.Color(204, 204, 204));
        userTxt1.setText("Ingrese su nombre completo");
        userTxt1.setBorder(null);
        userTxt1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTxt1MousePressed(evt);
            }
        });
        CREARCUENTA.add(userTxt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 320, 170, 30));

        jSeparator3.setForeground(new java.awt.Color(0, 0, 0));
        CREARCUENTA.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 350, 160, 20));

        passLabel4.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        passLabel4.setText("CONTRASEÑA");
        CREARCUENTA.add(passLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 100, -1));

        passTxt1.setForeground(new java.awt.Color(204, 204, 204));
        passTxt1.setText("********");
        passTxt1.setBorder(null);
        passTxt1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                passTxt1MousePressed(evt);
            }
        });
        CREARCUENTA.add(passTxt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, 100, 30));

        jSeparator10.setForeground(new java.awt.Color(0, 0, 0));
        CREARCUENTA.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 350, 160, 20));

        loginBtn4.setBackground(new java.awt.Color(0, 134, 190));

        loginBtnTxt4.setFont(new java.awt.Font("Roboto Condensed", 1, 14)); // NOI18N
        loginBtnTxt4.setForeground(new java.awt.Color(255, 255, 255));
        loginBtnTxt4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loginBtnTxt4.setText("CREAR");
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

        javax.swing.GroupLayout loginBtn4Layout = new javax.swing.GroupLayout(loginBtn4);
        loginBtn4.setLayout(loginBtn4Layout);
        loginBtn4Layout.setHorizontalGroup(
            loginBtn4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginBtnTxt4, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addContainerGap())
        );
        loginBtn4Layout.setVerticalGroup(
            loginBtn4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtn4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginBtnTxt4, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addContainerGap())
        );

        CREARCUENTA.add(loginBtn4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, 130, 40));

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N
        CREARCUENTA.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 130, 60));

        userLabel11.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel11.setText("USUARIO");
        CREARCUENTA.add(userLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 70, -1));

        userTxt8.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt8.setForeground(new java.awt.Color(204, 204, 204));
        userTxt8.setText("Ingrese su nombre de usuario");
        userTxt8.setBorder(null);
        userTxt8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTxt8MousePressed(evt);
            }
        });
        CREARCUENTA.add(userTxt8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, 170, 30));

        jSeparator11.setForeground(new java.awt.Color(0, 0, 0));
        CREARCUENTA.add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 160, 20));

        btnBack1.setText("Regresar");
        btnBack1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBack1ActionPerformed(evt);
            }
        });
        CREARCUENTA.add(btnBack1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, -1, -1));

        MainPanel.add(CREARCUENTA, "card3");

        LOCALIDADES.setBackground(new java.awt.Color(255, 255, 255));
        LOCALIDADES.setPreferredSize(new java.awt.Dimension(800, 500));
        LOCALIDADES.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 153, 255));
        jPanel1.setForeground(new java.awt.Color(27, 86, 253));

        jLabel11.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("CERRAR SESIÓN");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("VENTAS");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("LOCALIDADES");

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon4.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jLabel102.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel102.setForeground(new java.awt.Color(255, 255, 255));
        jLabel102.setText("PARTIDOS");
        jLabel102.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel102MouseClicked(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("DASHBOARD");

        jLabel57.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setText("DISPONIBILIDAD");
        jLabel57.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel57MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(favicon4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel102)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel22)))
                .addContainerGap(34, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22)
                .addGap(44, 44, 44)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(46, 46, 46)
                .addComponent(jLabel12)
                .addGap(43, 43, 43)
                .addComponent(jLabel102)
                .addGap(50, 50, 50)
                .addComponent(jLabel11)
                .addGap(48, 48, 48))
        );

        LOCALIDADES.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 500));

        jLabel14.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel14.setText("INICIO");
        LOCALIDADES.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel15.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel15.setText("                                LOCALIDADES");
        LOCALIDADES.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 390, -1));

        jTable2.setBackground(new java.awt.Color(255, 253, 246));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NOMBRE", "PRECIO", "DISPONIBLES"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        LOCALIDADES.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 110, 510, 260));

        MainPanel.add(LOCALIDADES, "card4");

        PARTIDOS.setBackground(new java.awt.Color(255, 255, 255));
        PARTIDOS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel16.setText("PARTIDOS");
        PARTIDOS.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 20, -1, -1));

        jPanel34.setBackground(new java.awt.Color(0, 153, 255));
        jPanel34.setForeground(new java.awt.Color(27, 86, 253));

        jLabel17.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("CERRAR SESIÓN");
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("VENTAS");
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel18MouseClicked(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("LOCALIDADES");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon35.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jLabel103.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel103.setForeground(new java.awt.Color(255, 255, 255));
        jLabel103.setText("PARTIDOS");

        jLabel104.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel104.setForeground(new java.awt.Color(255, 255, 255));
        jLabel104.setText("DASHBOARD");

        jLabel59.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(255, 255, 255));
        jLabel59.setText("DISPONIBILIDAD");

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel34Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel103)
                                    .addGroup(jPanel34Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(jLabel18)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel59)
                            .addGroup(jPanel34Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel104)))))
                .addGap(37, 37, 37))
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
                .addGap(28, 28, 28)
                .addComponent(jLabel104)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jLabel59)
                .addGap(41, 41, 41)
                .addComponent(jLabel19)
                .addGap(42, 42, 42)
                .addComponent(jLabel18)
                .addGap(40, 40, 40)
                .addComponent(jLabel103)
                .addGap(55, 55, 55)
                .addComponent(jLabel17)
                .addGap(36, 36, 36))
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

        PARTIDOS.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, 500, 200));

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
        userTxt5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTxt5MousePressed(evt);
            }
        });
        userTxt5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTxt5ActionPerformed(evt);
            }
        });
        VENTA.add(userTxt5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 330, 20, 30));

        passLabel3.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        passLabel3.setText("PRECIO UNITARIO");
        VENTA.add(passLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 230, 150, -1));

        userTxt6.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt6.setForeground(new java.awt.Color(204, 204, 204));
        userTxt6.setText("Q");
        userTxt6.setBorder(null);
        userTxt6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTxt6MousePressed(evt);
            }
        });
        userTxt6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTxt6ActionPerformed(evt);
            }
        });
        VENTA.add(userTxt6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, 40, 30));

        userTxt7.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt7.setForeground(new java.awt.Color(204, 204, 204));
        userTxt7.setText("asd");
        userTxt7.setBorder(null);
        userTxt7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTxt7MousePressed(evt);
            }
        });
        userTxt7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTxt7ActionPerformed(evt);
            }
        });
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

        jPanel35.setBackground(new java.awt.Color(0, 153, 255));
        jPanel35.setForeground(new java.awt.Color(27, 86, 253));

        jLabel21.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("CERRAR SESIÓN");
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });

        jLabel105.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel105.setForeground(new java.awt.Color(255, 255, 255));
        jLabel105.setText("VENTAS");
        jLabel105.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel105MouseClicked(evt);
            }
        });

        jLabel106.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel106.setForeground(new java.awt.Color(255, 255, 255));
        jLabel106.setText("LOCALIDADES");

        jLabel107.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon36.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jLabel108.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel108.setForeground(new java.awt.Color(255, 255, 255));
        jLabel108.setText("PARTIDOS");

        jLabel109.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel109.setForeground(new java.awt.Color(255, 255, 255));
        jLabel109.setText("DASHBOARD");

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(255, 255, 255));
        jLabel58.setText("DISPONIBILIDAD");

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(favicon36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel108)
                            .addComponent(jLabel105))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(41, 41, 41))
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58)
                    .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel109)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addComponent(jLabel109)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jLabel58)
                .addGap(50, 50, 50)
                .addComponent(jLabel106)
                .addGap(46, 46, 46)
                .addComponent(jLabel105)
                .addGap(40, 40, 40)
                .addComponent(jLabel108)
                .addGap(37, 37, 37)
                .addComponent(jLabel21)
                .addGap(55, 55, 55))
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
                .addGap(0, 800, Short.MAX_VALUE))
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
                .addContainerGap(25, Short.MAX_VALUE))
        );
        loginBtnLayout.setVerticalGroup(
            loginBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtnLayout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
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
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
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
                .addContainerGap(23, Short.MAX_VALUE))
        );
        loginBtn5Layout.setVerticalGroup(
            loginBtn5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn5Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
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
                .addContainerGap(19, Short.MAX_VALUE))
        );
        loginBtn6Layout.setVerticalGroup(
            loginBtn6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn6Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel26)
                .addContainerGap())
        );

        DISPONIBLE2.add(loginBtn6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 440, 110, 30));

        jPanel36.setBackground(new java.awt.Color(0, 153, 255));
        jPanel36.setForeground(new java.awt.Color(27, 86, 253));

        jLabel53.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setText("CERRAR SESIÓN");
        jLabel53.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel53MouseClicked(evt);
            }
        });

        jLabel110.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel110.setForeground(new java.awt.Color(255, 255, 255));
        jLabel110.setText("VENTAS");
        jLabel110.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel110MouseClicked(evt);
            }
        });

        jLabel111.setFont(new java.awt.Font("Roboto Light", 1, 18)); // NOI18N
        jLabel111.setForeground(new java.awt.Color(255, 255, 255));
        jLabel111.setText("LOCALIDADES");

        jLabel112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N

        favicon37.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N

        jLabel113.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel113.setForeground(new java.awt.Color(255, 255, 255));
        jLabel113.setText("PARTIDOS");

        jLabel114.setFont(new java.awt.Font("Roboto SemiCondensed", 1, 18)); // NOI18N
        jLabel114.setForeground(new java.awt.Color(255, 255, 255));
        jLabel114.setText("DASHBOARD");

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 255, 255));
        jLabel56.setText("DISPONIBILIDAD");

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(favicon37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel56)
                            .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel114)))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel113)
                            .addComponent(jLabel110))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel114)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(jLabel56)
                .addGap(47, 47, 47)
                .addComponent(jLabel111)
                .addGap(43, 43, 43)
                .addComponent(jLabel110)
                .addGap(51, 51, 51)
                .addComponent(jLabel113)
                .addGap(62, 62, 62)
                .addComponent(jLabel53)
                .addGap(29, 29, 29))
        );

        DISPONIBLE2.add(jPanel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 500));

        title.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title.setText("DISPONIBILIDAD");
        DISPONIBLE2.add(title, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, -1, -1));

        MainPanel.add(DISPONIBLE2, "card6");

        HISTORIAL.setBackground(new java.awt.Color(255, 255, 255));
        HISTORIAL.setMinimumSize(new java.awt.Dimension(800, 500));
        HISTORIAL.setPreferredSize(new java.awt.Dimension(800, 500));
        HISTORIAL.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        header5.setBackground(new java.awt.Color(255, 255, 255));
        header5.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                header5MouseDragged(evt);
            }
        });
        header5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                header5MousePressed(evt);
            }
        });

        exitBtn5.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout exitBtn5Layout = new javax.swing.GroupLayout(exitBtn5);
        exitBtn5.setLayout(exitBtn5Layout);
        exitBtn5Layout.setHorizontalGroup(
            exitBtn5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        exitBtn5Layout.setVerticalGroup(
            exitBtn5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout header5Layout = new javax.swing.GroupLayout(header5);
        header5.setLayout(header5Layout);
        header5Layout.setHorizontalGroup(
            header5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(header5Layout.createSequentialGroup()
                .addComponent(exitBtn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 800, Short.MAX_VALUE))
        );
        header5Layout.setVerticalGroup(
            header5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitBtn5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        HISTORIAL.add(header5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, 40));

        favicon6.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/favicon.png"))); // NOI18N
        HISTORIAL.add(favicon6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        title4.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title4.setText("HISTORIAL DE VENTAS");
        HISTORIAL.add(title4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        loginBtn7.setBackground(new java.awt.Color(0, 134, 190));

        jLabel27.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("EXPORTAR");

        javax.swing.GroupLayout loginBtn7Layout = new javax.swing.GroupLayout(loginBtn7);
        loginBtn7.setLayout(loginBtn7Layout);
        loginBtn7Layout.setHorizontalGroup(
            loginBtn7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtn7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel27)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        loginBtn7Layout.setVerticalGroup(
            loginBtn7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn7Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel27)
                .addContainerGap())
        );

        HISTORIAL.add(loginBtn7, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 440, 110, 30));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/ticketmaster_logo_icon_249397.png"))); // NOI18N
        HISTORIAL.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 130, 60));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "FECHA", "VENDEDOR", "PARTIDO", "LOCALIDAD", "CANTIDAD", "PRECIO UNIT."
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        HISTORIAL.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 670, 220));

        loginBtn8.setBackground(new java.awt.Color(0, 134, 190));

        jLabel29.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("BUSCAR");

        javax.swing.GroupLayout loginBtn8Layout = new javax.swing.GroupLayout(loginBtn8);
        loginBtn8.setLayout(loginBtn8Layout);
        loginBtn8Layout.setHorizontalGroup(
            loginBtn8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtn8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel29)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        loginBtn8Layout.setVerticalGroup(
            loginBtn8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn8Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addContainerGap())
        );

        HISTORIAL.add(loginBtn8, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 110, 90, 30));

        loginBtn9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel30.setBackground(new java.awt.Color(0, 0, 0));
        jLabel30.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel30.setText("ACTUALIZAR");

        javax.swing.GroupLayout loginBtn9Layout = new javax.swing.GroupLayout(loginBtn9);
        loginBtn9.setLayout(loginBtn9Layout);
        loginBtn9Layout.setHorizontalGroup(
            loginBtn9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginBtn9Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel30)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        loginBtn9Layout.setVerticalGroup(
            loginBtn9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginBtn9Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel30)
                .addContainerGap())
        );

        HISTORIAL.add(loginBtn9, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 440, 110, 30));

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        HISTORIAL.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 90, -1));
        HISTORIAL.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, 90, -1));

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        HISTORIAL.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 170, -1, -1));

        jLabel31.setText("FECHA INICIAL");
        HISTORIAL.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, -1, -1));

        jLabel32.setText("FECHA FINAL");
        HISTORIAL.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, -1, -1));

        jLabel33.setText("VENDEDOR");
        HISTORIAL.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 150, -1, -1));

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        HISTORIAL.add(jComboBox7, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 170, -1, -1));

        jLabel34.setText("PARTIDO");
        HISTORIAL.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 150, -1, -1));

        jLabel35.setText("LOCALIDAD");
        HISTORIAL.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 150, -1, -1));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PALCO", "TRUBUNA", "GENERAL NORTE", "GENERAL SUR", "TODAS" }));
        HISTORIAL.add(jComboBox8, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 170, 80, -1));

        jLabel36.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel36.setText("LIMPIAR");
        HISTORIAL.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 110, 50, 30));

        MainPanel.add(HISTORIAL, "card7");

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
            .addGap(0, 110, Short.MAX_VALUE)
        );
        loginBtn12Layout.setVerticalGroup(
            loginBtn12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(favicon7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(32, Short.MAX_VALUE))
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
                .addContainerGap(34, Short.MAX_VALUE))
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
                .addContainerGap(16, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(43, 43, 43)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
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

        ADMINISTRACION.add(LOBBY, "card7");

        NewUser.setBackground(new java.awt.Color(255, 255, 255));
        NewUser.setMinimumSize(new java.awt.Dimension(800, 500));
        NewUser.setPreferredSize(new java.awt.Dimension(800, 500));
        NewUser.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
                .addContainerGap(32, Short.MAX_VALUE))
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
                .addContainerGap(34, Short.MAX_VALUE))
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
                .addContainerGap(16, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(jLabel37))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addGap(43, 43, 43)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );

        NewUser.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel43.setBackground(new java.awt.Color(204, 204, 204));
        jLabel43.setFont(new java.awt.Font("Roboto Medium", 1, 48)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(204, 204, 204));
        jLabel43.setText("INVENTARIO");
        NewUser.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));

        jLabel44.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel44.setText("CANTIDAD");
        NewUser.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 130, -1, -1));

        jLabel45.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel45.setText("LOCALIDAD");
        NewUser.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 200, -1, -1));

        NewUser.add(jComboBox6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, 180, -1));

        jLabel46.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel46.setText("PARTIDO");
        NewUser.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, -1));
        NewUser.add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, 170, -1));

        jPanel14.setBackground(new java.awt.Color(102, 204, 255));

        jLabel47.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(255, 255, 255));
        jLabel47.setText("ELIMINAR");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel47)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel47)
                .addContainerGap())
        );

        NewUser.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 200, -1, 30));

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

        NewUser.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 290, 410, 170));

        NewUser.add(jComboBox19, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 180, -1));

        jPanel33.setBackground(new java.awt.Color(51, 153, 255));

        jLabel99.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel99.setForeground(new java.awt.Color(255, 255, 255));
        jLabel99.setText("GUARDAR");

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel99)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel99)
                .addContainerGap())
        );

        NewUser.add(jPanel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 240, -1, 30));

        ADMINISTRACION.add(NewUser, "card7");

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
                .addContainerGap(32, Short.MAX_VALUE))
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
                .addContainerGap(34, Short.MAX_VALUE))
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
                .addContainerGap(16, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel48))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel49)
                .addGap(43, 43, 43)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
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

        jLabel74.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel74.setText("FECHA");
        NewPartido.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 190, -1, -1));

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DOROTEO GUAMUCH", "CEMENTOS PROGRESO", "MATEO FLORES", "ISRAEL FLORES" }));
        NewPartido.add(jComboBox9, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 210, 180, -1));

        jLabel75.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel75.setText("EQUIPO LOCAL");
        NewPartido.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 120, -1, -1));

        jPanel20.setBackground(new java.awt.Color(51, 153, 255));

        jLabel76.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(255, 255, 255));
        jLabel76.setText("GUARDAR");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel76)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
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

        NewPartido.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 310, 340, 170));

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ANTIGUA", "COMUNICACIONES", "COMUNICACIONES", "XELAJU" }));
        NewPartido.add(jComboBox10, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 140, 180, -1));

        favicon24.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/antigua escudo.png"))); // NOI18N
        NewPartido.add(favicon24, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 100, -1, -1));

        favicon25.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/Doroteo guamuch.png"))); // NOI18N
        NewPartido.add(favicon25, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 310, -1, -1));

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ANTIGUA", "COMUNICACIONES", "COMUNICACIONES", "XELAJU" }));
        NewPartido.add(jComboBox11, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 180, -1));

        jLabel77.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel77.setText("ESTADIO");
        NewPartido.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 190, -1, -1));
        NewPartido.add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 210, 170, -1));

        favicon26.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        favicon26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/xela escudo.png"))); // NOI18N
        NewPartido.add(favicon26, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 100, -1, -1));

        ADMINISTRACION.add(NewPartido, "card7");

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
                .addContainerGap(32, Short.MAX_VALUE))
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
                .addContainerGap(34, Short.MAX_VALUE))
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
                .addContainerGap(16, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79)
                    .addComponent(jLabel78))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel78)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel79)
                .addGap(43, 43, 43)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );

        NewLocalidades.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel84.setBackground(new java.awt.Color(204, 204, 204));
        jLabel84.setFont(new java.awt.Font("Roboto Medium", 1, 48)); // NOI18N
        jLabel84.setForeground(new java.awt.Color(204, 204, 204));
        jLabel84.setText("LOCALIDADES");
        NewLocalidades.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));
        NewLocalidades.add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, 170, -1));

        jLabel86.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel86.setText("PRECIO");
        NewLocalidades.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 200, -1, -1));

        jLabel87.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel87.setText("LOCALIDAD");
        NewLocalidades.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, -1));

        jPanel26.setBackground(new java.awt.Color(51, 153, 255));

        jLabel88.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(255, 255, 255));
        jLabel88.setText("GUARDAR");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel88)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel88)
                .addContainerGap())
        );

        NewLocalidades.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 220, -1, 30));

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

        jComboBox18.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Palco", "Tribuna", "Preferencia", "General Norte", "General Sur", "General Este", "General Oeste", "Gradería Alta", "Gradería Baja", "Sombra", "Sol", "Sol Norte", "Sol Sur", "Sol General", "Sol Preferente", "Tribuna Norte", "Tribuna Sur", "Tribuna Central", "VIP", "Zona A", "Zona B", "Zona C", "Zona D", "Zona E", "Platea", "Butaca", "Numerado", "No Numerado", "Área Familiar", "Visitante", "Local", " " }));
        NewLocalidades.add(jComboBox18, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 160, 170, -1));

        ADMINISTRACION.add(NewLocalidades, "card7");

        INVENTARIO.setBackground(new java.awt.Color(255, 255, 255));
        INVENTARIO.setMinimumSize(new java.awt.Dimension(800, 500));
        INVENTARIO.setPreferredSize(new java.awt.Dimension(800, 500));
        INVENTARIO.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
                .addContainerGap(32, Short.MAX_VALUE))
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
                .addContainerGap(34, Short.MAX_VALUE))
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
                .addContainerGap(16, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89)
                    .addComponent(jLabel85))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel85)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel89)
                .addGap(43, 43, 43)
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );

        INVENTARIO.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel94.setBackground(new java.awt.Color(204, 204, 204));
        jLabel94.setFont(new java.awt.Font("Roboto Medium", 1, 48)); // NOI18N
        jLabel94.setForeground(new java.awt.Color(204, 204, 204));
        jLabel94.setText("USUARIO");
        INVENTARIO.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));

        jLabel95.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel95.setText("ROL");
        INVENTARIO.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 130, -1, -1));
        INVENTARIO.add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, 170, -1));

        jLabel96.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel96.setText("CONTRASEÑA");
        INVENTARIO.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 200, -1, -1));

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMINISTRADOR", "COMPRADOR", "VENDEDOR" }));
        INVENTARIO.add(jComboBox12, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 150, 180, -1));

        jLabel97.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel97.setText("USUARIO");
        INVENTARIO.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, -1));
        INVENTARIO.add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 150, 170, -1));

        jPanel32.setBackground(new java.awt.Color(51, 153, 255));

        jLabel98.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(255, 255, 255));
        jLabel98.setText("GUARDAR");

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel98)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel32Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel98)
                .addContainerGap())
        );

        INVENTARIO.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 220, -1, 30));

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

        INVENTARIO.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 290, 400, 170));

        ADMINISTRACION.add(INVENTARIO, "card7");

        MainPanel.add(ADMINISTRACION, "card9");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 916, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(MainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 516, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(MainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void inicializarSpinnerPorDefecto() {
    // Inicializar con valores por defecto inmediatamente
    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
    jSpinnerCantidad.setModel(spinnerModel);
    jSpinnerCantidad.setValue(1);
    
    System.out.println("Spinner inicializado con valor: " + jSpinnerCantidad.getValue());
}
    private void configurarTablaCarrito() {
    // Asegurar que la tabla tenga el modelo correcto
    String[] columnNames = {"PARTIDO", "LOCALIDAD", "CANTIDAD", "PRECIO U", "SUBTOTAL"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Hacer la tabla de solo lectura
        }
    };
    
    jTable1.setModel(model);
    
    // Ajustar anchos de columnas
    jTable1.getColumnModel().getColumn(0).setPreferredWidth(200); // PARTIDO
    jTable1.getColumnModel().getColumn(1).setPreferredWidth(150); // LOCALIDAD
    jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);  // CANTIDAD
    jTable1.getColumnModel().getColumn(3).setPreferredWidth(100); // PRECIO U
    jTable1.getColumnModel().getColumn(4).setPreferredWidth(100); // SUBTOTAL
}
    private void cargarDatosPartidos() {
    try {
        System.out.println("Cargando datos de partidos...");
        
        // Cargar partidos desde el servicio
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
        // Definir columnas
        String[] columnNames = {"EQUIPO LOCAL", "EQUIPO VISITANTE", "FECHA DEL PARTIDO", "ESTADO"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class; // EQUIPO LOCAL
                    case 1: return String.class; // EQUIPO VISITANTE  
                    case 2: return String.class; // FECHA DEL PARTIDO
                    case 3: return String.class; // ESTADO
                    default: return Object.class;
                }
            }
        };
        
        // Aplicar modelo a la tabla jTable9
        jTable9.setModel(model);
        
        // Configurar formato de la tabla
        configurarFormatoTablaPartidos();
        
        System.out.println("Tabla de partidos configurada correctamente");
        
    } catch (Exception e) {
        System.err.println("Error configurando tabla partidos: " + e.getMessage());
    }
}
    private void configurarFormatoTablaPartidos() {
    try {
        // Centrar el texto en todas las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Aplicar a todas las columnas
        for (int i = 0; i < jTable9.getColumnCount(); i++) {
            jTable9.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Formato especial para la columna de ESTADO (columna 3)
        jTable9.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                // Cambiar color según el estado
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
        
        // Formato especial para la columna de FECHA (columna 2)
        jTable9.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Si la fecha viene en formato diferente, puedes formatearla aquí
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
        
        // Ajustar anchos de columnas
        jTable9.getColumnModel().getColumn(0).setPreferredWidth(150); // EQUIPO LOCAL
        jTable9.getColumnModel().getColumn(1).setPreferredWidth(150); // EQUIPO VISITANTE
        jTable9.getColumnModel().getColumn(2).setPreferredWidth(180); // FECHA DEL PARTIDO
        jTable9.getColumnModel().getColumn(3).setPreferredWidth(120); // ESTADO
        
        // Alternar colores de filas para mejor legibilidad
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
        
        // Hacer que la tabla sea ordenable por columnas
        jTable9.setAutoCreateRowSorter(true);
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla partidos: " + e.getMessage());
    }
}
private void cargarPartidosEnTabla() {
    try {
        System.out.println("Cargando partidos en la tabla...");
        
        DefaultTableModel model = (DefaultTableModel) jTable9.getModel();
        model.setRowCount(0); // Limpiar tabla existente
        
        if (partidosListGeneral != null && !partidosListGeneral.isEmpty()) {
            for (Partido partido : partidosListGeneral) {
                // Formatear la fecha si es necesario
                String fechaFormateada = formatearFecha(partido.getFecha_partido());
                
                // Obtener el estado (ajusta según tu modelo)
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
        // Aquí debes ajustar según cómo manejes los estados en tu modelo
        // Estos son ejemplos de posibles métodos en tu clase Partido
        
        // Si tienes un campo específico para estado
        if (partido.getEstado() != null && !partido.getEstado().isEmpty()) {
            return partido.getEstado();
        }
        
        // Si no tienes campo estado, determinar por fecha
        if (partido.getFecha_partido() != null) {
            Date fechaPartido = convertirFecha(partido.getFecha_partido());
            
            if (fechaPartido != null) {
                Date ahora = new Date();
                
                if (fechaPartido.after(ahora)) {
                    return "Programado";
                } else {
                    // Si la fecha ya pasó, considerar como finalizado
                    // (esto es una simplificación, podrías tener lógica más compleja)
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
        
        // Recargar datos
        cargarDatosPartidos();
        
        // Volver a cargar la tabla
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
                // Formato ISO (2023-12-25T20:00:00)
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                return isoFormat.parse(fechaStr);
            } else {
                // Intentar otros formatos comunes
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
                        // Continuar con el siguiente formato
                    }
                }
                return null;
            }
        } else if (fecha instanceof java.util.Date) {
            return (Date) fecha;
        } else if (fecha instanceof java.sql.Date) {
            // Convertir java.sql.Date a java.util.Date
            return new Date(((java.sql.Date) fecha).getTime());
        } else if (fecha instanceof java.sql.Timestamp) {
            // Convertir java.sql.Timestamp a java.util.Date
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
        
        // Si no se pudo convertir, devolver como string
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
        // Escribir encabezados
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            pw.print(tabla.getColumnName(i));
            if (i < tabla.getColumnCount() - 1) {
                pw.print(",");
            }
        }
        pw.println();
        
        // Escribir datos
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
}
    private void header4MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header4MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_header4MouseDragged

    private void header4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header4MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_header4MousePressed

    private void userTxt4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxt4MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt4MousePressed

    private void passTxt2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_passTxt2MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_passTxt2MousePressed

    private void loginBtnTxt2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt2MouseClicked
    try {
        String usuario = userTxt4.getText();
        String contrasena = new String(passTxt2.getPassword());
        
        // Validar campos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos");
            return;
        }
        
        // Crear servicio de usuario
        ServicioUsuario servicio = new ServicioUsuario();
        
        // Intentar login
        boolean loginExitoso = servicio.login(usuario, contrasena);
        
        if (loginExitoso) {
            SessionManager session = servicio.getSessionManager();
            String rol = session.getUsuarioRol();
            String nombreCompleto = session.getNombreUsuario();
            
            switch (rol.toLowerCase()) {
                case "admin" -> {
                    JOptionPane.showMessageDialog(this, 
                        "Login exitoso como ADMINISTRADOR\nBienvenido: " + nombreCompleto);
                    CardLayout cl = (CardLayout) MainPanel.getLayout();
                    cl.show(MainPanel, "card9"); 
                    
                    // CARGAR DATOS PARA ADMIN
                    cargarDatosDespuesDeLogin();
                }
                case "vendedor" -> {
                    JOptionPane.showMessageDialog(this, 
                        "Login exitoso como VENDEDOR\nBienvenido: " + nombreCompleto);
                    CardLayout cl = (CardLayout) MainPanel.getLayout();
                    cl.show(MainPanel, "card4"); // DASHBOARD
                    
                    // CARGAR DATOS PARA VENDEDOR
                    cargarDatosDespuesDeLogin();
                    //Para ventas
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
    userTxt4.setText("");
    passTxt2.setText("");
}
private void cargarLocalidades() {
    try {
        ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        ServicioInventario servicioInventario = new Servicios.ServicioInventario();
        
        System.out.println("=== CARGANDO LOCALIDADES CON STOCK ===");
        
        List<Localidad> localidades = servicioLocalidades.getLocalidades();
        List<Inventario> inventarios = servicioInventario.getInventario();
        
        // Crear mapas para buscar inventario por localidad_id
        Map<Integer, Integer> stockPorLocalidad = new HashMap<>();
        Map<Integer, Double> precioPromedioPorLocalidad = new HashMap<>();
        Map<Integer, Integer> contadorPreciosPorLocalidad = new HashMap<>();
        
        for (Inventario inventario : inventarios) {
            int idLocalidad = inventario.getId_localidad();
            
            // Calcular stock total por localidad
            int stockActual = stockPorLocalidad.getOrDefault(idLocalidad, 0);
            stockPorLocalidad.put(idLocalidad, stockActual + inventario.getCantidad_disponible());
            
            // Calcular precio promedio por localidad
            double precioActual = precioPromedioPorLocalidad.getOrDefault(idLocalidad, 0.0);
            int contadorActual = contadorPreciosPorLocalidad.getOrDefault(idLocalidad, 0);
            
            precioPromedioPorLocalidad.put(idLocalidad, precioActual + inventario.getPrecio());
            contadorPreciosPorLocalidad.put(idLocalidad, contadorActual + 1);
        }
        
        // Calcular promedios finales
        for (Integer idLocalidad : precioPromedioPorLocalidad.keySet()) {
            double sumaPrecios = precioPromedioPorLocalidad.get(idLocalidad);
            int contador = contadorPreciosPorLocalidad.get(idLocalidad);
            precioPromedioPorLocalidad.put(idLocalidad, sumaPrecios / contador);
        }
        
        // Crear el modelo de tabla
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
        
        // Combinar datos
        for (Localidad localidad : localidades) {
            Integer stockDisponible = stockPorLocalidad.getOrDefault(localidad.getId_localidad(), 0);
            Double precioPromedio = precioPromedioPorLocalidad.getOrDefault(localidad.getId_localidad(), 0.0);
            
            model.addRow(new Object[]{
                localidad.getNombre(),
                precioPromedio, // Ahora viene del inventario, no de la localidad
                stockDisponible
            });
        }
        
        // Aplicar a la tabla jTable2 en el DASHBOARD
        jTable2.setModel(model);
        configurarFormatoTablaLocalidades();
        
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
        jSpinnerCantidad.setValue(1); // ✅ FORZAR valor a 1
        jSpinnerCantidad.setEnabled(true);
        
        System.out.println("Nuevo valor del spinner: " + jSpinnerCantidad.getValue());
        System.out.println("Caso: Stock normal - Spinner en 1");
    }
    
    // Forzar actualización visual
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
        
        // Verificar el rol para decidir qué datos cargar
        String rol = SessionManager.getInstance().getUsuarioRol();
        
        if ("vendedor".equalsIgnoreCase(rol)) {
            cargarLocalidades(); // Para el dashboard de vendedor
        } else if ("admin".equalsIgnoreCase(rol)) {
            cargarDatosParaAdmin(); // Para el panel de administrador
        }
        
        datosCargados = true;
        System.out.println("Datos cargados exitosamente para rol: " + rol);
        
    } catch (Exception e) {
        System.err.println("Error cargando datos después del login: " + e.getMessage());
        datosCargados = false;
        
        // Mostrar error específico según el tipo
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
private void cargarDatosParaAdmin() {
    try {
        System.out.println("Cargando datos para administrador...");
        // Aquí puedes cargar datos específicos para el panel de admin
        // Por ejemplo: usuarios, reportes, etc.
        
        // Por ahora, también cargamos las localidades para admin
        cargarLocalidades();
        
    } catch (Exception e) {
        System.err.println("Error cargando datos para admin: " + e.getMessage());
        throw new RuntimeException("Error cargando datos de administrador: " + e.getMessage(), e);
    }
}

private void manejarErrorCarga(Exception e) {
    String mensaje = "Error al cargar localidades:\n";
    
    if (e.getMessage().contains("Debe iniciar sesión primero")) {
        mensaje += "Sesión expirada. Por favor, inicie sesión nuevamente.";
        // Opcional: redirigir al login
        // irALogin();
    } else if (e.getMessage().contains("Connection refused")) {
        mensaje += "No se puede conectar al servidor. Verifique que esté ejecutándose.";
    } else {
        mensaje += e.getMessage();
    }
    
    JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    e.printStackTrace();
}
private void configurarFormatoTablaLocalidades() {
    try {
        // Centrar el texto en todas las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        // Aplicar a todas las columnas
        for (int i = 0; i < jTable2.getColumnCount(); i++) {
            jTable2.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Formato especial para la columna de PRECIO (columna 1)
        jTable2.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Formatear el precio como moneda
                if (value instanceof Double) {
                    value = String.format("Q%,.2f", (Double) value);
                }
                
                javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
                return label;
            }
        });
        
        // Formato especial para la columna de DISPONIBLES (columna 2)
        jTable2.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                javax.swing.JLabel label = (javax.swing.JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                // Cambiar color según disponibilidad
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
                        label.setForeground(new Color(0, 128, 0)); // Verde oscuro
                    }
                }
                
                label.setHorizontalAlignment(javax.swing.JLabel.CENTER);
                return label;
            }
        });
        
        // Ajustar anchos de columnas
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(200); // NOMBRE
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(120); // PRECIO
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(120); // DISPONIBLES
        
        // Alternar colores de filas para mejor legibilidad
        jTable2.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla: " + e.getMessage());
    }
}

private void validarStockDisponible() {
    if (jComboBox4.getSelectedItem() == null) {
        System.out.println("No hay localidad seleccionada");
        return;
    }
    
    String localidadSeleccionada = jComboBox4.getSelectedItem().toString();
    System.out.println("Validando stock para: " + localidadSeleccionada); // Debug
    
    int stockDisponible = obtenerStockActual(localidadSeleccionada);
    int cantidadSolicitada = (int) jSpinnerCantidad.getValue();
    
    System.out.println("Stock disponible: " + stockDisponible); // Debug
    
    // Actualizar jLabel101 con el stock disponible
    jLabel101.setText("DISPONIBLES: " + stockDisponible);
    
    // Validar si excede el stock
    if (cantidadSolicitada > stockDisponible) {
        System.out.println("Stock insuficiente"); // Debug
        // Cambiar color a rojo para indicar error
        jLabel101.setForeground(Color.RED);
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.BOLD));
        
        // Ajustar automáticamente el spinner al máximo disponible
        if (stockDisponible > 0) {
            jSpinnerCantidad.setValue(stockDisponible);
        } else {
            jSpinnerCantidad.setValue(0);
        }
    } else {
        System.out.println("Stock suficiente"); // Debug
        // Volver a color normal
        jLabel101.setForeground(new Color(204, 204, 204));
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));
    }
    
    // Actualizar el modelo del spinner con el máximo disponible
    actualizarLimitesSpinner(stockDisponible);
    
    // Actualizar el precio total
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
                // Usar la nueva versión que requiere partido
                precioUnitario = obtenerPrecioLocalidad(localidad, partido);
                userTxt7.setText(String.format("%.2f", precioUnitario));
            }
            
            double precioTotal = precioUnitario * cantidad;
            
            // Actualizar jLabel100 con el precio total
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
        System.out.println("Buscando stock para: " + nombreLocalidad); // Debug
        
        if (localidadesList == null || inventarioList == null) {
            System.out.println("Listas nulas"); // Debug
            return 0;
        }
        
        // Buscar el ID de la localidad por nombre
        Integer localidadId = null;
        for (Localidad localidad : localidadesList) {
            if (localidad.getNombre().equals(nombreLocalidad)) {
                localidadId = localidad.getId_localidad();
                System.out.println("ID encontrado: " + localidadId); // Debug
                break;
            }
        }
        
        if (localidadId == null) {
            System.out.println("ID no encontrado para: " + nombreLocalidad); // Debug
            return 0;
        }
        
        // Buscar el stock en el inventario
        for (Inventario inventario : inventarioList) {
            if (inventario.getId_localidad() == localidadId) {
                System.out.println("Stock encontrado: " + inventario.getCantidad_disponible()); // Debug
                return inventario.getCantidad_disponible();
            }
        }
        
        System.out.println("Stock no encontrado en inventario"); // Debug
        return 0;
        
    } catch (Exception e) {
        System.err.println("Error obteniendo stock: " + e.getMessage());
        return 0;
    }
}
    private void loginBtnTxt2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt2MouseEntered

    private void loginBtnTxt2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt2MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt2MouseExited

    private void userTxt1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxt1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt1MousePressed

    private void passTxt1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_passTxt1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_passTxt1MousePressed

    private void loginBtnTxt4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt4MouseClicked
        try {
            String nombreUsuario = userTxt8.getText();
            String contrasena = new String(passTxt1.getPassword());
            String nombreCompleto = userTxt1.getText();
                
            // Crear objeto Usuario
            Modelos.Usuario us = new Modelos.Usuario();
            us.setNombre_usuario(nombreUsuario);
            us.setContrasena_hash(contrasena);
            us.setNombre_completo(nombreCompleto);
            us.setRol("vendedor");
        
            // Crear servicio y llamar signup
            Servicios.ServicioUsuario servi = new Servicios.ServicioUsuario();
            boolean exito = servi.signup(us.getNombre_usuario(),us.getContrasena_hash(),us.getNombre_completo(),us.getRol());
        
            if (exito) {
                JOptionPane.showMessageDialog(this, "✅ Vendedor agregado correctamente.");
                cleanFields();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al agregar vendedor.");
            }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error al agregar usuario: " + ex.getMessage());
            }
    }//GEN-LAST:event_loginBtnTxt4MouseClicked

    private void loginBtnTxt4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt4MouseEntered

    private void loginBtnTxt4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnTxt4MouseExited

    private void userTxt8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxt8MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt8MousePressed

private double obtenerPrecioLocalidad(String nombreLocalidad, Object partidoSeleccionado) {
    try {
        // Obtener el ID de la localidad
        int idLocalidad = obtenerIdLocalidad(nombreLocalidad);
        if (idLocalidad == -1) {
            throw new Exception("No se encontró la localidad: " + nombreLocalidad);
        }
        
        // Obtener el ID del partido
        int idPartido = obtenerIdPartido(partidoSeleccionado);
        if (idPartido == -1) {
            throw new Exception("No se encontró el partido seleccionado");
        }
        
        // Buscar el precio en el inventario
        if (inventarioList != null) {
            for (Inventario inventario : inventarioList) {
                if (inventario.getId_localidad() == idLocalidad && 
                    inventario.getId_partido() == idPartido) {
                    return inventario.getPrecio(); // Ahora el precio viene del inventario
                }
            }
        }
        
        // Si no encuentra, consultar directamente a la API
        return consultarPrecioDirecto(idLocalidad, idPartido);
        
    } catch (Exception e) {
        System.err.println("Error obteniendo precio: " + e.getMessage());
        throw new RuntimeException("Error obteniendo precio de localidad: " + e.getMessage());
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
        return 0.0; // Precio por defecto si no se encuentra
    } catch (Exception e) {
        System.err.println("Error consultando precio directo: " + e.getMessage());
        return 0.0;
    }
}
private void añadirFilaAlCarrito(String partido, String localidad, int cantidad, double precioUnitario, double subtotal) {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    
    // Añadir nueva fila con datos reales
    model.addRow(new Object[]{
        partido,
        localidad,
        cantidad,
        String.format("%.2f", precioUnitario), // Solo números
        String.format("%.2f", subtotal)        // Solo números
    });
    
    // Actualizar la tabla
    jTable1.setModel(model);
}
private void actualizarTotales() {
    // Actualizar el total general en userTxt5
    userTxt5.setText(String.format("%.2f", totalVenta));
}
private void limpiarCamposVenta() {
    // No limpiar el partido (puede ser el mismo para múltiples items)
    
    // Limpiar localidad
    jComboBox4.setSelectedIndex(0);
    
    // Resetear spinner a 1
    jSpinnerCantidad.setValue(1);
    
    // Limpiar precio unitario
    jLabel100.setText("0.00");
    
    // Resetear label de disponibles (jLabel101)
    jLabel101.setText("DISPONIBLES: ");
    jLabel101.setForeground(new Color(204, 204, 204)); // Color gris original
    jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));
    
    // Mantener el focus en localidad para siguiente item
    jComboBox4.requestFocus();
}

private void inicializarPanelDisponibilidad() {
    try {
        System.out.println("=== INICIALIZANDO PANEL DISPONIBILIDAD ===");
        
        // Cargar datos necesarios
        cargarDatosParaDisponibilidad();
        
        // Configurar combo box de partidos
        configurarComboBoxPartidos();
        
        // Configurar tabla de disponibilidad
        configurarTablaDisponibilidad();
        
        // Agregar listeners
        agregarListenersDisponibilidad();
        
        // Cargar datos iniciales en la tabla
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
        
        // Cargar partidos
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        partidosListDisponibilidad = servicioPartidos.getPartidos();
        
        // Cargar localidades
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        localidadesListDisponibilidad = servicioLocalidades.getLocalidades();
        
        // Cargar inventario
        ServicioInventario servicioInventario = new ServicioInventario();
        inventarioListDisponibilidad = servicioInventario.getInventario();
        
        System.out.println("Partidos cargados: " + partidosListDisponibilidad.size());
        System.out.println("Localidades cargadas: " + localidadesListDisponibilidad.size());
        System.out.println("Inventario cargado: " + inventarioListDisponibilidad.size());
        
    } catch (Exception e) {
        System.err.println("Error cargando datos para disponibilidad: " + e.getMessage());
        throw new RuntimeException("Error cargando datos de disponibilidad: " + e.getMessage(), e);
    }
}
private void cargarDatosParaVenta() {
    try {
        // Cargar partidos desde la base de datos
        Servicios.ServicioPartido servicioPartidos = new Servicios.ServicioPartido();
        partidosList = servicioPartidos.getPartidos();
        
        // Cargar localidades desde la base de datos
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        localidadesList = servicioLocalidades.getLocalidades();
        
        // Cargar partidos en el ComboBox - MOSTRAR SOLO NOMBRES, NO FECHA
        jComboBox3.removeAllItems();
        if (partidosList != null && !partidosList.isEmpty()) {
            for (Partido partido : partidosList) {
                // Solo mostrar los nombres de equipos, no la fecha
                String display = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
                jComboBox3.addItem(display);
            }
        } else {
            jComboBox3.addItem("No hay partidos disponibles");
        }
        
        // Resto del código...
        
    } catch (Exception e) {
        System.err.println("Error cargando datos para venta: " + e.getMessage());
    }
}
private void configurarComboBoxPartidos() {
    try {
        jComboBox1.removeAllItems();
        
        if (partidosListDisponibilidad != null && !partidosListDisponibilidad.isEmpty()) {
            // Agregar opción "Todos los partidos"
            jComboBox1.addItem("Todos los partidos");
            
            // Agregar cada partido
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
        // Centrar el texto en todas las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // ← CORREGIDO
        
        // Aplicar a todas las columnas - CAMBIA "jTable3" POR EL NOMBRE REAL
        for (int i = 0; i < jTableDisponibilidad.getColumnCount(); i++) { // ← CAMBIAR POR EL NOMBRE CORRECTO
            jTableDisponibilidad.getColumnModel().getColumn(i).setCellRenderer(centerRenderer); // ← CAMBIAR
        }
        
        // Formato especial para la columna de PRECIO (columna 3) - CAMBIA "jTable3"
        jTableDisponibilidad.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() { // ← CAMBIAR
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Formatear el precio como moneda
                if (value instanceof Double) {
                    value = String.format("Q%,.2f", (Double) value);
                }
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.RIGHT);
                return label;
            }
        });
        
        // Formato especial para la columna de DISPONIBLE (columna 2) - CAMBIA "jTable3"
        jTableDisponibilidad.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() { // ← CAMBIAR
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                // Cambiar color según disponibilidad
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
                        label.setForeground(new Color(0, 128, 0)); // Verde oscuro
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });
        
        // Ajustar anchos de columnas - CAMBIA "jTable3"
        jTableDisponibilidad.getColumnModel().getColumn(0).setPreferredWidth(200); // LOCALIDAD // ← CAMBIAR
        jTableDisponibilidad.getColumnModel().getColumn(1).setPreferredWidth(120); // AFORO TOTAL // ← CAMBIAR
        jTableDisponibilidad.getColumnModel().getColumn(2).setPreferredWidth(120); // DISPONIBLE // ← CAMBIAR
        jTableDisponibilidad.getColumnModel().getColumn(3).setPreferredWidth(100); // PRECIO // ← CAMBIAR
        
        // Alternar colores de filas para mejor legibilidad - CAMBIA "jTable3"
        jTableDisponibilidad.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() { // ← CAMBIAR
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
        
    } catch (Exception e) {
        System.err.println("Error configurando formato de tabla disponibilidad: " + e.getMessage());
    }
}

private void agregarListenersDisponibilidad() {
    // Listener para cuando cambia la selección del partido
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
        
        // Obtener el modelo de la tabla - CAMBIA "jTable3"
        DefaultTableModel model = (DefaultTableModel) jTableDisponibilidad.getModel(); // ← CAMBIAR
        model.setRowCount(0); // Limpiar tabla
        
        // Determinar si mostrar todos los partidos o uno específico
        boolean mostrarTodos = "Todos los partidos".equals(partidoSeleccionado.toString());
        
        if (mostrarTodos) {
            // Mostrar disponibilidad consolidada de todos los partidos
            cargarDisponibilidadConsolidada();
        } else {
            // Mostrar disponibilidad para el partido específico
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
        
        // Buscar el partido seleccionado
        Partido partidoSeleccionado = buscarPartidoPorDescripcion(descripcionPartido);
        
        if (partidoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró el partido seleccionado", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Para cada localidad, mostrar disponibilidad para este partido específico
        for (Localidad localidad : localidadesListDisponibilidad) {
            int idPartido = partidoSeleccionado.getId_partido();
            int idLocalidad = localidad.getId_localidad();
            
            int disponible = obtenerDisponibilidadPorPartidoYLocalidad(idPartido, idLocalidad);
            
            // Obtener el precio desde el inventario, no desde la localidad
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
        
        // Para cada localidad, calcular disponibilidad total en todos los partidos
        for (Localidad localidad : localidadesListDisponibilidad) {
            int totalDisponible = calcularTotalDisponiblePorLocalidad(localidad.getId_localidad());
            
            // Obtener precio promedio desde el inventario
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
            // Asumiendo que existe el campo cantidad_total, si no, usa cantidad_disponible
            total += inventario.getCantidad_disponible(); // Cambia por getCantidad_total() si existe
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
            // Asumiendo que existe el campo cantidad_total, si no, usa cantidad_disponible
            return inventario.getCantidad_disponible(); // Cambia por getCantidad_total() si existe
        }
    }
    return 0;
}

private void limpiarTablaDisponibilidad() {
    // CAMBIA "jTable3" POR EL NOMBRE REAL
    DefaultTableModel model = (DefaultTableModel) jTableDisponibilidad.getModel(); // ← CAMBIAR
    model.setRowCount(0);
}

private void actualizarDatosDisponibilidad() {
    try {
        JOptionPane.showMessageDialog(this, 
            "Actualizando datos de disponibilidad...", 
            "Actualizando", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Recargar datos desde la base de datos
        cargarDatosParaDisponibilidad();
        
        // Actualizar la tabla
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
    // Cuando cambia la localidad, actualizar precio unitario
    jComboBox4.addActionListener(e -> {
        actualizarPrecioUnitario();  // Actualiza userTxt7
        actualizarStockDisponible(); // Actualiza jLabel101
    });
    
    // Cuando cambia el partido, también actualizar precio unitario
    jComboBox3.addActionListener(e -> {
        actualizarPrecioUnitario();  // Actualiza userTxt7  
        actualizarStockDisponible(); // Actualiza jLabel101
    });
    
    // Cuando cambia la cantidad, actualizar precio total
    jSpinnerCantidad.addChangeListener(e -> {
        actualizarPrecioTotal();     // Actualiza jLabel100
        validarCantidadConStock();   // Valida stock
    });
}
private void configurarTablaDisponibilidad() {
    try {
        // Definir columnas
        String[] columnNames = {"LOCALIDAD", "AFORO TOTAL", "DISPONIBLE", "PRECIO"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;  // LOCALIDAD
                    case 1: return Integer.class; // AFORO TOTAL
                    case 2: return Integer.class; // DISPONIBLE
                    case 3: return Double.class;  // PRECIO
                    default: return Object.class;
                }
            }
        };
        
        // Aplicar modelo a la tabla (asumo que se llama jTableDisponibilidad o similar)
        // Cambia "jTableDisponibilidad" por el nombre real de tu tabla en el JFrame
        jTableDisponibilidad.setModel(model);
        
        // Configurar formato de la tabla
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
        
        // Actualizar jLabel101 con el stock disponible real
        jLabel101.setText(String.valueOf(stockDisponible));
        
        // Cambiar color según disponibilidad
        actualizarColorStock(stockDisponible);
        
        // Actualizar las cantidades disponibles en el spinner
        actualizarOpcionesCantidad(stockDisponible);
    }
}
private void actualizarColorStock(int stockDisponible) {
    if (stockDisponible == 0) {
        jLabel101.setForeground(Color.RED);
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.BOLD));
    } else if (stockDisponible < 10) {
        jLabel101.setForeground(Color.ORANGE);
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.BOLD));
    } else {
        jLabel101.setForeground(Color.BLACK);
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
            // Ajustar automáticamente al máximo disponible
            jSpinnerCantidad.setValue(stockDisponible);
        }
    }
}
private int obtenerStockDisponible(String nombreLocalidad, Object partidoSeleccionado) {
    try {
        // Obtener el ID de la localidad
        int idLocalidad = obtenerIdLocalidad(nombreLocalidad);
        if (idLocalidad == -1) {
            throw new Exception("No se encontró la localidad: " + nombreLocalidad);
        }
        
        // Obtener el ID del partido
        int idPartido = obtenerIdPartido(partidoSeleccionado);
        if (idPartido == -1) {
            throw new Exception("No se encontró el partido seleccionado");
        }
        
        // Buscar en el inventario cargado
        if (inventarioList != null) {
            for (Inventario inventario : inventarioList) {
                if (inventario.getId_localidad() == idLocalidad && 
                    inventario.getId_partido() == idPartido) {
                    return inventario.getCantidad_disponible();
                }
            }
        }
        
        // Si no encuentra en la lista cargada, consultar directamente a la API
        return consultarStockDirecto(idLocalidad, idPartido);
        
    } catch (Exception e) {
        System.err.println("Error obteniendo stock: " + e.getMessage());
        return 0; // Si hay error, retornar 0 para evitar ventas incorrectas
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
        return 0; // No encontrado = 0 stock
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
            // Buscar por toString() en la lista
            String nombrePartido = partidoSeleccionado.toString();
            for (Partido partido : partidosList) {
                if (partido.toString().equals(nombrePartido)) {
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
            // Actualizar userTxt7 con el precio unitario
            userTxt7.setText(String.format("%.2f", precio));
            
            // También actualizar el precio total
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

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void header5MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header5MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_header5MouseDragged

    private void header5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_header5MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_header5MousePressed

    private void userTxt4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTxt4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt4ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void userLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userLabel5MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card3"); 
    }//GEN-LAST:event_userLabel5MouseClicked

    private void btnBack1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBack1ActionPerformed
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card2"); 
    }//GEN-LAST:event_btnBack1ActionPerformed

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card5"); 
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        CardLayout cl = (CardLayout) MainPanel.getLayout();
        cl.show(MainPanel, "card2"); 
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jLabel18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel18MouseClicked

    private void loginBtnTxt3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt3MouseExited
        loginBtn.setBackground(new Color(0,134,190));
    }//GEN-LAST:event_loginBtnTxt3MouseExited

    private void loginBtnTxt3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt3MouseEntered
        loginBtn.setBackground(new Color(0, 156, 223));
    }//GEN-LAST:event_loginBtnTxt3MouseEntered

    private void loginBtnTxt3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginBtnTxt3MouseClicked
        javax.swing.JOptionPane.showMessageDialog(this, "Intento de login con los datos:\nUsuario: " + userTxt8.getText() + "\nContraseña: " + String.valueOf(passTxt1.getPassword()), "LOGIN", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_loginBtnTxt3MouseClicked

    private void jLabel105MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel105MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel105MouseClicked

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel21MouseClicked

    private void btnCerrarVentaVendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarVentaVendedorActionPerformed
    try {
        // Verificar que haya items en el carrito
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmar venta
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Confirmar venta?\n\n" +
            "Total: Q" + String.format("%.2f", totalVenta) + "\n" +
            "Items: " + jTable1.getRowCount(),
            "Confirmar Venta",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Procesar la venta en la base de datos
            procesarVenta();

            // Limpiar todo
            limpiarCarrito();

            JOptionPane.showMessageDialog(this,
                "✅ Venta procesada exitosamente!\n" +
                "Total: Q" + String.format("%.2f", totalVenta),
                "Venta Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
        }

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
        // Validar que todos los campos estén seleccionados
        if (jComboBox3.getSelectedItem() == null ||
            jComboBox4.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Obtener valores seleccionados desde los componentes
        Object partidoObj = jComboBox3.getSelectedItem();
        String partido = partidoObj.toString();
        String localidad = jComboBox4.getSelectedItem().toString();
        int cantidad = (int) jSpinnerCantidad.getValue();
        
        // Validar que la cantidad sea mayor a 0
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this,
                "La cantidad debe ser mayor a 0",
                "Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar stock disponible con datos reales de la base de datos
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
        
        // Obtener precio unitario DESDE EL CAMPO userTxt7 (no directamente de la BD)
        double precioUnitario;
        try {
            precioUnitario = Double.parseDouble(userTxt7.getText().trim());
        } catch (NumberFormatException e) {
            // Si hay error al leer userTxt7, obtener de la BD como fallback
            // USAR LA NUEVA VERSIÓN QUE REQUIERE PARTIDO
            precioUnitario = obtenerPrecioLocalidad(localidad, partidoObj);
            // Y actualizar el campo para que muestre el valor correcto
            userTxt7.setText(String.format("%.2f", precioUnitario));
        }
        
        // Obtener precio total DESDE EL CAMPO jLabel100
        double precioTotal;
        try {
            precioTotal = Double.parseDouble(jLabel100.getText().trim());
        } catch (NumberFormatException e) {
            // Si hay error, calcularlo
            precioTotal = precioUnitario * cantidad;
            // Y actualizar el campo
            jLabel100.setText(String.format("%.2f", precioTotal));
        }
        
        double subtotal = precioUnitario * cantidad;
        
        // Añadir a la tabla
        añadirFilaAlCarrito(partido, localidad, cantidad, precioUnitario, subtotal);
        
        // Actualizar total de la venta
        totalVenta += subtotal;
        actualizarTotales();
        
        // Limpiar campos
        limpiarCamposVenta();
        
        // Mostrar mensaje de confirmación
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

    private void userTxt7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTxt7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt7ActionPerformed

    private void userTxt7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxt7MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt7MousePressed

    private void userTxt6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTxt6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt6ActionPerformed

    private void userTxt6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxt6MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt6MousePressed

    private void userTxt5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTxt5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt5ActionPerformed

    private void userTxt5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxt5MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxt5MousePressed

    private void jLabel53MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel53MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel53MouseClicked

    private void jLabel110MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel110MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel110MouseClicked

    private void jLabel57MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel57MouseClicked
       // DISPONIBILIDAD - Ir al panel de disponibilidad
    CardLayout cl = (CardLayout) MainPanel.getLayout();
    cl.show(MainPanel, "card6");
    }//GEN-LAST:event_jLabel57MouseClicked

    private void jLabel102MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel102MouseClicked
        // PARTIDOS - Ir al panel de partidos
    CardLayout cl = (CardLayout) MainPanel.getLayout();
    cl.show(MainPanel, "card10"); 
    }//GEN-LAST:event_jLabel102MouseClicked
private void procesarVenta() {
    try {
        System.out.println("=== PROCESANDO VENTA ===");
        
        // Obtener datos del carrito
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        // Crear lista de detalles
        java.util.List<Detalle> detalles = new java.util.ArrayList<>();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String partidoNombre = model.getValueAt(i, 0).toString();
            String localidadNombre = model.getValueAt(i, 1).toString();
            int cantidad = Integer.parseInt(model.getValueAt(i, 2).toString());
            double precioUnitario = Double.parseDouble(model.getValueAt(i, 3).toString());
            
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
        }
        
        // Crear y enviar venta
        Venta venta = new Venta();
        venta.setId_vendedor(SessionManager.getInstance().getUsuarioId());
        venta.setTotal_venta(totalVenta);
        venta.setDetalles(detalles);
        
        Servicios.ServicioVenta servicioVentas = new Servicios.ServicioVenta();
        
        // Solo intentar crear la venta - si no hay excepción, es éxito
        servicioVentas.createVenta(venta);
        
        // Si llegamos aquí sin excepciones, la venta se creó
        System.out.println("✅ Venta procesada exitosamente");
        
    } catch (Exception e) {
        System.err.println("❌ Error procesando venta: " + e.getMessage());
        throw new RuntimeException("Error al procesar venta: " + e.getMessage(), e);
    }
}
private int obtenerIdPartidoPorNombre(String nombrePartidoCompleto) {
    try {
        System.out.println("🔍 Búsqueda robusta para: '" + nombrePartidoCompleto + "'");
        
        if (partidosList == null) return -1;
        
        // Buscar por coincidencia parcial
        for (Partido partido : partidosList) {
            String partidoSimple = partido.getEquipo_local() + " vs " + partido.getEquipo_visitante();
            
            // Si el string completo contiene los nombres de equipos
            if (nombrePartidoCompleto.contains(partido.getEquipo_local()) && 
                nombrePartidoCompleto.contains(partido.getEquipo_visitante())) {
                
                System.out.println("✅ Partido encontrado por coincidencia: " + partidoSimple + " -> ID: " + partido.getId_partido());
                return partido.getId_partido();
            }
            
            // Si los nombres de equipos contienen el string (caso inverso)
            if (partidoSimple.contains(nombrePartidoCompleto) || 
                nombrePartidoCompleto.contains(partidoSimple)) {
                
                System.out.println("✅ Partido encontrado por contenido: " + partidoSimple + " -> ID: " + partido.getId_partido());
                return partido.getId_partido();
            }
        }
        
        System.err.println("❌ No se pudo encontrar partido para: " + nombrePartidoCompleto);
        return -1;
        
    } catch (Exception e) {
        System.err.println("Error en búsqueda robusta: " + e.getMessage());
        return -1;
    }
}
private List<String> generarVariacionesBusqueda(String partidoCompleto) {
    List<String> variaciones = new ArrayList<>();
    
    // Agregar el nombre completo
    variaciones.add(partidoCompleto);
    
    // Extraer solo equipos si tiene formato con fecha
    if (partidoCompleto.contains(" - ")) {
        String[] partes = partidoCompleto.split(" - ");
        if (partes.length > 0) {
            variaciones.add(partes[0].trim()); // "Guatemala vs México"
        }
    }
    
    // Extraer solo equipos si tiene formato con timestamp
    if (partidoCompleto.contains("T")) {
        String[] partes = partidoCompleto.split("T");
        if (partes.length > 0) {
            variaciones.add(partes[0].trim()); // "Guatemala vs México - 2025-11-21"
        }
    }
    
    // Intentar extraer solo los nombres sin "vs"
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
        // El formato es: "Guatemala vs México - 2025-11-21T01:00:00.000Z"
        // Queremos extraer: "Guatemala vs México"
        
        if (partidoCompleto.contains(" - ")) {
            String[] partes = partidoCompleto.split(" - ");
            if (partes.length > 0) {
                return partes[0].trim(); // Retorna "Guatemala vs México"
            }
        }
        
        // Si no tiene el formato esperado, retornar el original
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
        System.err.println("❌ No se encontró localidad: '" + nombreLocalidad + "'");
        System.err.println("Localidades disponibles: " + (localidadesList != null ? localidadesList.size() : 0));
        return -1;
    } catch (Exception e) {
        System.err.println("Error obteniendo ID localidad: " + e.getMessage());
        return -1;
    }
}
private void limpiarCarrito() {
    try {
        // 1. Limpiar la tabla del carrito
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Eliminar todas las filas
        
        // 2. Resetear el total de la venta
        totalVenta = 0.0;
        actualizarTotales(); // Esto actualizará userTxt5
        
        // 3. Limpiar todos los campos de selección
        jComboBox3.setSelectedIndex(0); // Partido
        jComboBox4.setSelectedIndex(0); // Localidad
        
        // 4. Resetear el spinner de cantidad
        jSpinnerCantidad.setValue(1);
        
        // 5. Limpiar el precio unitario
        jLabel100.setText("0.00");
        
        // 6. Resetear el label de disponibles (jLabel101)
        jLabel101.setText("DISPONIBLES: ");
        jLabel101.setForeground(new Color(204, 204, 204)); // Color gris original
        jLabel101.setFont(jLabel101.getFont().deriveFont(Font.PLAIN));
        
        // 7. Habilitar el spinner (por si estaba deshabilitado por stock 0)
        jSpinnerCantidad.setEnabled(true);
        
        // 8. Resetear el modelo del spinner a valores por defecto
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) jSpinnerCantidad.getModel();
        spinnerModel.setMinimum(1);
        spinnerModel.setMaximum(100); // O el valor máximo que uses
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
        System.out.println("=== CARGANDO LOCALIDADES EN TABLA ===");
        
        // Obtener localidades desde la base de datos
        Servicios.ServicioLocalidade servicioLocalidades = new Servicios.ServicioLocalidade();
        List<Localidad> localidades = servicioLocalidades.getLocalidades();
        
        // Obtener inventario desde la base de datos
        ServicioInventario servicioInventario = new ServicioInventario();
        List<Inventario> inventarios = servicioInventario.getInventario();
        
        System.out.println("Localidades obtenidas: " + localidades.size());
        System.out.println("Registros de inventario: " + inventarios.size());
        
        // Crear modelo de tabla
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"NOMBRE", "PRECIO", "DISPONIBLES"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;  // Nombre
                    case 1: return Double.class;  // Precio
                    case 2: return Integer.class; // Disponibles
                    default: return Object.class;
                }
            }
        };
        
        // Combinar datos de localidades con inventario
        for (Localidad localidad : localidades) {
            // Calcular total disponible para esta localidad (suma de todos los partidos)
            int totalDisponible = calcularTotalDisponible(localidad.getId_localidad(), inventarios);
            
            // Obtener el precio promedio de esta localidad (puede variar por partido)
            double precioPromedio = calcularPrecioPromedio(localidad.getId_localidad(), inventarios);
            
            model.addRow(new Object[]{
                localidad.getNombre(),
                precioPromedio,
                totalDisponible
            });
        }
        
        // Aplicar el modelo a la tabla
        jTable2.setModel(model);
        
        // Configurar formato de la tabla
        configurarFormatoTablaLocalidades();
        
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


    /**
     * @param args the command line arguments
     */
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
    private javax.swing.JPanel CREARCUENTA;
    private javax.swing.JPanel DISPONIBLE2;
    private javax.swing.JPanel HISTORIAL;
    private javax.swing.JPanel INICIO;
    private javax.swing.JPanel INVENTARIO;
    private javax.swing.JPanel LOBBY;
    private javax.swing.JPanel LOCALIDADES;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel NewLocalidades;
    private javax.swing.JPanel NewPartido;
    private javax.swing.JPanel NewUser;
    private javax.swing.JPanel PARTIDOS;
    private javax.swing.JPanel VENTA;
    private javax.swing.JButton btnBack1;
    private javax.swing.JButton btnCerrarVentaVendedor;
    private javax.swing.JPanel exitBtn3;
    private javax.swing.JPanel exitBtn4;
    private javax.swing.JPanel exitBtn5;
    private javax.swing.JLabel favicon;
    private javax.swing.JLabel favicon1;
    private javax.swing.JLabel favicon10;
    private javax.swing.JLabel favicon11;
    private javax.swing.JLabel favicon12;
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
    private javax.swing.JLabel favicon24;
    private javax.swing.JLabel favicon25;
    private javax.swing.JLabel favicon26;
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
    private javax.swing.JLabel favicon4;
    private javax.swing.JLabel favicon5;
    private javax.swing.JLabel favicon6;
    private javax.swing.JLabel favicon7;
    private javax.swing.JLabel favicon9;
    private javax.swing.JPanel header3;
    private javax.swing.JPanel header4;
    private javax.swing.JPanel header5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox12;
    private javax.swing.JComboBox<String> jComboBox18;
    private javax.swing.JComboBox<String> jComboBox19;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
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
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JLabel jLabel7;
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
    private javax.swing.JPanel jPanel14;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JSpinner jSpinnerCantidad;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTable9;
    private javax.swing.JTable jTableDisponibilidad;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel loginBtn;
    private javax.swing.JPanel loginBtn1;
    private javax.swing.JPanel loginBtn12;
    private javax.swing.JPanel loginBtn2;
    private javax.swing.JPanel loginBtn3;
    private javax.swing.JPanel loginBtn4;
    private javax.swing.JPanel loginBtn5;
    private javax.swing.JPanel loginBtn6;
    private javax.swing.JPanel loginBtn7;
    private javax.swing.JPanel loginBtn8;
    private javax.swing.JPanel loginBtn9;
    private javax.swing.JLabel loginBtnTxt1;
    private javax.swing.JLabel loginBtnTxt2;
    private javax.swing.JLabel loginBtnTxt3;
    private javax.swing.JLabel loginBtnTxt4;
    private javax.swing.JLabel logo2;
    private javax.swing.JLabel logo3;
    private javax.swing.JLabel passLabel1;
    private javax.swing.JLabel passLabel2;
    private javax.swing.JLabel passLabel3;
    private javax.swing.JLabel passLabel4;
    private javax.swing.JPasswordField passTxt1;
    private javax.swing.JPasswordField passTxt2;
    private javax.swing.JLabel title;
    private javax.swing.JLabel title1;
    private javax.swing.JLabel title2;
    private javax.swing.JLabel title3;
    private javax.swing.JLabel title4;
    private javax.swing.JLabel userLabel1;
    private javax.swing.JLabel userLabel10;
    private javax.swing.JLabel userLabel11;
    private javax.swing.JLabel userLabel2;
    private javax.swing.JLabel userLabel4;
    private javax.swing.JLabel userLabel5;
    private javax.swing.JLabel userLabel6;
    private javax.swing.JLabel userLabel7;
    private javax.swing.JLabel userLabel8;
    private javax.swing.JLabel userLabel9;
    private javax.swing.JTextField userTxt1;
    private javax.swing.JTextField userTxt4;
    private javax.swing.JTextField userTxt5;
    private javax.swing.JTextField userTxt6;
    private javax.swing.JTextField userTxt7;
    private javax.swing.JTextField userTxt8;
    // End of variables declaration//GEN-END:variables
}
