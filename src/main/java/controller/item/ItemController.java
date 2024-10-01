package controller.item;

import controller.customer.CustomerController;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Item;
import model.OrderDetail;
import util.CrudUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemController implements ItemService{
    private static ItemController instance;
    private ItemController () {}
    public static ItemController getInstance() {
        return (instance == null) ? instance = new ItemController() : instance;
    }

    @Override
    public boolean addItem(Item item) {
        try {
            String SQL = "INSERT INTO Item values(?,?,?,?,?)";

//            Connection connection = DBConnection.getInstance().getConnection();
//            PreparedStatement psTm = connection.prepareStatement(SQL);
//            psTm.setObject(1,item.getItemCode());
//            psTm.setObject(2,item.getDescription());
//            psTm.setObject(3,item.getPackSize());
//            psTm.setObject(4,item.getUnitPrice());
//            psTm.setObject(5,item.getQtyOnHand());
//
//            return psTm.executeUpdate() > 0;

            Object execute = CrudUtil.execute(SQL,
                    item.getItemCode(),
                    item.getDescription(),
                    item.getPackSize(),
                    item.getUnitPrice(),
                    item.getQtyOnHand()
            );
             return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Item> getAllItem() {
        ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM Item";
        try {
//            Connection connection = DBConnection.getInstance().getConnection();
//            PreparedStatement psTm = connection.prepareStatement(SQL);
//            ResultSet resultSet = psTm.executeQuery();

            ResultSet resultSet = CrudUtil.execute(SQL);

            while (resultSet.next()) {
                itemObservableList.add(new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                ));
            }
            return itemObservableList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateItem(Item item) {
        try {
            String SQL = "UPDATE Item SET Description=? , PackSize=?, UnitPrice=?, QtyOnHand=? WHERE ItemCode=?";
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(SQL);
            psTm.setObject(1,item.getDescription());
            psTm.setObject(2,item.getPackSize());
            psTm.setObject(3,item.getUnitPrice());
            psTm.setObject(4,item.getQtyOnHand());
            psTm.setObject(5,item.getItemCode());

            return psTm.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteItem(String id) {
        try {
            return DBConnection.getInstance().getConnection().createStatement().executeUpdate("DELETE FROM Item WHERE ItemCode = '" + id + "'") > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Item searchItem(String id) {
        String SQL = "SELECT * FROM Item WHERE ItemCode='" + id + "'";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement psTm = connection.prepareStatement(SQL);
            ResultSet resultSet = psTm.executeQuery();
            while(resultSet.next()) {
                return new Item (
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean updateStock(List<OrderDetail> orderDetails) {
        for (OrderDetail orderDetail : orderDetails) {
            boolean isUpdateStock = updateStock(orderDetail);
            if (!isUpdateStock) {
                return false;
            }
        }
        return true;
    }

    public boolean updateStock(OrderDetail orderDetail) {
        String SQL = "UPDATE Item SET QtyOnHand=QtyOnHand-? WHERE ItemCode=?";
        try {
            return CrudUtil.execute(SQL,
                    orderDetail.getQty(),
                    orderDetail.getItemCode());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
