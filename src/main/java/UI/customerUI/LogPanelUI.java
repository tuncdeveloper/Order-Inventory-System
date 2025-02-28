package UI.customerUI;

import model.Customer;
import model.Log;
import model.Order;
import model.Product;
import service.CustomerService;
import service.LogService;
import service.OrderService;
import service.ProductService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LogPanelUI extends JPanel {

    private DefaultListModel<String> logListModel;

    public LogPanelUI(Customer validatedCustomer) {
        setLayout(new BorderLayout());

        // Log modelini oluştur
        logListModel = new DefaultListModel<>();
        JList<String> logJList = new JList<>(logListModel);

        // JScrollPane ile JList'i ekle
        JScrollPane scrollPane = new JScrollPane(logJList);

        // Başlık ekle
        JLabel headerLabel = new JLabel("Log Listesi", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Bileşenleri panele ekle
        add(headerLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Logları yükle
        loadLogs(validatedCustomer);
    }

    private void loadLogs(Customer validatedCustomer) {
        LogService logService = new LogService(); // Logları almak için servis sınıfı
        OrderService orderService = new OrderService(); // Sipariş bilgileri için servis sınıfı
        ProductService productService = new ProductService(); // Ürün bilgileri için servis sınıfı
        // İlgili müşteri için logları alıyoruz
        ArrayList<Log> logs = logService.logShowListWithCustomer(validatedCustomer.getCustomerId());

        for (Log log : logs) {
            // Sipariş ve ürün bilgilerini al
            Order order = orderService.orderFindWithId(log.getOrderId());
            Product product = productService.productFindWithIdDb(order != null ? order.getProductId() : -1);


            Map<String, String> logDetails = new LinkedHashMap<>();
            logDetails.put("Log ID", String.valueOf(log.getLogId()));
            logDetails.put("Sipariş ID", String.valueOf(order.getOrderId()));
            logDetails.put("Ürün", product != null ? product.getName() : "Bilinmiyor");
            logDetails.put("Kayıt Tarihi", log.getLogDate().toString());
            logDetails.put("Tür", log.getLogType());

            // Log detaylarını birleştir
            StringBuilder logEntryBuilder = new StringBuilder();
            logDetails.forEach((key, value) -> logEntryBuilder.append(key).append(": ").append(value).append(" - "));

            // Fazla son karakterleri kaldır
            String logEntry = logEntryBuilder.toString().replaceAll(" - $", "");

            // Log detayını modele ekle
            logListModel.addElement(logEntry);
        }

    }

}
