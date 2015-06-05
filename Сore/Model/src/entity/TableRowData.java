package entity;

public class TableRowData {

    private Bank bank;

    private ExchangeRate currentRate;

    private ExchangeRate rateDifference;

    public TableRowData(Bank bank, ExchangeRate currentRate, ExchangeRate rateDifference) {
        this.bank = bank;
        this.currentRate = currentRate;
        this.rateDifference = rateDifference;
    }

    public TableRowData() {
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public ExchangeRate getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(ExchangeRate currentRate) {
        this.currentRate = currentRate;
    }

    public ExchangeRate getRateDifference() {
        return rateDifference;
    }

    public void setRateDifference(ExchangeRate rateDifference) {
        this.rateDifference = rateDifference;
    }
}
