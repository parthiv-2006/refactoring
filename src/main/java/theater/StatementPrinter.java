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
            // FIX: Variable 'play' should be declared final.
            // FIX: Use getter from Performance.java
            final Play play = plays.get(p.getPlayID());

            int thisAmount = 0;
            // FIX: Use getter from Play.java
            switch (play.getType()) {
                case "tragedy":
                    // FIX: '40000' is a magic number.
                    thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                    // FIX: Use getter from Performance.java
                    if (p.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                        // FIX: '1000' and '30' are magic numbers.
                        // FIX: Use getter from Performance.java
                        thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                    }
                    break;
                case "comedy":
                    thisAmount = Constants.COMEDY_BASE_AMOUNT;
                    // FIX: Use getter from Performance.java
                    if (p.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                                + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                                // FIX: Use getter from Performance.java
                                * (p.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                    }
                    // FIX: Use getter from Performance.java
                    thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * p.getAudience();
                    break;
                default:
                    // FIX: Use getter from Play.java
                    throw new RuntimeException(String.format("unknown type: %s", play.getType()));
            }

            // add volume credits
            // FIX: Use getter from Performance.java
            volumeCredits += Math.max(p.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
            // add extra credit for every five comedy attendees
            // FIX: 'if' construct must use '{}'.
            // FIX: Use getters from Play.java and Performance.java
            if ("comedy".equals(play.getType())) {
                volumeCredits += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }

            // print line for this order
            // FIX: Use getter from Play.java
            // FIX: '100' is a magic number.
            // FIX: Use getter from Performance.java
            result.append(String.format("  %s: %s (%s seats)%n",
                    play.getName(),
                    frmt.format(thisAmount / Constants.PERCENT_FACTOR),
                    p.getAudience()));
            totalAmount += thisAmount;
        }
        // FIX: '100' is a magic number.
        result.append(String.format("Amount owed is %s%n", frmt.format(totalAmount / Constants.PERCENT_FACTOR)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }
}
