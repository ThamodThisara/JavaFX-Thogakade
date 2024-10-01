package controller.customer;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerFormController implements Initializable {


    public TableColumn colCity;
    public TableColumn colProvince;
    public TableColumn colPostalCode;
    public JFXTextField txtCity;
    public JFXTextField txtPostalCode;
    public JFXTextField txtProvince;
    @FXML
    private JFXComboBox<String> cmbTitle;

    @FXML
    private TableColumn colAddress;

    @FXML
    private TableColumn colDob;

    @FXML
    private TableColumn colId;

    @FXML
    private TableColumn colName;

    @FXML
    private TableColumn colSalary;

    @FXML
    private DatePicker dateDob;

    @FXML
    private TableView<Customer> tblCustomers;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtSalary;

    CustomerService service = CustomerController.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));


        loadTable();

        ObservableList<String> titleList = FXCollections.observableArrayList();
        titleList.add("Mr.");
        titleList.add("Miss.");
        titleList.add("Ms.");
        cmbTitle.setItems(titleList);

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addValueText(newValue);
            }
        });
    }

    private void addValueText(Customer newValue) {
        txtId.setText(newValue.getId());
        txtName.setText(newValue.getName());
        txtSalary.setText("" + newValue.getSalary());
        txtAddress.setText(newValue.getAddress());
        cmbTitle.setValue(newValue.getTitle());
        dateDob.setValue(newValue.getDob());
        txtCity.setText(newValue.getCity());
        txtProvince.setText(newValue.getProvince());
        txtPostalCode.setText(newValue.getPostalCode());
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        Customer customer = new Customer(
                txtId.getText(),
                cmbTitle.getValue(),
                txtName.getText(),
                dateDob.getValue(),
                Double.parseDouble(txtSalary.getText()),
                txtAddress.getText(),
                txtCity.getText(),
                txtProvince.getText(),
                txtPostalCode.getText());

        if (service.addCustomer(customer)) {
            new Alert(Alert.AlertType.INFORMATION, "Customer Added Successfully").show();
            clearFields();
            loadTable();
        } else {
            new Alert(Alert.AlertType.ERROR, "Customer Not Added Successfully").show();
        }

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        if (service.deleteCustomer(txtId.getText())) {
            new Alert(Alert.AlertType.INFORMATION, "Customer Deleted Successfully").show();
            clearFields();
            loadTable();
        } else {
            new Alert(Alert.AlertType.ERROR, "Customer Not Deleted Successfully").show();
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadTable();
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        Customer customer = service.searchCustomer(txtId.getText());
        if (customer != null) {
            txtId.setText(customer.getId());
            txtName.setText(customer.getName());
            txtAddress.setText(customer.getAddress());
            cmbTitle.setValue(customer.getTitle());
            dateDob.setValue(customer.getDob());
            txtSalary.setText("" + customer.getSalary());
            txtAddress.setText(customer.getAddress());
            txtCity.setText(customer.getCity());
            txtProvince.setText(customer.getProvince());
            txtPostalCode.setText(customer.getPostalCode());
        } else {
            new Alert(Alert.AlertType.ERROR, "Customer Not Found").show();
            clearFields();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Customer customer = new Customer(
                txtId.getText(),
                cmbTitle.getValue(),
                txtName.getText(),
                dateDob.getValue(),
                Double.parseDouble(txtSalary.getText()),
                txtAddress.getText(),
                txtCity.getText(),
                txtProvince.getText(),
                txtPostalCode.getText());

        if (service.updateCustomer(customer)) {
            new Alert(Alert.AlertType.INFORMATION, "Customer Updated Successfully").show();
            clearFields();
            loadTable();
        } else {
            new Alert(Alert.AlertType.ERROR, "Customer Not Updated Successfully").show();
        }

    }
    private void loadTable(){
        tblCustomers.setItems(service.getAllCustomers());
    }

    private void clearFields(){
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
        cmbTitle.setValue(null);
        dateDob.setValue(null);
        txtAddress.setText(null);
        txtCity.setText(null);
        txtProvince.setText(null);
        txtPostalCode.setText(null);
    }
}
