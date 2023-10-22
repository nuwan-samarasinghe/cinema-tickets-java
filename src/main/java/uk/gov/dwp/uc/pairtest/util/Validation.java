package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.logging.Logger;

import static uk.gov.dwp.uc.pairtest.util.Constants.INVALID_ACCOUNT_NUMBER_ERROR_MSG;
import static uk.gov.dwp.uc.pairtest.util.Constants.INVALID_TICKET_COMBINATION_ERROR_MSG;
import static uk.gov.dwp.uc.pairtest.util.Constants.INVALID_TICKET_COUNT_ERROR_MSG;
import static uk.gov.dwp.uc.pairtest.util.Constants.TICKET_PURCHASE_LIMIT_PER_REQUEST;

/**
 * validator
 */
public interface Validation {
    Logger LOGGER = Logger.getLogger(Validation.class.getName());

    /**
     * validating the ticket purchase request
     *
     * @param ticketTypeRequests ticket purchase request data
     * @param accountId          account number
     */
    static void requestValidation(Long accountId, TicketTypeRequest... ticketTypeRequests) {
        accountNumberValidation(accountId);
        validateIsZeroCountRequests(ticketTypeRequests);
        validateIsAdultTicketAvailable(ticketTypeRequests);
        validateIsTotalTicketLimitExceeds(ticketTypeRequests);
    }

    /**
     * validate is the request has any child or infant ticket without an adult ticket
     */
    private static void validateIsAdultTicketAvailable(TicketTypeRequest[] ticketTypeRequests) {
        Arrays.stream(ticketTypeRequests)
                .filter(ticketTypeRequest -> ticketTypeRequest.getTicketType()
                        .equals(TicketTypeRequest.Type.ADULT))
                .findAny()
                .orElseThrow(() -> {
                    LOGGER.severe("Error: ticket purchase request does not have adult ticket type");
                    return new InvalidPurchaseException(INVALID_TICKET_COMBINATION_ERROR_MSG);
                });
    }

    /**
     * validating the account number
     * should be non-zero and non-negative
     */
    private static void accountNumberValidation(Long accountId) {
        if (accountId <= 0) {
            LOGGER.severe(String.format("Error: Invalid account number provided by the account no: [%s]", accountId));
            throw new InvalidPurchaseException(INVALID_ACCOUNT_NUMBER_ERROR_MSG);
        }
    }

    /**
     * if total ticket count exceeds maximum allowed quota then it will be an invalid request
     */
    private static void validateIsTotalTicketLimitExceeds(TicketTypeRequest[] ticketTypeRequests) {
        long totalTicketCount = Arrays.stream(ticketTypeRequests)
                .map(TicketTypeRequest::getNoOfTickets)
                .reduce(0, Integer::sum);
        if (totalTicketCount > TICKET_PURCHASE_LIMIT_PER_REQUEST) {
            LOGGER.severe(String
                    .format("Error: The given request has an invalid ticket count for purchase: [%s]", totalTicketCount));
            throw new InvalidPurchaseException(INVALID_TICKET_COUNT_ERROR_MSG);
        }
    }

    /**
     * if there are any ticket type that has 0 ticket count it will be an invalid ticket request
     */
    private static void validateIsZeroCountRequests(TicketTypeRequest[] ticketTypeRequests) {
        Arrays.stream(ticketTypeRequests)
                .filter(ticketTypeRequest -> ticketTypeRequest.getNoOfTickets() == 0)
                .findAny()
                .ifPresent(ticketTypeRequest -> {
                    LOGGER.severe(String.format("Error: The given request has invalid ticket numbers to buy for ticket type: [%s], ticket count: [%s]",
                            ticketTypeRequest.getTicketType(), ticketTypeRequest.getNoOfTickets()));
                    throw new InvalidPurchaseException(INVALID_TICKET_COUNT_ERROR_MSG);
                });
    }
}
