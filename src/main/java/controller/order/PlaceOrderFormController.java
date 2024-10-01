package controller.order;

import controller.customer.CustomerController;
import controller.item.ItemController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.*;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class PlaceOrderFormController implements Initializable {


    public TextField txtOrderId;
    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colItemCode;

    @FXML
    private TableColumn<?, ?> colQty;

    @FXML
    private TableColumn<?, ?> colTotal;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    public ComboBox<String> cmbCustomerId;

    @FXML
    public ComboBox<String> cmbItemCode;

    @FXML
    private Label lblNetTotal;

    @FXML
    private Label lblOrderDate;

    @FXML
    private Label lblOrderID;

    @FXML
    private Label lblOrderTime;

    @FXML
    private TableView<CartTm> tblCart;

    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtItemDescription;

    @FXML
    private TextField txtItemStock;

    @FXML
    private TextField txtUnitPrice;

    @FXML
    private TextField txtQty;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAndTime();
        loadCustomerIds();
        loadItemCodes();

        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observableValue, oldId, newId) -> {
            //System.out.println(newId);
            if (newId != null) {
                searchCustomer(newId);
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItemCode, newItemCode) -> {
            //System.out.println(newItemCode);
            if (newItemCode != null) {
                searchItemCode(newItemCode);
            }
        });
    }

    ObservableList<CartTm> cartTms = FXCollections.observableArrayList();
    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        String itemCode = cmbItemCode.getValue();
        String itemDesc = txtItemDescription.getText();
        Integer qty = Integer.valueOf(txtQty.getText());
        Double unitPrice = Double.valueOf(txtUnitPrice.getText());
        Double total = unitPrice * qty;

        Integer itemStock = Integer.valueOf(txtItemStock.getText());

        if (itemStock < qty) {
            new Alert(Alert.AlertType.WARNING, "Not enough stock to add to cart").show();
        } else {
            cartTms.add(new CartTm(itemCode, itemDesc, qty, unitPrice, total));
            tblCart.setItems(cartTms);
            calTotal();
        }
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) {
        String orderId = txtOrderId.getText();
        LocalDate date = LocalDate.parse(lblOrderDate.getText());
        String CustomerId = cmbCustomerId.getValue();
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        cartTms.forEach(obj -> {
            orderDetails.add(new OrderDetail(
                    orderId,
                    obj.getItemCode(),
                    obj.getQty(),
                    0.0));
        });

        Order order = new Order(orderId, date, CustomerId, orderDetails);
        try {
            new OrderController().placeOrder(order);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(order);


    }

    private void searchCustomer(String customerId){
        Customer customer = CustomerController.getInstance().searchCustomer(customerId);
        txtCustomerName.setText(customer.getName());
        txtCustomerAddress.setText(customer.getAddress());
    }

    private void searchItemCode(String itemCode) {
        Item item = ItemController.getInstance().searchItem(itemCode);
        txtItemDescription.setText(item.getDescription());
        txtItemStock.setText(item.getQtyOnHand().toString());
        txtUnitPrice.setText(item.getUnitPrice().toString());
    }

    private void loadDateAndTime (){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        lblOrderDate.setText(simpleDateFormat.format(date));

    //-------------------------------------------

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, actionEvent -> {
            LocalTime now = LocalTime.now();
            lblOrderTime.setText(now.getHour() + ":" + now.getMinute() + ":" + now.getSecond());
        }),
                new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void loadCustomerIds(){
        ObservableList<String> cusIdsObservableList = FXCollections.observableArrayList();
        List<String> allCustomerIds = CustomerController.getInstance().getAllCustomerIds();
        allCustomerIds.forEach(obj -> {
            cusIdsObservableList.add(obj);
        });
        cmbCustomerId.setItems(cusIdsObservableList);
    }

    private void loadItemCodes(){
        ObservableList<String> itemCodes = FXCollections.observableArrayList();
        ObservableList<Item> allItem = ItemController.getInstance().getAllItem();
        allItem.forEach(obj -> {
            itemCodes.add(obj.getItemCode());
        });
        cmbItemCode.setItems(itemCodes);

    }

    private void calTotal(){
        Double netTotal = 0.0;
        for (CartTm cartTm : cartTms) {
            netTotal += cartTm.getTotal();
        }
        lblNetTotal.setText(netTotal.toString());
    }


}
