/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ogengpress;

import java.sql.*;
/**
 *
 * @author Yajong
 */
public class Database {
    
    Connection con;
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost/ogengpress";
    private final String user = "root";
    private final String pwd = "";
    public Statement stm;
    
//    Method 1 (Koneksi Database)
    public void koneksi(){
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pwd);
            stm = con.createStatement();
            System.out.println("koneksi berhasil");
        } catch (ClassNotFoundException | SQLException e){
            System.out.println("Error:\nkoneksi data gagal\n"+e.getMessage());
        }
    }
//    Method 2 (Ambil data / cek data) (select)
    public ResultSet ambilData(String SQL){
        try{
            Statement st = con.createStatement();
            return st.executeQuery(SQL);
        } catch (SQLException e){
            System.out.println("Error:\npengecekan data gagal diakses");
            return null;    
        }
    }
    
//    Method 3 Aksi (Insert, Update, Delete)
    public void aksi(String SQL){
        try{
            Statement st = con.createStatement();
            st.executeUpdate(SQL);
        }catch (SQLException e){
            System.out.println("Error:\naksi gagal diakses");
        }
    }
    
    public static void main(String[] args){
        Database db = new Database();
        
        db.koneksi(); 
    }
}