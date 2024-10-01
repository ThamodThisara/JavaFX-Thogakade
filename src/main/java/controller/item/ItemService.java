package controller.item;

import javafx.collections.ObservableList;
import model.Customer;
import model.Item;
import model.OrderDetail;

import java.util.List;

public interface ItemService {
    boolean addItem(Item item);
    ObservableList<Item> getAllItem();
    boolean updateItem(Item item);
    boolean deleteItem(String id);
    Item searchItem(String id);
    boolean updateStock(List<OrderDetail> orderDetails);
}
