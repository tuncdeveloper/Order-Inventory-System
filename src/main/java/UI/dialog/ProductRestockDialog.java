package UI.dialog;

import javax.swing.*;
import java.awt.*;

public class ProductRestockDialog extends JDialog {
    private JTextField restockAmountField;
    private boolean isConfirmed;

    public ProductRestockDialog(JFrame parent, String productName) {
        super(parent, "Stok Güncelle", true);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Product Name
        add(new JLabel("Ürün:"));
        add(new JLabel(productName));

        // Restock Amount Input
        add(new JLabel("Eklenen Stok:"));
        restockAmountField = new JTextField();
        add(restockAmountField);

        // Buttons
        JButton confirmButton = new JButton("Onayla");
        confirmButton.addActionListener(e -> {
            isConfirmed = true;
            dispose();
        });
        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(e -> dispose());

        add(confirmButton);
        add(cancelButton);

        setSize(300, 150);
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public int getRestockAmount() {
        return Integer.parseInt(restockAmountField.getText());
    }
}

