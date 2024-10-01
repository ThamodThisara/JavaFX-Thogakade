package controller.item;

import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import model.Item;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemFormController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));



        tblItems.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal != null) {
                addValueToText((Item) newVal);
            }
        });

        loadTabel();
    }

    private void addValueToText(Item newVal) {
        txtItemCode.setText(newVal.getItemCode());
        txtDescription.setText(newVal.getDescription());
        txtPackSize.setText(newVal.getPackSize());
        txtUnitPrice.setText(newVal.getUnitPrice().toString());
        txtQty.setText(newVal.getQtyOnHand().toString());
    }

    public TableView tblItems;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public JFXTextField txtPackSize;
    public JFXTextField txtItemCode;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtDescription;
    public JFXTextField txtQty;

    ItemService service = ItemController.getInstance();

    public void btnAddOnAction(ActionEvent actionEvent) {
        Item item = new Item(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQty.getText()));

        boolean isAdded = service.addItem(item);
        if (isAdded){
            new Alert(Alert.AlertType.INFORMATION, "Item added successfully").show();
            clearFileds();
            loadTabel();
        } else {
            new Alert(Alert.AlertType.ERROR, "Item could not be added").show();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        Item item = new Item(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQty.getText()));

        boolean isUpdated = service.updateItem(item);
        if (isUpdated){
            new Alert(Alert.AlertType.INFORMATION, "Item updated successfully").show();
            clearFileds();
            loadTabel();
        } else {
            new Alert(Alert.AlertType.ERROR, "Item could not be updated").show();
            clearFileds();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        if (service.deleteItem(txtItemCode.getText())) {
            new Alert(Alert.AlertType.INFORMATION, "Customer Deleted Successfully").show();
            clearFileds();
            loadTabel();
        } else {
            new Alert(Alert.AlertType.ERROR, "Customer Not Deleted Successfully").show();
            clearFileds();
        }
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        Item item = service.searchItem(txtItemCode.getText());
        if (item != null) {
            txtDescription.setText(item.getDescription());
            txtPackSize.setText(item.getPackSize());
            txtUnitPrice.setText(item.getUnitPrice().toString());
            txtQty.setText(item.getQtyOnHand().toString());

        }  else {
            new Alert(Alert.AlertType.ERROR, "Customer Not Found").show();
        }

    }

    private void loadTabel(){
        ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = psTm.executeQuery();
            while (resultSet.next()){
                  Item item = new Item(
                        resultSet.getString("ItemCode"),
                        resultSet.getString("Description"),
                        resultSet.getString("PackSize"),
                        resultSet.getDouble("UnitPrice"),
                        resultSet.getInt("QtyOnHand"));

                itemObservableList.add(item);
                //System.out.println(item);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        tblItems.setItems(itemObservableList);
    }

    private void clearFileds(){
        txtItemCode.clear();
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQty.clear();
    }
}
