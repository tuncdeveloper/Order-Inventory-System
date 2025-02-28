package service;

import model.Customer;
import model.Order;
import model.Product;
import repository.OrderDb;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class OrderService {

    private OrderDb orderDb ;
    private static OrderService orderService;

    public OrderService(){
        this.orderDb=new OrderDb();
    }


    public void orderAdd(Order order){
        orderDb.orderAddDb(order);
    }

    public ArrayList<Order>orderShowList(){
        return orderDb.orderShowListDb();
    }

    public ArrayList<Order> orderShowListWithCustomer(int customerID){
        return orderDb.orderShowListWithCustomerDb(customerID);
    }

    public Order orderFindWithId(int id){
        return orderDb.orderFindWithIdDb(id);
    }

    public Order getByUserIdAndProductId(int customerID , int productID){
        return orderDb.getByUserIdAndProductIdDb(customerID,productID);
    }

    public void confirmById(int orderID){
            orderDb.confirmByIdDB(orderID);
    }

    public void orderDelete(int id){
        orderDb.orderDeleteDb(id);
    }

    public void orderUpdate(Order order){
        orderDb.orderUpdateDb(order);
    }

    public static OrderService getInstance() {
        if (orderService == null) {
            synchronized (OrderService.class) {
                if (orderService == null) {
                    orderService = new OrderService();
                }
            }
        }
        return orderService;
    }



    public void add(Customer customer, Product product, Integer quantity) {
        Order order = new Order();
        order.setCustomerId(customer.getCustomerId());
        order.setProductId(product.getProductID());
        order.setQuantity(quantity);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
        order.setOrderStatus("Beklemede");
        orderAdd(order);
    }


}
