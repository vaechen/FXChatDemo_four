package org.example.fxchatdemo.Users;

import org.example.fxchatdemo.Utils.SHA256Hashing;
import java.sql.*;

public class User {
    public static void main(String[] args) throws ClassNotFoundException {
        // JDBC连接的URL, 不同数据库有不同的格式:
        String JDBC_URL = "jdbc:mysql://localhost:3306/javafx?useSSL=false&characterEncoding=utf8";
        String JDBC_USER = "root";
        String JDBC_PASSWORD = "zjx";
        //加载驱动器
        Class.forName("com.mysql.jdbc.Driver");
        // 获取连接:
        try(Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);) {
            // TODO: 访问数据库...
            // 关闭连接:
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT qqmail, name, password FROM user")) {
                    while (rs.next()) {
                        String qqmail = rs.getString(1);
                        String name = rs.getString(2);
                        String password = rs.getString(3);
                        System.out.println(qqmail + name + password);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean selectUser(String Text_qqmail, String Text_name, String Text_password) throws ClassNotFoundException {
        //SHA256
        String Hash_password = SHA256Hashing.getSHA256Hash(Text_password);

        // JDBC连接的URL, 不同数据库有不同的格式:
        String JDBC_URL = "jdbc:mysql://localhost:3306/javafx?useSSL=false&characterEncoding=utf8";
        String JDBC_USER = "root";
        String JDBC_PASSWORD = "zjx";
        //加载驱动器
        Class.forName("com.mysql.jdbc.Driver");
        // 获取连接:
        try(Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);) {
            // TODO: 访问数据库...
            // 关闭连接:
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM user WHERE qqmail=? AND name=? AND password=?");) {
                ps.setObject(1, Text_qqmail);
                ps.setObject(2, Text_name);
                ps.setObject(3, Hash_password);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String qqmail = rs.getString(1);
                        String name = rs.getString(2);
                        String password = rs.getString(3);
                        if (Text_qqmail.equals(qqmail) || Text_name.equals(name) || Hash_password.equals(password)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public int registerUser(String Text_qqmail, String Text_name, String Text_password) throws ClassNotFoundException {
        //SHA256
        String Hash_password = SHA256Hashing.getSHA256Hash(Text_password);

        // JDBC连接的URL, 不同数据库有不同的格式:
        String JDBC_URL = "jdbc:mysql://localhost:3306/javafx?useSSL=false&characterEncoding=utf8";
        String JDBC_USER = "root";
        String JDBC_PASSWORD = "zjx";
        //加载驱动器
        Class.forName("com.mysql.jdbc.Driver");
        // 获取连接:
        try(Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);) {
            // TODO: 访问数据库...
            // 关闭连接:
            try (PreparedStatement ps1 = conn.prepareStatement("SELECT qqmail FROM user WHERE qqmail=?")){
                ps1.setObject(1, Text_qqmail);
                try(ResultSet resultSet = ps1.executeQuery()){
                    if (resultSet.next()){
                        //代表该用户已注册
                        return 2;
                    } else {
                        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO user(qqmail,name,password) VALUES(?, ?, ?)");) {
                            ps.setObject(1, Text_qqmail);
                            ps.setObject(2, Text_name);
                            ps.setObject(3, Hash_password);
                            ps.executeUpdate();
                            return 1;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}