package UI.adminUI;

import com.formdev.flatlaf.FlatLightLaf; // FlatLaf temasını ekleyin
import model.Product;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductStockPanelUI extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private ProductService productService;
    private static ProductStockPanelUI instance;

    public static ProductStockPanelUI getInstance() {
        if (instance == null) {
            instance = new ProductStockPanelUI();
        }
        return instance;
    }

    public ProductStockPanelUI() {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        productService = new ProductService(); // ProductService örneği oluşturuluyor
        setLayout(new BorderLayout());

        // Ürün Tablosu
        String[] columnNames = {"Ürün ID", "Ürün Adı", "Stok Miktarı", "Fiyat"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hiçbir hücre düzenlenemez
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Tekli seçim modu
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 300)); // Tablo boyutunu ayarla
        add(tableScrollPane, BorderLayout.WEST); // Tabloyu sol tarafa ekleyelim

        // Verileri tabloya yükle
        loadProductData();

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Butonlar arası boşluk ekle
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        JButton addButton = new JButton("Ekle");
        JButton deleteButton = new JButton("Sil");
        JButton updateButton = new JButton("Güncelle");

        // Buton stilleri
        addButton.setBackground(new Color(0, 153, 255)); // Mavi arka plan
        addButton.setForeground(Color.WHITE); // Beyaz yazı rengi
        addButton.setFocusPainted(false); // Odak çerçevesini kaldır

        deleteButton.setBackground(new Color(255, 89, 94)); // Kırmızı arka plan
        deleteButton.setForeground(Color.WHITE); // Beyaz yazı rengi
        deleteButton.setFocusPainted(false); // Odak çerçevesini kaldır

        updateButton.setBackground(new Color(50, 205, 50)); // Yeşil arka plan
        updateButton.setForeground(Color.WHITE); // Beyaz yazı rengi
        updateButton.setFocusPainted(false); // Odak çerçevesini kaldır

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        add(buttonPanel, BorderLayout.SOUTH); // Butonları alt panelde göstereceğiz

        // Butonların işlevselliğini ekle
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });

        // Grafik Paneli
        JPanel chartPanel = createPieChartPanel();
        add(chartPanel, BorderLayout.CENTER); // Grafiği ortada göstereceğiz
    }

    public void loadProductData() {
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

        // Tabloyu güncelle
        tableModel.fireTableDataChanged();
    }

    private void addProduct() {
        JTextField nameField = new JTextField(10);
        JTextField stockField = new JTextField(5);
        JTextField priceField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5)); // GridLayout ile düzenle
        panel.add(new JLabel("Ürün Adı:"));
        panel.add(nameField);
        panel.add(new JLabel("Stok Miktarı:"));
        panel.add(stockField);
        panel.add(new JLabel("Fiyat:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Ürün Ekle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            int stock = Integer.parseInt(stockField.getText());
            double price = Double.parseDouble(priceField.getText());

            // Yeni ürün objesi oluştur
            Product newProduct = new Product(0, name, stock, price); // ID otomatik atanır

            // Aynı isimde bir ürün olup olmadığını kontrol et
            if (productService.productIsAdd(newProduct)) {
                JOptionPane.showMessageDialog(this, "Bu isimde bir ürün zaten mevcut.",
                        "Hata", JOptionPane.ERROR_MESSAGE);
            } else {
                // Ürün eklenebilir
                productService.productAdd(newProduct);
                JOptionPane.showMessageDialog(this, newProduct.getName() + " başarıyla eklendi.",
                        "Başarı", JOptionPane.INFORMATION_MESSAGE);

                loadProductData();
                updateChart(); // Grafiği güncelle
            }
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow(); // Seçilen satırın index'ini al

        if (selectedRow != -1) { // Seçili öğe var ise
            // Seçilen ürün objesini al
            int productId = (int) productTable.getValueAt(selectedRow, 0); // 0. sütun ürün ID'sini al
            Product selectedProduct = productService.productFindWithIdDb(productId); // ID ile ürünü al

            // Ürün silme işlemi
            productService.productDelete(selectedProduct.getProductID()); // Veritabanındaki ürünü sil

            // Satırı modelden ve UI'dan kaldır
            ((DefaultTableModel) productTable.getModel()).removeRow(selectedRow); // JTable modelinden satırı kaldır

            // Başarı mesajı göster
            JOptionPane.showMessageDialog(this, selectedProduct.getName() + " başarıyla silindi.");

            // Listeyi güncelle
            loadProductData();
            updateChart(); // Grafiği güncelle

        } else {
            // Seçili bir öğe yoksa kullanıcıyı uyar
            JOptionPane.showMessageDialog(this, "Lütfen silmek istediğiniz ürünü seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            String currentName = (String) tableModel.getValueAt(selectedRow, 1);
            int currentStock = (int) tableModel.getValueAt(selectedRow, 2);
            double currentPrice = (double) tableModel.getValueAt(selectedRow, 3);

            JTextField nameField = new JTextField(currentName, 10);
            JTextField stockField = new JTextField(String.valueOf(currentStock), 5);
            JTextField priceField = new JTextField(String.valueOf(currentPrice), 5);

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5)); // GridLayout ile düzenle
            panel.add(new JLabel("Ürün Adı:"));
            panel.add(nameField);
            panel.add(new JLabel("Stok Miktarı:"));
            panel.add(stockField);
            panel.add(new JLabel("Fiyat:"));
            panel.add(priceField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Ürün Güncelle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                int stock = Integer.parseInt(stockField.getText());
                double price = Double.parseDouble(priceField.getText());

                // Güncelleme işlemi yapılmadan önce ürün adı değişmiş mi kontrol et
                Product updatedProduct = new Product(productId, name, stock, price);
                Product existingProduct = productService.productFindWithIdDb(productId); // Mevcut ürünü al

                if (!existingProduct.getName().equals(name) && productService.productIsAdd(updatedProduct)) {
                    // Ürün adı değiştirildi ve başka bir ürünle çakışıyor
                    JOptionPane.showMessageDialog(this, "Bu isimde bir ürün zaten mevcut.", "Hata", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Ürün güncellenebilir
                    productService.productUpdate(updatedProduct);
                    JOptionPane.showMessageDialog(this, updatedProduct.getName() + " başarıyla güncellenmiştir.", "Başarı", JOptionPane.INFORMATION_MESSAGE);
                    loadProductData();
                    updateChart(); // Grafiği güncelle
                }
            }

        } else {
            JOptionPane.showMessageDialog(this, "Lütfen güncellemek istediğiniz ürünü seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateChart() {
        JPanel chartPanel = createPieChartPanel();
        remove(2); // Eski grafiği kaldır
        add(chartPanel, BorderLayout.CENTER); // Yeni grafiği ekle
        revalidate();
        repaint();
    }

    private JPanel createPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String productName = (String) tableModel.getValueAt(i, 1); // Ürün Adı
            int stock = (int) tableModel.getValueAt(i, 2); // Stok Miktarı
            dataset.setValue(productName, stock);
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Stok Dağılımı",
                dataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        JPanel chartContainer = new JPanel();
        chartContainer.setLayout(new BorderLayout());
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        return chartContainer;
    }
}