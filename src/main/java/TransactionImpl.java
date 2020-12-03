import java.util.Objects;

public class TransactionImpl implements Comparable<TransactionImpl>,Transaction{

    private int id;
    private TransactionStatus status;
    private String from;
    private String to;
    private double amount;

    public TransactionImpl(int id, TransactionStatus status, String from, String to, double amount) {
        this.id = id;
        this.status = status;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public int compareTo(TransactionImpl o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionImpl that = (TransactionImpl) o;
        return id == that.id && Double.compare(that.amount, amount) == 0 && status == that.status && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, from, to, amount);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public TransactionStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(TransactionStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public double getAmount() {
        return this.amount;
    }

    @Override
    public String getFrom() {
        return this.from;
    }

    @Override
    public String getTo() {
        return this.to;
    }
}
