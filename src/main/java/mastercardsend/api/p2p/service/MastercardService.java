package mastercardsend.api.p2p.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.api.core.ApiConfig;
import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.oauth.OAuthAuthentication;
import com.mastercard.api.p2p.AccountInfo;
import com.mastercard.api.p2p.PaymentTransfer;
import mastercardsend.api.p2p.model.MastercardSendPaymentTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Service communicating with Mastercard Send PaymentTransfer API
 */
@Service
public class MastercardService {
    /** Correct String input for the account URI scheme. For PAN accounts. **/
    public static final String PAN = "pan";
    /** Correct String input for the account URI scheme. For generic account numbers. **/
    public static final String ACCOUNT_NUMBER = "raw";
    /** Correct String input for the account URI scheme. For account identifiers. **/
    public static final String ACCOUNT_ID = "id";
    /** Correct String input for the account URI scheme. For account references. **/
    public static final String ACCOUNT_REFERENCE = "acct-ref";
    /** Correct String input for the account URI scheme. For consumer reference identifiers. **/
    public static final String CONSUMER_REFERENCE = "consumer-ref";
    /** Correct String input for the account URI scheme. For temporary account tokens. **/
    public static final String ACCOUNT_TOKEN = "acct-token";
    /** Correct String input for the account URI scheme. For Mastercard-generated identfiers. **/
    public static final String NON_CARD_TOKEN = "non-card-token";
    /** Correct String input for the account URI scheme. For IBAN. **/
    public static final String IBAN = "iban";
    /** Correct String input for the account URI scheme. For Faster Payments Service accounts. **/
    public static final String FASTER_PAYMENTS_SERVICE = "fps-acct";
    /** Correct String input for the payment type. For business paymentTransfers. **/
    public static final String PERSON_TO_PERSON = "P2P";
    /** Correct String input for the payment type. For government paymentTransfers. **/
    public static final String ACCOUNT_TO_ACCOUNT = "A2A";
    /** Correct String input for the payment type. For credit card bill payments. **/
    public static final String CREDIT_CARD_BILLPAYMENT = "CBP";
    public static final String CREDIT = "CREDIT";
    public static final String DEBIT = "DEBIT";
    public static final String PREPAID = "PREPAID";
    public static final String DEPOSIT_ACCOUNT = "DEPOSIT_ACCOUNT";
    public static final String MOBILE_MONEY_ACCOUNT = "MOBILE_MONEY_ACCOUNT";
    public static final String CASH = "CASH";
    // Most recent error message
    private static String error;
    // Most recent request
    private static String request;

