package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    // FIX: Made private and final
    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        // REFACTOR: (Task 2.4) Moved totalAmount calculation to its own method
        // REFACTOR: (Task 2.4) Moved volumeCredits calculation to its own method
        // FIX: Variable 'result' should be declared final.
        final StringBuilder result =
                new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        // REFACTOR: (Task 2.4) This is the new loop, only for building the string
        for (final Performance p : invoice.getPerformances()) {
            // print line for this order
            // REFACTOR: (Task 2.3) Use new usd() helper method
            result.append(String.format("  %s: %s (%s seats)%n",
                    getPlay(p).getName(),
                    // REFACTOR: (Task 2.3) Call usd()
                    usd(getAmount(p)),
                    p.getAudience()));
        }

        // REFACTOR: (Task 2.4) Call new helper methods
        result.append(String.format("Amount owed is %s%n", usd(getTotalAmount())));
        result.append(String.format("You earned %s credits%n", getTotalVolumeCredits()));
        return result.toString();
    }

    // --- NEW HELPER METHOD (Task 2.4) ---
    private int getTotalAmount() {
        int result = 0;
        for (final Performance p : invoice.getPerformances()) {
            result += getAmount(p);
        }
        return result;
    }

    // --- NEW HELPER METHOD (Task 2.4) ---
    private int getTotalVolumeCredits() {
        int result = 0;
        for (final Performance p : invoice.getPerformances()) {
            result += getVolumeCredits(p);
        }
        return result;
    }

    // --- REFACTORED METHOD (Task 2.1, Step 4) ---
    private int getAmount(Performance performance) {
        // FIX: Renamed 'thisAmount' to 'result'
        int result = 0;
        // REFACTOR: Call getPlay() internally
        // FIX: Use getter from Play.java
        switch (getPlay(performance).getType()) {
            case "tragedy":
                // FIX: '40000' is a magic number.
                result = Constants.TRAGEDY_BASE_AMOUNT;
                // FIX: Use getter from Performance.java
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    // FIX: '1000' and '30' are magic numbers.
                    // FIX: Use getter from Performance.java
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                // FIX: Use getter from Performance.java
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            // FIX: Use getter from Performance.java
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                // FIX: Use getter from Performance.java
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                // REFACTOR: Call getPlay() internally
                // FIX: Use getter from Play.java
                throw new RuntimeException(String.format("unknown type: %s", getPlay(performance).getType()));
        }
        return result;
    }

    // --- NEW HELPER METHOD (Task 2.2) ---
    private int getVolumeCredits(Performance performance) {
        // FIX: Renamed local var to 'result' (Step 9)
        int result = 0;
        // add volume credits
        result += Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        // add extra credit for every five comedy attendees
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    // --- NEW HELPER METHOD (Task 2.3) ---
    private String usd(int amountInCents) {
        // FIX: Renamed parameter to be descriptive
        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        return frmt.format(amountInCents / Constants.PERCENT_FACTOR);
    }
    // ------------------------------------

    // --- NEW HELPER METHOD (Task 2.1, Step 4) ---
    private Play getPlay(Performance performance) {
        // FIX: Use getter from Performance.java
        return plays.get(performance.getPlayID());
    }
}
