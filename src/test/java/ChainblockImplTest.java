import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ChainblockImplTest {

    private Chainblock chainblock;
    private List<Transaction> transactionList;


    @Before
    public void setUp() {
        this.chainblock = new ChainblockImpl();
        this.createTransactions();
    }

    private void createTransactions() {
        this.transactionList = new ArrayList<>();
        Transaction t1 = new TransactionImpl(1, TransactionStatus.FAILED, "From", "To", 10.50);
        this.transactionList.add(t1);
        Transaction t2 = new TransactionImpl(2, TransactionStatus.SUCCESSFUL, "From", "To", 10.60);
        this.transactionList.add(t2);
        Transaction t3 = new TransactionImpl(3, TransactionStatus.SUCCESSFUL, "From", "To", 10.70);
        this.transactionList.add(t3);
        Transaction t4 = new TransactionImpl(4, TransactionStatus.SUCCESSFUL, "From", "To", 10.80);
        this.transactionList.add(t4);
    }

    private void fillChainBlock() {
        for (Transaction transaction : transactionList) {
            this.chainblock.add(transaction);
        }
    }


    //Contains
    @Test
    public void testContainsByIdReturnCorrectValue() {
        chainblock.add(transactionList.get(0));
        Assert.assertTrue(chainblock.contains(transactionList.get(0).getId()));

    }

    //Add
    @Test
    public void testAddHasToAddTransactionToChainBlock() {
        chainblock.add(transactionList.get(0));
        Assert.assertEquals(1, chainblock.getCount());
    }

    @Test
    public void testAddHasNotAddTransactionWhenDuplicateId() {
        chainblock.add(transactionList.get(0));
        assertEquals(1, chainblock.getCount());
        chainblock.add(transactionList.get(0));
        assertEquals(1, chainblock.getCount());
    }

    //changeTransactionStatus
    @Test
    public void testTransactionStatusHasToChangeCorrectly() {
        chainblock.add(transactionList.get(0));
        chainblock.changeTransactionStatus(transactionList.get(0).getId(), TransactionStatus.FAILED);
        Assert.assertEquals(TransactionStatus.FAILED, transactionList.get(0).getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransactionHasToThrowEx() {
        chainblock.changeTransactionStatus(transactionList.get(0).getId(), TransactionStatus.ABORTED);
    }

    //removeTransactionById
    @Test
    public void testRemoveCorrectTransactionById() {
        fillChainBlock();
        assertEquals(4, chainblock.getCount());
        chainblock.removeTransactionById(transactionList.get(0).getId());
        assertEquals(3, chainblock.getCount());
        assertFalse(chainblock.contains(transactionList.get(0).getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveThrowExIfTransactionNoPresent() {
        fillChainBlock();
        chainblock.removeTransactionById(chainblock.getCount() + 1);
    }

    //getById
    @Test
    public void testGetByIdHasToReturnCorrectTransaction() {
        fillChainBlock();
        Transaction expected = this.transactionList.get(2);
        Transaction actual = this.chainblock.getById(expected.getId());
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdHasToThrowExIfIdNotPresent() {
        fillChainBlock();
        this.chainblock.getById(10);
    }

    //getByTransactionStatus
    @Test(expected = IllegalArgumentException.class)
    public void testGetByTransactionStatusHasToThrowIfStatusNotExist() {
        fillChainBlock();
        chainblock.getByTransactionStatus(TransactionStatus.ABORTED);
    }

    @Test
    public void testGetByTransactionStatusReturnCorrectTransactions() {
        fillChainBlock();
        List<Transaction> expected = this.transactionList.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESSFUL)
                .collect(Collectors.toList());

        Iterable<Transaction> result = chainblock.getByTransactionStatus(TransactionStatus.SUCCESSFUL);
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        for (Transaction transaction : actual) {
            assertEquals(TransactionStatus.SUCCESSFUL, transaction.getStatus());
        }
    }

    @Test
    public void testGetByTransactionStatusReturnCorrectTransactionsInCorrectOrder() {
        fillChainBlock();
        List<Transaction> expected = this.transactionList.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESSFUL)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> result = chainblock.getByTransactionStatus(TransactionStatus.SUCCESSFUL);
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    //getAllSendersWithTransactionStatus
    @Test(expected = IllegalArgumentException.class)
    public void testGetSendersByTransactionStatusHasToThrowWhenStatusNotExist() {
        fillChainBlock();
        chainblock.getAllSendersWithTransactionStatus(TransactionStatus.UNAUTHORIZED);
    }

    @Test
    public void testGetSendersByTransactionStatusReturnCorrectSendersInCorrectOrder() {
        fillChainBlock();
        List<String> expected = transactionList.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESSFUL)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .map(Transaction::getFrom)
                .collect(Collectors.toList());

        Iterable<String> senders = chainblock.getAllSendersWithTransactionStatus(TransactionStatus.SUCCESSFUL);

        assertNotNull(senders);

        List<String> actual = new ArrayList<>();
        senders.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        for (String sender : actual) {
            assertEquals("From", sender);
        }
        assertEquals(expected, actual);
    }

    //getAllReceiversWithTransactionStatus
    @Test(expected = IllegalArgumentException.class)
    public void testGetReceiversByTransactionStatusHasToThrowWhenStatusNotExist() {
        fillChainBlock();
        chainblock.getAllReceiversWithTransactionStatus(TransactionStatus.UNAUTHORIZED);
    }

    @Test
    public void testGetReceiversByTransactionStatusReturnCorrectSendersInCorrectOrder() {
        fillChainBlock();
        List<String> expected = transactionList.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESSFUL)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .map(Transaction::getTo)
                .collect(Collectors.toList());

        Iterable<String> receivers = chainblock.getAllReceiversWithTransactionStatus(TransactionStatus.SUCCESSFUL);

        assertNotNull(receivers);

        List<String> actual = new ArrayList<>();
        receivers.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        for (String receiver : actual) {
            assertEquals("To", receiver);
        }
        assertEquals(expected, actual);
    }

    //getAllOrderedByAmountDescendingThenById
    @Test
    public void testGetAllOrderedByAmountDescendingThenById() {
        fillChainBlock();
        List<Transaction> expected = transactionList.stream()
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()
                        .thenComparing(Transaction::getId))
                .collect(Collectors.toList());

        Iterable<Transaction> all = chainblock.getAllOrderedByAmountDescendingThenById();
        assertNotNull(all);
        List<Transaction> actual = new ArrayList<>();
        all.forEach(actual::add);
        assertEquals(expected, actual);
    }

    //getBySenderOrderedByAmountDescending
    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderOrderedByAmountDescendingHasToThrowWhenSenderNotPresent() {
        fillChainBlock();
        chainblock.getBySenderOrderedByAmountDescending("Pesho");
    }

    @Test
    public void testGetBySenderOrderedByAmountDescendingReturnTrueResult() {
        fillChainBlock();
        List<Transaction> expected = this.transactionList.stream()
                .sorted(Comparator.comparing(Transaction::getAmount))
                .collect(Collectors.toList());

        Iterable<Transaction> result = chainblock.getBySenderOrderedByAmountDescending("From");
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        double startAmount = 10.90;
        for (Transaction transaction : actual) {
            assertEquals("From", transaction.getFrom());
            assertEquals(startAmount - 0.10, transaction.getAmount(), 0.01);
            startAmount -= 0.10;
        }
    }

    //getByReceiverOrderedByAmountThenById
    @Test(expected = IllegalArgumentException.class)
    public void testGetByReceiverOrderedByAmountDescendingHasToThrowWhenSenderNotPresent() {
        fillChainBlock();
        chainblock.getByReceiverOrderedByAmountThenById("Pesho");
    }

    @Test
    public void testGetByReceiverOrderedByAmountDescendingReturnTrueResult() {
        fillChainBlock();
        List<Transaction> expected = this.transactionList.stream()
                .sorted(Comparator.comparing(Transaction::getAmount))
                .collect(Collectors.toList());

        Iterable<Transaction> result = chainblock.getByReceiverOrderedByAmountThenById("To");
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        double startAmount = 10.90;
        for (Transaction transaction : actual) {
            assertEquals("To", transaction.getTo());
            assertEquals(startAmount - 0.10, transaction.getAmount(), 0.01);
            startAmount -= 0.10;
        }
    }

    //getByTransactionStatusAndMaximumAmount
    @Test
    public void testGetByTransactionStatusAndMaximumAmountReturnCorrectTransactions() {
        fillChainBlock();
        double amount = 10.80;
        double finalMaximumAmount = amount;
        List<Transaction> expected = transactionList.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESSFUL)
                .filter(t -> t.getAmount() <= finalMaximumAmount)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> result = chainblock.getByTransactionStatusAndMaximumAmount(TransactionStatus.SUCCESSFUL, amount);
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        for (Transaction transaction : actual) {
            assertEquals(TransactionStatus.SUCCESSFUL, transaction.getStatus());
            assertEquals(amount, transaction.getAmount(), 0.01);
            amount -= 0.10;
        }
    }

    @Test
    public void testGetByTransactionStatusAndMaximumAmountReturnEmptyCollectionIfNoSuchElement() {
        fillChainBlock();
        double maximumAmount = 10.80;
        Iterable<Transaction> result = chainblock.getByTransactionStatusAndMaximumAmount(TransactionStatus.ABORTED, maximumAmount - 10);
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertTrue(actual.isEmpty());
    }

    //getBySenderAndMinimumAmountDescending
    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderAndMinimumAmountDescendingThrowExTransactionNotExist() {
        double amount = 100;
        chainblock.getBySenderAndMinimumAmountDescending("Pesho", amount);
    }

    @Test
    public void testGetBySenderAndMinimumAmountDescendingReturnCorrectTransaction() {
        fillChainBlock();

        double finalAmount = 10.50;
        List<Transaction> expected = transactionList.stream()
                .filter(t -> t.getFrom().equals("From") && t.getAmount() > finalAmount)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> result = chainblock.getBySenderAndMinimumAmountDescending("From", finalAmount);
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        for (Transaction transaction : actual) {
            assertEquals("From", transaction.getFrom());
        }
        assertEquals(expected, actual);
    }


    //getByReceiverAndAmountRange
    @Test(expected = IllegalArgumentException.class)
    public void testGetByReceiverAndAmountRangeThrowExIfNoSuchTransaction() {
        double loAmount = 1;
        double hiAmount = 10;
        chainblock.getByReceiverAndAmountRange("Pesho", loAmount, hiAmount);
    }

    @Test
    public void testGetByReceiverAndAmountRangeReturnCorrectTransaction() {
        fillChainBlock();
        double loAmount = 10.50;
        double hiAmount = 10.80;
        List<Transaction> expected = transactionList.stream()
                .filter(t -> t.getTo().equals("To") && t.getAmount() >= loAmount && t.getAmount() < hiAmount)
                .sorted(Comparator.comparing(Transaction::getId).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> result = chainblock.getByReceiverAndAmountRange("To", loAmount, hiAmount);
        assertNotNull(result);
        List<Transaction> actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }


    //getAllInAmountRange
    @Test
    public void testGetAllInAmountRangeReturnEmptyCollectionIfTransactionsNotExist() {
        fillChainBlock();
        double loAmount = 11.00;
        double hiAmount = 11.50;
        Iterable<Transaction> result = chainblock.getAllInAmountRange(loAmount, hiAmount);
        assertNotNull(result);
        List<Transaction> transactions = new ArrayList<>();
        result.forEach(transactions::add);
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void testGetAllInAmountRangeReturnCorrectTransactions() {
        fillChainBlock();
        double loAmount = 10.60;
        double hiAmount = 10.80;
        List<Transaction> expected = transactionList.stream()
                .filter(t -> t.getAmount() <= hiAmount && t.getAmount() >= loAmount)
                .collect(Collectors.toList());

        Iterable<Transaction>result = chainblock.getAllInAmountRange(loAmount,hiAmount);
        assertNotNull(result);
        List<Transaction>actual = new ArrayList<>();
        result.forEach(actual::add);
        assertEquals(expected.size(),actual.size());
        assertEquals(expected,actual);
    }


}