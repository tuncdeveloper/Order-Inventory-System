package UI.adminUI;

import com.formdev.flatlaf.FlatLightLaf; // FlatLaf temasını ekleyin
import model.Customer;
import model.Log;
import model.Order;
import model.Product;
import service.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderProcessingAnimationUI extends JPanel {
    private JList<Order> orderJList;
    private DefaultListModel<Order> orderListModel;
    private JPanel buttonPanel;
    private JComboBox<String> logComboBox;
    private DefaultComboBoxModel<String> logComboBoxModel = new DefaultComboBoxModel<>();
    private ProductStockPanelUI productStockPanelUI;

    public OrderProcessingAnimationUI(ProductStockPanelUI productStockPanelUI) {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        this.productStockPanelUI = productStockPanelUI;

        // Üst panel: Başlık ve ComboBox
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        // Başlık
        JLabel processingLabel = new JLabel("Siparişler", SwingConstants.CENTER);
        processingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(processingLabel, BorderLayout.CENTER);

        // ComboBox için sağ üst kutucuk
        JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel logLabel = new JLabel("Loglar: ");
        logComboBox = new JComboBox<>();
        loadLogs();
        comboBoxPanel.add(logLabel);
        comboBoxPanel.add(logComboBox);

        topPanel.add(comboBoxPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Sipariş listesi için DefaultListModel ve JList
        orderListModel = new DefaultListModel<>();
        orderJList = new JList<>(orderListModel);
        orderJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderJList.setCellRenderer(new CustomOrderRenderer());

        // Kaydırma çubuğu
        JScrollPane scrollPane = new JScrollPane(orderJList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları
        add(scrollPane, BorderLayout.CENTER);

        // Siparişleri yükle
        loadOrders();

        // Alt panel: Butonlar
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        JButton approveAllButton = new JButton("Hepsini Onayla");
        approveAllButton.setBackground(new Color(50, 205, 50)); // Yeşil arka plan
        approveAllButton.setForeground(Color.WHITE); // Beyaz yazı rengi
        approveAllButton.setFocusPainted(false); // Odak çerçevesini kaldır
        approveAllButton.addActionListener(e -> handleApproveAllAction());
        buttonPanel.add(approveAllButton);

        add(buttonPanel, BorderLayout.SOUTH);

        ConcurrentConfirmManager.getInstance();
    }

    class CustomOrderRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Order) {
                Order order = (Order) value;
                String status = order.getOrderStatus();

                // Durum null kontrolü ekleyin
                if (status != null) {
                    // Durumlara göre renk belirleme
                    if (status.equalsIgnoreCase("Onaylandı")) {
                        component.setForeground(new Color(34, 139, 34)); // Yeşil
                    } else if (status.equalsIgnoreCase("Reddedildi")) {
                        component.setForeground(new Color(255, 0, 0)); // Kırmızı
                    } else {
                        component.setForeground(Color.BLACK); // Siyah
                    }
                } else {
                    // Eğer durum null ise varsayılan renk belirleme
                    component.setForeground(Color.BLACK);
                }
            }

            return component;
        }
    }

    private void loadLogs() {
        LogService logService = new LogService();
        OrderService orderService = new OrderService();
        ProductService productService = new ProductService();
        CustomerService customerService = new CustomerService();

        // Admin için tüm logları al
        ArrayList<Log> logs = logService.logShowList();

        // Logları logPriorities'e göre azalan sıralama
        logs.sort((log1, log2) -> Double.compare(log2.getLogPriorities(), log1.getLogPriorities()));

        // Her bir log için işlem yap
        for (Log log : logs) {
            // Sipariş ve ürün bilgilerini al
            Order order = orderService.orderFindWithId(log.getOrderId());
            Product product = productService.productFindWithIdDb(order != null ? order.getProductId() : -1);
            Customer customer = (order != null) ? customerService.customerFindWithId(order.getCustomerId()) : null;

            // Log detaylarını birleştir
            String logEntry = String.format(
                    "Müşteri: %s | Ürün: %s | Tarih: %s | Tür: %s | Bekleme Süresi: %s | Skor: %s",
                    (customer != null) ? customer.getName() : "Bilinmiyor",
                    (product != null) ? product.getName() : "Bilinmiyor",
                    log.getLogDate(),
                    log.getLogType(),
                    log.getWaitingTime(),
                    log.getLogPriorities()
            );

            // Model'e ekle
            logComboBoxModel.addElement(logEntry);
        }

        // ComboBox'a model ataması
        logComboBox.setModel(logComboBoxModel);

        // Özel renderer ile düzenli görünüm
        logComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.LEFT); // Daha iyi hizalama
                return label;
            }
        });
    }

    private void loadOrders() {
        OrderService orderService = new OrderService();
        ProductService productService = new ProductService();
        CustomerService customerService = new CustomerService();
        List<Order> orders = orderService.orderShowList();

        // Müşteri adı ile gruplanmış siparişleri tutan bir harita (map) oluştur
        Map<String, List<Order>> customerOrdersMap = new HashMap<>();

        for (Order order : orders) {
            Product product = productService.productFindWithIdDb(order.getProductId());
            Customer customer = customerService.customerFindWithId(order.getCustomerId());

            String customerName = customer != null ? customer.getName() : "Bilinmiyor";
            String productName = product != null ? product.getName() : "Bilinmiyor";
            int quantity = order.getQuantity();
            String orderDate = order.getOrderDate().toString();
            String status = order.getOrderStatus();

            // Sipariş bilgisini oluştur ve Order nesnesine ekle
            String orderDetails = String.format("%s - %d adet - %s - Durum: %s",
                    productName, quantity, orderDate, status);

            // Siparişi order nesnesine set et
            order.setOrderDetails(orderDetails);

            // Eğer müşteri adı daha önce haritada yoksa, yeni bir liste oluştur
            customerOrdersMap.putIfAbsent(customerName, new ArrayList<>());
            // Müşteriye ait siparişi ekle
            customerOrdersMap.get(customerName).add(order);
        }

        // Müşteri isimleri ve siparişleri sırasıyla ekle
        for (Map.Entry<String, List<Order>> entry : customerOrdersMap.entrySet()) {
            String customerName = entry.getKey();
            List<Order> customerOrders = entry.getValue();

            // Başlık olarak müşteri ismini ekle (Bu başlık bir Order nesnesi değil, bir grup başlığı olacak)
            Order headerOrder = new Order(); // Başlık için geçici bir Order nesnesi
            headerOrder.setOrderDetails(customerName); // Müşteri adı başlık olarak set edilir
            orderListModel.addElement(headerOrder); // Bu başlık, orderListModel'a eklenir

            // Müşteri için siparişleri ekle
            for (Order order : customerOrders) {
                orderListModel.addElement(order); // Her bir sipariş Order nesnesi olarak eklenir
            }
        }
    }

    private void initLogCombobox() {
        logComboBoxModel.removeAllElements();
        LogService logService = new LogService();
        for (Log log : logService.logShowList()) {
            logComboBoxModel.addElement(log.getLogDetails());
        }
    }

    private void handleApproveAllAction() {
        // Yüklenme penceresi
        JDialog loadingDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "İşlem Devam Ediyor", Dialog.ModalityType.APPLICATION_MODAL);
        loadingDialog.setLayout(new BorderLayout());
        JLabel loadingLabel = new JLabel("Siparişler onaylanıyor, lütfen bekleyin...");
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingDialog.add(loadingLabel, BorderLayout.CENTER);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(34, 139, 34)); // Yeşil renk
        loadingDialog.add(progressBar, BorderLayout.SOUTH);

        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);

        // SwingWorker ile işlemleri arka planda çalıştırma
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // Uzun süren işlemler burada çalışacak
                OrderService orderService = new OrderService();
                ProductService productService = new ProductService();
                CustomerService customerService = new CustomerService();
                LogService logService = new LogService();

                ConcurrentConfirmManager.getInstance().confirmAll();

                int totalOrders = orderListModel.size();
                for (int i = 0; i < totalOrders; i++) {
                    Order order = orderListModel.get(i);

                    // İşlemleri yavaşlatmak için simülasyon
                    try {
                        Thread.sleep(500); // Her sipariş için yarım saniye bekle
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (order != null && order.getOrderStatus() != null && order.getOrderStatus().equals("Beklemede")) {
                        Customer customer = customerService.customerFindWithId(order.getCustomerId());
                        Product product = productService.productFindWithIdDb(order.getProductId());

                        if (customer != null && product != null) {
                            if (order.getQuantity() > product.getStock()) {
                                int basePriorityScore = "premium".equals(customer.getType()) ? 15 : 10;
                                long waitingTime = (System.currentTimeMillis() - order.getOrderTime().getTime()) / 1000;
                                double waitingTimeWeight = 0.5;
                                double priorityScore = basePriorityScore + (waitingTime * waitingTimeWeight / 100);
                                order.setPriority(priorityScore);

                                Log log = new Log();
                                log.setCustomerId(customer.getCustomerId());
                                log.setOrderId(order.getOrderId());
                                log.setLogDate(new java.sql.Date(new java.util.Date().getTime()));
                                log.setLogType("Hata");
                                log.setLogDetails("Müşteri " + customer.getName() + " ürünü sipariş etmeye çalıştı "
                                        + product.getName() + " (Miktar: " + order.getQuantity()
                                        + "), ancak stok yetersizdi.");
                                log.setLogPriorities(priorityScore);
                                log.setWaitingTime(waitingTime);
                                logService.logAdd(log);

                                logComboBoxModel.addElement(
                                        "Müşteri: " + customer.getName() +
                                                "| Ürün: " + product.getName() +
                                                "| Tarih: " + log.getLogDate().toString() +
                                                "| Tür: " + log.getLogType() +
                                                "| Bekleme süresi:" + log.getWaitingTime() / 1000 +
                                                "| Skor: " + log.getLogPriorities());

                                order.setOrderStatus("Reddedildi");
                                customer.setBudget(customer.getBudget() + order.getQuantity() * product.getPrice());
                                customerService.customerUpdate(customer);
                                orderService.orderUpdate(order);
                            } else {
                                double totalCost = order.getQuantity() * product.getPrice();
                                product.setStock(product.getStock() - order.getQuantity());
                                productService.productUpdate(product);

                                int basePriorityScore = "premium".equals(customer.getType()) ? 15 : 10;
                                long waitingTime = (System.currentTimeMillis() - order.getOrderTime().getTime()) / 1000;
                                double waitingTimeWeight = 0.5;
                                double priorityScore = basePriorityScore + (waitingTime * waitingTimeWeight / 1000);
                                order.setPriority(priorityScore);

                                Log log = new Log();
                                log.setCustomerId(customer.getCustomerId());
                                log.setOrderId(order.getOrderId());
                                log.setLogDate(new java.sql.Date(new java.util.Date().getTime()));
                                log.setLogType("Onaylandı");
                                log.setLogDetails("Müşteri " + customer.getName() + " ürün siparişi onaylandı "
                                        + product.getName() + " (Miktar: " + order.getQuantity()
                                        + ", Toplam Maliyet: " + totalCost + ")");
                                log.setLogPriorities(priorityScore);
                                log.setWaitingTime(waitingTime);
                                logService.logAdd(log);

                                logComboBoxModel.addElement(
                                        "Müşteri: " + customer.getName() +
                                                "| Ürün: " + product.getName() +
                                                "| Tarih: " + log.getLogDate().toString() +
                                                "| Tür: " + log.getLogType() +
                                                "| Bekleme süresi:" + log.getWaitingTime() / 1000 +
                                                "| Skor: " + log.getLogPriorities());

                                order.setOrderStatus("Onaylandı");
                                orderService.orderUpdate(order);
                                double totalSpent = customer.getTotalSpent() + totalCost;
                                customer.setTotalSpent(totalSpent);
                                customerService.customerUpdate(customer);
                                productStockPanelUI.updateChart();
                            }
                        }
                    }

                    // İlerleme durumunu güncelle
                    int progress = (int) ((i + 1) * 100.0 / totalOrders);
                    publish(progress);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                for (int progress : chunks) {
                    progressBar.setValue(progress); // İlerleme çubuğunu güncelle
                }
            }

            @Override
            protected void done() {
                // İşlem bittiğinde yüklenme penceresini kapat
                loadingDialog.dispose();
                logComboBox.repaint();
                orderJList.repaint();
                JOptionPane.showMessageDialog(null, "Tüm uygun siparişler başarıyla onaylandı.");
            }
        };

        // Arka planda işlemi başlat ve yüklenme penceresini göster
        worker.execute();
        loadingDialog.setVisible(true);
    }
}