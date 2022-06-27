import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.max.revolut.ArrayUtils;

/**
 * JVM parameter to print memory when JVM process exited:
 * -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintNMTStatistics
 */
public class Main {

    static final class Interval {

        private static final Comparator<Interval> START_DATE_ASC = Comparator.comparing(Interval::getStart);

        private final int start;
        private final int end;

        public Interval(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException("'start' can't be greater than 'end': start = " + start + ", end = " + end);
            }
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Interval interval = (Interval) o;

            if (start != interval.start) {
                return false;
            }
            return end == interval.end;
        }

        @Override
        public int hashCode() {
            int result = start;
            result = 31 * result + end;
            return result;
        }

        public boolean hasIntersection(Interval cur) {

            Interval left = this;
            Interval right = cur;

            if (right.start < left.start) {
                Interval temp = left;
                left = right;
                right = temp;
            }

            return left.end >= right.start;
        }

        public boolean notOverlapping(Interval cur) {
            return !hasIntersection(cur);
        }
    }

    public static void main(String[] args) throws Exception {

        Interval[] intervals = {
            new Interval(6, 10),
            new Interval(11, 13),
            new Interval(9, 17),
            new Interval(1, 5),
            new Interval(14, 15),
            new Interval(8, 9),
            new Interval(4, 5),
            new Interval(12, 15),
            new Interval(2, 7),
        };

        List<List<Interval>> scheduled = schedule(intervals);
        System.out.println(scheduled.size());

        for (List<Interval> layer : scheduled) {
            System.out.println(layer);
        }

        System.out.println("Maine done...");
    }

    public static List<List<Interval>> schedule(Interval[] intervals) {

        ArrayUtils.checkNotNull(intervals, () -> "'intervals' can't be null");

        Arrays.sort(intervals, Interval.START_DATE_ASC);

        List<List<Interval>> scheduled = new ArrayList<>();

        for (Interval cur : intervals) {
            boolean wasInserted = insertIntoExistingLayer(scheduled, cur);

            if (!wasInserted) {
                addNewLayer(scheduled, cur);
            }
        }

        return scheduled;
    }

    private static void addNewLayer(List<List<Interval>> scheduled, Interval cur) {
        List<Interval> newLayer = new ArrayList<>();
        newLayer.add(cur);
        scheduled.add(newLayer);
    }

    private static boolean insertIntoExistingLayer(List<List<Interval>> scheduled, Interval cur) {
        for (List<Interval> layer : scheduled) {
            if (last(layer).notOverlapping(cur)) {
                layer.add(cur);
                return true;
            }
        }
        return false;
    }

    private static Interval last(List<Interval> layer) {
        assert layer != null;
        return layer.get(layer.size() - 1);
    }

}
