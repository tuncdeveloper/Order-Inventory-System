package service;

import model.Admin;
import repository.AdminDb;
import repository.LogDb;

public class AdminService {

    private AdminDb adminDb ;

    public AdminService(){
        this.adminDb = new AdminDb();
    }

    public Admin adminShow(Admin admin){
       return adminDb.adminShowDb(admin);
    }

}
