package UI.customerUI;

import model.Customer;
import model.Order;
import model.Product;
import service.CustomerService;
import service.OrderService;
import service.ProductService;
import com.formdev.flatlaf.FlatLightLaf; // FlatLaf temasını ekleyin

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class OrderProcessingAnimationUI extends JPanel {
    private Customer validatedCustomer; // Mevcut müşteri
    private JList<Order> orderList; // Siparişlerin gösterileceği liste
    private DefaultListModel<Order> orderListModel; // Liste modeli
    private JScrollPane scrollPane;

    public OrderProcessingAnimationUI(Customer validatedCustomer) {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        this.validatedCustomer = validatedCustomer;
        initializeUI();
        loadOrders();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Sipariş listesi ve kaydırma çubuğu
        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        orderList.setCellRenderer(new OrderListCellRenderer()); // Özel renderer ekle
        scrollPane = new JScrollPane(orderList);
        add(scrollPane, BorderLayout.CENTER);

        // İptal butonu
        JButton cancelButton = new JButton("İptal Et");
        cancelButton.setBackground(new Color(255, 89, 94)); // Kırmızı renkli buton
        cancelButton.setForeground(Color.WHITE); // Beyaz yazı rengi
        cancelButton.setFocusPainted(false); // Odak çerçevesini kaldır
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14)); // Yazı tipini ayarla

        cancelButton.addActionListener(e -> cancelOrder());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadOrders() {
        orderListModel.clear();
        OrderService orderService = new OrderService();
        ProductService productService = new ProductService();
        ArrayList<Order> orders = orderService.orderShowListWithCustomer(validatedCustomer.getCustomerId());

        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                Product product = productService.productFindWithIdDb(order.getProductId());
                if (product != null) {
                    order.setOrderDetails(formatOrderDetails(order, product));
                    orderListModel.addElement(order);
                }
            }
            orderList.repaint();
            orderList.revalidate();
            remove(scrollPane);
            scrollPane = new JScrollPane(orderList);
            add(scrollPane, BorderLayout.CENTER);
        } else {
            // JOptionPane.showMessageDialog(this, "Sipariş bulunamadı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String formatOrderDetails(Order order, Product product) {
        return String.format(
                "Order ID: %d, Product: %s, Quantity: %d, Price: %.2f, Total: %.2f, Date: %s, Status: %s",
                order.getOrderId(),
                product.getName(),
                order.getQuantity(),
                product.getPrice(),
                order.getQuantity() * product.getPrice(),
                order.getOrderDate(),
                order.getOrderStatus()
        );
    }

    private void cancelOrder() {
        int selectedIndex = orderList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen iptal edilecek bir sipariş seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Order selectedOrder = orderList.getSelectedValue();
        if (selectedOrder == null) return;

        // Sipariş durumunu kontrol et
        String orderStatus = selectedOrder.getOrderStatus();
        if ("Onaylandı".equals(orderStatus) || "Reddedildi".equals(orderStatus)) {
            JOptionPane.showMessageDialog(this,
                    "Bu sipariş iptal edilemez. Durum: " + orderStatus,
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        OrderService orderService = new OrderService();
        ProductService productService = new ProductService();
        CustomerService customerService = new CustomerService();

        Order order = orderService.orderFindWithId(selectedOrder.getOrderId());
        if (order == null) return;

        Product product = productService.productFindWithIdDb(order.getProductId());
        if (product == null) return;

        // Siparişi sil ve bütçeyi güncelle
        orderService.orderDelete(order.getOrderId());
        double newBudget = validatedCustomer.getBudget() + (order.getQuantity() * product.getPrice());
        validatedCustomer.setBudget(newBudget);
        customerService.customerUpdate(validatedCustomer);

        // Listeden çıkar
        orderListModel.remove(selectedIndex);

        JOptionPane.showMessageDialog(this, "Sipariş başarıyla iptal edildi!", "İptal Edildi", JOptionPane.INFORMATION_MESSAGE);
    }

    public void addOrderToList(Order order, Product product) {
        order.setOrderDetails(formatOrderDetails(order, product));
        orderListModel.addElement(order);
    }

    public void updateOrderList() {
        orderListModel.clear();
        loadOrders();
    }

    // Özel JList hücre renderer'ı
    private static class OrderListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Order) {
                Order order = (Order) value;
                String status = order.getOrderStatus();

                // Duruma göre renklendirme
                if ("Onaylandı".equals(status)) {
                    c.setForeground(new Color(17, 73, 17)); // Yeşil
                } else if ("Reddedildi".equals(status)) {
                    c.setForeground(new Color(255, 0, 0)); // Kırmızı
                } else {
                    c.setForeground(new Color(0, 0, 0)); // Siyah
                }
            }

            return c;
        }
    }
}