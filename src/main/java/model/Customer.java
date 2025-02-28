package model;

public class Customer {
    private int customerId;
    private String username;
    private String password;
    private String name;
    private double budget;
    private String type; // "Premium" veya "Standard"
    private double totalSpent;
    private double waitingTime;
    private double priorityScore;

    public Customer(){

    }
    public void calculatePriorityScore() {
        double basePriorityScore = type.equals("Premium") ? 15 : 10;
        priorityScore = basePriorityScore + (waitingTime * 0.5);
    }


    public Customer(int customerId, String username, String password, String name, double budget,
                    String type, double totalSpent, double waitingTime, double priorityScore) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.budget = budget;
        this.type = type;
        this.totalSpent = totalSpent;
        this.waitingTime = waitingTime;
        this.priorityScore = priorityScore;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", totalSpent=" + totalSpent +
                ", waitingTime=" + waitingTime +
                ", priorityScore=" + priorityScore +
                '}';
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }
}
