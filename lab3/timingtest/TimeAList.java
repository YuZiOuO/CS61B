package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        int MAX_TRIES = 128000;
        Stopwatch timer = new Stopwatch();
        AList<Integer> list = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> OPCounts = new AList<>();
        int i = 0;
        int checkPoint = 1000;
        while(i != MAX_TRIES+1){
            if(i == checkPoint){
                Double timeStamp = timer.elapsedTime();
                times.addLast(timeStamp);
                OPCounts.addLast(checkPoint);
                checkPoint *= 2;
            }
            list.addLast(i);
            i++;
        }
        printTimingTable(OPCounts, times, OPCounts);
    }
}
