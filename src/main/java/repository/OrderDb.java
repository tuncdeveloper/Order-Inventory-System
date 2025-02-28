package repository;

import model.Order;
import model.Product;
import service.ProductService;

import java.sql.*;
import java.util.ArrayList;

public class OrderDb {


   public void orderAddDb(Order order){
        Connection connection = ConnectDb.instance();
        String query = "INSERT INTO orders (customer_id_fk, product_id_fk, quantity,order_date,order_status,order_time,priority) VALUES (?,?,?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getCustomerId());
            preparedStatement.setInt(2, order.getProductId());
            preparedStatement.setInt(3, order.getQuantity());
            preparedStatement.setDate(4, order.getOrderDate());
            preparedStatement.setString(5, order.getOrderStatus());
            preparedStatement.setTime(6,order.getOrderTime());
            preparedStatement.setDouble(7,order.getPriority());


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

    public ArrayList<Order> orderShowListDb() {
        ArrayList<Order> orders = new ArrayList<>();
        Connection connection = ConnectDb.instance();

        // SQL sorgusu, orders tablosundaki gerekli kolonları seçiyor
        String query = "SELECT order_id, customer_id_fk, product_id_fk, quantity, order_date, order_status,order_time , priority FROM orders";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Sonuçları işleyerek Order nesnelerini oluşturuyoruz
            while (resultSet.next()) {


                Order order = new Order();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setCustomerId(resultSet.getInt("customer_id_fk"));
                order.setProductId(resultSet.getInt("product_id_fk"));
                order.setQuantity(resultSet.getInt("quantity"));
                order.setOrderDate(resultSet.getDate("order_date"));
                order.setOrderStatus(resultSet.getString("order_status"));
                order.setOrderTime(resultSet.getTime("order_time"));
                order.setPriority(resultSet.getDouble("priority"));

                // Order nesnesini listeye ekliyoruz
                orders.add(order);
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

        return orders;  // Order listesi döndürülüyor
    }

    public ArrayList<Order> orderShowListWithCustomerDb(int customerId) {
        ArrayList<Order> orders = new ArrayList<>();
        Connection connection = ConnectDb.instance();

        // SQL sorgusu, belirli bir müşteri için orders ve products tablolarını birleştirir
        String query = "SELECT " +
                "o.order_id, o.customer_id_fk, o.product_id_fk, o.quantity, o.order_date, o.order_status, o.order_time, o.priority ," +
                "p.product_id, p.name AS product_name, p.stock, p.price " +
                "FROM orders o " +
                "JOIN products p ON o.product_id_fk = p.product_id " +
                "WHERE o.customer_id_fk = ?";


        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, customerId); // Müşteri ID'sini sorguya ekliyoruz
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                // Sonuçları işleyerek Order ve Product nesnelerini oluşturuyoruz
                while (resultSet.next()) {
                    // Order verilerini alıyoruz
                    int orderId = resultSet.getInt("order_id");
                    int productId = resultSet.getInt("product_id_fk");
                    int quantity = resultSet.getInt("quantity");
                    Date orderDate = resultSet.getDate("order_date");
                    String orderStatus = resultSet.getString("order_status");
                    Time orderTime = resultSet.getTime("order_time");
                    Double priority = resultSet.getDouble("priority");

                    // Product verilerini alıyoruz
                    Product product = new Product();
                    product.setProductID(resultSet.getInt("product_id"));
                    product.setName(resultSet.getString("product_name"));
                    product.setStock(resultSet.getInt("stock"));
                    product.setPrice(resultSet.getDouble("price"));

                    // Order nesnesi oluşturuluyor ve set ediliyor
                    Order order = new Order();
                    order.setOrderId(orderId);
                    order.setCustomerId(customerId); // Belirli bir müşteri ID'sini set ediyoruz
                    order.setProductId(productId);
                    order.setQuantity(quantity);
                    order.setOrderDate(orderDate);
                    order.setOrderStatus(orderStatus);
                    order.setOrderTime(orderTime);

                    // Product nesnesini set ediyoruz
                   // order.setProduct(product);

                    // Order nesnesini listeye ekliyoruz
                    orders.add(order);
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

        return orders;  // Order listesi döndürülüyor
    }


    public Order orderFindWithIdDb(Integer id) {
        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM orders WHERE order_id = ?";
        Order order = null;  // Tarif bulunamazsa null dönecek

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Eğer sonuç varsa, yeni bir Tarif nesnesi oluştur
                order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id_fk"));
                order.setProductId(rs.getInt("product_id_fk"));
                order.setQuantity(rs.getInt("quantity"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setOrderTime(rs.getTime("order_time"));
                order.setPriority(rs.getDouble("priority"));

            }

            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Veritabanında tarif bulmada hata: " + e.getMessage(), e);
        }

        return order;  // Eğer tarif bulunmadıysa null dönecek
    }


    public void orderDeleteDb(int orderID) {
        Connection connection = ConnectDb.instance();
        String query = "DELETE FROM orders WHERE order_id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderID);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("sipariş başarıyla silindi, ID: " + orderID);
            } else {
                System.out.println("Silinecek sipariş bulunamadı, ID: " + orderID);
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

    public void orderUpdateDb(Order order) {
        Connection connection = ConnectDb.instance();
        String query = "UPDATE orders SET customer_id_fk = ?, product_id_fk = ?, quantity = ?," +
                "order_status = ?,order_time = ? , priority = ? WHERE order_id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, order.getCustomerId());
            preparedStatement.setInt(2, order.getProductId());
            preparedStatement.setInt(3, order.getQuantity());
            preparedStatement.setString(4, order.getOrderStatus());
            preparedStatement.setTime(5,order.getOrderTime());
            preparedStatement.setDouble(6,order.getPriority());
            preparedStatement.setInt(7, order.getOrderId());


            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ürün başarıyla güncellendi, ID: " + order.getOrderId());
            } else {
                System.out.println("Güncellenecek ürün bulunamadı, ID: " + order.getOrderId());
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


    public Order getByUserIdAndProductIdDb(int customerID, int productID) {
        Order order = null;
        String query = "SELECT * FROM orders WHERE customer_id_fk = ? AND product_id_fk = ?";

        try (Connection connection = ConnectDb.instance();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Parametreleri hazırlama
            preparedStatement.setInt(1, customerID);
            preparedStatement.setInt(2, productID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Sipariş bilgilerini doldurma
                    order = new Order();
                    order.setOrderId(resultSet.getInt("order_id"));
                    order.setCustomerId(resultSet.getInt("customer_id_fk"));
                    order.setProductId(resultSet.getInt("product_id_fk"));
                    order.setQuantity(resultSet.getInt("quantity"));
                    order.setOrderDate(resultSet.getDate("order_date"));
                    order.setOrderStatus(resultSet.getString("order_status"));
                    order.setOrderTime(resultSet.getTime("order_time"));
                    order.setPriority(resultSet.getDouble("priority"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    public void confirmByIdDB(int orderID) {
        String queryUpdateOrder = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        String queryGetOrder = "SELECT * FROM orders WHERE order_id = ?";
        String queryUpdateProductStock = "UPDATE products SET stock = stock - ? WHERE product_id = ?";
        String queryUpdateCustomerSpent = "UPDATE customers SET total_spent = total_spent + ?, budget = budget - ? WHERE customer_id = ?";

        try (Connection connection = ConnectDb.instance()) {
            connection.setAutoCommit(false); // Transaction başlat
            ProductService productService = new ProductService() ;
            // Siparişi veritabanından al
            try (PreparedStatement getOrderStmt = connection.prepareStatement(queryGetOrder)) {
                getOrderStmt.setInt(1, orderID);
                try (ResultSet resultSet = getOrderStmt.executeQuery()) {
                    if (resultSet.next()) {
                        int customerId = resultSet.getInt("customer_id_fk");
                        int productId = resultSet.getInt("product_id_fk");
                        int quantity = resultSet.getInt("quantity");
                        double totalPrice = resultSet.getDouble("quantity") * productService.productFindWithIdDb(productId).getPrice();

                        // Sipariş durumu güncelle
                        try (PreparedStatement updateOrderStmt = connection.prepareStatement(queryUpdateOrder)) {
                            updateOrderStmt.setString(1, "Onaylandı");
                            updateOrderStmt.setInt(2, orderID);
                            updateOrderStmt.executeUpdate();
                        }

                        // Ürün stoğunu güncelle
                        try (PreparedStatement updateProductStmt = connection.prepareStatement(queryUpdateProductStock)) {
                            updateProductStmt.setInt(1, quantity);
                            updateProductStmt.setInt(2, productId);
                            updateProductStmt.executeUpdate();
                        }

                        // Müşteri toplam harcama ve bütçe güncelle
                        try (PreparedStatement updateCustomerStmt = connection.prepareStatement(queryUpdateCustomerSpent)) {
                            updateCustomerStmt.setDouble(1, totalPrice);
                            updateCustomerStmt.setDouble(2, totalPrice);
                            updateCustomerStmt.setInt(3, customerId);
                            updateCustomerStmt.executeUpdate();
                        }

                        // Transaction commit
                        connection.commit();
                    } else {
                        System.out.println("Sipariş bulunamadı.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
