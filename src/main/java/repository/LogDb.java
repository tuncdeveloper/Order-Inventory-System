package repository;

import model.Customer;
import model.Log;
import model.Order;
import model.Product;

import java.sql.*;
import java.util.ArrayList;

public class LogDb {

    public void logAddDb(Log log) {
        Connection connection = ConnectDb.instance();
        String query = "INSERT INTO logs (customer_id_fk, order_id_fk, log_date,log_type,log_details,log_priorities,log_waiting_time) VALUES (?, ?, ?,?, ?,?,?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, log.getCustomerId());
            preparedStatement.setInt(2, log.getOrderId());
            preparedStatement.setDate(3, log.getLogDate());
            preparedStatement.setString(4, log.getLogType());
            preparedStatement.setString(5, log.getLogDetails());
            preparedStatement.setDouble(6,log.getLogPriorities());
            preparedStatement.setDouble(7,log.getWaitingTime());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newProductId = generatedKeys.getInt(1);
                    System.out.println("Yeni ürün eklendi, ID: " + newProductId);
                }
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


    public Log logFindWithIdDb(Integer id) {
        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM logs WHERE log_id = ?";
        Log log = null;  // Tarif bulunamazsa null dönecek

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Eğer sonuç varsa, yeni bir Tarif nesnesi oluştur
                log = new Log();
                log.setCustomerId(rs.getInt("customer_id_fk"));
                log.setOrderId(rs.getInt("order_id_fk"));
                log.setLogDate(rs.getDate("log_date"));
                log.setLogType(rs.getString("log_type"));
                log.setLogDetails(rs.getString("log_details"));
                log.setLogPriorities(rs.getDouble("log_priorities"));
                log.setWaitingTime(rs.getDouble("log_waiting_time"));

            }

        } catch (SQLException e) {
            throw new RuntimeException("Veritabanında tarif bulmada hata: " + e.getMessage(), e);
        }

        return log;  // Eğer tarif bulunmadıysa null dönecek
    }


    public ArrayList<Log> logShowListWithCustomerDb(int customerId) {
        ArrayList<Log> logs = new ArrayList<>();
        Connection connection = ConnectDb.instance();

        String query = "SELECT " +
                "l.log_id, l.log_date, l.log_type, l.log_details, l.log_priorities ,l.log_waiting_time, " +
                "c.customer_id, c.name AS customer_name, c.budget, c.type AS customer_type, c.total_spent, " +
                "o.order_id, o.quantity, o.order_date, o.order_status,o.order_time,o.priority, " +
                "p.product_id, p.name AS product_name, p.stock, p.price " +
                "FROM logs l " +
                "INNER JOIN orders o ON l.order_id_fk = o.order_id " +
                "INNER JOIN customers c ON o.customer_id_fk = c.customer_id " +
                "INNER JOIN products p ON o.product_id_fk = p.product_id " +
                "WHERE c.customer_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Log bilgilerini al
                    Log log = new Log();
                    log.setLogId(resultSet.getInt("log_id"));
                    log.setLogDate(resultSet.getDate("log_date"));
                    log.setLogType(resultSet.getString("log_type"));
                    log.setLogDetails(resultSet.getString("log_details"));
                    log.setLogPriorities(resultSet.getDouble("log_priorities"));
                    log.setWaitingTime(resultSet.getDouble("log_waiting_time"));

                    // Müşteri bilgilerini al
                    Customer customer = new Customer();
                    customer.setCustomerId(resultSet.getInt("customer_id"));
                    customer.setName(resultSet.getString("customer_name"));
                    customer.setBudget(resultSet.getDouble("budget"));
                    customer.setType(resultSet.getString("customer_type"));
                    customer.setTotalSpent(resultSet.getDouble("total_spent"));

                    // Sipariş bilgilerini al
                    Order order = new Order();
                    order.setOrderId(resultSet.getInt("order_id"));
                    order.setQuantity(resultSet.getInt("quantity"));
                    order.setOrderDate(resultSet.getDate("order_date"));
                    order.setOrderStatus(resultSet.getString("order_status"));
                    order.setOrderTime(resultSet.getTime("order_time"));
                    order.setPriority(resultSet.getDouble("priority"));

                    // Ürün bilgilerini al
                    Product product = new Product();
                    product.setProductID(resultSet.getInt("product_id"));
                    product.setName(resultSet.getString("product_name"));
                    product.setStock(resultSet.getInt("stock"));
                    product.setPrice(resultSet.getDouble("price"));

                    // İlişkileri set et
                    order.setCustomerId(customer.getCustomerId());
                    order.setProductId(product.getProductID());
                    log.setOrderId(order.getOrderId());

                    // Log'u listeye ekle
                    logs.add(log);
                }
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

        return logs; // Log listesi döndürülüyor
    }

    public ArrayList<Log> logShowListDb() {
        ArrayList<Log> logs = new ArrayList<>();
        Connection connection = ConnectDb.instance();

        String query = "SELECT " +
                "l.log_id, l.log_date, l.log_type, l.log_details, l.log_priorities , l.log_waiting_time, " +
                "c.customer_id, c.name AS customer_name, c.budget, c.type AS customer_type, c.total_spent, " +
                "o.order_id, o.quantity, o.order_date, o.order_status, o.order_time, o.priority, " +
                "p.product_id, p.name AS product_name, p.stock, p.price " +
                "FROM logs l " +
                "INNER JOIN orders o ON l.order_id_fk = o.order_id " +
                "INNER JOIN customers c ON o.customer_id_fk = c.customer_id " +
                "INNER JOIN products p ON o.product_id_fk = p.product_id " +
                "ORDER BY l.log_priorities DESC";


        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Log bilgilerini al
                    Log log = new Log();
                    log.setLogId(resultSet.getInt("log_id"));
                    log.setLogDate(resultSet.getDate("log_date"));
                    log.setLogType(resultSet.getString("log_type"));
                    log.setLogDetails(resultSet.getString("log_details"));
                    log.setLogPriorities(resultSet.getDouble("log_priorities"));
                    log.setWaitingTime(resultSet.getDouble("log_waiting_time"));

                    // Müşteri bilgilerini al
                    Customer customer = new Customer();
                    customer.setCustomerId(resultSet.getInt("customer_id"));
                    customer.setName(resultSet.getString("customer_name"));
                    customer.setBudget(resultSet.getDouble("budget"));
                    customer.setType(resultSet.getString("customer_type"));
                    customer.setTotalSpent(resultSet.getDouble("total_spent"));

                    // Sipariş bilgilerini al
                    Order order = new Order();
                    order.setOrderId(resultSet.getInt("order_id"));
                    order.setQuantity(resultSet.getInt("quantity"));
                    order.setOrderDate(resultSet.getDate("order_date"));
                    order.setOrderStatus(resultSet.getString("order_status"));
                    order.setOrderTime(resultSet.getTime("order_time"));
                    order.setPriority(resultSet.getDouble("priority"));

                    // Ürün bilgilerini al
                    Product product = new Product();
                    product.setProductID(resultSet.getInt("product_id"));
                    product.setName(resultSet.getString("product_name"));
                    product.setStock(resultSet.getInt("stock"));
                    product.setPrice(resultSet.getDouble("price"));

                    // İlişkileri set et
                    order.setCustomerId(customer.getCustomerId());
                    order.setProductId(product.getProductID());
                    log.setOrderId(order.getOrderId());

                    // Log'u listeye ekle
                    logs.add(log);
                }
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

        return logs; // Log listesi döndürülüyor
    }



}
