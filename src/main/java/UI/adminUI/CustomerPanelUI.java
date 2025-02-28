package UI.adminUI;

import com.formdev.flatlaf.FlatLightLaf;
import model.Customer;
import service.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class CustomerPanelUI extends JPanel {
    private JTable customerTable;
    private JButton orderButton;
    private JComboBox<String> productComboBox;
    private JTextField quantityField;
    private CustomerService customerService;

    public CustomerPanelUI() {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        customerService = new CustomerService(); // CustomerService örneği oluşturuluyor

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        // Customer Table
        String[] columnNames = {"CustomerID", "Ad", "Tür", "Bütçe"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hiçbir hücre düzenlenemez
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Tablo yazı tipi
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Tekli seçim modu
        customerTable.setRowHeight(25); // Satır yüksekliği

        // Tablo başlık stilini ayarla
        JTableHeader header = customerTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14)); // Başlık yazı tipi
        header.setBackground(new Color(0, 153, 255)); // Başlık arka plan rengi
        header.setForeground(Color.WHITE); // Başlık yazı rengi

        JScrollPane tableScrollPane = new JScrollPane(customerTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları
        add(tableScrollPane, BorderLayout.CENTER);

        // Verileri tabloya yükle
        loadCustomerData(tableModel);

        // Order Form (Opsiyonel, yorum satırı olarak bırakıldı)
        /*
        JPanel orderFormPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        orderFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Kenar boşlukları

        productComboBox = new JComboBox<>(new String[]{"Product1", "Product2", "Product3", "Product4", "Product5"});
        productComboBox.setFont(new Font("Arial", Font.PLAIN, 14)); // ComboBox yazı tipi

        quantityField = new JTextField(5);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14)); // TextField yazı tipi

        orderButton = new JButton("Sipariş Ver");
        orderButton.setFont(new Font("Arial", Font.BOLD, 14)); // Buton yazı tipi
        orderButton.setBackground(new Color(0, 153, 255)); // Buton arka plan rengi
        orderButton.setForeground(Color.WHITE); // Buton yazı rengi
        orderButton.setFocusPainted(false); // Odak çerçevesini kaldır

        orderFormPanel.add(new JLabel("Ürün:"));
        orderFormPanel.add(productComboBox);
        orderFormPanel.add(new JLabel("Adet:"));
        orderFormPanel.add(quantityField);
        orderFormPanel.add(orderButton);

        add(orderFormPanel, BorderLayout.SOUTH);
        */
    }

    private void loadCustomerData(DefaultTableModel tableModel) {
        List<Customer> customers = customerService.customerShowList(); // Customer bilgilerini getir

        for (Customer customer : customers) {
            Object[] rowData = {
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getType(),
                    customer.getBudget()
            };
            tableModel.addRow(rowData);
        }
    }
}