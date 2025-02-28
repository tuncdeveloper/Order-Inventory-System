package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDb {

    private static final String URL = "jdbc:postgresql://localhost/order_inventory";
    private static final String USER = "postgres";
    private static final String PASSWORD = "test";

    private static Connection connection ;
    //private static ConnectDb connectDb;


    private ConnectDb(){

    }

    private static Connection getConnection(){

        try {
            if (connection == null||connection.isClosed()) {
                try {
                    connection = DriverManager.getConnection(URL,USER,PASSWORD);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    public static Connection instance(){
        return getConnection();
    }

}
