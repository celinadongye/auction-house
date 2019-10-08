package auctionhouse;

public class Seller {

    private String name;
    private String address;
    private String bankAccount;
    
    
    public Seller(String name, String address, String bankAccount) {
        super();
        this.name = name;
        this.address = address;
        this.bankAccount = bankAccount;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getBankAccount() {
        return bankAccount;
    }


    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    
    
    
}
