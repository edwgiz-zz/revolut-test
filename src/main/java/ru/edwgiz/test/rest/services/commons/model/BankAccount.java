package ru.edwgiz.test.rest.services.commons.model;

import ru.edwgiz.test.rest.framework.jaxb.BigDecimalAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

import static javax.xml.bind.annotation.XmlAccessType.NONE;


@Entity
@Table(name = "BANK_ACCOUNT")
@XmlRootElement(name = "BankAccount")
@XmlAccessorType(NONE)
@NamedQueries({
        @NamedQuery(name = "BankAccount.findByNumber", query = "select b from BankAccount b where b.number=:number")
})
public class BankAccount {

    private static final long serialVersionUID = 0;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BankAccountSequence")
    @SequenceGenerator(name = "BankAccountSequence", sequenceName = "BANK_ACCOUNT_SEQUENCE")
    @XmlAttribute
    private Long id;

    @Column(name = "NUMBER", nullable = false, unique = true)
    @XmlAttribute
    private long number;

    @Column(name = "BALANCE", precision = 20, scale = 4, nullable = false)
    @XmlAttribute
    @XmlJavaTypeAdapter(BigDecimalAdapter.class)
    private BigDecimal balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal amount) {
        this.balance = amount;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "id=" + id +
                ", number=" + number +
                ", amount=" + balance +
                '}';
    }
}
