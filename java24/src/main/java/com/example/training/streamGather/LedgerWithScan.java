package com.example.training.streamGather;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Gatherers;

//@formatter:off
/**
 * LedgerWithScan (Java 24)
 *
 * Real-life use of Gatherers.scan:
 *  - Input: stream of bank transactions (amount in cents).
 *  - Need: running balance after each transaction, plus alerts when balance dips.
 *
 * Why scan?
 *  - Emits a value for EVERY step (prefix accumulation), perfect for ledgers, KPIs,
 *    cumulative quotas, rolling budgets, etc.
 */
//@formatter:on
public class LedgerWithScan {

  record Transaction(LocalDate date, String id, long amountCents, String memo) {

  }

  record LedgerEntry(Transaction txn, long balanceAfter) {

  }

  public static void main(String[] args) {
    long openingBalanceCents = 125_00;   // 1.25 RON (example currency)
    long lowBalanceThreshold = 100_00;   // 1.00 RON threshold for alert

    List<Transaction> transactions = List.of(
        new Transaction(LocalDate.of(2025, 12, 1), "T001", +500_00, "Salary"),
        new Transaction(LocalDate.of(2025, 12, 2), "T002", -120_00, "Groceries"),
        new Transaction(LocalDate.of(2025, 12, 3), "T003", -50_00, "Coffee"),
        new Transaction(LocalDate.of(2025, 12, 4), "T004", -300_00, "Utilities"),
        new Transaction(LocalDate.of(2025, 12, 5), "T005", -200_00, "Rent top-up"),
        new Transaction(LocalDate.of(2025, 12, 6), "T006", +250_00, "Refund"),
        new Transaction(LocalDate.of(2025, 12, 7), "T007", -220_00, "Transport")
    );

    // Scan turns (state, txn) -> nextState and emits every state.
    List<LedgerEntry> ledger = transactions.stream()
        .gather(Gatherers.scan(
            () -> new LedgerEntry(null, openingBalanceCents),
            (prev, txn) -> new LedgerEntry(txn, prev.balanceAfter() + txn.amountCents())
        ))
        .toList();

    // Pretty print with alerts
    System.out.println("Opening balance: " + fmt(openingBalanceCents));
    for (LedgerEntry e : ledger) {
      Transaction t = e.txn();
      String delta = (t.amountCents() >= 0 ? "+" : "") + fmt(t.amountCents());
      String bal = fmt(e.balanceAfter());
      String alert =
          e.balanceAfter() < 0 ? "  << OVERDRAFT" :
              e.balanceAfter() < lowBalanceThreshold ? "  << LOW BALANCE" : "";
      System.out.printf("%s  %-4s  %-12s  Δ %-8s  balance=%s%s%n",
          t.date(), t.id(), t.memo(), delta, bal, alert);
    }

    long finalBalance = ledger.isEmpty() ? openingBalanceCents : ledger.get(ledger.size() - 1).balanceAfter();
    System.out.println("Final balance:   " + fmt(finalBalance));
  }

  private static String fmt(long cents) {
    BigDecimal v = BigDecimal.valueOf(cents, 2).setScale(2, RoundingMode.HALF_UP);
    return v.toPlainString() + " RON";
  }
}
