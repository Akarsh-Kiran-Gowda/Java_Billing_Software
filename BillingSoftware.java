// @author Akarsh Kiran Gowda
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.io.FileOutputStream;

public class BillingSoftware {
    private JTextField customerNameField;
    private JTextField mobileNumberField;
    private JTextField searchItemField;
    private JComboBox<String> itemComboBox;
    private JTextField quantityField;
    private JButton addButton;
    private JButton generateInvoiceButton;
    public JPanel mainPanel;

    private Connection connection;

    public BillingSoftware() {
        // Initialize GUI components
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        customerNameField = new JTextField(20);
        mobileNumberField = new JTextField(20);
        searchItemField = new JTextField(20);
        itemComboBox = new JComboBox<>();
        quantityField = new JTextField(20);

        addButton = new JButton("Add Order");
        generateInvoiceButton = new JButton("Generate Invoice");

        // Layout for components
        gbc.insets = new Insets(5, 5, 5, 5);  // Add some space around components
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(customerNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Mobile Number:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(mobileNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Search Item:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(searchItemField, gbc);

        gbc.gridx = 2;
        mainPanel.add(itemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(addButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(generateInvoiceButton, gbc);

        connectToDatabase();

        // ActionListener for searching items
        searchItemField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchItems();
            }
        });

        // ActionListener for adding order
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerName = customerNameField.getText();
                String mobileNumber = mobileNumberField.getText();
                String selectedItem = (String) itemComboBox.getSelectedItem();
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                    // Add the customer details and proceed with the order
                    addCustomerAndOrder(customerName, mobileNumber, selectedItem, quantity);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number for quantity.");
                }
            }
        });

        // ActionListener for generating invoice
        generateInvoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerName = customerNameField.getText();
                generateInvoice(customerName);
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

    private boolean isMobileNumberExists(String mobileNumber) {
        try {
            String query = "SELECT * FROM orders WHERE mobile_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, mobileNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            // If the mobile number already exists, return true
            return resultSet.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error checking mobile number: " + e.getMessage());
            return false;
        }
    }

    private void searchItems() {
        String searchQuery = searchItemField.getText().toLowerCase();

        // Fetch matching items from the database
        try {
            String query = "SELECT name FROM items WHERE name LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchQuery + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear the combo box and add the results
            itemComboBox.removeAllItems();

            while (resultSet.next()) {
                itemComboBox.addItem(resultSet.getString("name"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching items: " + e.getMessage());
        }
    }

    private void addCustomerAndOrder(String customerName, String mobileNumber, String product, int quantity) {
        try {
            // Insert customer details (mobile number, name) and order details (product, quantity)
            String query = "INSERT INTO orders (customer_name, mobile_number, product, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customerName);
            preparedStatement.setString(2, mobileNumber);
            preparedStatement.setString(3, product);
            preparedStatement.setInt(4, quantity);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Order added successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to add order: " + e.getMessage());
        }
    }

    private void generateInvoice(String customerName) {
        try {
            String query = "SELECT * FROM orders WHERE customer_name = ?";
            // Make ResultSet scrollable
            PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, customerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(null, "No orders found for customer: " + customerName);
                return;
            }

            Document document = new Document();
            String fileName = customerName + "_Invoice.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            // Add company name and current date
            document.add(new Paragraph("Akarsh Kiran Gowda"));
            document.add(new Paragraph("Invoice Date: " + new java.util.Date()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Invoice for " + customerName));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(3);
            table.addCell("Product");
            table.addCell("Quantity");
            table.addCell("Price");

            double totalAmount = 0;
            resultSet.beforeFirst(); // Go back to the beginning of the result set
            while (resultSet.next()) {
                String product = resultSet.getString("product");
                int quantity = resultSet.getInt("quantity");
                double price = getProductPrice(product);  // Get price dynamically

                table.addCell(product);
                table.addCell(String.valueOf(quantity));
                table.addCell("$" + price);

                totalAmount += price * quantity;
            }

            document.add(table);
            document.add(new Paragraph("\nTotal: $" + totalAmount));
            document.close();

            JOptionPane.showMessageDialog(null, "Invoice generated: " + fileName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to generate invoice: " + e.getMessage());
        }
    }

    private double getProductPrice(String product) {
        double price = 0.0;
        try {
            String query = "SELECT price FROM items WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                price = resultSet.getDouble("price");
            } else {
                JOptionPane.showMessageDialog(null, "Product not found: " + product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching product price: " + e.getMessage());
        }
        return price;
    }

    public static void main(String[] args) {
        // Show login screen first
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
    }
}
