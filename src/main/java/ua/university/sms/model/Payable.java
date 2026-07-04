package ua.university.sms.model;

public interface Payable {

    boolean isPaid();

    void markPaid();
}
