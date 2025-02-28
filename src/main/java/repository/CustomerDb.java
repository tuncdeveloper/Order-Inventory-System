package repository;


import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDb {

    public void customerInsertDb(Customer customer) {
        String query = "INSERT INTO customers (name, budget, type, total_spent, username, password) VALUES (?, ?, ?, ?, ?, ?)";

        // ConnectDb üzerinden bağlantıyı alıyoruz
        try (Connection connection = ConnectDb.instance();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, customer.getName());
            preparedStatement.setDouble(2, customer.getBudget());
            preparedStatement.setString(3, customer.getType());
            preparedStatement.setDouble(4, customer.getTotalSpent());
            preparedStatement.setString(5, customer.getUsername());
            preparedStatement.setString(6, customer.getPassword());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Müşteri başarıyla veritabanına eklendi.");
            } else {
                System.out.println("Müşteri eklenirken bir sorun oluştu.");
            }
        } catch (SQLException e) {
            System.err.println("Veritabanına ekleme sırasında hata: " + e.getMessage());
        }
    }

    public Customer customerLogin(Customer customer) {
        // Müşteri bilgilerini tutacak nesne
        Customer loggedCustomer = null;

        // Veritabanı bağlantısını alıyoruz
        Connection connection = ConnectDb.instance();

        // Kullanıcı adı ve parolayı kontrol eden sorgu
        String query = "SELECT * FROM customers WHERE username = ? AND password = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, customer.getUsername());  // Kullanıcı adı
            preparedStatement.setString(2, customer.getPassword());  // Parola

            // Sorguyu çalıştırıyoruz ve sonucu alıyoruz
            ResultSet rs = preparedStatement.executeQuery();

            // Eğer veri varsa, müşteri bilgilerini alıyoruz
            if (rs.next()) {
                loggedCustomer = new Customer();
                loggedCustomer.setCustomerId(rs.getInt("customer_id"));
                loggedCustomer.setUsername(rs.getString("username"));
                loggedCustomer.setName(rs.getString("name"));
                loggedCustomer.setBudget(rs.getDouble("budget"));
                loggedCustomer.setType(rs.getString("type"));
                loggedCustomer.setTotalSpent(rs.getDouble("total_spent"));
            }

            // ResultSet nesnesini kapatıyoruz
            rs.close();

        } catch (SQLException e) {
            // Burada daha anlamlı bir hata mesajı gösterilebilir
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
        }

        return loggedCustomer;  // Eğer geçerli kullanıcı varsa, müşteri nesnesini döndürür, yoksa null döner
    }




    public ArrayList<Customer> customerShowListDb() {
        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM customers";

        ArrayList<Customer> productList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(resultSet.getInt("customer_id"));
                customer.setName(resultSet.getString("name"));
                customer.setBudget(resultSet.getDouble("budget"));
                customer.setType(resultSet.getString("type"));
                customer.setTotalSpent(resultSet.getDouble("total_spent"));
                customer.setUsername(resultSet.getString("username"));
                customer.setPassword(resultSet.getString("password"));

                productList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }

    public Customer customerFindWithIdDb(Integer id) {
        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM customers WHERE customer_id = ?";
        Customer customer = null;  // Tarif bulunamazsa null dönecek

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Eğer sonuç varsa, yeni bir Tarif nesnesi oluştur
                customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setBudget(rs.getDouble("budget"));
                customer.setType(rs.getString("type"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                customer.setUsername(rs.getString("username"));
                customer.setPassword(rs.getString("password"));

            }

            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Veritabanında tarif bulmada hata: " + e.getMessage(), e);
        }

        return customer;  // Eğer tarif bulunmadıysa null dönecek
    }

    public void customerUpdateDb(Customer customer) {
        Connection connection = ConnectDb.instance();
        String query = "UPDATE customers SET name = ?, budget = ?, type = ?, total_spent = ? WHERE customer_id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setDouble(2, customer.getBudget());
            preparedStatement.setString(3, customer.getType());
            preparedStatement.setDouble(4, customer.getTotalSpent());

            preparedStatement.setInt(5, customer.getCustomerId());


            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ürün başarıyla güncellendi, ID: " + customer.getCustomerId());
            } else {
                System.out.println("Güncellenecek ürün bulunamadı, ID: " + customer.getCustomerId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
