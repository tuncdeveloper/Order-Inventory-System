package UI;

import UI.adminUI.MainFrameUI;
import com.formdev.flatlaf.FlatLightLaf;
import model.Admin;
import model.Customer;
import service.AdminService;
import service.CustomerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AdminService adminService;
    private CustomerService customerService;
    public static String username;
    public static String password;
    public static UI.adminUI.MainFrameUI mainFrameUIAdmin;
    public static UI.customerUI.MainFrameUI mainFrameUICustomer;

    public Login() {
        // FlatLaf temasını uygula
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        adminService = new AdminService(); // AdminService sınıfını oluşturuyoruz
        customerService = new CustomerService(); // CustomerService sınıfını oluşturuyoruz

        setTitle("Giriş Yap");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300); // Pencere boyutunu biraz büyüttük
        setLocationRelativeTo(null);

        // Ana panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Kenarlık boşlukları
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Bileşenler arasındaki boşluklar
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Uygulama ismi başlığı
        JLabel appTitleLabel = new JLabel("Eş Zamanlı Sipariş ve Stok Yönetimi Uygulaması", JLabel.CENTER);
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(appTitleLabel, gbc);

        // Hoş geldiniz yazısı
        JLabel welcomeLabel = new JLabel("Hoş Geldiniz", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy++;
        mainPanel.add(welcomeLabel, gbc);

        // Kullanıcı adı alanı
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Kullanıcı Adı:"), gbc);

        usernameField = new JTextField();
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Parola alanı
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Parola:"), gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Giriş butonu
        loginButton = new JButton("Giriş Yap");
        loginButton.setBackground(new Color(0, 153, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        // Giriş butonunun tıklama olayını dinliyoruz
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kullanıcı adı ve parolayı alıyoruz
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                Login.username = username;
                Login.password = password;

                // Admin doğrulama
                Admin admin = new Admin();
                admin.setUsername(username);
                admin.setPassword(password);

                Admin validatedAdmin = adminService.adminShow(admin);
                if (validatedAdmin != null && validatedAdmin.getUsername() != null) {
                    // Admin giriş başarılı
                    JOptionPane.showMessageDialog(null, "Giriş Başarılı Admin");
                    MainFrameUI mainFrameUI = new MainFrameUI();
                    mainFrameUI.setVisible(true);
                    mainFrameUIAdmin = mainFrameUI;
                } else {
                    // Admin değilse, müşteri doğrulama
                    Customer customer = new Customer();
                    customer.setUsername(username);
                    customer.setPassword(password);

                    Customer validatedCustomer = customerService.customerLoginDb(customer);

                    if (validatedCustomer != null && validatedCustomer.getUsername() != null) {
                        // Müşteri giriş başarılı
                        JOptionPane.showMessageDialog(null, "Giriş Başarılı\nHoşgeldiniz " + validatedCustomer.getName());
                        UI.customerUI.MainFrameUI mainFrameUI = new UI.customerUI.MainFrameUI(validatedCustomer);
                        mainFrameUICustomer = mainFrameUI;
                        mainFrameUI.setVisible(true);
                    } else {
                        // Geçersiz kullanıcı adı veya parola
                        JOptionPane.showMessageDialog(null, "Geçersiz kullanıcı adı veya parola");
                    }
                }
            }
        });
    }

    public static void restart() {
        if (mainFrameUIAdmin != null) {
            mainFrameUIAdmin.closeAllPanels();
            mainFrameUIAdmin.dispose();
            mainFrameUIAdmin = new MainFrameUI();
            mainFrameUIAdmin.setVisible(true);
        } else if (mainFrameUICustomer != null) {
            mainFrameUICustomer.closeAllPanels();
            mainFrameUICustomer.dispose();
            Customer customer = new Customer();
            customer.setUsername(username);
            customer.setPassword(password);
            CustomerService customerServiceForStatic = new CustomerService();
            Customer validatedCustomer = customerServiceForStatic.customerLoginDb(customer);
            mainFrameUICustomer = new UI.customerUI.MainFrameUI(customer);
            mainFrameUICustomer.setVisible(true);
        }
    }

    public static void main(String[] args) {
        // FlatLaf temasını uygula
        FlatLightLaf.setup();

        // Login ekranını başlat
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}