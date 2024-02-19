module com.currencyexchange.currency_converter {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.postgresql.jdbc;


    opens com.currencyexchange.currency_converter to javafx.fxml;
    exports com.currencyexchange.currency_converter;
}