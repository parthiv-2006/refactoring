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
        int totalAmount = 0;
        int volumeCredits = 0;
        // FIX: Variable 'result' should be declared final.
        final StringBuilder result =
                new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        // FIX: Variable 'frmt' should be declared final.
        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        for (final Performance p : invoice.getPerformances()) {
            // add volume credits
            // FIX: Use getter from Performance.java
            volumeCredits += Math.max(p.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
            // add extra credit for every five comedy attendees
            // FIX: 'if' construct must use '{}'.
            // FIX: Use getters from Play.java and Performance.java
            // REFACTOR: (Task 2.1) Inlined 'play' variable
            if ("comedy".equals(getPlay(p).getType())) {
                volumeCredits += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }

            // print line for this order
            // FIX: '100' is a magic number.
            // REFACTOR: (Task 2.1) Inlined 'play' and 'thisAmount' variables
            result.append(String.format("  %s: %s (%s seats)%n",
                    getPlay(p).getName(),
                    frmt.format(getAmount(p) / Constants.PERCENT_FACTOR),
                    p.getAudience()));
            // REFACTOR: (Task 2.1) Inlined 'thisAmount' variable
            totalAmount += getAmount(p);
        }
        // FIX: '100' is a magic number.
        result.append(String.format("Amount owed is %s%n", frmt.format(totalAmount / Constants.PERCENT_FACTOR)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    // --- REFACTORED METHOD (Task 2.1, Step 4) ---
    // REFACTOR: Removed 'play' parameter
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

    // --- NEW HELPER METHOD (Task 2.1, Step 4) ---
    // FIX: Renamed 'p' to 'performance' for CheckStyle
    private Play getPlay(Performance performance) {
        // FIX: Use getter from Performance.java
        return plays.get(performance.getPlayID());
    }
    // ----------------------------------------------------
}
