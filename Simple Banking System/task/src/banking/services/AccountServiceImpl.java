package banking.services;

import banking.util.CreditCardNumberValidator;
import banking.util.PropertiesLoader;
import banking.util.RequestUserTo;

import java.util.Properties;
import java.util.function.Predicate;

public class AccountServiceImpl implements AccountService {

    private String cardNumber;
    private final CardService cardService;

    private final Properties properties;

    {
        properties = PropertiesLoader.getInstance().messages();
    }

    private final Predicate<String> isNotPresent;
    private final Predicate<String> isValid = CreditCardNumberValidator::isValid;

    public AccountServiceImpl(CardService cardService) {
        this.cardService = cardService;
        this.isNotPresent = cardService.cardNumberPresentChecker().negate();
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
        cardService.updateBalanceByCardNumber(cardNumber, income);
        log("INCOME_ADDED_TEXT");
    }

    @Override
    public void doTransfer() {
        String targetCardNumber;
        System.out.println(properties.getProperty("TRANSFER_TEXT"));
        targetCardNumber = RequestUserTo.inputTargetCardNumber();

        if (isValid.negate().test(targetCardNumber)) {
            log("CARD_NUMBER_ERROR_MSG");
        } else if (targetCardNumber.equals(cardNumber)) {
            log("SAME_ACCOUNT_ERROR_MSG");
        } else if (isNotPresent.test(targetCardNumber)) {
            log("CARD_NOT_EXISTS_ERROR_MSG");
        } else {
            long amount = RequestUserTo.inputAmount(properties.getProperty("AMOUNT_TO_TRANSFER_INPUT_REQUEST_MSG"));
            if (amount > cardService.readBalanceByCardNumber(cardNumber)) {
                log("NOT_ENOUGH_MONEY_ERROR_MSG");
            } else {
                cardService.updateBalanceByCardNumber(cardNumber, targetCardNumber, amount);
                log("SUCCESS_MSG");
            }
        }
    }

    private void log(String propertyKey) {
        System.out.println(properties.getProperty(propertyKey) + "\n");
    }

    @Override
    public void printAccountBalance() {
        System.out.printf(properties.getProperty("ACCOUNT_BALANCE_TEXT") + "\n\n", cardService.readBalanceByCardNumber(cardNumber));
    }

    @Override
    public void closeAccount() {
        cardService.deleteCardByNumber(cardNumber);
        log("ACCOUNT_CLOSED_TEXT");
    }
}
