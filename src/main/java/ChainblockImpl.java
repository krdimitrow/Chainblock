import java.util.*;
import java.util.stream.Collectors;

public class ChainblockImpl implements Chainblock {

    private Map<Integer, Transaction> transactionById;

    public ChainblockImpl() {
        this.transactionById = new HashMap<>();
    }

    public int getCount() {
        return this.transactionById.size();
    }

    public void add(Transaction transaction) {
        int id = transaction.getId();
        if (!contains(id)) {
            this.transactionById.put(id, transaction);
        }
    }

    public boolean contains(Transaction transaction) {
        return contains(transaction.getId());
    }

    public boolean contains(int id) {
        return this.transactionById.containsKey(id);
    }

    public void changeTransactionStatus(int id, TransactionStatus newStatus) {
        if (!contains(id)) {
            throw new IllegalArgumentException();
        }
        this.transactionById.get(id).setStatus(newStatus);
    }

    @Override
    public void removeTransactionById(int id) {
        if (contains(id)) {
            this.transactionById.remove(id);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Transaction getById(int id) {
        if (!contains(id)) {
            throw new IllegalArgumentException();
        }
        return this.transactionById.get(id);
    }

    public Iterable<Transaction> getByTransactionStatus(TransactionStatus status) {
        List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : transactionById.values()) {
            if (transaction.getStatus() == status) {
                transactionList.add(transaction);
            }
        }
        if (transactionList.size() == 0) {
            throw new IllegalArgumentException();
        }
        transactionList.sort(Comparator.comparing(Transaction::getAmount).reversed());
        return transactionList;
    }

    public Iterable<String> getAllSendersWithTransactionStatus(TransactionStatus status) {
        List<Transaction> transactions = new ArrayList<>();
        getByTransactionStatus(status).forEach(transactions::add);
        return transactions.stream()
                .map(Transaction::getFrom)
                .collect(Collectors.toList());
    }

    public Iterable<String> getAllReceiversWithTransactionStatus(TransactionStatus status) {
        List<Transaction> transactions = new ArrayList<>();
        getByTransactionStatus(status).forEach(transactions::add);
        return transactions.stream()
                .map(Transaction::getTo)
                .collect(Collectors.toList());
    }

    public Iterable<Transaction> getAllOrderedByAmountDescendingThenById() {
        return this.transactionById.values().stream()
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()
                        .thenComparing(Comparator.comparing(Transaction::getId)))
                .collect(Collectors.toList());
    }

    public Iterable<Transaction> getBySenderOrderedByAmountDescending(String sender) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : transactionById.values()) {
            if (transaction.getFrom().equals(sender)) {
                transactions.add(transaction);
            }
        }
        if (transactions.size() == 0) {
            throw new IllegalArgumentException();
        }
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()).collect(Collectors.toList());
    }

    public Iterable<Transaction> getByReceiverOrderedByAmountThenById(String receiver) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : transactionById.values()) {
            if (transaction.getTo().equals(receiver)) {
                transactions.add(transaction);
            }
        }
        if (transactions.size() == 0) {
            throw new IllegalArgumentException();
        }
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()).collect(Collectors.toList());
    }

    public Iterable<Transaction> getByTransactionStatusAndMaximumAmount(TransactionStatus status, double amount) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction value : transactionById.values()) {
            if (value.getStatus().equals(status) && (value.getAmount() <= amount)) {
                transactions.add(value);
            }
        }
        Collections.reverse(transactions);
        return transactions;
    }

    public Iterable<Transaction> getBySenderAndMinimumAmountDescending(String sender, double amount) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction value : transactionById.values()) {
            if (value.getFrom().equals(sender) && (value.getAmount() > amount)) {
                transactions.add(value);
            }
        }
        if (transactions.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Collections.reverse(transactions);
        return transactions;
    }

    public Iterable<Transaction> getByReceiverAndAmountRange(String receiver, double lo, double hi) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction value : transactionById.values()) {
            if (value.getTo().equals("To") && value.getAmount() >= lo && value.getAmount() < hi){
                transactions.add(value);
            }
        }
        if(transactions.isEmpty()){
            throw new IllegalArgumentException();
        }
        Collections.reverse(transactions);
        return transactions;
    }

    public Iterable<Transaction> getAllInAmountRange(double lo, double hi) {
       List<Transaction>transactions = new ArrayList<>();
        for (Transaction value : transactionById.values()) {
            if(value.getAmount()<=hi&&value.getAmount()>=lo){
                transactions.add(value);
            }
        }
        return transactions;
    }

    public Iterator<Transaction> iterator() {
        return null;
    }
}
