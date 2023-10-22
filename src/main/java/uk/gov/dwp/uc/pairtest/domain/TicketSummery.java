package uk.gov.dwp.uc.pairtest.domain;

public record TicketSummery(int seatCount, int cost) {

    @Override
    public String toString() {
        return "TicketSummery{" +
                "seatCount=" + seatCount +
                ", cost=" + cost +
                '}';
    }
}
