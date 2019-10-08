/**
 * 
 */
package auctionhouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * @author pbj
 *
 */
public class AuctionHouseImp implements AuctionHouse {

    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    private Parameters parameters = new Parameters(0, 0, null, null, null, null, null);
    private TreeMap<Integer, CatalogueEntry> onlineCatalogue = new TreeMap<Integer, CatalogueEntry>();
    private HashMap<String, Buyer> buyers = new HashMap<String, Buyer>();
    private HashMap<String, Seller> sellers = new HashMap<String, Seller>();
    private HashMap<Integer, Auction> auctions = new HashMap<Integer, Auction>(); //lots associated with auctions 
    private HashMap<Integer, Seller> lots = new HashMap<Integer, Seller>(); // lots to be sold associated with the seller
    private HashMap<Integer, Money> reservePrices = new HashMap<Integer, Money>(); //lot associated with its reserve price
   
    private String startBanner(String messageName) {
        return  LS 
          + "-------------------------------------------------------------" + LS
          + "MESSAGE IN: " + messageName + LS
          + "-------------------------------------------------------------";
    }
   
    public AuctionHouseImp(Parameters parameters) {
        this.parameters = parameters;
    }
    
    

    public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public TreeMap<Integer, CatalogueEntry> getOnlineCatalogue() {
		return onlineCatalogue;
	}

	public void setOnlineCatalogue(TreeMap<Integer, CatalogueEntry> onlineCatalogue) {
		this.onlineCatalogue = onlineCatalogue;
	}

	public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        Buyer buyer = new Buyer(name, address, bankAccount, bankAuthCode);
        if (buyers.containsKey(name)) {
            return Status.error("Buyer with this name is already registered");
        }
        buyers.put(buyer.getName(), buyer);
        logger.fine("Buyer " + name + " registered succesfully");
        
