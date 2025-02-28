package UI.customerUI;

import model.Customer;

import javax.swing.*;
import java.awt.*;

public class MainFrameUI extends JFrame {
    public MainFrameUI(Customer validatedCustomer) {
        setTitle("Müşteri ve Sipariş Yönetimi");
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        OrderProcessingAnimationUI orderProcessingAnimationUI = new OrderProcessingAnimationUI(validatedCustomer);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ürün Stok Paneli", new ProductStockPanelUI(validatedCustomer,orderProcessingAnimationUI));
       tabbedPane.addTab("Sipariş İşleme", new OrderProcessingAnimationUI(validatedCustomer));
       // tabbedPane.addTab("Log Paneli", new LogPanelUI(validatedCustomer));


        add(tabbedPane);
    }

    public void closeAllPanels () {
        Component[] components = this.getComponents() ;
        for (Component component : components) {
            if (component instanceof  JPanel) {
                remove(component);
            }
        }
    }

}
