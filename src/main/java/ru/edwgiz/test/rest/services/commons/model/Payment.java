package ru.edwgiz.test.rest.services.commons.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

@Entity
@Table(name = "PAYMENT")
@XmlRootElement(name = "Payment")
@XmlAccessorType(NONE)
public class Payment {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PaymentSequence")
    @SequenceGenerator(name = "PaymentSequence", sequenceName = "PAYMENT_SEQUENCE")
    @XmlAttribute
    private Long id;

    @Column(name = "WITHDRAWAL_BANK_ACCOUNT_NUMBER", nullable = false)
    @XmlAttribute
    private long withdrawalBankAccountNumber;

    @Column(name = "DEPOSIT_BANK_ACCOUNT_NUMBER", nullable = false)
    @XmlAttribute
    private long depositBankAccountNumber;

    @Column(name = "FUNDS", precision = 20, scale = 4, nullable = false)
    @XmlAttribute
    private BigDecimal funds;

    @Column(name = "DATE", nullable = false)
    @Temporal(TIMESTAMP)
    @XmlAttribute
    private Date date;


    public Long getId() {
        return id;
    }

    public long getWithdrawalBankAccountNumber() {
        return withdrawalBankAccountNumber;
    }

    public void setWithdrawalBankAccountNumber(long withdrawalBankAccountNumber) {
        this.withdrawalBankAccountNumber = withdrawalBankAccountNumber;
    }

    public long getDepositBankAccountNumber() {
        return depositBankAccountNumber;
    }

    public void setDepositBankAccountNumber(long depositBankAccountNumber) {
        this.depositBankAccountNumber = depositBankAccountNumber;
    }

    public BigDecimal getFunds() {
        return funds;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
