package service;

import model.Log;
import repository.LogDb;

import java.util.ArrayList;

public class LogService {

    private LogDb logDb ;

    public LogService(){
        this.logDb = new LogDb();
    }

    public void logAdd(Log log){
        logDb.logAddDb(log);
    }

    public Log logFindWithId(int id){
        return logDb.logFindWithIdDb(id);
    }

    public ArrayList<Log> logShowListWithCustomer(int id){
        return logDb.logShowListWithCustomerDb(id);
    }

    public ArrayList<Log> logShowList() {
        return logDb.logShowListDb();
    }



}