        return Status.OK();
    }

    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
        logger.fine(startBanner("registerSeller " + name));
        Seller seller = new Seller(name, address, bankAccount);
        if (sellers.containsKey(name)) {
            return Status.error("Seller with this name is already registered");
        }
        sellers.put(seller.getName(), seller);
        logger.fine("Seller " + name + " regisered succesfully");
        return Status.OK();      
    }

    public Status addLot(
            String sellerName,
            int number,
            String description,
            Money reservePrice) {
        
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        
        if (!(sellers.containsKey(sellerName))) {
            return Status.error("Unregistered seller, cannot add lot");
        }
        if (onlineCatalogue.containsKey(number)) {
        	return Status.error("Lot with this number already exists");
        }
        
        // Creates a new lot
        Lot newLot = new Lot(sellerName, LotStatus.UNSOLD, number, reservePrice, description); 
        // Creates a new onlineCatalogueEntry for the lot just created
        CatalogueEntry newEntry = new CatalogueEntry(newLot.getId(), newLot.getLotDescription(), newLot.getLotStatus()); 
        onlineCatalogue.put(newEntry.lotNumber, newEntry);
        
        reservePrices.put(number, reservePrice);
        
        for (String name : sellers.keySet()) {
            if (name.equals(sellerName)) {
                lots.put(newLot.getId(), sellers.get(name));
            }
        }
        logger.fine("Lot " + number + " added by " + sellerName);
        
        return Status.OK();    
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        
        List<CatalogueEntry> catalogue = new ArrayList<CatalogueEntry>();
        
        Set<Integer> keys = new TreeSet<Integer>();
        keys = onlineCatalogue.keySet();
        
        for (int k : keys) {
            catalogue.add(onlineCatalogue.get(k));
        }
   
        
        logger.fine("Catalogue: " + catalogue.toString());
        return catalogue;
    }

    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        
        if (!(buyers.containsKey(buyerName))) {
            return Status.error("Unregistered buyer, cannot note interest in lot");
        }
        
        if (!(onlineCatalogue.containsKey(lotNumber))) {
            return Status.error("Lot is not in the online catalogue");
        }
        
        Buyer buyer = buyers.get(buyerName);
        if (buyer.getLotsInterestedIn().contains(lotNumber)) {
            return Status.error("Already interested in this lot");
        }
        
        CatalogueEntry entry = new CatalogueEntry(lotNumber, null, null);
        entry = onlineCatalogue.get(lotNumber);
        
        if (entry.status == LotStatus.SOLD || entry.status == LotStatus.SOLD_PENDING_PAYMENT) {
            return Status.error("Cannot note interest in this lot");
        }
        
        buyer.getLotsInterestedIn().add(lotNumber);
        
        logger.fine("Buyer " + buyerName + " noted Interest in lot " + lotNumber);
        
        return Status.OK();   
    }
    
    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        if (!(onlineCatalogue.containsKey(lotNumber))) {
            return Status.error("Lot is not in the online catalogue");
        }
        
       
        for (Auction auction : auctions.values()) {
        	if (auction.getAuctioneerName().equals(auctioneerName)) {
        		return Status.error("Auctioneer is already running different auction");
        	}
        }
        
        
        
        if (auctions.containsKey(lotNumber)) {
            return Status.error("Lot is already being sold in another auction");
        }
        
        CatalogueEntry entry = onlineCatalogue.get(lotNumber);
        if (!(entry.status.equals(LotStatus.UNSOLD))) {
            return Status.error("Lot cannot be sold");
        }
        
        Auction auction = new Auction(auctioneerName, auctioneerAddress, lotNumber);
        
        for (Buyer buyer : buyers.values()) {
            if ((buyer).getLotsInterestedIn().contains(lotNumber)) {
                auction.getBuyersInterestedInLot().add(buyer);
            }
        }
        for (int number : lots.keySet()) {
            if (number == lotNumber) {
                auction.setLotSeller(lots.get(number));
            }
        }
        
        entry.setStatus(LotStatus.IN_AUCTION);
            
        auctions.put(lotNumber, auction);
        
        String sellerAddress = auction.getLotSeller().getAddress();
        parameters.messagingService.auctionOpened(sellerAddress, lotNumber);
        
        for (Buyer buyer : auction.getBuyersInterestedInLot()) {
            String buyerAddress = buyer.getAddress();
            parameters.messagingService.auctionOpened(buyerAddress, lotNumber);
        }
        
        logger.fine(auctioneerName + " opened auction of lot " + lotNumber);
        
        return Status.OK();
    }
    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));
        
        
        Auction auction = auctions.get(lotNumber);
        
        if (!(buyers.containsKey(buyerName))) {
            return Status.error("Unregistered buyer, cannot note interest in lot");
        }
        
        if (!(onlineCatalogue.containsKey(lotNumber))) {
            return Status.error("Lot is not in the online catalogue");
        }
        
        
        
        CatalogueEntry entry = onlineCatalogue.get(lotNumber);
        if (!(entry.status.equals(LotStatus.IN_AUCTION))) {
            return Status.error("Lot is not being auctioned");
        }
        
        
        if (!(auction.getBuyersInterestedInLot().contains(buyers.get(buyerName)))) {
            return Status.error("Buyer is not interested in the lot being auctioned, cannot make bids");
        }
        
        Money currenthighestBid = auction.getCurrentHighestBid();
        Money increment = parameters.increment;
        Money difference = bid.subtract(currenthighestBid);
        
        if (difference.lessEqual(increment) && !(currenthighestBid.equals(new Money("0")))) {
        	return Status.error("Bid too low");
        }
        
        
        auction.setCurrentHighestBid(bid);
        for (Buyer bidder : auction.getBuyersInterestedInLot()) {
            if (bidder.getName().equals(buyerName)) {
                auction.setCurrentHighestBidder(bidder);
            }
        }
        
        String sellerAddress = auction.getLotSeller().getAddress();
        parameters.messagingService.bidAccepted(sellerAddress, lotNumber, bid);
        
        for (Buyer buyer : auction.getBuyersInterestedInLot()) {
            if (!(buyer.getName().equals(buyerName))) {
                String buyerAddress = buyer.getAddress();
                parameters.messagingService.bidAccepted(buyerAddress, lotNumber, bid);
            }
        }
        
        String auctioneerAddress = auction.getAuctioneerAddress();
        parameters.messagingService.bidAccepted(auctioneerAddress, lotNumber, bid);
        
        logger.fine("Buyer " + buyerName + " made bid " + bid + " on lot " + lotNumber);
        
        return Status.OK();    
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
        
        if (!(onlineCatalogue.containsKey(lotNumber))) {
            return Status.error("Lot is not in the online catalogue");
        }
        
        
        CatalogueEntry entry = onlineCatalogue.get(lotNumber);
        if (!(entry.status.equals(LotStatus.IN_AUCTION))) {
            return Status.error("Lot is not being auctioned");
        }

        
        Auction auction = auctions.get(lotNumber);
        
        String correctAuctioneerName = auction.getAuctioneerName();
        
        if (!(correctAuctioneerName.equals(auctioneerName))) {
        	return Status.error(auctioneerName + " does not run this auction");
        }
        
        
        if ((auction.getCurrentHighestBid().lessEqual(reservePrices.get(lotNumber)) &&
                !(auction.getCurrentHighestBid().equals(reservePrices.get(lotNumber)))) || 
                auction.getCurrentHighestBid() == null) {
            entry.setStatus(LotStatus.UNSOLD);
            
            String sellerAddress = auction.getLotSeller().getAddress();
            parameters.messagingService.lotUnsold(sellerAddress, lotNumber);
            
            for (Buyer buyer : auction.getBuyersInterestedInLot()) {
                    String buyerAddress = buyer.getAddress();
                    parameters.messagingService.lotUnsold(buyerAddress, lotNumber);
                
            }
            logger.fine(auctioneerName + " closed auction of lot " + lotNumber + LS
                    + "Lot was not sold");
            auctions.remove(lotNumber, auction);
            Status noSale = new Status (Status.Kind.NO_SALE);
            return noSale;
        } else {
            String buyerAccount = auction.getCurrentHighestBidder().getBankAccount();
            String buyerAuthCode = auction.getCurrentHighestBidder().getBankAuthCode();
            String houseAccount = parameters.houseBankAccount;
            String houseAuthCode = parameters.houseBankAuthCode;
            String sellerAccount = auction.getLotSeller().getBankAccount();
            
            Money bidAmount = auction.getCurrentHighestBid();
            Money buyerTransferAmount = bidAmount.addPercent(parameters.buyerPremium);
            
            // transfer amount from Buyer to Auction House
            if (parameters.bankingService.transfer(buyerAccount, buyerAuthCode, houseAccount, buyerTransferAmount).kind
                    == Status.Kind.ERROR) {
                entry.setStatus(LotStatus.SOLD_PENDING_PAYMENT);
                logger.fine(auctioneerName + " closed auction of lot " + lotNumber + LS
                        + "Lot was sold but payment is still pending");
                auctions.remove(lotNumber, auction);
                Status salePendingPayment = new Status (Status.Kind.SALE_PENDING_PAYMENT);
                return salePendingPayment;
            } else {
                parameters.bankingService.transfer(buyerAccount, buyerAuthCode, houseAccount, buyerTransferAmount);
                
                Money commission = bidAmount.addPercent(parameters.commission);
                commission = commission.subtract(bidAmount);
                Money sellerTransferAmount = bidAmount.subtract(commission);
                
                if (parameters.bankingService.transfer(houseAccount, houseAuthCode, sellerAccount, sellerTransferAmount).kind
                        == Status.Kind.ERROR) {
                    entry.setStatus(LotStatus.SOLD_PENDING_PAYMENT);
                    logger.fine(auctioneerName + " closed auction of lot " + lotNumber + LS
                            + "Lot was sold but payment is still pending");
                    auctions.remove(lotNumber, auction);
                    Status salePendingPayment = new Status (Status.Kind.SALE_PENDING_PAYMENT);
                    return salePendingPayment;
                } else {
                    parameters.bankingService.transfer(houseAccount, houseAuthCode, sellerAccount, sellerTransferAmount);
                    entry.setStatus(LotStatus.SOLD);
                    
                    String sellerAddress = auction.getLotSeller().getAddress();
                    parameters.messagingService.lotSold(sellerAddress, lotNumber);
                    
                    for (Buyer buyer : auction.getBuyersInterestedInLot()) {
                            String buyerAddress = buyer.getAddress();
                            parameters.messagingService.lotSold(buyerAddress, lotNumber);
                    }
                    logger.fine(auctioneerName + " closed auction of lot " + lotNumber + LS
                            + "Lot was sold");
                    auctions.remove(lotNumber, auction);
                    Status sold = new Status (Status.Kind.SALE);
                    return sold;
                }
            
            }
        }
    }
}
