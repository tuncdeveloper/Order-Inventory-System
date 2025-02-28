package UI.adminUI;

import com.formdev.flatlaf.FlatLightLaf; // FlatLaf temasını ekleyin
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

    public LogPanelUI() {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());

        // Log modelini oluştur
        logListModel = new DefaultListModel<>();
        JList<String> logJList = new JList<>(logListModel);
        logJList.setFont(new Font("Arial", Font.PLAIN, 14)); // Yazı tipini ayarla
        logJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Tekli seçim modu

        // JScrollPane ile JList'i ekle
        JScrollPane scrollPane = new JScrollPane(logJList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        // Başlık ekle
        JLabel headerLabel = new JLabel("Log Listesi", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Başlık yazı tipini ayarla
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        // Bileşenleri panele ekle
        add(headerLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Logları yükle
        loadLogs();
    }

    private void loadLogs() {
        LogService logService = new LogService(); // Logları almak için servis sınıfı
        OrderService orderService = new OrderService(); // Sipariş bilgileri için servis sınıfı
        ProductService productService = new ProductService(); // Ürün bilgileri için servis sınıfı
        CustomerService customerService = new CustomerService();

        // Admin için tüm logları alıyoruz
        ArrayList<Log> logs = logService.logShowList(); // Admin için tüm logları almak

        // Her bir log için işlem yap
        for (Log log : logs) {
            // Sipariş ve ürün bilgilerini al
            Order order = orderService.orderFindWithId(log.getOrderId());
            Product product = productService.productFindWithIdDb(order != null ? order.getProductId() : -1);
            Customer customer = customerService.customerFindWithId(order.getCustomerId());

            // Eğer müşteri bulunamadıysa, boş bir değer kullan (örneğin "Bilinmiyor")
            String customerName = (customer != null) ? customer.getName() : "Bilinmiyor";

            // Log bilgilerini harita olarak tutalım
            Map<String, String> logDetails = new LinkedHashMap<>();
            logDetails.put("Log ID", String.valueOf(log.getLogId()));
            logDetails.put("Sipariş ID", String.valueOf(order.getOrderId()));
            logDetails.put("Müşteri Adı", customerName);
            logDetails.put("Ürün Adı", product != null ? product.getName() : "Bilinmiyor");
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