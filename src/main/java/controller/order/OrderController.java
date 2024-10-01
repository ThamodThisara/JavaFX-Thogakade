package controller.order;

import controller.item.ItemController;
import db.DBConnection;
import javafx.scene.control.Alert;
import model.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderController {
    public boolean placeOrder (Order order) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            String SQL = "INSERT INTO orders VALUES (?,?,?)";
            PreparedStatement psTm = connection.prepareStatement(SQL);
            psTm.setObject(1,order.getOrderID());
            psTm.setObject(2,order.getOrderDate());
            psTm.setObject(3,order.getCustomerID());

            boolean isOrderAdded = psTm.executeUpdate() > 0;
            if (isOrderAdded) {
                boolean isOrderDetailAdd = new OrderDetailController().addOrderDetail(order.getOrderDetails());

                if (isOrderDetailAdd) {
                    boolean isUpdateStock = ItemController.getInstance().updateStock(order.getOrderDetails());

                    if (isUpdateStock) {
                        connection.commit();
                        new Alert(Alert.AlertType.INFORMATION, "Order placed successfully").show();
                        return true;
                    }
                }
            }
            new Alert(Alert.AlertType.ERROR, "Order NOT placed successfully").show();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
