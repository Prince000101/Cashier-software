import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SupermarketCashier extends JFrame {

    private JTextField itemNameField, itemPriceField, itemQtyField;
    private JTextField buyerNameField, phoneField;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double total = 0.0;

    public SupermarketCashier() {
        setTitle("Supermarket Cashier");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel - Buyer + Item Input
        JPanel topPanel = new JPanel(new BorderLayout());

        // Buyer Info Panel
        JPanel buyerPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buyerNameField = new JTextField();
        phoneField = new JTextField();

        buyerPanel.add(new JLabel("Buyer Name:"));
        buyerPanel.add(buyerNameField);
        buyerPanel.add(new JLabel("Phone:"));
        buyerPanel.add(phoneField);

        // Item Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        itemNameField = new JTextField();
        itemPriceField = new JTextField();
        itemQtyField = new JTextField();
        JButton addButton = new JButton("Add to Cart");

        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(itemPriceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(itemQtyField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addButton);

        topPanel.add(buyerPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Cart Table
        tableModel = new DefaultTableModel(new Object[]{"Item", "Price", "Qty", "Subtotal"}, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel - Checkout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: ₹0.00");
        JButton checkoutButton = new JButton("Checkout");

        bottomPanel.add(totalLabel);
        bottomPanel.add(checkoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        addButton.addActionListener(e -> addItem());
        checkoutButton.addActionListener(e -> showPaymentDialog());

        setVisible(true);
    }

    private void addItem() {
        try {
            String name = itemNameField.getText().trim();
            double price = Double.parseDouble(itemPriceField.getText().trim());
            int qty = Integer.parseInt(itemQtyField.getText().trim());

            if (name.isEmpty() || price < 0 || qty <= 0) {
                throw new NumberFormatException();
            }

            double subtotal = price * qty;
            total += subtotal;

            tableModel.addRow(new Object[]{
                name,
                String.format("₹%.2f", price),
                qty,
                String.format("₹%.2f", subtotal)
            });

            totalLabel.setText(String.format("Total: ₹%.2f", total));

            itemNameField.setText("");
            itemPriceField.setText("");
            itemQtyField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPaymentDialog() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String buyerName = buyerNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (buyerName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter buyer's name and phone number.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, String.format("Total is ₹%.2f\nEnter payment amount:", total), "Payment", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return; // Cancelled

        try {
            double payment = Double.parseDouble(input.trim());

            if (payment < total) {
                JOptionPane.showMessageDialog(this, "Insufficient payment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double change = payment - total;
            showReceipt(payment, change, buyerName, phone);

            // Confirm reset
            int confirm = JOptionPane.showConfirmDialog(this, "Transaction complete. Clear cart and start new?", "New Transaction", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.setRowCount(0);
                total = 0.0;
                totalLabel.setText("Total: ₹0.00");
                buyerNameField.setText("");
                phoneField.setText("");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid payment input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceipt(double payment, double change, String buyerName, String phone) {
        StringBuilder receipt = new StringBuilder();

        // Date and time
        String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        receipt.append("   *** SUPER MART ***\n");
        receipt.append("  123 Market Street, India\n");
        receipt.append("-------------------------------\n");
        receipt.append("Date: ").append(dateTime).append("\n");
        receipt.append("Buyer: ").append(buyerName).append("\n");
        receipt.append("Phone: ").append(phone).append("\n");
        receipt.append("-------------------------------\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String item = (String) tableModel.getValueAt(i, 0);
            String price = (String) tableModel.getValueAt(i, 1);
            int qty = (int) tableModel.getValueAt(i, 2);
            String subtotal = (String) tableModel.getValueAt(i, 3);

            receipt.append(String.format("%s x%d - %s\n", item, qty, subtotal));
        }

        receipt.append("-------------------------------\n");
        receipt.append(String.format("TOTAL:     ₹%.2f\n", total));
        receipt.append(String.format("PAID:      ₹%.2f\n", payment));
        receipt.append(String.format("CHANGE:    ₹%.2f\n", change));
        receipt.append("===============================\n");
        receipt.append("   Thank you for shopping!\n");

        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);

        JOptionPane.showMessageDialog(this, new JScrollPane(receiptArea), "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupermarketCashier::new);
    }
}
