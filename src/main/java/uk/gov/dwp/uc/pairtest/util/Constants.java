package uk.gov.dwp.uc.pairtest.util;

public class Constants {
    public static String INVALID_ACCOUNT_NUMBER_ERROR_MSG = "Invalid account number provided. Please double-check and ensure the account number is correct.";
    public static String INVALID_TICKET_COUNT_ERROR_MSG = "Invalid ticket count provided. Please enter a number between 1 and 20.";
    public static String INVALID_TICKET_COMBINATION_ERROR_MSG = "You cannot purchase child or infant tickets without at least one accompanying adult ticket.";
    public static String GENERAL_ERROR_MSG = "An error occurred while buying the tickets.";

    public static Integer TICKET_PURCHASE_LIMIT_PER_REQUEST = 20;

    public static Integer CHILD_COST = 10;
    public static Integer ADULT_COST = 20;
}
