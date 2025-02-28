package UI.customerUI;

import model.Customer;
import model.Order;
import model.Product;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import service.*;
import com.formdev.flatlaf.FlatLightLaf; // FlatLaf temasını ekleyin

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductStockPanelUI extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private ProductService productService;
    private Customer validatedCustomer;
    private Map<String, Integer> productNameToIdMap;  // Ürün adı - ürün ID eşleşmesi için map
    private JLabel budgetLabel;  // Bütçe bilgisi için JLabel
    // Sınıfın başında, global olarak tanımlayın
    private JLabel budgetInfoLabel;

    private OrderProcessingAnimationUI orderProcessingAnimationUI;
    private JComboBox<String> ordersComboBox; // Siparişler için ComboBox

    private LogService logService ;

    public ProductStockPanelUI(Customer validatedCustomer, OrderProcessingAnimationUI orderProcessingAnimationUI) {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        this.validatedCustomer = validatedCustomer;  // validatedCustomer parametresini alıyoruz
        this.orderProcessingAnimationUI = orderProcessingAnimationUI;
        productService = new ProductService(); // ProductService örneği oluşturuluyor
        productNameToIdMap = new HashMap<>();  // Map başlatılıyor
        setLayout(new BorderLayout());

        // Bütçe JLabel'ı sağ üst köşeye yerleştirilecek
        budgetLabel = new JLabel("Bütçe: " + validatedCustomer.getBudget());
        budgetLabel.setHorizontalAlignment(SwingConstants.RIGHT);  // Sağda hizala
        budgetLabel.setFont(new Font("Arial", Font.BOLD, 16));  // Yazı tipini değiştirebilirsiniz
        add(budgetLabel, BorderLayout.NORTH);  // Bütçeyi üst kısma ekle

        // Product Table
        String[] columnNames = {"Ürün ID", "Ürün Adı", "Stok Miktarı", "Fiyat"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hiçbir hücre düzenlenemez
                return false;
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 150)); // Tablo için sabit yükseklik

        // Verileri tabloya yükle
        loadProductData();

        // Grafik Paneli
        JPanel chartPanel = createPieChartPanel();
        JPanel orderFormPanel = createOrderForm();

        ordersComboBox = new JComboBox<>();
        loadOrdersIntoComboBox(); // Siparişleri ComboBox'a yükle

        // ComboBox'ı ayrı bir panelde göster
        JPanel ordersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ordersPanel.add(new JLabel("Siparişler:"));
        ordersPanel.add(ordersComboBox);

        // İptal butonunu ekleyelim
        JButton cancelOrderButton = new JButton("İptal Et");
        cancelOrderButton.addActionListener(e -> cancelOrder());
        ordersPanel.add(cancelOrderButton); // İptal butonunu ordersPanel'e ekle

        // Grafik panelini yukarı, tabloyu aşağı ekleyin
        add(chartPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);  // Tabloyu 'SOUTH' yerine 'CENTER' da koyabilirsiniz
        add(orderFormPanel, BorderLayout.NORTH);   // Sipariş formunu üstte göster
        add(ordersPanel, BorderLayout.EAST);      // ComboBox'ı sağa ekle

        ConcurrentPurchaseManager.getInstance().init();

        logService = new LogService() ;
    }

    private void cancelOrder() {
        String selectedOrderInfo = (String) ordersComboBox.getSelectedItem();

        if (selectedOrderInfo != null) {
            try {
                // Order ID'yi al
                int orderId = Integer.parseInt(selectedOrderInfo.split(",")[0].split(":")[1].trim());

                // Sipariş ve ürün servislerini oluştur
                OrderService orderService = new OrderService();
                ProductService productService = new ProductService();

                // Siparişi ve ürünü veritabanından al
                Order order = orderService.orderFindWithId(orderId);
                if (order == null) {
                    // JOptionPane.showMessageDialog(this, "Sipariş bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Product product = productService.productFindWithIdDb(order.getProductId());
                if (product == null) {
                    JOptionPane.showMessageDialog(this, "Ürün bilgisi alınamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Sipariş durumu kontrolü
                if (order.getOrderStatus().equals("Beklemede")) {
                    // Müşteri bütçesini güncelle
                    double updatedBudget = validatedCustomer.getBudget() + order.getQuantity() * product.getPrice();
                    validatedCustomer.setBudget(updatedBudget);

                    CustomerService customerService = new CustomerService();
                    customerService.customerUpdate(validatedCustomer); // Müşteri bilgilerini güncelle

                    // Siparişi veritabanından sil
                    orderService.orderDelete(orderId);

                    // Kullanıcıya başarılı mesajı göster
                    JOptionPane.showMessageDialog(this, "Sipariş başarıyla iptal edildi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                    // Bütçe etiketini güncelle
                    budgetInfoLabel.setText("Bütçe: " + validatedCustomer.getBudget());

                    // Siparişleri ComboBox'a yeniden yükle
                    loadOrdersIntoComboBox();
                } else {
                    JOptionPane.showMessageDialog(this, "Bu sipariş iptal edilemez. Yalnızca 'Beklemede' durumundaki siparişler iptal edilebilir.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Sipariş ID'si hatalı. Lütfen doğru bir sipariş seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen iptal etmek istediğiniz siparişi seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ComboBox öğelerine özel bir renderer tanımlayın
    class OrderComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Değer metin olarak geçiliyor
            String item = value != null ? value.toString() : "";
            if (item.contains("Durum: Onaylandı")) { // Eğer "Durum: Onaylandı" içeriyorsa
                c.setForeground(Color.GREEN); // Metin rengini yeşil yap
            } else if (item.contains("Durum: Reddedildi")) {
                c.setForeground(Color.RED); // Metin rengini kırmızı yap
            } else {
                c.setForeground(Color.BLACK); // Diğer durumlarda metin rengini siyah yap
            }

            return c;
        }
    }

    // loadOrdersIntoComboBox metodunda renderer'ı uygulayın
    private void loadOrdersIntoComboBox() {
        ordersComboBox.removeAllItems(); // Önce eski öğeleri temizle
        OrderService orderService = new OrderService();
        List<Order> orders = orderService.orderShowListWithCustomer(validatedCustomer.getCustomerId()); // Tüm siparişleri al
        Product product;

        for (Order order : orders) {
            product = productService.productFindWithIdDb(order.getProductId());
            String orderInfo = "Sipariş ID: " + order.getOrderId() + ", Ürün: " + product.getName() + ", Adet: "
                    + order.getQuantity() + ", Fiyat: " + product.getPrice() + ", Toplam Maliyet: "
                    + (product.getPrice() * order.getQuantity()) + "\n Durum: " + order.getOrderStatus();
            ordersComboBox.addItem(orderInfo); // Sipariş bilgisini ComboBox'a ekle
        }

        // Özel renderer'ı JComboBox'a ata
        ordersComboBox.setRenderer(new OrderComboBoxRenderer());
    }

    // Bakiye etiketini güncelleyen metodu ekliyoruz
    public void updateBudgetLabel(JLabel budgetInfoLabel) {
        budgetInfoLabel.setText("Bütçe: " + validatedCustomer.getBudget());
    }

    public JLabel getBudgetInfoLabel() {
        return this.budgetLabel; // JLabel nesnesini döndürdüğümüz metot
    }

    // Sipariş formu oluşturma fonksiyonu
    public JPanel createOrderForm() {
        // Sipariş formu için panel oluştur
        JPanel orderFormPanel = new JPanel(new FlowLayout());

        // Dinamik olarak ürün isimlerini ComboBox'a eklemek için liste al
        List<Product> productList = productService.productShowList(); // productService'den ürünleri al
        JComboBox<String> productComboBox = new JComboBox<>();
        for (Product product : productList) {
            productComboBox.addItem(product.getName()); // Ürün isimlerini ekle
            productNameToIdMap.put(product.getName(), product.getProductID());  // Ürün adı - ID eşleşmesini map'e ekle
        }

        // Miktar girişi için TextField
        JTextField quantityField = new JTextField(5);

        // Bakiye etiketi
        budgetInfoLabel = new JLabel("Bütçe: " + validatedCustomer.getBudget());

        budgetInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));  // Yazı tipini düzenleyebilirsiniz

        // Sipariş butonu
        JButton orderButton = new JButton("Sipariş Ver");

        orderButton.addActionListener(e -> {
            // Yükleme penceresi
            JDialog loadingDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "İşlem Devam Ediyor", Dialog.ModalityType.APPLICATION_MODAL);
            loadingDialog.setLayout(new BorderLayout());
            JLabel loadingLabel = new JLabel("Sipariş veriliyor, lütfen bekleyin...");
            loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            loadingDialog.add(loadingLabel, BorderLayout.CENTER);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); // Sürekli hareket eden bir yüklenme animasyonu
            progressBar.setForeground(Color.GREEN); // Yükleme çubuğunun rengini yeşil yap
            loadingDialog.add(progressBar, BorderLayout.SOUTH);

            loadingDialog.setSize(300, 100);
            loadingDialog.setLocationRelativeTo(this);

            // SwingWorker ile işlemleri arka planda çalıştırma
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    String selectedProduct = (String) productComboBox.getSelectedItem();
                    String quantityText = quantityField.getText();

                    try {
                        // Yavaşça ilerleme için döngüyle gecikme ekleyelim
                        for (int i = 0; i < 5; i++) {
                            Thread.sleep(500); // 500 ms bekleme (toplamda 2.5 saniye sürer)
                            progressBar.setValue((i + 1) * 20); // Yükleme çubuğunu %20 artır
                        }

                        int quantity = Integer.parseInt(quantityText);

                        if (quantity > 5) {
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                                    null, "Bir üründen en fazla 5 adet satın alabilirsiniz.", "Hata", JOptionPane.ERROR_MESSAGE));
                            return null;
                        }

                        int selectedProductId = productNameToIdMap.get(selectedProduct);
                        Product selectedProductObj = productService.productFindWithIdDb(selectedProductId); // Ürün objesini al
                        double productPrice = selectedProductObj.getPrice(); // Ürünün fiyatı

                        // Toplam tutar hesapla
                        double totalAmount = productPrice * quantity;

                        // Bütçe kontrolü
                        if (validatedCustomer.getBudget() >= totalAmount) {
                            // Siparişi oluştur
                            Order order = new Order();
                            order.setCustomerId(validatedCustomer.getCustomerId()); // validatedCustomer ID'yi al
                            order.setProductId(selectedProductId);
                            order.setQuantity(quantity);
                            order.setOrderDate(Date.valueOf(LocalDate.now()));
                            order.setOrderStatus("Beklemede");
                            order.setOrderTime(Time.valueOf(LocalTime.now()));
                            order.setPriority(0.0);

                            // Siparişi kaydet
                            OrderService orderService = new OrderService();
                            orderService.orderAdd(order);

                            // Bütçeyi güncelle
                            validatedCustomer.setBudget(validatedCustomer.getBudget() - totalAmount); // Yeni bütçeyi ayarla
                            CustomerService customerService = new CustomerService();
                            customerService.customerUpdate(validatedCustomer);

                            // Bütçe etiketi güncelle
                            SwingUtilities.invokeLater(() -> budgetInfoLabel.setText("Bütçe: " + validatedCustomer.getBudget()));

                            // Siparişleri ComboBox'a yeniden yükle
                            SwingUtilities.invokeLater(() -> loadOrdersIntoComboBox());

                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                                    null, "Sipariş başarıyla kaydedildi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE));
                        } else {
                            // Yetersiz bütçe hatası
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                                    null, "Yetersiz bakiye! Toplam tutar: " + totalAmount, "Hata", JOptionPane.ERROR_MESSAGE));
                        }
                    } catch (NumberFormatException ex) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                                null, "Lütfen geçerli bir adet girin!", "Hata", JOptionPane.ERROR_MESSAGE));
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                                null, "Sipariş kaydedilirken bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE));
                    }

                    return null;
                }

                @Override
                protected void done() {
                    // İşlem bittiğinde yüklenme penceresini kapat
                    loadingDialog.dispose();
                }
            };

            // Arka planda işlemi başlat ve yüklenme penceresini göster
            worker.execute();
            loadingDialog.setVisible(true);
        });

        // Bileşenleri panele ekle
        orderFormPanel.add(new JLabel("Ürün:"));
        orderFormPanel.add(productComboBox);
        orderFormPanel.add(new JLabel("Adet:"));
        orderFormPanel.add(quantityField);
        orderFormPanel.add(orderButton);

        // Bakiye bilgisini de ekleyin
        orderFormPanel.add(budgetInfoLabel);  // Bakiye bilgisi burada ekleniyor

        return orderFormPanel;
    }

    private void loadProductData() {
        List<Product> products = productService.productShowList();
        tableModel.setRowCount(0); // Tablodaki mevcut verileri temizle

        for (Product product : products) {
            Object[] rowData = {
                    product.getProductID(),
                    product.getName(),
                    product.getStock(),
                    product.getPrice()
            };
            tableModel.addRow(rowData);
        }
    }

    private JPanel createPieChartPanel() {
        // PieDataset oluştur
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Tablo verilerinden dataset'e ekleme yap
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String productName = (String) tableModel.getValueAt(i, 1); // Ürün Adı
            int stock = (int) tableModel.getValueAt(i, 2); // Stok Miktarı
            dataset.setValue(productName, stock);
        }

        // PieChart oluştur
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Stok Dağılımı", // Başlık
                dataset,        // Dataset
                true,           // Legend gösterilsin mi
                true,           // Tooltip gösterilsin mi
                false           // URL'ler gereksiz
        );

        // ChartPanel oluştur ve geri döndür
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }
}