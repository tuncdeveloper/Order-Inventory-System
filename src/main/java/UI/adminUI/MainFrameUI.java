package UI.adminUI;

import com.formdev.flatlaf.FlatLightLaf; // FlatLaf temasını ekleyin

import javax.swing.*;
import java.awt.*;

public class MainFrameUI extends JFrame {
    public MainFrameUI() {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Müşteri ve Sipariş Yönetimi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); // Pencere boyutunu büyüttük
        setLocationRelativeTo(null);

        // Ana panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        // TabbedPane oluştur
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14)); // Yazı tipini ayarla

        // Müşteri Paneli
        JPanel customerPanel = new CustomerPanelUI();
        tabbedPane.addTab("Müşteri Paneli", new ImageIcon("icons/customer.png"), customerPanel); // İkon ekle

        // Ürün Stok Paneli
        JPanel productStockPanel = new ProductStockPanelUI();
        tabbedPane.addTab("Ürün Stok Paneli", new ImageIcon("icons/stock.png"), productStockPanel); // İkon ekle

        // Sipariş İşleme Paneli
        ProductStockPanelUI productStockPanelUI = new ProductStockPanelUI();
        JPanel orderProcessingPanel = new OrderProcessingAnimationUI(productStockPanelUI);
        tabbedPane.addTab("Sipariş İşleme", new ImageIcon("icons/order.png"), orderProcessingPanel); // İkon ekle

        // Log Paneli
        JPanel logPanel = new LogPanelUI();
        tabbedPane.addTab("Log Paneli", new ImageIcon("icons/log.png"), logPanel); // İkon ekle

        // Bekleme ve Öncelik Paneli (Opsiyonel)
        // tabbedPane.addTab("Bekleme ve Öncelik Paneli", new ImageIcon("icons/priority.png"), new PriorityAndWaitingPanelUI());

        // TabbedPane'yi ana panele ekle
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Ana paneli frame'e ekle
        add(mainPanel);

        // Pencereyi görünür yap
        setVisible(true);
    }

    public void closeAllPanels() {
        Component[] components = this.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                remove(component);
            }
        }
    }

    public static void main(String[] args) {
        // FlatLaf temasını uygula
        FlatLightLaf.setup();

        // MainFrameUI'yi başlat
        SwingUtilities.invokeLater(() -> {
            MainFrameUI mainFrameUI = new MainFrameUI();
            mainFrameUI.setVisible(true);
        });
    }
}