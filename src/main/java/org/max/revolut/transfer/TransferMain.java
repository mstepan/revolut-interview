package org.max.revolut.transfer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;


public final class TransferMain {

    public static void main(String[] args) {

        BalanceTransferService transfer = new BalanceTransferService();


        Account from = null;
        Account to = null;

        transfer.transfer(from, from, new BigDecimal(133.45)); // th1

        transfer.transfer(from, to, new BigDecimal(133.45)); // th1

        transfer.transfer(to, from, new BigDecimal(133.45)); // th2


        //select * for update;
        //select * for update;
        //
// R_R
//        update account set amount = xx where id = id1;
//        update account set amount = yy where id = id1;


        System.out.println("Main done...");
    }

    // explain
    // select * from transaction where from_acc_id = xxx and time between XXX and YYY
    static class Transaction {
        Account from;
        Account to;
        BigDecimal amount;
        Timestamp time;
    }

    static class Account {

        final UUID id;

        // empty skeleton, fill in
        BigDecimal amount;
        final String name;


        public UUID getId() {
            return id;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public Account(UUID id, BigDecimal amount, String name) {
            this.id = Objects.requireNonNull(id);
            this.amount = checkNotNull(amount, "amount can't be null");
            this.name = name;
        }

        private BigDecimal checkNotNull(BigDecimal value, String errorMsg) {
            if (value == null) {
                throw new IllegalArgumentException(errorMsg);
            }
            return value;
        }
    }

    static class BalanceTransferService {


        void transfer(Account from, Account to, BigDecimal amount) {
            Objects.requireNonNull(from, "");
            Objects.requireNonNull(to, "");
            Objects.requireNonNull(amount, "");

//            checkNotNegative(amount, ()->{
//                return ""
//            });

            if (from == to || from.getId().equals(to.getId())) {
                return;
            }

            assert from.getId().compareTo(to.getId()) != 0 : "";

            // first locked one with smallest identity hashCode
            Account first = from;
            Account second = to;

            if (from.getId().compareTo(to.getId()) < 0) {
                first = to;
                second = from;
            }

            synchronized (first) {
                synchronized (second) {
                    if (from.getAmount().compareTo(amount) < 0) {
                        throw new IllegalStateException(".....");
                    }

                    from.setAmount(from.getAmount().subtract(amount));
                    to.setAmount(to.getAmount().add(amount));
                }
            }
        }
    }
}
