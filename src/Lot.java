package auctionhouse;

public class Lot {
    
    private String sellerName;
    private LotStatus lotStatus;
    private Integer id;
    private Money reservePrice;
    private String lotDescription;
    

    public Lot(String sellerName, LotStatus lotStatus, Integer id, Money reservePrice, String lotDescription) {
        super();
        this.sellerName = sellerName;
        this.lotStatus = lotStatus;
        this.id = id;
        this.reservePrice = reservePrice;
        this.lotDescription = lotDescription;
    }


    public String getSellerName() {
        return sellerName;
    }


    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }


    public LotStatus getLotStatus() {
        return lotStatus;
    }


    public void setLotStatus(LotStatus lotStatus) {
        this.lotStatus = lotStatus;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Money getReservePrice() {
        return reservePrice;
    }


    public void setReservePrice(Money reservePrice) {
        this.reservePrice = reservePrice;
    }


    public String getLotDescription() {
        return lotDescription;
    }


    public void setLotDescription(String lotDescription) {
        this.lotDescription = lotDescription;
    }
    
}
