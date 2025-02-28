package UI.adminUI;

import model.Order;

import javax.swing.*;
import java.awt.*;

public class OrderListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Order) {
            Order order = (Order) value;
            label.setText(order.getOrderDetails()); // Sipariş bilgilerini göster

            // Yazı tipini kalın yap
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        }

        return label;
    }
}

