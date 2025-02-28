package UI.dialog;

import javax.swing.*;
import java.awt.*;

public class CustomerDetailsDialog extends JDialog {
    public CustomerDetailsDialog(JFrame parent, String customerName, String customerType, double budget) {
        super(parent, "Müşteri Detayları", true);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Customer Details
        add(new JLabel("Ad:"));
        add(new JLabel(customerName));
        add(new JLabel("Tür:"));
        add(new JLabel(customerType));
        add(new JLabel("Bütçe:"));
        add(new JLabel(String.valueOf(budget) + " TL"));

        // Close Button
        JButton closeButton = new JButton("Kapat");
        closeButton.addActionListener(e -> dispose());
        add(new JLabel());
        add(closeButton);

        setSize(300, 200);
        setLocationRelativeTo(parent);
    }
}
