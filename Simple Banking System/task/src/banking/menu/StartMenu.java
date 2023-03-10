package banking.menu;

import banking.services.CardService;

import java.io.IOException;

public class StartMenu extends Menu {

    public StartMenu(CardService cardService) {
        super(cardService);
    }

    @Override
    protected boolean process(MenuItem menuItem) throws IOException {

        switch (menuItem) {
            case CREATE_AN_ACCOUNT:
                cardService.createCard();
                break;
            case LOG_INTO_ACCOUNT:
                new AccountMenu(cardService).process();
                break;
            case EXIT:
                exit();
                return false;
            default:
        }

        return true;
    }

    @Override
    protected MenuItem getMenuItem(int choice) {
        return getMenuItem(choice, MenuItem.start());
    }

    @Override
    protected int displayMenu() {
        return displayOptions(MenuItem.start());
    }
}
