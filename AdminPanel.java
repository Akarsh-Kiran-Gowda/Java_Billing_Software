// @author Akarsh Kiran Gowda
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.*;

public class AdminPanel {
    private JTextField itemNameField;
    private JTextField itemPriceField;
    private JTextField itemDateField;
    private JButton addItemButton;
    private JButton generateReportButton;
    public JPanel mainPanel;
    private JTextField startDateField;
    private JTextField endDateField;

    private Connection connection;

    public AdminPanel() {
        // Initialize GUI components
        mainPanel = new JPanel(new GridBagLayout());
        itemNameField = new JTextField(20);
        itemPriceField = new JTextField(20);
        itemDateField = new JTextField(20);
        startDateField = new JTextField(10);
        endDateField = new JTextField(10);

        addItemButton = new JButton("Add Item");
        generateReportButton = new JButton("Generate Transaction Report");

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add components to the panel
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Item Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        mainPanel.add(itemNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Item Price:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(itemPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Manufacturing Date:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(itemDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(addItemButton, gbc);

        // Add components for generating report
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        mainPanel.add(startDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        mainPanel.add(endDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        mainPanel.add(generateReportButton, gbc);

        connectToDatabase();

        // ActionListener for adding new item
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = itemNameField.getText();
                double itemPrice = 0;
                String itemDate = itemDateField.getText();
                try {
                    itemPrice = Double.parseDouble(itemPriceField.getText());
                    addItem(itemName, itemPrice, itemDate);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid price.");
                }
            }
        });

        // ActionListener for generating transaction report
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startDate = startDateField.getText();
                String endDate = endDateField.getText();
                generateTransactionReport(startDate, endDate);
            }
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/billing_db", "akarsh", "akarsh");
            System.out.println("Connected to MySQL Database.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        }
    }

    private void addItem(String itemName, double itemPrice, String itemDate) {
        try {
            String query = "INSERT INTO items (name, price, date_of_manufacturing) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, itemName);
            preparedStatement.setDouble(2, itemPrice);
            preparedStatement.setString(3, itemDate);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item added successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to add item: " + e.getMessage());
        }
    }

    private void generateTransactionReport(String startDate, String endDate) {
        try {
            // Use TYPE_SCROLL_INSENSITIVE to allow movement through the result set
            String query = "SELECT * FROM orders WHERE order_date BETWEEN ? AND ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(null, "No transactions found for the specified date range.");
                return;
            }

            Document document = new Document();
            String fileName = "Transaction_Report_" + startDate + "_to_" + endDate + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();
            document.add(new Paragraph("Akarsh Kiran Gowda"));
            document.add(new Paragraph("Transaction Report"));
            document.add(new Paragraph("From: " + startDate + " To: " + endDate));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Order ID");
            table.addCell("Customer Name");
            table.addCell("Product");
            table.addCell("Quantity");

            resultSet.beforeFirst();
            while (resultSet.next()) {
                int orderId = resultSet.getInt("id");
                String customerName = resultSet.getString("customer_name");
                String product = resultSet.getString("product");
                int quantity = resultSet.getInt("quantity");

                table.addCell(String.valueOf(orderId));
                table.addCell(customerName);
                table.addCell(product);
                table.addCell(String.valueOf(quantity));
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(null, "Transaction report generated: " + fileName);
        } catch (SQLException | DocumentException | java.io.IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to generate report: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Create JFrame for the Admin Panel
        JFrame adminFrame = new JFrame("Admin Panel");
        adminFrame.setContentPane(new AdminPanel().mainPanel);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(600, 400);  // Adjust frame size for better UI
        adminFrame.setVisible(true);
    }
}
