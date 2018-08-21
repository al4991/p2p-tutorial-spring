package mastercardsend.api.p2p.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import mastercardsend.api.p2p.service.MastercardService;

import java.util.LinkedList;
import java.util.List;

/**
 * Request Spring model that contains required information for making Payment Transfer API calls.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MastercardSendPaymentTransfer {
    private String partnerId;
    private String transferReference;
    private String paymentType = MastercardService.PERSON_TO_PERSON; // as defined during the onboarding process
    private String amount;
    private String currency;
    private String senderUriScheme = "PAN"; // There are multiple possible values as listed in the PaymentTransfer API documentation. For the purposes of this reference application, we will only use PAN.
    private String senderUriIdentifier;
    private String senderUriExpYear;
    private String senderUriExpMonth;
    private String senderUriCvc;
    private String senderAccountUri;
    private String senderFirstName;
    private String senderLastName;
    private String senderAddressLine1;
    private String senderCity;
    private String senderPostalCode;
    private String senderCountrySubdivision;
    private String senderCountry;
    private String fundingSource;
    private String recipientUriScheme;
    private String recipientUriIdentifier;
    private String recipientUriExpYear;
    private String recipientUriExpMonth;
    private String recipientUriCvc;
    private String recipientAccountUri;
    private String recipientFirstName;
    private String recipientLastName;
    private String recipientAddressLine1;
    private String recipientCity;
    private String recipientPostalCode;
    private String recipientNameOnAccount;

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getTransferReference() {
        return transferReference;
    }

    public void setTransferReference(String transferReference) {
        this.transferReference = transferReference;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSenderUriScheme() {
        return senderUriScheme;
    }

    public void setSenderUriScheme(String senderUriScheme) {
        this.senderUriScheme = senderUriScheme;
    }

    public String getSenderUriIdentifier() {
        return senderUriIdentifier;
    }

    public void setSenderUriIdentifier(String senderUriIdentifier) {
        this.senderUriIdentifier = senderUriIdentifier;
    }

    public String getSenderUriExpYear() {
        return senderUriExpYear;
    }

    public void setSenderUriExpYear(String senderUriExpYear) {
        this.senderUriExpYear = senderUriExpYear;
    }

    public String getSenderUriExpMonth() {
        return senderUriExpMonth;
    }

    public void setSenderUriExpMonth(String senderUriExpMonth) {
        this.senderUriExpMonth = senderUriExpMonth;
    }

    public String getSenderUriCvc() {
        return senderUriCvc;
    }

    public void setSenderUriCvc(String senderUriCvc) {
        this.senderUriCvc = senderUriCvc;
    }

    public String getSenderAccountUri() {
        return senderAccountUri;
    }

    /**
     * Sets the recipient account URI in the correct format for the API call.
     */
    public void setSenderAccountUri() {
        String scheme = getFormattedUriScheme(senderUriScheme);
        if (scheme == null) return;
        senderAccountUri = scheme + ":" + senderUriIdentifier + ";exp=" + senderUriExpYear + "-" + senderUriExpMonth;
        // add CVC info if given
        if (senderUriCvc != null) {
            senderAccountUri = senderAccountUri + ";cvc=" + senderUriCvc;
        }
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
    }

    public String getSenderAddressLine1() {
        return senderAddressLine1;
    }

    public void setSenderAddressLine1(String senderAddressLine1) {
        this.senderAddressLine1 = senderAddressLine1;
    }

    public String getSenderCity() {
        return senderCity;
    }

    public void setSenderCity(String senderCity) {
        this.senderCity = senderCity;
    }

    public String getSenderPostalCode() {
        return senderPostalCode;
    }

    public void setSenderPostalCode(String senderPostalCode) {
        this.senderPostalCode = senderPostalCode;
    }

    public String getSenderCountrySubdivision() {
        return senderCountrySubdivision;
    }

    public void setSenderCountrySubdivision(String senderCountrySubdivision) {
        this.senderCountrySubdivision = senderCountrySubdivision;
    }

    public String getSenderCountry() {
        return senderCountry;
    }

    public void setSenderCountry(String senderCountry) {
        this.senderCountry = senderCountry;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = getFormattedFundingSource(fundingSource);
    }

    public String getRecipientUriScheme() {
        return recipientUriScheme;
    }

    public void setRecipientUriScheme(String recipientUriScheme) {
        this.recipientUriScheme = recipientUriScheme;
    }

    public String getRecipientUriIdentifier() {
        return recipientUriIdentifier;
    }

    public void setRecipientUriIdentifier(String recipientUriIdentifier) {
        this.recipientUriIdentifier = recipientUriIdentifier;
    }

    public String getRecipientUriExpYear() {
        return recipientUriExpYear;
    }

    public void setRecipientUriExpYear(String recipientUriExpYear) {
        this.recipientUriExpYear = recipientUriExpYear;
    }

    public String getRecipientUriExpMonth() {
        return recipientUriExpMonth;
    }

    public void setRecipientUriExpMonth(String recipientUriExpMonth) {
        this.recipientUriExpMonth = recipientUriExpMonth;
    }

    public String getRecipientUriCvc() {
        return recipientUriCvc;
    }

    public void setRecipientUriCvc(String recipientUriCvc) {
        this.recipientUriCvc = recipientUriCvc;
    }

    public String getRecipientAccountUri() {
        return recipientAccountUri;
    }

    public void setRecipientAccountUri(String recipientAccountUri) {
        this.recipientAccountUri = recipientAccountUri;
    }

    /**
     * Sets the recipient account URI in the correct format for the API call.
     */
    public void setRecipientAccountUri() {
        String scheme = getFormattedUriScheme(recipientUriScheme);
        if (scheme == null) {
            System.err.println("Recipient account URI could not be set because of invalid URI scheme.");
            return;
        } else if (scheme.equals(MastercardService.PAN)) {
            recipientAccountUri = scheme + ":" + recipientUriIdentifier + ";exp=" + recipientUriExpYear + "-" + recipientUriExpMonth;
            // add CVC info if given
            if (recipientUriCvc != null) {
                recipientAccountUri = recipientAccountUri + ";cvc=" + recipientUriCvc;
            }
        } else {
            recipientAccountUri = scheme + ":" + recipientUriIdentifier;
        }
    }

    public String getRecipientFirstName() {
        return recipientFirstName;
    }

    public void setRecipientFirstName(String recipientFirstName) {
        this.recipientFirstName = recipientFirstName;
    }

    public String getRecipientLastName() {
        return recipientLastName;
    }

    public void setRecipientLastName(String recipientLastName) {
        this.recipientLastName = recipientLastName;
    }

    public String getRecipientAddressLine1() {
        return recipientAddressLine1;
    }

    public void setRecipientAddressLine1(String recipientAddressLine1) {
        this.recipientAddressLine1 = recipientAddressLine1;
    }

    public String getRecipientCity() {
        return recipientCity;
    }

    public void setRecipientCity(String recipientCity) {
        this.recipientCity = recipientCity;
    }

    public String getRecipientPostalCode() {
        return recipientPostalCode;
    }

    public void setRecipientPostalCode(String recipientPostalCode) {
        this.recipientPostalCode = recipientPostalCode;
    }

    public String getRecipientNameOnAccount() {
        return recipientNameOnAccount;
    }

    public void setRecipientNameOnAccount(String recipientNameOnAccount) {
        this.recipientNameOnAccount = recipientNameOnAccount;
    }

    /**
     * Returns the correct corresponding value of the URI scheme for making the PaymentTransfer API call.
     * @param scheme the URI scheme as displayed on the form
     * @return The corresponding value to include in the request
     */
    private String getFormattedUriScheme(String scheme) {
        switch (scheme.toLowerCase()) {
            case "pan":
                return MastercardService.PAN;
            case "account token":
                return MastercardService.ACCOUNT_TOKEN;
            case "iban":
                return MastercardService.IBAN;
            case "faster payments service":
                return MastercardService.FASTER_PAYMENTS_SERVICE;
            default:
                break;
        }
        System.err.println("Invalid URI Scheme.");
        return null;
    }

    /**
     * Returns the correct corresponding value of the funding source for making the PaymentTransfer API call.
     * @param source the funding source as displayed on the form
     * @return The corresponding value to include in the request
     */
    private String getFormattedFundingSource(String source) {
        switch (source.toLowerCase()) {
            case "credit":
                return MastercardService.CREDIT;
            case "debit":
                return MastercardService.DEBIT;
            case "prepaid":
                return MastercardService.PREPAID;
            case "deposit account":
                return MastercardService.DEPOSIT_ACCOUNT;
            case "mobile money account":
                return MastercardService.MOBILE_MONEY_ACCOUNT;
            case "cash":
                return MastercardService.CASH;
            default:
                break;
        }
        System.err.println("Invalid funding source.");
        return null;
    }

    /**
     * Get all the currently valid recipient URI schemes.
     * @return the recipient URI schemes
     */
    public static List<String> getRecipientUriSchemes() {
        List<String> recipientUriSchemes = new LinkedList<String>();
        recipientUriSchemes.add("PAN");
        recipientUriSchemes.add("Account Token");
        recipientUriSchemes.add("IBAN");
        recipientUriSchemes.add("Faster Payments Service");
        return recipientUriSchemes;
    }

    /**
     * Get all the valid funding sources.
     * @return the funding sources
     */
    public static List<String> getFundingSources() {
        List<String> fundingSources = new LinkedList<String>();
        fundingSources.add("Credit");
        fundingSources.add("Debit");
        fundingSources.add("Prepaid");
        fundingSources.add("Deposit Account");
        fundingSources.add("Mobile Money Account");
        fundingSources.add("Cash");
        return fundingSources;
    }

}
