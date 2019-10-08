package auctionhouse;

import java.util.HashSet;

public class Buyer {
    
    private String name;        
    private String address;     
    private String bankAccount; 
    private String bankAuthCode;
    private HashSet<Integer> lotsInterestedIn = new HashSet<Integer>();
    
    public Buyer(String name, String address, String bankAccount, String bankAuthCode) {
        super();
        this.name = name;
        this.address = address;
        this.bankAccount = bankAccount;
        this.bankAuthCode = bankAuthCode;
        lotsInterestedIn = new HashSet<Integer>();
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

    public HashSet<Integer> getLotsInterestedIn() {
        return lotsInterestedIn;
    }

    public void setLotsInterestedIn(HashSet<Integer> lotsInterestedIn) {
        this.lotsInterestedIn = lotsInterestedIn;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAuthCode() {
        return bankAuthCode;
    }

    public void setBankAuthCode(String bankAuthCode) {
        this.bankAuthCode = bankAuthCode;
    }
    

    
}
