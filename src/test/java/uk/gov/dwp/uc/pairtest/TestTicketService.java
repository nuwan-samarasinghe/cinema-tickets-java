package uk.gov.dwp.uc.pairtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketSummery;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.util.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class TestTicketService {

    private TicketService ticketService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;

    @Before
    public void init() {
        ticketPaymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    public void testPurchaseTicketWithZeroAccountNumberErrorSC1() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.INVALID_ACCOUNT_NUMBER_ERROR_MSG);
        ticketService.purchaseTickets(0L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));
    }

    @Test
    public void testPurchaseTicketWithNegativeAccountNumberErrorSC2() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.INVALID_ACCOUNT_NUMBER_ERROR_MSG);
        ticketService.purchaseTickets(-1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));
    }

    @Test
    public void testPurchaseTicketWithZeroTicketCountErrorSC3() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.INVALID_TICKET_COUNT_ERROR_MSG);
        ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0));
    }

    @Test
    public void testPurchaseTicketWithMoreThan20TicketCountErrorSC4() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.INVALID_TICKET_COUNT_ERROR_MSG);
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10);
        ticketService.purchaseTickets(1L, ticketTypeRequest1, ticketTypeRequest2, ticketTypeRequest3);
    }

    @Test
    public void testPurchaseTicketOnlyWithChildErrorSC5() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.INVALID_TICKET_COMBINATION_ERROR_MSG);
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        ticketService.purchaseTickets(1L, ticketTypeRequest1);
    }

    @Test
    public void testPurchaseTicketOnlyWithInfantErrorSC6() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.INVALID_TICKET_COMBINATION_ERROR_MSG);
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);
        ticketService.purchaseTickets(1L, ticketTypeRequest1);
    }

    @Test
    public void testPurchaseTicketWithValidDataButSeatReservationFailed() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5);
        TicketSummery ticketSummery = getTicketSummery(new TicketTypeRequest[]{ticketTypeRequest1});
        doThrow(RuntimeException.class).when(seatReservationService).reserveSeat(1L, ticketSummery.seatCount());
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.GENERAL_ERROR_MSG);
        ticketService.purchaseTickets(1L, ticketTypeRequest1);
    }

    @Test
    public void testPurchaseTicketWithValidDataButPaymentFailed() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5);
        TicketSummery ticketSummery = getTicketSummery(new TicketTypeRequest[]{ticketTypeRequest1});
        doThrow(RuntimeException.class).when(ticketPaymentService).makePayment(1L, ticketSummery.cost());
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage(Constants.GENERAL_ERROR_MSG);
        ticketService.purchaseTickets(1L, ticketTypeRequest1);
    }

    @Test
    public void testPurchaseTicketOnlyAdultTicketsSuccess() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        ticketService.purchaseTickets(1L, ticketTypeRequest1, ticketTypeRequest2);
    }

    @Test
    public void testPurchaseTicketWithAdultTicketsAndChildSuccess() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        ticketService.purchaseTickets(1L, ticketTypeRequest1, ticketTypeRequest2);
    }

    @Test
    public void testPurchaseTicketWithAdultTicketsAndAllSuccess() {
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        ticketService.purchaseTickets(1L, ticketTypeRequest1, ticketTypeRequest2, ticketTypeRequest3);
    }


    @Test
    public void testGetTicketSummerySuccessWithAll() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getTicketSummery = TicketServiceImpl.class.getDeclaredMethod("getTicketSummery", TicketTypeRequest[].class);
        getTicketSummery.setAccessible(true);
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        TicketSummery summery = (TicketSummery) getTicketSummery.invoke(ticketService, (Object) new TicketTypeRequest[]{ticketTypeRequest1, ticketTypeRequest2, ticketTypeRequest3});
        Assert.assertEquals("Invalid seat count", 9, summery.seatCount());
        Assert.assertEquals("Invalid seat cost", 130, summery.cost());
    }

    @Test
    public void testGetTicketSummery() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getTicketSummery = TicketServiceImpl.class.getDeclaredMethod("getTicketSummery", TicketTypeRequest[].class);
        getTicketSummery.setAccessible(true);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);
        TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        TicketSummery summery = (TicketSummery) getTicketSummery.invoke(ticketService, (Object) new TicketTypeRequest[]{ticketTypeRequest2, ticketTypeRequest3});
        Assert.assertEquals("Invalid seat count", 4, summery.seatCount());
        Assert.assertEquals("Invalid seat cost", 80, summery.cost());
    }

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
        return new TicketSummery(seatCount, cost);
    }
}
