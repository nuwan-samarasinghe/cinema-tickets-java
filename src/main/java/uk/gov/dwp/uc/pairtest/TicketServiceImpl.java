package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketSummery;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.util.Constants;
import uk.gov.dwp.uc.pairtest.util.Validation;

import java.util.logging.Logger;

public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = Logger.getLogger(TicketServiceImpl.class.getName());

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;


    public TicketServiceImpl(TicketPaymentService ticketPaymentService,
                             SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Should only have private methods other than the one below.
     * ticket purchasing for the given account and ticket request
     *
     * @param accountId          account id
     * @param ticketTypeRequests ticket type request
     * @throws InvalidPurchaseException throws an exception if ticket cannot be taken
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        Validation.requestValidation(accountId, ticketTypeRequests);
        try {
            TicketSummery ticketSummery = getTicketSummery(ticketTypeRequests);
            seatReservationService.reserveSeat(accountId, ticketSummery.seatCount());
            ticketPaymentService.makePayment(accountId, ticketSummery.cost());
            LOGGER.info("Successfully Reserved the tickets");
        } catch (Exception e) {
            LOGGER.severe(Constants.GENERAL_ERROR_MSG);
            throw new InvalidPurchaseException(Constants.GENERAL_ERROR_MSG, e);
        }
    }

    /**
     * get ticket summery from the request
     *
     * @param ticketTypeRequests ticket request
     * @return summery for the request
     */
    private TicketSummery getTicketSummery(TicketTypeRequest[] ticketTypeRequests) {
        int seatCount = 0;
        int cost = 0;
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            if (!ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT)) {
                seatCount = seatCount + ticketTypeRequest.getNoOfTickets();
                if (ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.ADULT)) {
                    cost = cost + ticketTypeRequest.getNoOfTickets() * Constants.ADULT_COST;
                } else {
                    cost = cost + ticketTypeRequest.getNoOfTickets() * Constants.CHILD_COST;
                }
            }
        }
        TicketSummery summery = new TicketSummery(seatCount, cost);
        LOGGER.info(String.valueOf(summery));
        return summery;
    }

}
