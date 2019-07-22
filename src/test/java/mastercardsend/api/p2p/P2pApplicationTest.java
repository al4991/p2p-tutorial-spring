package mastercardsend.api.p2p;

import com.fasterxml.jackson.databind.ObjectMapper;
import mastercardsend.api.p2p.model.MastercardSendPaymentTransfer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.ResourceUtils.getFile;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@PropertySource("classpath:application.properties")
public class P2pApplicationTest {
    public static final String PAYMENT_TRANSFER_JSON_FILE = "src/test/resources/MastercardSendPaymentTransfer";
    public static final String PAYMENT_TRANSFER_JSON_FILE_FOR_FORM = "src/test/resources/MastercardSendPaymentTransferForm";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Value("${partnerId}")
    private String partnerId;

    @Test
    public void testCreatePaymentTransferMissingPartnerId() throws Exception {
        MastercardSendPaymentTransfer paymentTransfer = getMastercardSendPaymentTransfer(PAYMENT_TRANSFER_JSON_FILE);
        mvc.perform(post("/createPaymentTransfer")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(paymentTransfer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatePaymentTransferIneligibleRecipientAccount() throws Exception {
        MastercardSendPaymentTransfer paymentTransfer = getMastercardSendPaymentTransfer(PAYMENT_TRANSFER_JSON_FILE);
        paymentTransfer.setPartnerId(partnerId);
        paymentTransfer.setRecipientAccountUri("pan:5432123456789012;exp=2099-02;cvc=123");
        mvc.perform(post("/createPaymentTransfer")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(paymentTransfer)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testCreatePaymentTransferSuccessWithForm() throws Exception {
        MastercardSendPaymentTransfer paymentTransfer = getMastercardSendPaymentTransfer(PAYMENT_TRANSFER_JSON_FILE_FOR_FORM);
        paymentTransfer.setPartnerId(partnerId);
        mvc.perform(post("/submitForm").flashAttr("paymentTransfer", paymentTransfer)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.flash().attribute("success", "Payment for Jane Smith was successfully made!"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testCreatePaymentTransferSuccess() throws Exception {
        MastercardSendPaymentTransfer paymentTransfer = getMastercardSendPaymentTransfer(PAYMENT_TRANSFER_JSON_FILE);
        paymentTransfer.setPartnerId(partnerId);
        mvc.perform(post("/createPaymentTransfer")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(paymentTransfer)))
                .andExpect(status().isOk());
    }

    private MastercardSendPaymentTransfer getMastercardSendPaymentTransfer(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(getFile(filePath), MastercardSendPaymentTransfer.class);
    }

}