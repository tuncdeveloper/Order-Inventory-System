package model;


import service.ConcurrentPurchaseManager;
import service.OrderService;

import java.time.Instant;


public class PurchaseProcess implements Runnable, Comparable<PurchaseProcess> {

    private Customer customer;

    private Product product;

    private Integer quantity;

    private Double priority;

    private Long timestamp;

    private OrderService orderService;

    private PurchaseProcess(Customer customer, Product product, Integer quantity) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.priority = customer.getPriorityScore();
        timestamp = Instant.now().toEpochMilli();
        orderService = OrderService.getInstance();
    }

    @Override
    public void run() {
        orderService.add(customer, product, quantity);
    }

    @Override
    public int compareTo(PurchaseProcess o) {
        int priorityComparison = Double.compare(this.priority, o.priority);
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return Long.compare(this.timestamp, o.timestamp);
    }

    public static void createProcess(Customer customer, Product product, Integer quantity) {
        ConcurrentPurchaseManager.getInstance().getQueue().put(new PurchaseProcess(customer, product, quantity));

    }
}