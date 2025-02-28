package UI.adminUI;

import javax.swing.*;
import java.awt.*;

public class PriorityAndWaitingPanelUI extends JPanel {
    private JTable priorityTable;

    public PriorityAndWaitingPanelUI() {
        setLayout(new BorderLayout());

        // Priority Table
        String[] columnNames = {"CustomerID", "Bekleme Süresi", "Öncelik Skoru"};
        Object[][] data = {
                {1, "5dk", 95},
                {2, "10dk", 50}
        };
        priorityTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(priorityTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Placeholder for Animation
        JLabel animationPlaceholder = new JLabel("Animasyon burada gösterilecek.", SwingConstants.CENTER);
        add(animationPlaceholder, BorderLayout.SOUTH);
    }
}

