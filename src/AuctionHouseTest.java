/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class AuctionHouseTest {

    private static final double BUYER_PREMIUM = 10.0;
    private static final double COMMISSION = 15.0;
    private static final Money INCREMENT = new Money("10.00");
    private static final String HOUSE_ACCOUNT = "AH A/C";
    private static final String HOUSE_AUTH_CODE = "AH-auth";

    private AuctionHouseImp house;
    private MockMessagingService messagingService;
    private MockBankingService bankingService;

    /*
     * Utility methods to help shorten test text.
     */
    private static void assertOK(Status status) { 
        assertEquals(Status.Kind.OK, status.kind);
    }
    private static void assertError(Status status) { 
        assertEquals(Status.Kind.ERROR, status.kind);
    }
    private static void assertSale(Status status) { 
        assertEquals(Status.Kind.SALE, status.kind);
    }
    private static void assertNoSale(Status status) {
    	assertEquals(Status.Kind.NO_SALE, status.kind);
    }
    private static void assertSalePendingPayment(Status status) {
    	assertEquals(Status.Kind.SALE_PENDING_PAYMENT, status.kind);
    }
    
    /*
     * Logging functionality
     */

    // Convenience field.  Saves on getLogger() calls when logger object needed.
    private static Logger logger;

    // Update this field to limit logging.
    public static Level loggingLevel = Level.ALL;

    private static final String LS = System.lineSeparator();

    @BeforeClass
    public static void setupLogger() {

        logger = Logger.getLogger("auctionhouse"); 
        logger.setLevel(loggingLevel);

        // Ensure the root handler passes on all messages at loggingLevel and above (i.e. more severe)
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(loggingLevel);
    }

    private String makeBanner(String testCaseName) {
        return  LS 
                + "#############################################################" + LS
                + "TESTCASE: " + testCaseName + LS
                + "#############################################################";
    }

    @Before
    public void setup() {
        messagingService = new MockMessagingService();
        bankingService = new MockBankingService();
        house = new AuctionHouseImp(
                    new Parameters(
                        BUYER_PREMIUM,
                        COMMISSION,
                        INCREMENT,
                        HOUSE_ACCOUNT,
                        HOUSE_AUTH_CODE,
                        messagingService,
                        bankingService));

    }
    /*
     * Setup story running through all the test cases.
     * 
     * Story end point is made controllable so that tests can check 
     * story prefixes and branch off in different ways. 
     */
    private void runStory(int endPoint) {
        assertOK(house.registerSeller("SellerY", "@SellerY", "SY A/C"));       
        assertOK(house.registerSeller("SellerZ", "@SellerZ", "SZ A/C")); 
        if (endPoint == 1) return;
        
        assertOK(house.addLot("SellerY", 2, "Painting", new Money("200.00")));
        assertOK(house.addLot("SellerY", 1, "Bicycle", new Money("80.00")));
        assertOK(house.addLot("SellerZ", 5, "Table", new Money("100.00")));
        if (endPoint == 2) return;
        
        assertOK(house.registerBuyer("BuyerA", "@BuyerA", "BA A/C", "BA-auth"));       
        assertOK(house.registerBuyer("BuyerB", "@BuyerB", "BB A/C", "BB-auth"));
        assertOK(house.registerBuyer("BuyerC", "@BuyerC", "BC A/C", "BC-auth"));
        if (endPoint == 3) return;
        
        assertOK(house.noteInterest("BuyerA", 1));
        assertOK(house.noteInterest("BuyerA", 5));
        assertOK(house.noteInterest("BuyerB", 1));
        assertOK(house.noteInterest("BuyerB", 2));
        if (endPoint == 4) return;
        
        assertOK(house.openAuction("Auctioneer1", "@Auctioneer1", 1));

        messagingService.expectAuctionOpened("@BuyerA", 1);
        messagingService.expectAuctionOpened("@BuyerB", 1);
        messagingService.expectAuctionOpened("@SellerY", 1);
        messagingService.verify(); 
        if (endPoint == 5) return;
        
        Money m70 = new Money("70.00");
        assertOK(house.makeBid("BuyerA", 1, m70));
        
        messagingService.expectBidReceived("@BuyerB", 1, m70);
        messagingService.expectBidReceived("@Auctioneer1", 1, m70);
        messagingService.expectBidReceived("@SellerY", 1, m70);
        messagingService.verify();
        if (endPoint == 6) return;
        
        Money m100 = new Money("100.00");
        assertOK(house.makeBid("BuyerB", 1, m100));

        messagingService.expectBidReceived("@BuyerA", 1, m100);
        messagingService.expectBidReceived("@Auctioneer1", 1, m100);
        messagingService.expectBidReceived("@SellerY", 1, m100);
        messagingService.verify();
        if (endPoint == 7) return;
        
        assertSale(house.closeAuction("Auctioneer1",  1));
        messagingService.expectLotSold("@BuyerA", 1);
        messagingService.expectLotSold("@BuyerB", 1);
        messagingService.expectLotSold("@SellerY", 1);
        messagingService.verify();       

        bankingService.expectTransfer("BB A/C",  "BB-auth",  "AH A/C", new Money("110.00"));
        bankingService.expectTransfer("AH A/C",  "AH-auth",  "SY A/C", new Money("85.00"));
        bankingService.verify();
        
    }
    
    @Test
    public void testEmptyCatalogue() {
        logger.info(makeBanner("emptyLotStore"));
        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();
        
        assertEquals(expectedCatalogue, actualCatalogue);

    }

    @Test
    public void testRegisterSeller() {
        logger.info(makeBanner("testRegisterSeller"));
        runStory(1);
    }

    @Test
    public void testRegisterSellerDuplicateNames() {
        logger.info(makeBanner("testRegisterSellerDuplicateNames"));
        runStory(1);     
        assertError(house.registerSeller("SellerY", "@SellerZ", "SZ A/C"));       
    }
    
   
    @Test
    public void testUnregisteredSeller() {
        logger.info(makeBanner("testUnregisteredSeller"));
        assertError(house.addLot("SellerW", 2, "Bicyclez", new Money("90.00")));
    }
   

    @Test
    public void testAddLot() {
        logger.info(makeBanner("testAddLot"));
        runStory(2);
    }
    
    @Test
    public void testViewCatalogue() {
        logger.info(makeBanner("testViewCatalogue"));
        runStory(2);
        
        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        expectedCatalogue.add(new CatalogueEntry(1, "Bicycle", LotStatus.UNSOLD)); 
        expectedCatalogue.add(new CatalogueEntry(2, "Painting", LotStatus.UNSOLD));
        expectedCatalogue.add(new CatalogueEntry(5, "Table", LotStatus.UNSOLD));

        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogue, actualCatalogue);
    }

    @Test
    public void testRegisterBuyer() {
        logger.info(makeBanner("testRegisterBuyer"));
        runStory(3);       
    }

    @Test
    public void testNoteInterest() {
        logger.info(makeBanner("testNoteInterest"));
        runStory(4);
    }
      
    @Test
    public void testOpenAuction() {
        logger.info(makeBanner("testOpenAuction"));
        runStory(5);       
    }
      
    @Test
    public void testMakeBid() {
        logger.info(makeBanner("testMakeBid"));
        runStory(7);
    }
  
    @Test
    public void testCloseAuctionWithSale() {
        logger.info(makeBanner("testCloseAuctionWithSale"));
        runStory(8);
    }
    
    private void runFullStory(int stop) {
    	assertOK(house.registerSeller("SellerY", "@SellerY", "SY A/C"));       
        assertOK(house.registerSeller("SellerZ", "@SellerZ", "SZ A/C"));
        assertError(house.registerSeller("SellerZ", "@SellerZ", "SZ A/C"));// registering seller with the same name
        if (stop == 0) return; 
        
        assertOK(house.addLot("SellerY", 2, "Painting", new Money("280.00")));
        assertOK(house.addLot("SellerY", 1, "Bicycle", new Money("75.00")));
        assertOK(house.addLot("SellerZ", 5, "Table", new Money("150.00")));
        if (stop == 1) return; 
        
        assertOK(house.registerBuyer("BuyerA", "@BuyerA", "BA A/C", "BA-auth"));       
        assertOK(house.registerBuyer("BuyerB", "@BuyerB", "BB A/C", "BB-auth"));
        assertOK(house.registerBuyer("BuyerC", "@BuyerC", "BC A/C", "BC-auth"));
        if (stop == 2) return;
        
        assertOK(house.noteInterest("BuyerA", 1));
        assertOK(house.noteInterest("BuyerA", 5));
        assertOK(house.noteInterest("BuyerB", 1));
        assertOK(house.noteInterest("BuyerB", 2));
        if (stop == 3) return;
        
        assertOK(house.openAuction("Auctioneer1", "@Auctioneer1", 1));

        messagingService.expectAuctionOpened("@BuyerA", 1);
        messagingService.expectAuctionOpened("@BuyerB", 1);
        messagingService.expectAuctionOpened("@SellerY", 1);
        messagingService.verify(); 
        if (stop == 4) return;
        
        Money m70 = new Money("70.00");
        assertOK(house.makeBid("BuyerA", 1, m70));
        
        messagingService.expectBidReceived("@BuyerB", 1, m70);
        messagingService.expectBidReceived("@Auctioneer1", 1, m70);
        messagingService.expectBidReceived("@SellerY", 1, m70);
        messagingService.verify();
        if (stop == 5) return;
        
        Money m100 = new Money("100.00");
        assertOK(house.makeBid("BuyerB", 1, m100));

        messagingService.expectBidReceived("@BuyerA", 1, m100);
        messagingService.expectBidReceived("@Auctioneer1", 1, m100);
        messagingService.expectBidReceived("@SellerY", 1, m100);
        messagingService.verify();
        if (stop == 6) return;
        
        assertSale(house.closeAuction("Auctioneer1",  1));
        
        messagingService.expectLotSold("@BuyerA", 1);
        messagingService.expectLotSold("@BuyerB", 1);
        messagingService.expectLotSold("@SellerY", 1);
        messagingService.verify();       

        bankingService.expectTransfer("BB A/C",  "BB-auth",  "AH A/C", new Money("110.00"));
        bankingService.expectTransfer("AH A/C",  "AH-auth",  "SY A/C", new Money("85.00"));
        bankingService.verify();
        if (stop == 7) return;
    }
    
    @Test
    public void testWholeStory() {
    	logger.info(makeBanner("testWholeStory"));
    	runFullStory(7);
    }
    
    @Test
    public void testRegisteringSellerWithSameName() {
    	logger.info(makeBanner("testRegisteringSellerWithSameName"));
    	runFullStory(0);
    }
    
    @Test
    public void testErrorsWhenAddingLot() {
    	runFullStory(1);
    	logger.info(makeBanner("test adding lot of unregistered seller"));
    	assertError(house.addLot("SellerRT", 6, "Table", new Money("100.00")));
    	logger.info(makeBanner("test adding lot with the same number"));
        assertError(house.addLot("SellerY", 5, "Book", new Money("100.00")));
        logger.info(makeBanner("test adding lot with the same name but different number"));
        assertOK(house.addLot("SellerZ", 7, "Table", new Money("100.00")));
    }
    
    @Test
    public void testRegisterBuyerWithSameName() {
    	logger.info(makeBanner("testRegisterBuyerWithSameName"));
    	runFullStory(2);
        assertError(house.registerBuyer("BuyerC", "@BuyerC", "BC A/C", "BC-auth"));
    }
    
    @Test
    public void testErrorsWhenNotingInterest() {
    	runFullStory(3);
    	logger.info(makeBanner("test noting interest in lot that is not in the online catalogue"));
        assertError(house.noteInterest("BuyerA", 128));
        logger.info(makeBanner("test unregistered buyer notes interest"));
        assertError(house.noteInterest("BuyerGH", 1));
        logger.info(makeBanner("test buyer notes interest in lot for the second time"));
        assertError(house.noteInterest("BuyerA", 1));
    }
    
    @Test
    public void testErrorsWhenOpeningAuction() {
    	runFullStory(4);
    	logger.info(makeBanner("test lot is not in online catalogue"));
        assertError(house.openAuction("Auctioneer2", "@Auctioneer2", 65));
        logger.info(makeBanner("test lot is already being sold in another auction"));
        assertError(house.openAuction("Auctioneer3", "@Auctioneer1", 1));
        logger.info(makeBanner("test auctioneer is already running another auction"));
        assertError(house.openAuction("Auctioneer1", "@Auctioneer1", 2));
    }
    
    @Test
    public void testLotStatusCorrectAfterAuctionOpened() {
    	logger.info(makeBanner("testLotStatusCorrectAfterAuctionOpened"));
    	runFullStory(4);
    	assertTrue(house.getOnlineCatalogue().get(1).status == LotStatus.IN_AUCTION);
    }
    
    @Test
    public void testErrorsWhenMakingBid() {
    	runFullStory(5);
    	Money m70 = new Money("70.00");
    	logger.info(makeBanner("test unregistered buyer makes a bid"));
        assertError(house.makeBid("UnregisteredBuyer", 1, m70));
        logger.info(makeBanner("test buyer makes a bid on a lot that does not exist"));
        assertError(house.makeBid("BuyerA", 45, m70));
        logger.info(makeBanner("test bid on lot that is not being auctioned"));
        assertError(house.makeBid("BuyerA", 2, m70));
        logger.info(makeBanner("test Buyer not interested in lot makes a bid"));
        assertError(house.makeBid("BuyerC", 1, new Money("85.00")));
    }
    
    @Test
    public void testErrorsWhenClosingAuction() {
    	runFullStory(7);
    	logger.info(makeBanner("test closing the auction with a wrong auctioneer"));
        assertError(house.closeAuction("Auctioneer54", 1));
        logger.info(makeBanner("test closing the auction of a lot that wasn't being sold"));
        assertError(house.closeAuction("Auctioneer1", 2));
        logger.info(makeBanner("test closing the auction of a lot that does not exist"));
        assertError(house.closeAuction("Auctioneer1", 53));
    }
    
    @Test
    public void testOpenAuctionOfSoldLot() {
    	logger.info(makeBanner("testOpenAuctionOfSoldLot"));
    	runFullStory(7);
        assertError(house.openAuction("Auctioneer1", "@Auctioneer1", 1));
    }
    
    @Test
    public void testNoteInterestInSoldLot() {
    	logger.info(makeBanner("testNoteInterestInSoldLot"));
    	runFullStory(7);
        assertError(house.noteInterest("BuyerA", 1));
    }
    
    @Test
    public void testLotStatusAfterSale() {
    	logger.info(makeBanner("testLotStatusAfterSale"));
    	runFullStory(7);
    	assertTrue(house.getOnlineCatalogue().get(1).status == LotStatus.SOLD);
    }
    @Test
    public void testLotNotSoldUnderReservePrice() {
    	logger.info(makeBanner("testLotNotSoldUnderReservePrice"));
    	runFullStory(5);
    	assertNoSale(house.closeAuction("Auctioneer1",  1));
    	assertTrue(house.getOnlineCatalogue().get(1).status == LotStatus.UNSOLD);
    }
    @Test
    public void testBidToolow() {
    	logger.info(makeBanner("testBidToolow"));
    	runFullStory(5);
    	house.openAuction("AuctioneerQ", "@AuctioneerQ", 2);
    	Money m272 = new Money("272.00");
    	assertOK(house.makeBid("BuyerB", 2, m272));
    	
    	Money m274 = new Money("274.00");
        assertError(house.makeBid("BuyerB", 2, m274));
    }
    @Test
    public void testCloseAuctionSalePending() {
    	logger.info(makeBanner("testCloseAuctionSalePending"));
    	runFullStory(6);
    	MockBankingService bank = (MockBankingService)(house.getParameters().bankingService);
		bank.setBadAccount("BB A/C");
		assertSalePendingPayment(house.closeAuction("Auctioneer1", 1));
    	
    }
    @Test
    public void testOpenAuctionWithSameAuctioneer() {
    	logger.info(makeBanner("testOpenAuctionWithTheAuctioneerThatJustClosedAuction"));
    	runFullStory(7);
    	assertOK(house.openAuction("Auctioneer1", "@Auctioneer1", 2));
    }
    @Test
    public void testMakeFirstBid() {
    	logger.info(makeBanner("testMakeFirstBid"));
    	runFullStory(4);
    	Money m01 = new Money("1");
    	assertOK(house.makeBid("BuyerA", 1, m01));
    }
    @Test 
    public void testNoBidsMade() {
    	logger.info(makeBanner("testNoBidsMade"));
    	runFullStory(4);
    	assertNoSale(house.closeAuction("Auctioneer1",  1));
    	assertTrue(house.getOnlineCatalogue().get(1).status == LotStatus.UNSOLD);
    }
}
