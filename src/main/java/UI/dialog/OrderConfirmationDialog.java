package UI.dialog;

import javax.swing.*;
import java.awt.*;

public class OrderConfirmationDialog extends JDialog {
    private boolean isConfirmed;

    public OrderConfirmationDialog(JFrame parent, String productName, int quantity, double totalPrice) {
        super(parent, "Sipariş Onayı", true);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Order Details
        add(new JLabel("Ürün:"));
        add(new JLabel(productName));
        add(new JLabel("Adet:"));
        add(new JLabel(String.valueOf(quantity)));
        add(new JLabel("Toplam Fiyat:"));
        add(new JLabel(totalPrice + " TL"));

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

        setSize(300, 200);
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}

