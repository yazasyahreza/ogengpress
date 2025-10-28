/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Ogengpress;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author Yajong
 */
public class FormBarang extends javax.swing.JFrame {

    /**
     * Creates new form FormBarang
     */
    Database db = new Database();
    
    public FormBarang() {
        initComponents();
        db.koneksi();
        
        // Fullscreen 
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        
        viewData();
        loadFilter();
        nonAktifButton();
        aktifButton();
        jkd_brg.requestFocus();
        designButton();
        shortcutApp();
    }
    
    private void designButton(){
        btn_tambah.setOpaque(true);
        btn_tambah.setContentAreaFilled(true);
        btn_tambah.setBorderPainted(false);
        btn_tambah.setBackground(new Color(51,153,255));
        
        btn_perbarui.setOpaque(true);
        btn_perbarui.setContentAreaFilled(true);
        btn_perbarui.setBorderPainted(false);
        btn_perbarui.setBackground(new Color(255,255,102));
        
        btn_batal.setOpaque(true);
        btn_batal.setContentAreaFilled(true);
        btn_batal.setBorderPainted(false);
        btn_batal.setBackground(new Color(240,240,240));
        
        btn_hapus.setOpaque(true);
        btn_hapus.setContentAreaFilled(true);
        btn_hapus.setBorderPainted(false);
        btn_hapus.setBackground(new Color(255,102,102));
        
        btn_kembali.setOpaque(true);
        btn_kembali.setContentAreaFilled(true);
        btn_kembali.setBorderPainted(false);
        btn_kembali.setBackground(new Color(255,204,204));
    }
    
