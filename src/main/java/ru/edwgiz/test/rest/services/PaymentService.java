package ru.edwgiz.test.rest.services;

import org.glassfish.hk2.extras.interception.Intercepted;
import ru.edwgiz.test.rest.framework.hk2.persistence.Transactional;
import ru.edwgiz.test.rest.framework.jersey.exceptions.EntityHasIdentifierException;
import ru.edwgiz.test.rest.framework.jersey.exceptions.EntityNotFoundException;
import ru.edwgiz.test.rest.framework.jersey.exceptions.WebApplicationException;
import ru.edwgiz.test.rest.services.commons.model.BankAccount;
import ru.edwgiz.test.rest.services.commons.model.Payment;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import java.util.Date;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;


@Path("/payments")
@Intercepted
public class PaymentService {

    @Inject
    private EntityManager em;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Transactional
    public Payment create(Payment payment) {
        if (payment.getId() != null) {
            throw new EntityHasIdentifierException();
        }
        if (payment.getFunds() == null) {
            throw new WebApplicationException("Payment funds is undefined", BAD_REQUEST);
        }
        payment.setDate(new Date());

        // set timeout to obtain pessimistic lock over the bank accounts
        em.setProperty("javax.persistence.lock.timeout", 1000L);

        final BankAccount src;
        final BankAccount dst;
        if (payment.getWithdrawalBankAccountNumber() < payment.getDepositBankAccountNumber()) {
            src = getWithdrawalBankAccount(payment);
            dst = getBankAccount(payment.getDepositBankAccountNumber(), "deposit");
        } else if (payment.getWithdrawalBankAccountNumber() > payment.getDepositBankAccountNumber()) {
            // revert the lock order of the bank accounts to avoid temporary dead locking
            dst = getBankAccount(payment.getDepositBankAccountNumber(), "deposit");
            src = getWithdrawalBankAccount(payment);
        } else {
            throw new WebApplicationException("Withdrawal and deposit bank accounts are the same", BAD_REQUEST);
        }


        src.setBalance(src.getBalance().subtract(payment.getFunds()));
        dst.setBalance(dst.getBalance().add(payment.getFunds()));

        em.merge(src);
        em.merge(dst);
        em.persist(payment);

        return payment;
    }

    private BankAccount getWithdrawalBankAccount(Payment payment) {
        final BankAccount bankAccount = getBankAccount(payment.getWithdrawalBankAccountNumber(), "withdrawal");
        if (bankAccount.getBalance().compareTo(payment.getFunds()) < 0) {
            throw new WebApplicationException("Withdrawal bank account has insufficient balance for the payment", BAD_REQUEST);
        }
        return bankAccount;
    }

    private BankAccount getBankAccount(long number, String type) {
        try {
            return em.createNamedQuery("BankAccount.findByNumber", BankAccount.class)
                    .setParameter("number", number)
                    .setLockMode(PESSIMISTIC_WRITE)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new WebApplicationException("No " + type + "BankAccount found", BAD_REQUEST);
        }
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Transactional
    public Payment read(@PathParam("id") long id) {
        final Payment payment = em.find(Payment.class, id);
        if (payment == null) {
            throw new EntityNotFoundException();
        }
        return payment;
    }

}
