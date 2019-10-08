package auctionhouse;

import java.util.HashSet;

public class Auction {
    
    private String auctioneerName;
    private String auctioneerAddress;
    private int lotId;
    private Money currentHighestBid = new Money("0");
    private Buyer currentHighestBidder;
    private HashSet<Buyer> buyersInterestedInLot = new HashSet<Buyer>();
    private Seller lotSeller;
    
    public Auction(String auctioneerName, String auctioneerAddress, int lotId) {
        super();
        this.auctioneerName = auctioneerName;
        this.auctioneerAddress = auctioneerAddress;
        this.lotId = lotId;
        currentHighestBidder = null;
        buyersInterestedInLot = new HashSet<Buyer>();
        lotSeller = null;
    }
    

    public Seller getLotSeller() {
        return lotSeller;
    }


    public void setLotSeller(Seller lotSeller) {
        this.lotSeller = lotSeller;
    }


    public HashSet<Buyer> getBuyersInterestedInLot() {
        return buyersInterestedInLot;
    }


    public void setBuyersInterestedInLot(HashSet<Buyer> buyersInterestedInLot) {
        this.buyersInterestedInLot = buyersInterestedInLot;
    }


    public String getAuctioneerName() {
        return auctioneerName;
    }

    public void setAuctioneerName(String auctioneerName) {
        this.auctioneerName = auctioneerName;
    }

    public String getAuctioneerAddress() {
        return auctioneerAddress;
    }

    public void setAuctioneerAddress(String auctioneerAddress) {
        this.auctioneerAddress = auctioneerAddress;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public Money getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(Money currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public Buyer getCurrentHighestBidder() {
        return currentHighestBidder;
    }

    public void setCurrentHighestBidder(Buyer currentHighestBidder) {
        this.currentHighestBidder = currentHighestBidder;
    }
    
    
    
    
}
