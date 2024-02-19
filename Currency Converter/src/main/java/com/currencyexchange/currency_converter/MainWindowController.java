package com.currencyexchange.currency_converter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable
{
    private Button RevertButton;
    private Button ConvertButton;
    private javafx.scene.layout.VBox VBox;
    public javafx.scene.layout.Pane Pane;
    @FXML
    private Label ExchangeRate;
    @FXML
    private ChoiceBox<String> FromCurrency;
    @FXML
    private ChoiceBox<String> ToCurrency;
    @FXML
    private Label ConvertField;
    @FXML
    private TextField AmountBox;

    //https://app.freecurrencyapi.com/dashboard
    private final String key = "fca_live_lVRx6nqYivh1grQAFhICYi8lI0N20TBXxC0ol37z";

    private final String[] currency = {"USD", "GBP", "EUR", "INR"};
    Map<String,Character> currencySymbol = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        FromCurrency.getItems().addAll(currency);
        ToCurrency.getItems().addAll(currency);
    }


    private boolean isNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void ConvertButtonClicked(ActionEvent actionEvent) throws IOException {
        String num = AmountBox.getText();
        if(num == null || num.isEmpty() || !isNumeric(num))
        {
            ConvertField.setText("Wrong Format");
            return;
        }

        double val = Double.parseDouble(num);

        if(val <= 0)
        {
            ConvertField.setText("Negative and 0 currency Conversion not possible");
            return;
        }

        String from = FromCurrency.getValue();
        String to = ToCurrency.getValue();

        if(Objects.equals(from, to))
        {
            ConvertField.setText("No conversion needed as both of them are same");
            return;
        }

        ConvertField.setText("Converted Amount = " + convert(from,to,val)+" "+to);

    }

    private double convert(String from, String to, Double val) throws IOException {
        String GetUrl = "https://api.freecurrencyapi.com/v1/latest?apikey="+key+"&currencies="+to+"&base_currency="+from;
        URL url = new URL(GetUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");

        int responseCode = http.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK)
        {
            BufferedReader bf = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while((line = bf.readLine()) != null)
            {
                response.append(line);
            }

            bf.close();

            //parsing the response
            JSONObject obj = new JSONObject(response.toString());
            Double exchangeRate = obj.getJSONObject("data").getDouble(to);
            //Round to 2 dp
            DecimalFormat df = new DecimalFormat("#.##");

            ExchangeRate.setText("Exchange Rate is "+String.valueOf(Double.parseDouble(df.format(exchangeRate))));

            double money = exchangeRate*val;
            money = Double.parseDouble(df.format(money));
            return money;
        }
        else
        {
            ConvertField.setText("API problem");
            return -1;
        }
    }

    public void ResetButtonClicked(ActionEvent actionEvent)
    {
        AmountBox.setText(null);
        ConvertField.setText(null);
        ExchangeRate.setText(null);
        FromCurrency.setValue(null);
        ToCurrency.setValue(null);
    }
}