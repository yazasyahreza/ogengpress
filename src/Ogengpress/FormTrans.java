/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Ogengpress;

import java.sql.ResultSet;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class FormTrans extends javax.swing.JFrame {

    /**
     * Creates new form FormTrans
     */
    
    DefaultTableModel model = new DefaultTableModel();
    
    Database db = new Database();
    
    public FormTrans() {
        initComponents();
        db.koneksi();
        
        // Fullscreen 
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        
        shortcutApp();
        designButton();        
        tglSkrg();
        aturTabel();          
        shortcutBatalPesan();        
        hitungQtyManual();       
        jno_trans.setText(generateNoTransaksi());
        jno_trans.setHorizontalAlignment(jkembali.CENTER);        
        jscan_brg.requestFocus();       
        jscan_brg.addActionListener(e -> {
            scanBarang();
        });      
        formatBayar();
    }
    
    private void designButton(){
        btn_bayar.setOpaque(true);
        btn_bayar.setContentAreaFilled(true);
        btn_bayar.setBorderPainted(false);
        btn_bayar.setBackground(new Color(51,153,255));
        
        btn_cekBarang.setOpaque(true);
        btn_cekBarang.setContentAreaFilled(true);
        btn_cekBarang.setBorderPainted(false);
        btn_cekBarang.setBackground(new Color(255,204,204));
        
        btn_cekDataTrans.setOpaque(true);
        btn_cekDataTrans.setContentAreaFilled(true);
        btn_cekDataTrans.setBorderPainted(false);
        btn_cekDataTrans.setBackground(new Color(255,204,204));
    }
    
    private void shortcutApp(){
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke("alt F1"), "cekBarang");
        actionMap.put("cekBarang", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_cekBarang.doClick();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("alt F2"), "cekDataTrans");
        actionMap.put("cekDataTrans", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_cekDataTrans.doClick();
            }
        });
        
        inputMap.put(KeyStroke.getKeyStroke("alt F3"), "bayar");
        actionMap.put("bayar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               btn_bayar.doClick();
            }
        });
    }
    
    private void formatBayar(){
        jbayar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // biar tidak konflik dengan Enter
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    // saat Enter ditekan → hitung kembali
                    hitungKembali();
                } else {
                    String text = jbayar.getText().replaceAll("[^0-9]", "");
                    if (text.isEmpty()) {
                        jbayar.setText("Rp ");
                        return;
                    }
                    try {
                        long value = Long.parseLong(text);
                        jbayar.setText("Rp " + String.format("%,d", value).replace(",", "."));
                    } catch (NumberFormatException ex) {
                        jbayar.setText("Rp ");
                    }
                }
            }
       });
    }
    
    private void hitungKembali() {
        try {
            // ambil total
            String totalText = jtotal_bayar.getText().replaceAll("[^0-9]", "");
            long total = totalText.isEmpty() ? 0 : Long.parseLong(totalText);

            // ambil bayar
            String bayarText = jbayar.getText().replaceAll("[^0-9]", "");
            long bayar = bayarText.isEmpty() ? 0 : Long.parseLong(bayarText);

            long kembali = bayar - total;

            if (kembali < 0) {
                jkembali.setText("Belum cukup");
            } else {
                jkembali.setText("Rp " + String.format("%,d", kembali).replace(",", "."));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error hitung kembali: " + ex.getMessage());
        }
    }
    
    private void tglSkrg() {
        Date skrg = new Date();
        // format: Hari, dd-MMMM-yyyy
        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        String tanggal = format.format(skrg);
        jtanggal.setText(tanggal);
    }
    
    private void aturTabel(){
        model.addColumn("No");
        model.addColumn("Kode Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Harga");
        model.addColumn("Qty");
        model.addColumn("Subtotal");
        tbl_trans.setModel(model);
        
        tbl_trans.setAutoResizeMode(tbl_trans.AUTO_RESIZE_OFF);
        
        tbl_trans.getColumnModel().getColumn(0).setPreferredWidth(28); 
        tbl_trans.getColumnModel().getColumn(1).setPreferredWidth(280); 
        tbl_trans.getColumnModel().getColumn(2).setPreferredWidth(280); 
        tbl_trans.getColumnModel().getColumn(3).setPreferredWidth(185); 
        tbl_trans.getColumnModel().getColumn(4).setPreferredWidth(110); 
        tbl_trans.getColumnModel().getColumn(5).setPreferredWidth(185);
        
        tbl_trans.getTableHeader().setFont(new Font("Yu Gothic UI Semilight", Font.BOLD, 12));
        tbl_trans.getTableHeader().setOpaque(false);
        tbl_trans.getTableHeader().setBackground(new Color(205, 220, 234));
    }
    
    private void shortcutBatalPesan() {
    // Shortcut D (hapus pesanan)
        tbl_trans.getInputMap(JComponent.WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "hapusPesanan"
        );
        tbl_trans.getActionMap().put("hapusPesanan", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tbl_trans.getSelectedRow();
                if (row >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(FormTrans.this,
                        "Batalkan pesanan untuk barang ini?",
                        "Konfirmasi",
                        JOptionPane.OK_CANCEL_OPTION
                    );
                    if (confirm == JOptionPane.OK_OPTION) {
                        model.removeRow(row);
                        updateNomorUrut();
                        hitungTotal();
                    }
                }
            }
        });
    }
    
    private void hitungQtyManual(){
        model.addTableModelListener(e -> {
            if (e.getColumn() == 4) { // kolom Qty
                int row = e.getFirstRow();
                int qty = Integer.parseInt(model.getValueAt(row, 4).toString());
                int harga = ambilAngkSaja(model.getValueAt(row, 3).toString());
                model.setValueAt(harga * qty, row, 5);
                hitungTotal();
            }
        });
    }

    private void scanBarang(){
        String kode = jscan_brg.getText().trim();
        if (kode.isEmpty()) return;
        
        try {
            ResultSet rs = db.ambilData("SELECT kd_brg, nama, harga FROM barang WHERE kd_brg='"+ kode + "'");
            if (rs.next()) {
                String kd_brg = rs.getString("kd_brg");
                String nama = rs.getString("nama");
                int harga = rs.getInt("harga");
                
                tambahKeTabel(kd_brg, nama, harga);
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada data barang.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        jscan_brg.setText("");
    }
    
    private void tambahKeTabel(String kode, String nama, int harga){ 
        boolean found = false; 
        for (int i = 0; i < model.getRowCount(); i++) { 
            String namaRow = model.getValueAt(i, 2).toString(); 
            if (namaRow.equals(nama)){ 
                int qty = Integer.parseInt(model.getValueAt(i, 4).toString()) + 1; 
                
                model.setValueAt(qty, i, 4); 
                model.setValueAt(harga * qty, i, 5); 
                found = true; 
                break; 
            } 
        } 
        if(!found){ 
            model.addRow(new Object[]{ 
                model.getRowCount() + 1, kode, nama, harga, 1, harga }); 
        } 
        updateNomorUrut(); 
        hitungTotal(); 
    }

    private void updateNomorUrut(){
        for (int i = 0; i < model.getRowCount(); i++){
            model.setValueAt(i + 1, i, 0);
        }
    }
    
    private void hitungTotal(){
        int total = 0;
        for (int i = 0; i < model.getRowCount(); i++){
            int subtotal = ambilAngkSaja(model.getValueAt(i, 5).toString());
            total += subtotal;
        }

        jtotal_bayar.setText("Rp " + NumberFormat.getIntegerInstance(new Locale("id", "ID")).format(total));
    }

    
    private String generateNoTransaksi(){
        String noTrans = "OP1";
        try {
            String sql = "SELECT id FROM transaksi ORDER BY id DESC LIMIT 1";
            ResultSet rs = db.ambilData(sql);
            if (rs.next()){
                String lastId = rs.getString("id");
                int num = Integer.parseInt(lastId.substring(2));
                num++;
                noTrans = "OP" + num;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generate no transaksi: " + e.getMessage());
        }
        return noTrans;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtanggal = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jscan_brg = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jtotal_bayar = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_trans = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jkembali = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jbayar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jno_trans = new javax.swing.JTextField();
        btn_cekBarang = new javax.swing.JButton();
        btn_bayar = new javax.swing.JButton();
        btn_cekDataTrans = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        jPanel1.setBackground(new java.awt.Color(0, 51, 153));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Goudy Stout", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("OGENG");

        jLabel2.setFont(new java.awt.Font("Goudy Stout", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 102, 255));
        jLabel2.setText("PRESS");

        jtanggal.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jtanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 19, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jtanggal)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel1.setText("Scan Barang");

        jscan_brg.setBackground(new java.awt.Color(255, 255, 51));
        jscan_brg.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jscan_brg, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jscan_brg, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel4.setText("Total Pembayaran:");

        jtotal_bayar.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 48)); // NOI18N
        jtotal_bayar.setForeground(new java.awt.Color(255, 102, 102));
        jtotal_bayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtotal_bayarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtotal_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(220, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtotal_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(0, 51, 153));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(984, 431));

        jScrollPane1.setBackground(new java.awt.Color(242, 242, 242));

        tbl_trans.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        tbl_trans.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_trans.setRowHeight(30);
        tbl_trans.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_transMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_trans);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jkembali.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 28)); // NOI18N
        jkembali.setForeground(new java.awt.Color(255, 102, 102));

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel5.setText("Kembali:");

        jbayar.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 28)); // NOI18N
        jbayar.setForeground(new java.awt.Color(255, 102, 102));
        jbayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbayarActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 12)); // NOI18N
        jLabel6.setText("Bayar:");

        jno_trans.setBackground(new java.awt.Color(255, 102, 102));
        jno_trans.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 28)); // NOI18N
        jno_trans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jno_transActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jkembali, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addComponent(jno_trans)
                    .addComponent(jbayar)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jno_trans, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbayar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jkembali, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btn_cekBarang.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        btn_cekBarang.setText("Cek Barang (alt F1)");
        btn_cekBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cekBarangActionPerformed(evt);
            }
        });

        btn_bayar.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 16)); // NOI18N
        btn_bayar.setText("Bayar (alt F3)");
        btn_bayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bayarActionPerformed(evt);
            }
        });

        btn_cekDataTrans.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 12)); // NOI18N
        btn_cekDataTrans.setText("Cek Data Penjualan (alt F2)");
        btn_cekDataTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cekDataTransActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 1085, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_bayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btn_cekBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_cekDataTrans, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_bayar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_cekBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btn_cekDataTrans, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jtotal_bayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtotal_bayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtotal_bayarActionPerformed

    private void jno_transActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jno_transActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jno_transActionPerformed

    private void jbayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbayarActionPerformed

    private void tbl_transMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_transMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_transMouseClicked
    
    private int ambilAngkSaja(String value) {
        // Hapus semua yang bukan angka (Rp, titik, spasi, dll)
        value = value.replaceAll("[^0-9]", "");  

        if (value.isEmpty()) {
            return 0; // kalau kosong anggap 0
        }
        return Integer.parseInt(value);
}
    
    private void btn_cekBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cekBarangActionPerformed
        // TODO add your handling code here:
        this.dispose();
        
        FormBarang formData = new FormBarang();
        formData.setVisible(true);
    }//GEN-LAST:event_btn_cekBarangActionPerformed

    private void btn_bayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_bayarActionPerformed
        // TODO add your handling code here:
        int row = tbl_trans.getRowCount();
        Date skrg = new Date();
        
        try {
            int total = ambilAngkSaja(jtotal_bayar.getText());
            int bayar = ambilAngkSaja(jbayar.getText());
            int kembali = ambilAngkSaja(jkembali.getText());
            
            SimpleDateFormat formatDB = new SimpleDateFormat("yyyy-MM-dd");
            String tgl_skrg = formatDB.format(skrg);  
            
            db.aksi("INSERT INTO transaksi(id,tanggal,total,bayar,kembali)VALUES('"+jno_trans.getText()+"',"
                    + "'"+tgl_skrg+"','"+total+"','"+bayar+"','"+kembali+"')");
            
            for (int i = 0; i < row; i++) {
                db.aksi("UPDATE barang set stok=stok-" + tbl_trans.getValueAt(i, 4) + " where kd_brg='" + tbl_trans.getValueAt(i, 1) +"'");
                
                db.aksi("INSERT INTO transaksi_detail(id_trans,kd_brg,jumlah,subtotal)VALUES"
                        + "('"+jno_trans.getText()+"','"+tbl_trans.getValueAt(i, 1)+"','"+tbl_trans.getValueAt(i, 4)+"'"
                                + ",'"+tbl_trans.getValueAt(i, 5)+"')");
            }
            JOptionPane.showMessageDialog(null, "Total belanja sudah dibayar.");
            
            model.setRowCount(0);
            jtotal_bayar.setText("");
            jbayar.setText("");
            jkembali.setText(""); 
            jno_trans.setText(generateNoTransaksi());
        } catch (Exception e) {
        }
    }//GEN-LAST:event_btn_bayarActionPerformed

    private void btn_cekDataTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cekDataTransActionPerformed
        // TODO add your handling code here:
        this.dispose();
        
        FormDataTrans dataTrans = new FormDataTrans();
        dataTrans.setVisible(true);
    }//GEN-LAST:event_btn_cekDataTransActionPerformed

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
            java.util.logging.Logger.getLogger(FormTrans.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormTrans.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormTrans.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormTrans.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormTrans().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_bayar;
    private javax.swing.JButton btn_cekBarang;
    private javax.swing.JButton btn_cekDataTrans;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jbayar;
    private javax.swing.JTextField jkembali;
    private javax.swing.JTextField jno_trans;
    private javax.swing.JTextField jscan_brg;
    private javax.swing.JTextField jtanggal;
    private javax.swing.JTextField jtotal_bayar;
    private javax.swing.JTable tbl_trans;
    // End of variables declaration//GEN-END:variables
}