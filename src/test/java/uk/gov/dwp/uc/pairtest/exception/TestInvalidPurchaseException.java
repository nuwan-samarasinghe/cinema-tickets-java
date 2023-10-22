package uk.gov.dwp.uc.pairtest.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestInvalidPurchaseException {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testExceptionWithOnlyMessage() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("This is an exception testing message");
        throw new InvalidPurchaseException("This is an exception testing message");
    }

    @Test
    public void testExceptionWithMessageAndException() {
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("This is an exception testing message");
        throw new InvalidPurchaseException("This is an exception testing message", new Exception("Testing"));
    }

}
