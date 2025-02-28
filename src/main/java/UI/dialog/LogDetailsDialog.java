package UI.dialog;

import javax.swing.*;
import java.awt.*;

public class LogDetailsDialog extends JDialog {
    public LogDetailsDialog(JFrame parent, String logDetails) {
        super(parent, "Log Detayları", true);
        setLayout(new BorderLayout());

        // Log Details
        JTextArea logDetailsArea = new JTextArea(logDetails);
        logDetailsArea.setEditable(false);
        add(new JScrollPane(logDetailsArea), BorderLayout.CENTER);

        // Close Button
        JButton closeButton = new JButton("Kapat");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}
