package model;

import service.ConcurrentConfirmManager;
import service.LogService;
import service.OrderService;
import service.ProductService;

import java.time.Instant;


public class ConfirmProcess implements Runnable, Comparable<ConfirmProcess> {

    private Customer customer;

    private Product product;

    private Double priority;

    private Long timestamp;

    private Long confirmingTime;

    private boolean isActive;

    private OrderService orderService;

    private LogService logService ;

    private ConfirmProcess(Customer customer, Product product) {
        this.customer = customer;
        this.product = product;
        timestamp = Instant.now().toEpochMilli();
        confirmingTime = Instant.now().toEpochMilli();
        orderService = OrderService.getInstance();
        logService = new LogService() ;
        priority = 0.0 ;
    }

    @Override
    public void run() {
        Order order = orderService.getByUserIdAndProductId(customer.getCustomerId(), product.getProductID());
        Log log = new Log();
        if (order.getQuantity() > new ProductService().productFindWithIdDb(order.getProductId()).getStock()) {
            log.setCustomerId(customer.getCustomerId());
            log.setOrderId(order.getOrderId());
            log.setLogDate(new java.sql.Date(new java.util.Date().getTime()));
            log.setLogType("Hata");
            log.setLogDetails("Müşteri " + customer.getName() + " ürünü sipariş etmeye çalıştı "
                    + product.getName() + " (Miktar: " + order.getQuantity()
                    + "), ancak stok yetersizdi.");
            logService.logAdd(log);
        }
        else {
            double totalCost = order.getQuantity() * product.getPrice();
            orderService.confirmById(order.getOrderId());
            log.setCustomerId(customer.getCustomerId());
            log.setOrderId(order.getOrderId());
            log.setLogDate(new java.sql.Date(new java.util.Date().getTime()));
            log.setLogType("Onaylandı");
            log.setLogDetails("Müşteri " + customer.getName() + " ürün siparişi onaylandı "
                    + product.getName() + " (Miktar: " + order.getQuantity()
                    + ", Toplam Maliyet: " + totalCost + ")");
        }
        logService.logAdd(log);
    }

    @Override
    public int compareTo(ConfirmProcess o) {
        int priorityComparison = Double.compare(o.priority, this.priority); // Büyük öncelikli önce
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return Long.compare(this.timestamp, o.timestamp); // Zaman damgası erken olan önce
    }


    public void setPriority(Double priority) {
        this.priority = priority;
    }

    public void setConfirmingTime(Long confirmingTime) {
        this.confirmingTime = confirmingTime;
    }

    // her siparis verildigi zaman bu metotta calısacak
    public static void createConfirmProcess(Customer customer, Product product) {
       ConfirmProcess confirmProcess = new ConfirmProcess(customer, product);
     ConcurrentConfirmManager.getInstance().getQueue().add(confirmProcess);
    }

    public Customer getCustomer() {
        return customer;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getConfirmingTime() {
        return confirmingTime;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

}