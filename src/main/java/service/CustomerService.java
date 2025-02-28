package service;

import com.github.javafaker.Faker;
import model.Customer;
import repository.CustomerDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerService {

    private CustomerDb customerDb;
    private Faker faker;
    private Random random;

    public CustomerService() {
        this.customerDb = new CustomerDb();
        this.faker = new Faker();
        this.random = new Random();
    }

    public Customer customerLoginDb(Customer customer) {
        return customerDb.customerLogin(customer);
    }

    public ArrayList<Customer> customerShowList() {
        return customerDb.customerShowListDb();
    }

    public Customer customerFindWithId(int id) {
        return customerDb.customerFindWithIdDb(id);
    }

    public void customerUpdate(Customer customer) {
        customerDb.customerUpdateDb(customer);
    }

    // Rastgele müşteriler oluştur
    public List<Customer> createRandomCustomers() {
        List<Customer> customers = new ArrayList<>();

        // Rastgele müşteri sayısı 5 ile 10 arasında
        int customerCount = random.nextInt(6) + 5;

        // En az 2 Premium müşteri oluştur
        for (int i = 0; i < 2; i++) {
            customers.add(createCustomer("Premium"));
        }

        // Diğer müşterileri rastgele türde oluştur
        for (int i = 2; i < customerCount; i++) {
            String type = random.nextBoolean() ? "Premium" : "Standard";
            customers.add(createCustomer(type));
        }

        // Veritabanına ekle
        for (Customer customer : customers) {
            customerDb.customerInsertDb(customer);
        }

        return customers;
    }

    // Belirli bir müşteri türü oluştur
    private Customer createCustomer(String type) {
        Customer customer = new Customer();

        // Rastgele username ve password
        customer.setUsername(faker.name().username());
        customer.setPassword(faker.internet().password(8, 12));

        // Rastgele isim ve soyisim
        customer.setName(faker.name().fullName());

        // Rastgele bütçe
        double budget = 500 + (3000 - 500) * random.nextDouble();
        customer.setBudget(Math.round(budget * 100.0) / 100.0); // İki ondalık basamak

        // Tür
        customer.setType(type);

        return customer;
    }
}
