## Payment Receiver

This project is implement a backend for a simple payment gateway that accepts credit card payments.

### Technologies Used

- Java 8
- Spring Boot (with Spring AOP, Spring Data JPA, Spring MVC, Spring Test)
- Mockito
- SLF4j
- H2

### Getting Started

To get started with this project, you will need to have the following installed:

- Java Development Kit (JDK) version 8 or later
- Maven

Once you have Java and Maven installed, you can clone the repository and run the project using the following commands:

- git clone https://github.com/SemeniukRuslan/PaymentReceiver
- cd PaymentReceiver
- mvn spring-boot:run

This will start the application on port 8081. You can access it by visiting http://localhost:8081 in Postman.

### Features

The gateway accepts credit card payments for processing and stores transaction history while encoding data when written
to the database.

Methods url actions:

- POST /pay - create a new transaction
- GET /pay/{id} - find transaction with invoice number
- GET /pay - find all transactions

### Validation

Before the transaction is approved, the following conditions must be met:

* all fields should be present;
* amount should be a positive integer;
* email should have a valid format;
* PAN should be 16 digits long and pass a Luhn check;
* expiry date should be valid and not in the past.

### Audit

The gateway writes the transaction to the local `audit.log` file in JSON format, one record per line.
It is necessary for finding in case of loss of information on other media.

### Register a new transaction

To register a new transaction, send a POST request to the /pay endpoint with the following JSON payload:

```json
{
  "invoice": 1239,
  "amount": 2000,
  "currency": "EUR",
  "cardHolder": {
    "email": "testEmail@gmail.com",
    "name": "testUsername"
  },
  "card": {
    "pan": "4073132327345026",
    "expiryDate": "2927",
    "cvv": "983"
  }
}
```

Successful transaction:

```json
{
  "approved": "true"
}
```

Declined transaction with incorrect data example:

```json
{
  "approved": "false",
  "errors": {
    "amount": "amount should be more than zero",
    "card.expiryDate": "invalid expire date",
    "cardHolder.name": "name should be not empty",
    "invoice": "invoice should be more than zero"
  }
}
```

### Find transaction

To find transaction with invoice number, send a GET request to the /pay/{numberInvoice} endpoint without JSON payload.
Successful transaction response and audit:

```json
{
  "invoice": 1239,
  "amount": 2000,
  "currency": "EUR",
  "cardholder": {
    "name": "***********",
    "email": "testEmail@gmail.com"
  },
  "card": {
    "pan": "************5026",
    "expiry": "****",
    "cvv": "***"
  }
}
```

### Find all transactions

To find all transactions, send a GET request to the /pay endpoint without JSON payload.
Successful all transactions response and audit:

```yaml
{
  "invoice": 678,
  "amount": 357,
  "currency": "GBP",
  "cardholder": {
    "name": "***********",
    "email": "testEmail2@gmail.com"
  },
  "card": {
    "pan": "************1293",
    "expiry": "****",
    "cvv": "***"
  }
},
  {
    "invoice": 1239,
    "amount": 2000,
    "currency": "EUR",
    "cardholder": {
      "name": "***********",
      "email": "testEmai@gmail.com"
    },
    "card": {
      "pan": "************5026",
      "expiry": "****",
      "cvv": "***"
    }
  }
```

### Future Plans

* add a library ReactJS to better display information about the payment system;
* update class MaskUtils;
* create class authorization and login system with JWT token.