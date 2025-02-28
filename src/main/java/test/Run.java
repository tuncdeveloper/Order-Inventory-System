package test;

import UI.Login;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Run {
    public static void main(String[] args) {

        FlatLightLaf.setup();


        //CustomerService customerService = new CustomerService();
        //customerService.createRandomCustomers();


        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}