    private void shortcutApp(){
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke("alt F1"), "tambah");
        actionMap.put("tambah", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_tambah.doClick();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("alt F2"), "perbarui");
        actionMap.put("perbarui", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_perbarui.doClick();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("alt F3"), "batal");
        actionMap.put("batal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_batal.doClick();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("alt F4"), "hapus");
        actionMap.put("hapus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_hapus.doClick();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("alt F5"), "kembali");
        actionMap.put("kembali", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_kembali.doClick();
            }
        });
    }
    
    private void viewData(){
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("Kode");
        model.addColumn("Nama");
        model.addColumn("Stok");
        model.addColumn("Kategori");
        model.addColumn("Harga");
        
        try {
            ResultSet rs = db.ambilData("select * from barang");
            while(rs.next()){
                NumberFormat kursIndo = NumberFormat.getIntegerInstance(new Locale("id", "ID"));
                int harga = rs.getInt("harga");
                
                model.addRow(new Object[]{rs.getString("kd_brg"), rs.getString("nama"), rs.getString("stok"),
                rs.getString("kategori"), kursIndo.format(harga)});
                tbl_data.setModel(model);
                
                tbl_data.getColumnModel().getColumn(0).setPreferredWidth(260); 
                tbl_data.getColumnModel().getColumn(1).setPreferredWidth(285); 
                tbl_data.getColumnModel().getColumn(2).setPreferredWidth(85); 
                tbl_data.getColumnModel().getColumn(3).setPreferredWidth(185); 
                tbl_data.getColumnModel().getColumn(4).setPreferredWidth(110); 
                
                tbl_data.getTableHeader().setFont(new Font("Yu Gothic UI Semilight", Font.BOLD, 12));
                tbl_data.getTableHeader().setOpaque(false);
                tbl_data.getTableHeader().setBackground(new Color(205, 220, 234));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Debug", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadFilter(){
        try{
            jfilter.removeAllItems();
            jfilter.addItem("-- Pilih Kategori --");
            
            ResultSet rs = db.ambilData("select distinct kategori from barang");
            while(rs.next()){
                jfilter.addItem(rs.getString("kategori"));
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error ambil data kategori: "+e.getMessage());
        }
        
        jfilter.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            String selectedKategori = (String) jfilter.getSelectedItem();
            if (selectedKategori != null && !selectedKategori.equals("-- Pilih Kategori --")) {
                filterByJenis(selectedKategori);
            } else {
                viewData(); // tampilkan semua lagi
            }
        }
        });
    }
    
    private void filterByJenis(String kategori){
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("Kode");
        model.addColumn("Nama");
        model.addColumn("Stok");
        model.addColumn("Kategori");
        model.addColumn("Harga");
        
        try {
            ResultSet rs = db.ambilData("select * from barang where kategori='"+kategori+"'");
            while (rs.next()) {                
                model.addRow(new Object[]{
                    rs.getString("kd_brg"),
                    rs.getString("nama"),
                    rs.getString("stok"),
                    rs.getString("kategori"),
                    rs.getString("harga")
                });
            }
            tbl_data.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filter kategori: "+e.getMessage());
        }
    }
    
    private void nonAktifButton(){
        btn_hapus.setEnabled(false);
        btn_perbarui.setEnabled(false);
    }
    
    private void aktifButton(){
        btn_tambah.setEnabled(true);
        btn_batal.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jkd_brg = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jnama = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jstok = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jkategori = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jharga = new javax.swing.JTextField();
        btn_tambah = new javax.swing.JButton();
        btn_perbarui = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcari = new javax.swing.JTextField();
        jfilter = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_data = new javax.swing.JTable();
        btn_kembali = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(1515, 785));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel2.setFont(new java.awt.Font("Goudy Stout", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("PENGELOLAAN DATA");

        jLabel3.setFont(new java.awt.Font("Goudy Stout", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 102, 255));
        jLabel3.setText("OGENG PRESS");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(512, 512, 512))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(0, 51, 153));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel7.setText("Kode");

        jkd_brg.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jkd_brg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jkd_brgActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel8.setText("Nama");

        jnama.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel9.setText("Stok");

        jstok.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel10.setText("Kategori");

        jkategori.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel11.setText("Harga");

        jharga.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jharga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jhargaActionPerformed(evt);
            }
        });
        jharga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jhargaKeyReleased(evt);
            }
        });

        btn_tambah.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        btn_tambah.setText("TAMBAH (alt F1)");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_perbarui.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        btn_perbarui.setText("PERBARUI (alt F2)");
        btn_perbarui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_perbaruiActionPerformed(evt);
            }
        });

        btn_batal.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        btn_batal.setText("BATAL (alt F3)");
        btn_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batalActionPerformed(evt);
            }
        });

        btn_hapus.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        btn_hapus.setText("HAPUS (alt F4)");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btn_tambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_perbarui)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_batal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_hapus))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel11)
                        .addComponent(jLabel10)
                        .addComponent(jLabel9)
                        .addComponent(jLabel8)
                        .addComponent(jLabel7)
                        .addComponent(jkategori, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                        .addComponent(jstok)
                        .addComponent(jnama)
                        .addComponent(jkd_brg)
                        .addComponent(jharga)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jkd_brg, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jnama, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jstok, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jkategori, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jharga, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_perbarui, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel1.setText("Cari Data:");

        jcari.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jcari.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jcariMouseMoved(evt);
            }
        });
        jcari.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jcariMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jcariMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jcariMouseReleased(evt);
            }
        });
        jcari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcariActionPerformed(evt);
            }
        });
        jcari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jcariKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jcariKeyTyped(evt);
            }
        });

        jfilter.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jfilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jfilter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jfilterMouseClicked(evt);
            }
        });
        jfilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jfilterActionPerformed(evt);
            }
        });

        tbl_data.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        tbl_data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_data.setRowHeight(30);
        tbl_data.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_dataMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbl_data);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jfilter, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcari, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jfilter, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
                .addContainerGap())
        );

        btn_kembali.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        btn_kembali.setText("Kembali (alt F5)");
        btn_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jhargaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jhargaKeyReleased
        // TODO add your handling code here:
        formatRupiah();
    }//GEN-LAST:event_jhargaKeyReleased

    private void jhargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jhargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jhargaActionPerformed

    private void jkd_brgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jkd_brgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jkd_brgActionPerformed

    private void tbl_dataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_dataMouseClicked
        // TODO add your handling code here:
        int selectedRow = tbl_data.getSelectedRow();
        if(selectedRow != -1){
            String kode = tbl_data.getValueAt(selectedRow, 0).toString();
            String nama = tbl_data.getValueAt(selectedRow, 1).toString();
            String stok = tbl_data.getValueAt(selectedRow, 2).toString();
            String kategori = tbl_data.getValueAt(selectedRow, 3).toString();
            String harga = tbl_data.getValueAt(selectedRow, 4).toString();

            jkd_brg.setText(kode);
            jnama.setText(nama);
            jstok.setText(stok);
            jkategori.setText(kategori);
            jharga.setText(harga);
            btn_tambah.setEnabled(false);
            btn_perbarui.setEnabled(true);
            btn_hapus.setEnabled(true);
        }
    }//GEN-LAST:event_tbl_dataMouseClicked

    private void jfilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jfilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jfilterActionPerformed

    private void jfilterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jfilterMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jfilterMouseClicked

    private void jcariKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcariKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jcariKeyTyped

    private void jcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcariKeyReleased
        // TODO add your handling code here:
        String key = jcari.getText();
        System.out.println(key);

        if (key!="") {
            cariData(key);
        } else {
            viewData();
        }
    }//GEN-LAST:event_jcariKeyReleased

    private void jcariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcariActionPerformed

    private void jcariMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcariMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jcariMouseReleased

    private void jcariMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcariMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jcariMouseExited

    private void jcariMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcariMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jcariMouseClicked

    private void jcariMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcariMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jcariMouseMoved

    private void btn_kembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kembaliActionPerformed
        // TODO add your handling code here:
        this.dispose();

        FormTrans formKasir = new FormTrans();
        formKasir.setVisible(true);
    }//GEN-LAST:event_btn_kembaliActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        String kode = jkd_brg.getText();
        String nama = jnama.getText();
        String stok = jstok.getText();
        String kategori = jkategori.getText();
        String harga = jharga.getText();

        if(kode.isEmpty() || nama.isEmpty() || stok.isEmpty() || kategori.isEmpty() || harga.isEmpty()){
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi !", "Validasi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ResultSet rs = db.ambilData("select * from barang where kd_brg='"+jkd_brg.getText()+"'");
            if (rs.next()){
                JOptionPane.showMessageDialog(null, "Data telah terdaftar.");
                jnama.setText(rs.getString("nama"));
                jstok.setText(rs.getString("stok"));
                jkategori.setText(rs.getString("kategori"));
                jharga.setText(rs.getString("harga"));
            }else{
                int hargaInt = parseHarga(jharga.getText());
                db.aksi("insert into barang(kd_brg,nama,stok,kategori,harga)values('"+jkd_brg.getText()+"',"
                    + "'"+jnama.getText()+"',"
                    + "'"+jstok.getText()+"',"
                    + "'"+jkategori.getText()+"',"
                    + "'"+hargaInt+"')");
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan.");
                viewData();
                resetData();
            }
        } catch (Exception e){
            JOptionPane.showConfirmDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_perbaruiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_perbaruiActionPerformed
        // TODO add your handling code here:
        String kode = jkd_brg.getText();
        String nama = jnama.getText();
        String stok = jstok.getText();
        String kategori = jkategori.getText();
        String harga = jharga.getText();

        if(kode.isEmpty() || nama.isEmpty() || stok.isEmpty() || kategori.isEmpty() || harga.isEmpty()){
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi !", "Validasi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ResultSet rs = db.ambilData("select * from barang where kd_brg='"+jkd_brg.getText()+"'");
            if (rs.next()){
                int hargaInt = parseHarga(jharga.getText());

                db.aksi("update barang set nama='"+jnama.getText()+"', stok='"+jstok.getText()+"', kategori='"+jkategori.getText()+"', harga='"+hargaInt+"'"
                    + "where kd_brg='"+jkd_brg.getText()+"'");
                JOptionPane.showMessageDialog(null, "Data berhasil di edit.");
                viewData();
                resetData();
                aktifButton();
                nonAktifButton();
            } else{
                JOptionPane.showMessageDialog(null, "Tidak ada data yang hendak di edit.");
            }
            jkd_brg.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_btn_perbaruiActionPerformed

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        // TODO add your handling code here:
        resetData();
        aktifButton();
        nonAktifButton();
        jcari.setText("");
        jfilter.setSelectedItem("-- Pilih Kategori --");
        viewData();
    }//GEN-LAST:event_btn_batalActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        try {
            ResultSet rs = db.ambilData("select * from barang where kd_brg='"+jkd_brg.getText()+"'");
            if (rs.next() ){
                int n = JOptionPane.showConfirmDialog(this, "Anda yakin?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                if (n == JOptionPane.YES_OPTION) {
                    db.aksi("delete from barang where kd_brg='"+jkd_brg.getText()+"'");
                    JOptionPane.showMessageDialog(null, "Data sudah berhasil dihapus.");
                    viewData();
                    resetData();
                    nonAktifButton();
                    aktifButton();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_btn_hapusActionPerformed
    
    private void formatRupiah(){
        if (!jharga.getText().equals("")) {
            String replace = jharga.getText().replaceAll("[^\\d]", "");
            double formatRp = Double.parseDouble(replace);
            DecimalFormat dcf = new DecimalFormat("#,###,###");
            jharga.setText(dcf.format(formatRp));
        }
    }
    
    private int parseHarga(String hargaText) {
        if (hargaText == null || hargaText.trim().isEmpty()) {
            return 0;
        }
        // Hapus semua karakter selain angka
        String cleaned = hargaText.replaceAll("[^0-9]", "");
        return Integer.parseInt(cleaned);
    }
        
    private void cariData(String key){
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("Kode");
        model.addColumn("Nama");
        model.addColumn("Stok");
        model.addColumn("Kategori");
        model.addColumn("Harga");
        
        try {
            model.setRowCount(0);
            ResultSet rs=db.ambilData("select * from barang where nama LIKE '%"+key+"%' ");
            while (rs.next()) {            
                model.addRow(new Object[]{rs.getString("kd_brg"), rs.getString("nama"), rs.getString("stok"), rs.getString("kategori"), rs.getString("harga")});
                tbl_data.setModel(model);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormBarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormBarang().setVisible(true);
            }
        });
    }
    
    private void resetData(){
        jkd_brg.setText("");
        jnama.setText("");
        jstok.setText("");
        jkategori.setText("");
        jharga.setText("");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_kembali;
    private javax.swing.JButton btn_perbarui;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jcari;
    private javax.swing.JComboBox<String> jfilter;
    private javax.swing.JTextField jharga;
    private javax.swing.JTextField jkategori;
    private javax.swing.JTextField jkd_brg;
    private javax.swing.JTextField jnama;
    private javax.swing.JTextField jstok;
    private javax.swing.JTable tbl_data;
    // End of variables declaration//GEN-END:variables
}
