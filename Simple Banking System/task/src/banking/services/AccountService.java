package banking.services;

import java.io.IOException;

public interface AccountService {

    void addIncome();
    void doTransfer() throws IOException;
    void printAccountBalance();
    void closeAccount();
}
