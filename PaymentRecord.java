package org.example.loans;

public record PaymentRecord (
    int month,
    double payment,
    double principal,
    double interest,
    double balance,
    double totalInterest
) {}