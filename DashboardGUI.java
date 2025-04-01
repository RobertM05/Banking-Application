package org.example.gui;

import org.example.User;
import org.example.db.MyJDBC;
import org.example.loans.LoanCalculator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

public class DashboardGUI extends JFrame {
    private User currentUser;
    private JLabel balanceLabel;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public DashboardGUI(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Banking Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Content
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setLeftComponent(createQuickActionsPanel());
        centerSplit.setRightComponent(createAccountPanel());
        centerSplit.setResizeWeight(0.3);
        centerSplit.setDividerLocation(300);
        mainPanel.add(centerSplit, BorderLayout.CENTER);

        // Transaction History
        mainPanel.add(createTransactionHistoryPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        balanceLabel = new JLabel("Current Balance: " + currencyFormat.format(currentUser.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(balanceLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        panel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Buttons
        JButton depositButton = createActionButton("Deposit", new Color(76, 175, 80));
        JButton withdrawButton = createActionButton("Withdraw", new Color(244, 67, 54));
        JButton transferButton = createActionButton("Transfer", new Color(33, 150, 243));
        JButton loanButton = createActionButton("Loan Calculator", new Color(255, 152, 0));

        // Add buttons
        gbc.gridy = 0;
        panel.add(depositButton, gbc);

        gbc.gridy = 1;
        panel.add(withdrawButton, gbc);

        gbc.gridy = 2;
        panel.add(transferButton, gbc);

        gbc.gridy = 3;
        panel.add(loanButton, gbc);

        // Add vertical space
        gbc.weighty = 1.0;
        gbc.gridy = 4;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setOpaque(true);

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        // Action listeners
        switch(text) {
            case "Deposit" -> button.addActionListener(this::performDeposit);
            case "Withdraw" -> button.addActionListener(this::performWithdrawal);
            case "Transfer" -> button.addActionListener(this::performTransfer);
            case "Loan Calculator" -> button.addActionListener(e -> new LoanCalculator().setVisible(true));
        }

        return button;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Account Overview"));
        panel.setBackground(Color.WHITE);

        JTextArea accountDetails = new JTextArea();
        accountDetails.setEditable(false);
        accountDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountDetails.setText(String.format(
                "Account Number: %s\n\nMember Since: 2023-01-01\n\nLast Login: Today",
                "****" + currentUser.getId()
        ));

        panel.add(new JScrollPane(accountDetails), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createTransactionHistoryPanel() {
        String[] columns = {"Date", "Description", "Amount", "Balance"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Sample transactions
        model.addRow(new Object[]{"2023-10-01", "Initial Deposit", "$5,000.00", "$5,000.00"});
        model.addRow(new Object[]{"2023-10-05", "Grocery Store Purchase", "-$125.45", "$4,874.55"});

        JTable transactionTable = new JTable(model);
        transactionTable.setFillsViewportHeight(true);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        scrollPane.setPreferredSize(new Dimension(800, 150));

        return scrollPane;
    }

    private void performDeposit(ActionEvent e) {
        String amountString = JOptionPane.showInputDialog(
                this,
                "Enter deposit amount:",
                "Deposit",
                JOptionPane.PLAIN_MESSAGE
        );

        if (amountString == null || amountString.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Amount must be positive!",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            boolean success = MyJDBC.updateBalance(currentUser.getId(), amount);
            if (success) {
                refreshBalance();
                JOptionPane.showMessageDialog(
                        this,
                        currencyFormat.format(amount) + " deposited successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Deposit failed. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid amount format!",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void performWithdrawal(ActionEvent e) {
        String amountString = JOptionPane.showInputDialog(
                this,
                "Enter withdrawal amount:",
                "Withdraw",
                JOptionPane.PLAIN_MESSAGE
        );

        if (amountString == null || amountString.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Amount must be positive!",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (amount > currentUser.getBalance()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Insufficient funds!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            boolean success = MyJDBC.updateBalance(currentUser.getId(), -amount);
            if (success) {
                refreshBalance();
                JOptionPane.showMessageDialog(
                        this,
                        currencyFormat.format(amount) + " withdrawn successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Withdrawal failed. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid amount format!",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void performTransfer(ActionEvent e) {
        // Get recipient username
        String recipientUsername = JOptionPane.showInputDialog(
                this,
                "Enter recipient's username:",
                "Transfer",
                JOptionPane.PLAIN_MESSAGE
        );

        if (recipientUsername == null || recipientUsername.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a recipient username",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if recipient exists
        if (!MyJDBC.userExists(recipientUsername)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Recipient not found!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Get transfer amount
        String amountString = JOptionPane.showInputDialog(
                this,
                "Enter transfer amount:",
                "Transfer",
                JOptionPane.PLAIN_MESSAGE
        );

        if (amountString == null || amountString.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Amount must be positive!",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (amount > currentUser.getBalance()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Insufficient funds!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            boolean success = MyJDBC.transferFunds(
                    currentUser.getId(),
                    recipientUsername,
                    amount
            );

            if (success) {
                refreshBalance();
                JOptionPane.showMessageDialog(
                        this,
                        currencyFormat.format(amount) + " transferred to " + recipientUsername,
                        "Transfer Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Transfer failed. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid amount format!",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshBalance() {
        double updatedBalance = MyJDBC.getUserBalance(currentUser.getId());
        currentUser.setBalance(updatedBalance);
        balanceLabel.setText("Current Balance: " + currencyFormat.format(updatedBalance));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User testUser = new User(1, "john_doe", "john@example.com", 5000.00);
            new DashboardGUI(testUser);
        });
    }
}