package repository;

import model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDb {


    public Admin adminShowDb(Admin admin) {

        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?"; // Kullanıcı adı ve parola kontrolü ekliyoruz

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, admin.getUsername());  // Kullanıcı adı
            preparedStatement.setString(2, admin.getPassword());  // Parola

            ResultSet rs = preparedStatement.executeQuery();

            // Eğer kullanıcı adı ve parola doğruysa admin bilgilerini alıyoruz
            if (rs.next()) {
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
            } else {
                // Eğer kullanıcı adı ve parola eşleşmiyorsa admin nesnesi null döndürülmez
                admin = null;
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return admin;  // Null döndürülürse giriş başarısız olur
    }
}
