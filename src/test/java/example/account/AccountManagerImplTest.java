package example.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerImplTest {
    private AccountManager accountManager;
    private Customer customer;

    @BeforeEach
    void setup() {
        accountManager = new AccountManagerImpl();
        customer = new Customer();
        customer.setBalance(1000);
    }

    @Test
    void testDeposit() {
        accountManager.deposit(customer, 100);
        assertEquals(1100, customer.getBalance(), "Balance should be updated after deposit");
    }

    @Test
    void testWithdrawWithSufficientBalance() {
        String result = accountManager.withdraw(customer, 100);

        assertEquals("success", result, "Withdrawal should succeed");
        assertEquals(900, customer.getBalance(), "Balance should be updated after withdrawal");
    }

    @Test
    void testWithdrawWithInsufficientBalanceButCreditsAllowed() {
        customer.setCreditAllowed(true);

        String result = accountManager.withdraw(customer, 1100);

        assertEquals("success", result, "Withdrawal should fail");
        assertEquals(-100, customer.getBalance(), "Balance should be updated after withdrawal");
    }

    @Test
    void testWithdrawWithInsufficientBalanceAndNotCreditsAllowed() {
        customer.setCreditAllowed(false);

        String result = accountManager.withdraw(customer, 1100);

        assertEquals("insufficient account balance", result, "Withdrawal should fail");
        assertEquals(1000, customer.getBalance(), "Balance should remain unchanged after withdrawal");
    }

    @Test
    void testWithdrawWithInsufficientBalanceAndCreditsAllowedButExceededMaxCredit() {
        customer.setCreditAllowed(true);

        String result = accountManager.withdraw(customer, 2100);

        assertEquals("maximum credit exceeded", result, "Withdrawal should fail");
        assertEquals(1000, customer.getBalance(), "Balance should remain unchanged after withdrawal");
    }

    @Test
    void testWithdrawWithInsufficientBalanceButVipCustomer() {
        customer.setVip(true);

        String result = accountManager.withdraw(customer, 2100);

        assertEquals("success", result, "Withdrawal should succeed");
        assertEquals(-1100, customer.getBalance(), "Balance should reflect the withdrawal including credit");
    }
}
