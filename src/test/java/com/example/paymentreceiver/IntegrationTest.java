package com.example.paymentreceiver;

import com.example.paymentreceiver.controllers.PaymentController;
import com.example.paymentreceiver.dto.CardDTO;
import com.example.paymentreceiver.dto.CardHolderDTO;
import com.example.paymentreceiver.dto.PaymentDTO;
import com.example.paymentreceiver.dto.ResponseHolder;
import com.example.paymentreceiver.models.Payment;
import com.example.paymentreceiver.repositories.PaymentsRepository;
import com.example.paymentreceiver.service.CryptoService;
import com.example.paymentreceiver.util.MaskUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Value("audit.log")
    private String auditFilePath;

    @Autowired
    private PaymentsRepository paymentRepository;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private PaymentController paymentController;

    private TestRestTemplate restTemplate;
    private DateTimeFormatter dateTimeFormatter;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        baseUrl = "http://localhost:" + port;
        dateTimeFormatter = DateTimeFormatter.ofPattern("MMyy");
    }

    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(Path.of(auditFilePath));
        paymentRepository.deleteAll();
    }

    private PaymentDTO createCorrectPaymentDTO() {
        final int invoiceId1 = (int) (Math.random() * 100000);
        LocalDate expiryDateTime1 = LocalDate.now().plusMonths(1);
        return PaymentDTO.builder()
                .amount(999)
                .invoice(invoiceId1)
                .currency("USD")
                .cardHolder(CardHolderDTO.builder()
                        .email("pre@gmail.com")
                        .name("FirstName LastName")
                        .build())
                .card(CardDTO.builder()
                        .pan("4073132327345026")
                        .expiryDate(expiryDateTime1.format(dateTimeFormatter))
                        .cvv("447")
                        .build())
                .build();
    }

    private PaymentDTO createIncorrectPaymentDTO() {
        final int invoiceId2 = -23231;
        LocalDate expiryDateTime2 = LocalDate.now().minusYears(1);
        return PaymentDTO.builder()
                .amount(-11)
                .invoice(invoiceId2)
                .currency("")
                .card(CardDTO.builder()
                        .cvv("cvv")
                        .pan("")
                        .expiryDate(expiryDateTime2.format(dateTimeFormatter))
                        .build())
                .cardHolder(CardHolderDTO.builder()
                        .email("email")
                        .name("")
                        .build())
                .build();
    }

    @Test
    public void savePaymentOk() throws Exception {
        PaymentDTO paymentDTO1 = createCorrectPaymentDTO();

        final ResponseEntity<ResponseHolder> postResponseEntity =
                restTemplate.postForEntity(baseUrl + "/pay", paymentDTO1, ResponseHolder.class);

        final List<String> auditLines = Files.readAllLines(Path.of(auditFilePath));
        MatcherAssert.assertThat(postResponseEntity.getStatusCode(), Matchers.is(HttpStatus.OK));
        MatcherAssert.assertThat(postResponseEntity.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(postResponseEntity.getBody().isApproved(), Matchers.is(true));
        MatcherAssert.assertThat(auditLines, Matchers.hasSize(1));

        final Optional<Payment> dbPaymentOptional = paymentRepository.findById(paymentDTO1.getInvoice());
        MatcherAssert.assertThat(dbPaymentOptional.isPresent(), Matchers.is(true));

        paymentDTO1.setInvoice(+1);
        restTemplate.postForEntity(baseUrl + "/pay", paymentDTO1, ResponseHolder.class);

        final List<String> auditLinesAfterSecondPost = Files.readAllLines(Path.of(auditFilePath));
        MatcherAssert.assertThat(auditLinesAfterSecondPost, Matchers.hasSize(2));
    }

    @Test
    public void savePaymentIncorrectDataException() throws Exception {
        PaymentDTO incorrectPaymentDTO = createIncorrectPaymentDTO();

        final ResponseEntity<ResponseHolder> postResponseEntity =
                restTemplate.postForEntity(baseUrl + "/pay", incorrectPaymentDTO, ResponseHolder.class);

        ResponseHolder responseEntityBody = postResponseEntity.getBody();
        MatcherAssert.assertThat(postResponseEntity.getStatusCode(), Matchers.is(HttpStatus.BAD_REQUEST));
        MatcherAssert.assertThat(responseEntityBody, Matchers.notNullValue());
        MatcherAssert.assertThat(responseEntityBody.isApproved(), Matchers.is(false));
    }

    @Test
    public void savePaymentWithTheSameInvoiceNum() throws Exception {
        PaymentDTO paymentDTO = createCorrectPaymentDTO();
        Payment entity = paymentController.convertToPayments(paymentDTO);
        Payment encoded = cryptoService.encode(entity);
        paymentRepository.save(encoded);

        final ResponseEntity<PaymentDTO> paymentResponseEntity = restTemplate
                .getForEntity(baseUrl + "/pay/{invoice}",
                        PaymentDTO.class, paymentDTO.getInvoice());
        final ResponseEntity<ResponseHolder> transactionResponseEntity = restTemplate
                .postForEntity(baseUrl + "/pay", paymentDTO, ResponseHolder.class);

        ResponseHolder getResponseEntityBody = transactionResponseEntity.getBody();
        MatcherAssert.assertThat(transactionResponseEntity.getStatusCode(), Matchers.is(HttpStatus.CONFLICT));
        MatcherAssert.assertThat(getResponseEntityBody.isApproved(), Matchers.is(false));
        MatcherAssert.assertThat(getResponseEntityBody.getErrors().size(), Matchers.is(1));

        Map<String, String> errors = transactionResponseEntity.getBody().getErrors();
        MatcherAssert.assertThat(errors, Matchers.hasKey("invoice"));
        MatcherAssert.assertThat(errors, Matchers.hasValue("payment with invoice already exists"));
    }

    @Test
    public void getPaymentNotFound() {
        int id = 77;
        final ResponseEntity<ResponseHolder> getResponseEntity =
                this.restTemplate.getForEntity(
                        baseUrl + "/pay/{id}", ResponseHolder.class, id);

        ResponseHolder getResponseEntityBody = getResponseEntity.getBody();
        MatcherAssert.assertThat(getResponseEntity.getStatusCode(), Matchers.is(HttpStatus.NOT_FOUND));
        MatcherAssert.assertThat(getResponseEntityBody.isApproved(), Matchers.is(false));
        MatcherAssert.assertThat(getResponseEntityBody.getErrors().size(), Matchers.is(1));

        Map<String, String> errors = getResponseEntityBody.getErrors();
        MatcherAssert.assertThat(errors, Matchers.hasKey("invoice"));
        MatcherAssert.assertThat(errors, Matchers.hasValue("payment with invoice number not found"));
    }

    @Test
    public void getPaymentOk() {
        PaymentDTO paymentDTO = createCorrectPaymentDTO();
        paymentRepository.save(cryptoService.encode(paymentController.convertToPayments(paymentDTO)));

        final ResponseEntity<PaymentDTO> paymentResponseEntity = restTemplate
                .getForEntity(baseUrl + "/pay/{invoice}",
                        PaymentDTO.class, paymentDTO.getInvoice());

        MatcherAssert.assertThat(paymentResponseEntity.getStatusCode(), Matchers.is(HttpStatus.OK));

        final PaymentDTO paymentResponse = paymentResponseEntity.getBody();
        MatcherAssert.assertThat(paymentResponse, Matchers.notNullValue());
        MatcherAssert.assertThat(paymentResponse.getCurrency(), Matchers.is(paymentDTO.getCurrency()));
        MatcherAssert.assertThat(paymentResponse.getAmount(), Matchers.is(paymentDTO.getAmount()));
        MatcherAssert.assertThat(paymentResponse.getInvoice(), Matchers.is(paymentDTO.getInvoice()));

        final CardDTO responseCard = paymentResponse.getCard();
        MatcherAssert.assertThat(responseCard.getCvv(), Matchers.is(MaskUtils.maskCvv(paymentDTO.getCard().getCvv())));
        MatcherAssert.assertThat(responseCard.getPan(), Matchers.is(MaskUtils.maskPan(paymentDTO.getCard().getPan())));
        MatcherAssert.assertThat(responseCard.getExpiryDate(), Matchers.is(MaskUtils.maskExpiryDate(paymentDTO.getCard().getExpiryDate())));

        final CardHolderDTO responseCardHolder = paymentResponse.getCardHolder();
        MatcherAssert.assertThat(responseCardHolder, Matchers.notNullValue());
        MatcherAssert.assertThat(responseCardHolder.getEmail(), Matchers.notNullValue());
        MatcherAssert.assertThat(responseCardHolder.getName(),
                Matchers.is(MaskUtils.maskName(paymentDTO.getCardHolder().getName())));
    }

    @Test
    public void getAllPaymentOk() {
        PaymentDTO paymentDTO1 = createCorrectPaymentDTO();
        PaymentDTO paymentDTO2 = createCorrectPaymentDTO();

        paymentRepository.save(cryptoService.encode(paymentController.convertToPayments(paymentDTO1)));
        paymentRepository.save(cryptoService.encode(paymentController.convertToPayments(paymentDTO2)));

        ResponseEntity<List<PaymentDTO>> paymentResponseEntity = restTemplate.exchange(
                baseUrl + "/pay",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PaymentDTO>>() {
                });

        MatcherAssert.assertThat(paymentResponseEntity.getStatusCode(), Matchers.is(HttpStatus.OK));

        final List<PaymentDTO> paymentResponse = paymentResponseEntity.getBody();
        MatcherAssert.assertThat(paymentResponse, Matchers.notNullValue());
        MatcherAssert.assertThat(paymentResponse, Matchers.hasSize(2));
    }
}