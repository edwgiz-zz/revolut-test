package ru.edwgiz.test.rest.framework;

import junit.framework.ComparisonFailure;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.jetty.JettyTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;
import ru.edwgiz.test.rest.services.commons.model.BankAccount;
import ru.edwgiz.test.rest.services.commons.model.Payment;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.edwgiz.test.rest.framework.WebApp.createMoxyJsonResolver;


public class WebAppTest extends JerseyTest {

    @Test
    public void test() throws Exception {
        testEntityNotFound("bankAccounts/1");
        testEntityNotFound("payments/1");

        { // create first bank account
            final Invocation post = target("bankAccounts").request()
                    .buildPost(entity(// language=JSON
                            "{\n" +
                                    "\t\"BankAccount\":{\n" +
                                    "\t\t\"number\": 1,\n" +
                                    "\t\t\"balance\": \"10000.03\"\n" +
                                    "\t}\n" +
                                    "}", APPLICATION_JSON_TYPE));
            final Response response = post.invoke();
            assertEquals(SC_OK, response.getStatus());
            assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
            assertEquals("{\"BankAccount\":{\"id\":1,\"number\":1,\"balance\":\"10000.03\"}}", response.readEntity(String.class));
        }
        { // create second bank account
            final Invocation post = target("bankAccounts").request()
                    .buildPost(entity(// language=JSON
                            "{\n" +
                                    "\t\"BankAccount\":{\n" +
                                    "\t\t\"number\": 2,\n" +
                                    "\t\t\"balance\": \"10\"\n" +
                                    "\t}\n" +
                                    "}", APPLICATION_JSON_TYPE));
            final Response response = post.invoke();
            assertEquals(SC_OK, response.getStatus());
            assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
            assertEquals("{\"BankAccount\":{\"id\":2,\"number\":2,\"balance\":\"10\"}}", response.readEntity(String.class));
        }
        { // create first payment
            final Invocation post = target("payments").request()
                    .buildPost(entity(// language=JSON
                            "{\n" +
                                    "\t\"Payment\":{\n" +
                                    "\t\t\"withdrawalBankAccountNumber\": 1,\n" +
                                    "\t\t\"depositBankAccountNumber\": 2,\n" +
                                    "\t\t\"funds\": \"5001.01\"\n" +
                                    "\t}\n" +
                                    "}", APPLICATION_JSON_TYPE));
            final Response response = post.invoke();
            assertEquals(SC_OK, response.getStatus());
            assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
            final Payment actual = response.readEntity(Payment.class);
            assertEquals((Long) 1L, actual.getId());
            assertEquals(1L, actual.getWithdrawalBankAccountNumber());
            assertEquals(2L, actual.getDepositBankAccountNumber());
            assertEquals(new BigDecimal("5001.01"), actual.getFunds());
            assertNotNull(actual.getDate());
        }
        { // try to create second payment
            final Invocation post = target("payments").request()
                    .buildPost(entity(// language=JSON
                            "{\n" +
                                    "\t\"Payment\":{\n" +
                                    "\t\t\"withdrawalBankAccountNumber\": 1,\n" +
                                    "\t\t\"depositBankAccountNumber\": 2,\n" +
                                    "\t\t\"funds\": \"5001\"\n" +
                                    "\t}\n" +
                                    "}", APPLICATION_JSON_TYPE));
            final Response response = post.invoke();
            assertEquals(SC_BAD_REQUEST, response.getStatus());
            assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
            assertEquals("{\"error\":{\"message\":\"Withdrawal bank account has insufficient balance for the payment\"}}", response.readEntity(String.class));
        }
        { // read first payment
            final Response response = target("payments/1").request().get();
            assertEquals(SC_OK, response.getStatus());
            assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
            final Payment actual = response.readEntity(Payment.class);
            assertEquals((Long) 1L, actual.getId());
            assertEquals(1L, actual.getWithdrawalBankAccountNumber());
            assertEquals(2L, actual.getDepositBankAccountNumber());
            assertEquals(new BigDecimal("5001.01"), actual.getFunds());
            assertNotNull(actual.getDate());
        }
        { // read first bank account
            final Response response = target("bankAccounts/1").request().get();
            assertEquals(SC_OK, response.getStatus());
            assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
            final BankAccount actual = response.readEntity(BankAccount.class);
            assertEquals((Long) 1L, actual.getId());
            assertEquals(1L, actual.getNumber());
            assertSimilar(new BigDecimal("4999.02"), actual.getBalance());
        }
        { // read second bank account
            final Response response = target("bankAccounts/2").request().get();
            assertEquals(SC_OK, response.getStatus());
            assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
            final BankAccount actual = response.readEntity(BankAccount.class);
            assertEquals((Long) 2L, actual.getId());
            assertEquals(2L, actual.getNumber());
            assertSimilar(new BigDecimal("5011.01"), actual.getBalance());
        }
    }

    private  <T extends Comparable<T>> void assertSimilar(T expected, T actual) {
        if(expected == actual) {
            return;
        }
        if (expected == null || actual == null || expected.compareTo(actual) != 0) {
            throw new ComparisonFailure(null,
                    expected != null ? expected.toString() : null,
                    actual != null ? actual.toString() : null);
        }
    }

    private void testEntityNotFound(String path) {
        final Response response = target(path).request().buildGet().invoke();
        assertEquals(SC_NOT_FOUND, response.getStatus());
        assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        assertEquals("{\"error\":{\"message\":\"No entity found\"}}", response.readEntity(String.class));
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new JettyTestContainerFactory();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MoxyJsonFeature.class);
        config.register(createMoxyJsonResolver());
        super.configureClient(config);
    }

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return WebApp.createJerseyConfig();
    }

}