package mastercardsend.api.p2p.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.api.p2p.PaymentTransfer;
import mastercardsend.api.p2p.model.MastercardSendPaymentTransfer;
import mastercardsend.api.p2p.service.MastercardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaymentTransferController {
    @Autowired
    private MastercardService service;

    // Partner ID obtained and injected from application.properties
    @Value("${partnerId}")
    private String partnerId;

    /**
     * Index page displays a form to transfer payments.
     * Visit localhost:8080 to view.
     * @param model Spring model for adding attributes
     * @return index page
     */
    @GetMapping("/")
    public String index(Model model) {
        MastercardSendPaymentTransfer paymentTransfer = new MastercardSendPaymentTransfer();
        model.addAttribute("paymentTransfer", paymentTransfer);
        model.addAttribute("recipientUriSchemes", paymentTransfer.getRecipientUriSchemes());
        model.addAttribute("fundingSources", paymentTransfer.getFundingSources());
        return "index";
    }

    /**
     * Submits the form to check for recipient receiving eligibility.
     * If eligible, then the payment will be transferred.
     * @param paymentTransfer PaymentTransfer model bound to the form
     * @param redirectAttrs for flash notifications when redirecting
     * @return
     */
    @PostMapping("/submitForm")
    public String submitForm(@ModelAttribute("paymentTransfer") MastercardSendPaymentTransfer paymentTransfer,
                             RedirectAttributes redirectAttrs) {
        paymentTransfer.setPartnerId(partnerId);
        paymentTransfer.setSenderAccountUri();
        paymentTransfer.setRecipientAccountUri();

        PaymentTransfer response = service.create(paymentTransfer);

        try {
            if (response != null) {
                redirectAttrs.addFlashAttribute("request", service.getRequest()); // JSON request will be displayed
                redirectAttrs.addFlashAttribute("response", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response)); // JSON response will be displayed
                redirectAttrs.addFlashAttribute("success", "Payment for " + paymentTransfer.getRecipientFirstName() + " " + paymentTransfer.getRecipientLastName() + " was successfully made!");
                return "redirect:/";
            } else {
                redirectAttrs.addFlashAttribute("error", "Failed to create payment transfer. " + service.getError());
                return "redirect:/";
            }
        } catch (JsonProcessingException e) {
            System.err.println("Unable to convert response to JSON String.");
            return null;
        }
    }

    /**
     * Transfer payments directly. Used for testing.
     * @param paymentTransferRequest PaymentTransfer model containing the transfer details
     * @return 200 status code if the transfer was successful, 400 status code if unsuccessful
     */
    @PostMapping(value = "/createPaymentTransfer")
    public ResponseEntity createPaymentTransfer(@RequestBody MastercardSendPaymentTransfer paymentTransferRequest) {
        if (service.isEligible(paymentTransferRequest)) {
            PaymentTransfer response = service.create(paymentTransferRequest);
            if (response != null) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }
}
