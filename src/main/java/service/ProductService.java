package service;

import model.Product;
import repository.ProductDb;

import java.util.ArrayList;

public class ProductService {

   private ProductDb productDb ;

    public ProductService() {
        this.productDb = new ProductDb();
    }

    public void productAdd(Product product){
        productDb.productAddDb(product);
    }

    public void productDelete(int prpductID){
        productDb.productDeleteDb(prpductID);
    }


    public void productUpdate(Product product){
        productDb.productUpdateDb(product);
    }

    public ArrayList<Product> productShowList(){
        return productDb.productShowListDb();
    }


    public Product productFind(String name){
        return productDb.productFindDb(name);
    }
    public Product productFindWithIdDb(int id){
        return productDb.productFindWithIdDb(id);
    }

    public boolean productIsAdd(Product product){

        boolean flag= false;

        ArrayList<Product> productArrayList = productDb.productShowListDb();

        for (Product wantedProduct : productArrayList) {

            if (product.getName().equals(wantedProduct.getName())) {

                flag = true;
                break;
            }
        }

        return flag;

    }


    public Double calculationOrderedProduct(int quantity ,double price){
        return quantity*price;
    }

}
