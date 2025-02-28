package repository;

import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductDb {

    public void productAddDb(Product product) {
        Connection connection = ConnectDb.instance();
        String query = "INSERT INTO products (name, stock, price) VALUES (?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setInt(2, product.getStock());
            preparedStatement.setDouble(3, product.getPrice());

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

    public void productDeleteDb(int productId) {
        Connection connection = ConnectDb.instance();
        String deleteLogsQuery = "DELETE FROM logs WHERE order_id_fk IN (SELECT order_id FROM orders WHERE product_id_fk = ?)";
        String deleteOrdersQuery = "DELETE FROM orders WHERE product_id_fk = ?";
        String deleteProductQuery = "DELETE FROM products WHERE product_id = ?";

        try {
            connection.setAutoCommit(false); // Transaction başlatılır.

            // 1. Logları sil
            try (PreparedStatement logStatement = connection.prepareStatement(deleteLogsQuery)) {
                logStatement.setInt(1, productId);
                logStatement.executeUpdate();
            }

            // 2. Siparişleri sil
            try (PreparedStatement orderStatement = connection.prepareStatement(deleteOrdersQuery)) {
                orderStatement.setInt(1, productId);
                orderStatement.executeUpdate();
            }

            // 3. Ürünü sil
            try (PreparedStatement productStatement = connection.prepareStatement(deleteProductQuery)) {
                productStatement.setInt(1, productId);
                int rowsAffected = productStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Ürün başarıyla silindi, ID: " + productId);
                } else {
                    System.out.println("Silinecek ürün bulunamadı, ID: " + productId);
                }
            }

            connection.commit(); // İşlemler onaylanır.
        } catch (SQLException e) {
            try {
                connection.rollback(); // Hata durumunda işlemler geri alınır.
                System.out.println("Bir hata oluştu, işlemler geri alındı.");
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Varsayılan duruma geri dön.
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void productUpdateDb(Product product) {
        Connection connection = ConnectDb.instance();
        String query = "UPDATE products SET name = ?, stock = ?, price = ? WHERE product_id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setInt(2, product.getStock());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getProductID());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ürün başarıyla güncellendi, ID: " + product.getProductID());
            } else {
                System.out.println("Güncellenecek ürün bulunamadı, ID: " + product.getProductID());
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
    public ArrayList<Product> productShowListDb() {
        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM products";

        ArrayList<Product> productList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setProductID(resultSet.getInt("product_id"));
                product.setName(resultSet.getString("name"));
                product.setStock(resultSet.getInt("stock"));
                product.setPrice(resultSet.getDouble("price"));

                productList.add(product);
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

    public Product productFindWithIdDb(Integer id) {
        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM products WHERE product_id = ?";
        Product product = null;  // Tarif bulunamazsa null dönecek

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Eğer sonuç varsa, yeni bir Tarif nesnesi oluştur
                product = new Product();
                product.setProductID(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getDouble("price"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Veritabanında tarif bulmada hata: " + e.getMessage(), e);
        }

        return product;  // Eğer tarif bulunmadıysa null dönecek
    }

    public Product productFindDb(String name) {
        Connection connection = ConnectDb.instance();
        String query = "SELECT * FROM products WHERE name = ?";
        Product product = null;  // Tarif bulunamazsa null dönecek

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Eğer sonuç varsa, yeni bir Tarif nesnesi oluştur
                product = new Product();
                product.setProductID(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getDouble("price"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Veritabanında tarif bulmada hata: " + e.getMessage(), e);
        }

        return product;  // Eğer tarif bulunmadıysa null dönecek
    }


}
