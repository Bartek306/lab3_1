package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;
@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    private static final String dataName = "test data name";
    private static final String surname = "test surname";
    private static final Id clientId = Id.generate();
    private static final ClientData clientData = new ClientData(clientId, surname);
    private static final Id productId = Id.generate();
    private static final String taxDescription = "test description";
    private static final Money money = Money.ZERO;
    private static final Tax tax = new Tax(money, taxDescription);
    private ProductData productData = new ProductDataBuilder()
            .name(dataName)
            .productId(productId)
            .price(Money.ZERO)
            .snapshotDate(null)
            .type(ProductType.FOOD)
            .build();
    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;



    @Mock
    private InvoiceFactory invoiceFactory;

    @Mock
    private TaxPolicy taxPolicy;

    @Captor
    ArgumentCaptor<ClientData> clientDataArgumentCaptor;

    @Captor
    ArgumentCaptor<Money> moneyArgumentCaptor;

    @Captor
    ArgumentCaptor<ProductType> productTypeArgumentCaptor;

    @BeforeEach
    void setUp() throws Exception {
        bookKeeper = new BookKeeper(invoiceFactory);
        invoiceRequest = new InvoiceRequest(clientData);

    }

    @Test
    void OneInvoiceTest() {
        invoiceRequest.add(new RequestItem(productData, 0, money));
        Invoice testInvoice = new Invoice(productId, clientData);
        when(invoiceFactory.create(clientData)).thenReturn(testInvoice);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(tax);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(1, invoice.getItems().size());
    }

    @Test
    void threeInvoiceTest(){
        RequestItem firstRequestItem = new RequestItem(productData, 1, money);
        RequestItem secondRequestItem = new RequestItem(productData, 0, money);
        RequestItem thirdRequestItem = new RequestItem(productData, 2, money);
        invoiceRequest.add(firstRequestItem);
        invoiceRequest.add(secondRequestItem);
        invoiceRequest.add(thirdRequestItem);
        Invoice testInvoice = new Invoice(productId, clientData);
        when(invoiceFactory.create(clientData)).thenReturn(testInvoice);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(tax);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(3, invoice.getItems().size());

    }

    @Test
    void zeroInvoiceTest(){
        Invoice testInvoice = new Invoice(productId, clientData);
        when(invoiceFactory.create(clientData)).thenReturn(testInvoice);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(0, invoice.getItems().size());
    }

    @Test
    void twoInvoiceElementShouldInvokeCalculateTaxMethodTwiceTest(){
        RequestItem firstRequestItem = new RequestItem(productData, 0, money);
        RequestItem secondRequestItem = new RequestItem(productData, 1, money);
        invoiceRequest.add(firstRequestItem);
        invoiceRequest.add(secondRequestItem);
        Invoice testInvoice = new Invoice(productId, clientData);
        when(invoiceFactory.create(clientData)).thenReturn(testInvoice);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(tax);
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(2)).calculateTax(productTypeArgumentCaptor.capture(), moneyArgumentCaptor.capture());

    }


    @Test
    void fiveInvoiceElementShouldInvokeCalculateTaxMethodFiveTimesTest(){
        RequestItem firstRequestItem = new RequestItem(productData, 0, money);
        RequestItem secondRequestItem = new RequestItem(productData, 1, money);
        RequestItem thirdRequestItem = new RequestItem(productData, 3, money);
        RequestItem fourthRequestItem = new RequestItem(productData, 4, money);
        RequestItem fifthRequestItem = new RequestItem(productData, 5, money);
        invoiceRequest.add(firstRequestItem);
        invoiceRequest.add(secondRequestItem);
        invoiceRequest.add(thirdRequestItem);
        invoiceRequest.add(fourthRequestItem);
        invoiceRequest.add(fifthRequestItem);
        Invoice testInvoice = new Invoice(productId, clientData);
        when(invoiceFactory.create(clientData)).thenReturn(testInvoice);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(tax);
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(5)).calculateTax(productTypeArgumentCaptor.capture(), moneyArgumentCaptor.capture());
    }
    @Test
    void zeroInvoiceElementShouldInvokeCalculateTaxMethodZeroTimesTest(){
        Invoice testInvoice = new Invoice(productId, clientData);
        when(invoiceFactory.create(clientData)).thenReturn(testInvoice);
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(0)).calculateTax(productTypeArgumentCaptor.capture(), moneyArgumentCaptor.capture());
    }

}