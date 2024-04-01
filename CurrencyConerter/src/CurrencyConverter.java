import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

public class CurrencyConverter extends JFrame {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    private JComboBox<String> baseCurrencyComboBox;
    private JComboBox<String> targetCurrencyComboBox;
    private JTextField amountField;
    private JLabel resultLabel;

    private String[] currencies = {"USD", "EUR", "GBP", "JPY", "AUD"}; // Sample currencies

    public CurrencyConverter() {
        setTitle("Currency Converter");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(null);

        JLabel baseCurrencyLabel = new JLabel("Base Currency:");
        baseCurrencyLabel.setBounds(20, 20, 100, 25);
        mainPanel.add(baseCurrencyLabel);

        baseCurrencyComboBox = new JComboBox<>(currencies);
        baseCurrencyComboBox.setBounds(130, 20, 100, 25);
        mainPanel.add(baseCurrencyComboBox);

        JLabel targetCurrencyLabel = new JLabel("Target Currency:");
        targetCurrencyLabel.setBounds(20, 50, 100, 25);
        mainPanel.add(targetCurrencyLabel);

        targetCurrencyComboBox = new JComboBox<>(currencies);
        targetCurrencyComboBox.setBounds(130, 50, 100, 25);
        mainPanel.add(targetCurrencyComboBox);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(20, 80, 100, 25);
        mainPanel.add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(130, 80, 100, 25);
        mainPanel.add(amountField);

        JButton convertButton = new JButton("Convert");
        convertButton.setBounds(20, 110, 100, 25);
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });
        mainPanel.add(convertButton);

        resultLabel = new JLabel();
        resultLabel.setBounds(20, 140, 350, 25);
        mainPanel.add(resultLabel);

        add(mainPanel);
    }

    private void convertCurrency() {
        try {
            String baseCurrency = (String) baseCurrencyComboBox.getSelectedItem();
            String targetCurrency = (String) targetCurrencyComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());

            double exchangeRate = getExchangeRate(baseCurrency, targetCurrency);
            double convertedAmount = amount * exchangeRate;

            resultLabel.setText(amount + " " + baseCurrency + " equals " + convertedAmount + " " + targetCurrency);
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Conversion Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double getExchangeRate(String baseCurrency, String targetCurrency) throws IOException {
        @SuppressWarnings("deprecation")
        URL url = new URL(API_URL + baseCurrency);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONObject rates = jsonResponse.getJSONObject("rates");
        return rates.getDouble(targetCurrency);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CurrencyConverter().setVisible(true);
            }
        });
    }
}
