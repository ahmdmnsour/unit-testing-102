package example.store;

import example.account.AccountManager;
import example.account.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StoreImplTest {
    private Store store;
    @Mock
    private AccountManager accountManager;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setup() {
        accountManager = mock(AccountManager.class);

        store = new StoreImpl(accountManager);

        product = new Product();
        product.setPrice(500);
        product.setQuantity(5);

        customer = new Customer();
        customer.setBalance(1000);
    }

    @Test
    void testBuyWithAvailableStockProduct() {
        when(accountManager.withdraw(customer, product.getPrice())).thenReturn("success");

        store.buy(product, customer);

        assertEquals(
                4,
                product.getQuantity(),
                "Product quantity should decrease by 1 after purchase"
        );
        verify(accountManager, times(1))
                .withdraw(customer, product.getPrice());
    }

    @Test
    void testBuyWithNoStockProduct() {
        product.setQuantity(0);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> store.buy(product, customer)
        );

        assertEquals(
                "Product out of stock",
                exception.getMessage(),
                "Should throw 'Product out of stock' error when quantity is zero"
        );
    }

    @Test
    void testBuyWithPaymentFailure() {
        when(accountManager.withdraw(customer, product.getPrice()))
                .thenReturn("insufficient account balance");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> store.buy(product, customer)
        );

        assertEquals(
                "Payment failure: insufficient account balance",
                exception.getMessage(),
                "Should throw payment failure error when withdrawal fails");
        verify(accountManager, times(1)).withdraw(customer, product.getPrice());
    }
}