    /**
     * Initiate SDK authentication.
     * @param env Environment from which property details are obtained
     * @throws IOException
     */
    @Autowired
    public MastercardService(Environment env) throws IOException {
        String consumerKey = env.getProperty("consumerKey");
        String keyAlias = env.getProperty("keyAlias");
        String keyPassword = env.getProperty("keyPassword");
        InputStream is = new FileInputStream(env.getProperty("p12PrivateKey"));

        ApiConfig.setAuthentication(new OAuthAuthentication(consumerKey, is, keyAlias, keyPassword));
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);
    }

    /**
     * Initiate the Payment Transfer API call to get the recipient account information.
     * Specifically checks whether the recipient is able to receive funds.
     * @param paymentTransfer PaymentTransfer Spring model containing the transfer details
     * @return True if the the recipient can receive funds, false if not
     */
    public boolean isEligible(MastercardSendPaymentTransfer paymentTransfer) {
        try {
            RequestMap map = new RequestMap();
            map.set("partnerId", paymentTransfer.getPartnerId());
            map.set("account_info.account_uri", paymentTransfer.getRecipientAccountUri());
            map.set("account_info.amount", paymentTransfer.getAmount());
            map.set("account_info.currency", paymentTransfer.getCurrency());
            map.set("account_info.payment_type", paymentTransfer.getPaymentType());
            AccountInfo accountInfo = new AccountInfo(map).read(); // API call

            boolean eligible = Boolean.parseBoolean((String) accountInfo.get("account_info.receiving_eligibility.eligible")); // check eligibility
            if (!eligible) {
                error = (String) accountInfo.get("account_info.receiving_eligibility.reason_description");
            }
            return eligible;
        } catch (ApiException e) {
            error = e.getMessage();
            printErrors(e);
            return false;
        }
    }

    /**
     * Initiate the Disbursements API call to push a paymentTransfer.
     * @param paymentTransfer Disbursement Spring model containing the paymentTransfer details
     * @return Response containing all the paymentTransfer details if the paymentTransfer was successfully pushed, null if failed
     */
    public static PaymentTransfer create(MastercardSendPaymentTransfer paymentTransfer) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            RequestMap map = new RequestMap();
            map.set("partnerId", paymentTransfer.getPartnerId());
            map.set("payment_transfer.transfer_reference", UUID.randomUUID().toString());
            map.set("payment_transfer.funding_source", paymentTransfer.getFundingSource());
            map.set("payment_transfer.payment_type", paymentTransfer.getPaymentType());
            map.set("payment_transfer.amount", paymentTransfer.getAmount());
            map.set("payment_transfer.currency", paymentTransfer.getCurrency());
            map.set("payment_transfer.sender_account_uri", paymentTransfer.getSenderAccountUri());
            map.set("payment_transfer.sender.first_name", paymentTransfer.getSenderFirstName());
            map.set("payment_transfer.sender.last_name", paymentTransfer.getSenderLastName());
            map.set("payment_transfer.sender.address.line1", paymentTransfer.getSenderAddressLine1());
            map.set("payment_transfer.sender.address.city", paymentTransfer.getSenderCity());
            map.set("payment_transfer.sender.address.postal_code", paymentTransfer.getSenderPostalCode());
            map.set("payment_transfer.sender.address.country_subdivision", paymentTransfer.getSenderCountrySubdivision());
            map.set("payment_transfer.sender.address.country", paymentTransfer.getSenderCountry());
            map.set("payment_transfer.recipient_account_uri", paymentTransfer.getRecipientAccountUri());
            map.set("payment_transfer.recipient.first_name", paymentTransfer.getRecipientFirstName());
            map.set("payment_transfer.recipient.last_name", paymentTransfer.getRecipientLastName());
            map.set("payment_transfer.recipient.address.line1", paymentTransfer.getRecipientAddressLine1());
            map.set("payment_transfer.recipient.address.city", paymentTransfer.getRecipientCity());
            map.set("payment_transfer.recipient.address.postal_code", paymentTransfer.getRecipientPostalCode());
            if (paymentTransfer.getRecipientNameOnAccount() != null) { // for when payment is transferred into an account and not a card
                map.set("payment_transfer.recipient.name_on_account", paymentTransfer.getRecipientNameOnAccount());
            }
            request = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map); // to display on page

            PaymentTransfer response = PaymentTransfer.create(map); // API call

            String responseString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);

            System.out.println("Request:\n" + request);
            System.out.println("\nResponse:\n" + responseString);
            System.out.println("\nPayment transfer success!");

            return response;
        } catch (JsonProcessingException e) {
            System.err.println("Could not convert request to JSON.");
            return null;
        } catch (ApiException e) {
            error = e.getMessage();
            printErrors(e);
            return null;
        }
    }

    /**
     * Print errors to the console.
     * @param e
     */
    private static void printErrors(ApiException e) {
        System.err.println("HttpStatus: " + e.getHttpStatus());
        System.err.println("Message: " + e.getMessage());
        System.err.println("ReasonCode: " + e.getReasonCode());
        System.err.println("Source: " + e.getSource());
        System.err.println("Request:\n" + request);
    }

    /**
     * Return the last received error message.
     * @return The last received error message
     */
    public String getError() {
        return error;
    }


    /**
     * Return the last request.
     * @return The last request submitted
     */
    public String getRequest() {
        return request;
    }
}
