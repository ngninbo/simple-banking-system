package banking.generator;

import banking.util.CreditCardNumberValidator;

import java.util.function.Predicate;

public class CreditCardNumberGenerator extends NumberGenerator {

    private static final Predicate<String> isValid = CreditCardNumberValidator::isValid;

    public static String generateCardNumber(long bankIdentificationNumber,
                                            long minAccountIdentifier,
                                            long maxAccountIdentifier) {
        long accountIdentifier = generate(minAccountIdentifier, maxAccountIdentifier);
        String cardNumber = String.format("%s%s", bankIdentificationNumber, accountIdentifier);

        int checkSum = 0;

        while (isValid.negate().test(cardNumber + checkSum)) {
            checkSum += 1;
        }

        return cardNumber + checkSum;
    }

    public static String cardNumber() {
        return generateCardNumber(Long.parseLong(PROPERTIES.getProperty("BANK_IDENTIFICATION_NUMBER")),
                Long.parseLong(PROPERTIES.getProperty("MIN_ACCOUNT_IDENTIFIER")),
                Long.parseLong(PROPERTIES.getProperty("MAX_ACCOUNT_IDENTIFIER")));
    }
}
