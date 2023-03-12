package banking.services;

import banking.util.CreditCardNumberValidator;
import banking.util.PropertiesLoader;
import banking.util.RequestUserTo;

import java.util.Properties;
import java.util.function.Predicate;

import static banking.services.TransferResult.CARD_NOT_EXISTS_ERROR;
import static banking.services.TransferResult.CARD_NUMBER_ERROR;

public class AccountServiceImpl implements AccountService {

    private String cardNumber;
    private final CardService cardService;

    private final Properties properties;

    {
        properties = PropertiesLoader.getInstance().messages();
    }

    public AccountServiceImpl(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public boolean login() {
        this.cardNumber = RequestUserTo.inputCardInformation(properties.getProperty("USER_CARD_NUMBER_INPUT_REQUEST_MSG"));
        String pin = RequestUserTo.inputCardInformation(properties.getProperty("USER_PIN_INPUT_REQUEST_MSG"));
        return cardService.isCardWithCardNumberAndPinAvailable(cardNumber, pin);
    }


    @Override
    public void addIncome() {
        long income = RequestUserTo.inputAmount(properties.getProperty("INCOME_INPUT_REQUEST_MSG"));
        cardService.addIncome(cardNumber, income);
        log("INCOME_ADDED_TEXT");
    }

    @Override
    public void doTransfer() {
        System.out.println(properties.getProperty("TRANSFER_TEXT"));
        String targetCardNumber = RequestUserTo.inputTargetCardNumber();
        TransferResult result;
        result = getTransferResult(targetCardNumber);
        log(result.name().concat("_MSG"));
    }

    private TransferResult getTransferResult(String targetCardNumber) {
        final Predicate<String> isValid = CreditCardNumberValidator::isValid;
        TransferResult result;
        if (isValid.negate().test(targetCardNumber)) {
            result = CARD_NUMBER_ERROR;
        } else if (cardService.isCardNumberPresent().negate().test(targetCardNumber)) {
            result = CARD_NOT_EXISTS_ERROR;
        } else {
            long amount = RequestUserTo.inputAmount(properties.getProperty("AMOUNT_TO_TRANSFER_INPUT_REQUEST_MSG"));
            result = cardService.transfer(amount, cardNumber, targetCardNumber);
        }
        return result;
    }

    private void log(String propertyKey) {
        System.out.println(properties.getProperty(propertyKey) + "\n");
    }

    @Override
    public void printAccountBalance() {
        System.out.printf(properties.getProperty("ACCOUNT_BALANCE_TEXT") + "\n\n", cardService.findBalanceByCardNumber(cardNumber));
    }

    @Override
    public void closeAccount() {
        cardService.deleteCardByNumber(cardNumber);
        log("ACCOUNT_CLOSED_TEXT");
    }

    @Override
    public void createAccount() {
        cardService.createCard();
    }
}
