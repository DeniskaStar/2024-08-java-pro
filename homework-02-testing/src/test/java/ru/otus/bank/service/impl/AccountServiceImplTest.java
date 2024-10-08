package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    public void testTransfer() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        assertEquals(new BigDecimal(90), sourceAccount.getAmount());
        assertEquals(new BigDecimal(20), destinationAccount.getAmount());
    }

    @Test
    public void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));
            }
        });
        assertEquals("No source account", result.getLocalizedMessage());
    }


    @Test
    public void testTransferWithVerify() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(1L);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));
        destinationAccount.setId(2L);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(new BigDecimal(90));

        ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(2L) && argument.getAmount().equals(new BigDecimal(20));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        verify(accountDao).save(argThat(sourceMatcher));
        verify(accountDao).save(argThat(destinationMatcher));
    }

    @Test
    void testAddAccount() {
        // Given
        Agreement agreement = new Agreement();
        agreement.setId(1L);
        agreement.setName("dummy_agreement");

        String accountNumber = "12345";
        Integer type = 1;
        BigDecimal amount = new BigDecimal(100);

        Account expectedAccount = new Account();
        expectedAccount.setAgreementId(agreement.getId());
        expectedAccount.setNumber(accountNumber);
        expectedAccount.setType(type);
        expectedAccount.setAmount(amount);

        when(accountDao.save(any(Account.class))).thenReturn(expectedAccount);

        // When
        Account actualAccount = accountServiceImpl.addAccount(agreement, accountNumber, type, amount);

        // Then
        assertNotNull(actualAccount);
        assertThat(actualAccount)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedAccount);

        verify(accountDao).save(any(Account.class));
    }

    @Test
    void testGetAccounts() {
        // Given
        Account firstAccount = new Account();
        firstAccount.setId(1L);
        firstAccount.setNumber("12345");
        firstAccount.setType(1);

        Account secondAccount = new Account();
        secondAccount.setId(2L);
        secondAccount.setNumber("54355");
        secondAccount.setType(1);

        when(accountDao.findAll()).thenReturn(List.of(firstAccount, secondAccount));

        // When
        List<Account> actualAccounts = accountServiceImpl.getAccounts();

        // Then
        assertThat(actualAccounts)
                .isNotEmpty()
                .hasSize(2)
                .allMatch(acc -> acc.getId() != null)
                .allMatch(acc -> !acc.getNumber().isBlank())
                .allMatch(acc -> acc.getType().equals(1));

        verify(accountDao).findAll();
    }

    @Test
    void testGetAccountByAgreement() {
        // Given
        Agreement agreement = new Agreement();
        agreement.setId(1L);

        Account firstAccount = new Account();
        firstAccount.setAgreementId(agreement.getId());
        firstAccount.setNumber("12345");

        Account secondAccount = new Account();
        secondAccount.setAgreementId(agreement.getId());
        secondAccount.setNumber("54654");

        when(accountDao.findByAgreementId(agreement.getId())).thenReturn(List.of(firstAccount, secondAccount));

        // When
        List<Account> actualAccounts = accountServiceImpl.getAccounts(agreement);

        // Then
        assertThat(actualAccounts)
                .isNotEmpty()
                .hasSize(2)
                .allMatch(acc -> !acc.getNumber().isBlank());

        verify(accountDao).findByAgreementId(agreement.getId());
    }
}
