package org.example;

import java.io.*;
import java.util.*;

class DataCenter implements Comparable<DataCenter> {
    public TreeSet<Integer> offedServers;
    public int id;
    public int reloadsCount;
    public int serversCount;

    public DataCenter(int id, int serversCount) {
        this.id = id;
        this.serversCount = serversCount;
        this.offedServers = new TreeSet<>();
    }

    @Override
    public int compareTo(DataCenter o) {
        if((serversCount - offedServers.size()) * reloadsCount == o.reloadsCount * (serversCount - o.offedServers.size())){
            return id - o.id;
        } else {
            return (serversCount - offedServers.size()) * reloadsCount - o.reloadsCount * (o.serversCount - o.offedServers.size());
        }
    }

    @Override
    public String toString(){
        return String.valueOf(id);
    }
}

class DataCenterGavno extends DataCenter {

    public DataCenterGavno(DataCenter o) {
        super(o.id, o.serversCount);
        this.offedServers = o.offedServers;
        this.reloadsCount = o.reloadsCount;
    }

    public DataCenterGavno(int id, int serversCount) {
        super(id, serversCount);
        this.offedServers = new TreeSet<>();
    }

    @Override
    public int compareTo(DataCenter o) {
        if((serversCount - offedServers.size()) * reloadsCount == o.reloadsCount * (serversCount - o.offedServers.size())){
            return o.id - id;
        } else {
            return (serversCount - offedServers.size()) * reloadsCount - o.reloadsCount * (o.serversCount - o.offedServers.size());
        }
    }
}

public class FirstOptimazed {

    private static final String DISABLE = "DISABLE";
    private static final String GETMAX = "GETMAX";
    private static final String GETMIN = "GETMIN";
    private static final String RESET = "RESET";

    private static final class FastScanner {
        private final BufferedReader reader;
        private StringTokenizer tokenizer;

        FastScanner(final InputStream in) {
            reader = new BufferedReader(new InputStreamReader(in));
        }

        String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return tokenizer.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    private static void solve(final FastScanner in, final PrintWriter out) {
        int numOfDC = in.nextInt();
        int numOfServers = in.nextInt();
        int numOfCommands = in.nextInt();

        ArrayList<DataCenter> dataCentersList = new ArrayList<>();
        TreeSet<DataCenter> dataCentersTreeSet = new TreeSet<>();
        TreeSet<DataCenterGavno> dataCentersTreeSetGavno = new TreeSet<>();

        for (int i = 1; i <= numOfDC; i++) {
            DataCenter cur = new DataCenter(i, numOfServers);
            dataCentersList.add(cur);
            dataCentersTreeSet.add(cur);
            dataCentersTreeSetGavno.add(new DataCenterGavno(cur));
        }

        for (int i = 0; i < numOfCommands; i++) {
            switch (in.next()) {
                case DISABLE -> {
                    int dcIndex = in.nextInt() - 1;
                    int srIndex = in.nextInt() - 1;

                    if (!dataCentersList.get(dcIndex).offedServers.contains(srIndex)) {
                        dataCentersTreeSet.remove(dataCentersList.get(dcIndex));
                        dataCentersTreeSetGavno.remove(new DataCenterGavno(dataCentersList.get(dcIndex)));
                        dataCentersList.get(dcIndex).offedServers.add(srIndex);
                        dataCentersTreeSet.add(dataCentersList.get(dcIndex));
                        dataCentersTreeSetGavno.add(new DataCenterGavno(dataCentersList.get(dcIndex)));
                    }

                }

                case GETMAX -> {
                    System.out.println(dataCentersTreeSetGavno.last().id);
                }
                case GETMIN -> {
                    System.out.println(dataCentersTreeSet.first().id);
                }

                case RESET -> {
                    int dcIndex = in.nextInt() - 1;

                    dataCentersTreeSet.remove(dataCentersList.get(dcIndex));
                    dataCentersTreeSetGavno.remove(new DataCenterGavno(dataCentersList.get(dcIndex)));
                    dataCentersList.get(dcIndex).reloadsCount++;
                    dataCentersList.get(dcIndex).offedServers.clear();
                    dataCentersTreeSet.add(dataCentersList.get(dcIndex));
                    dataCentersTreeSetGavno.add(new DataCenterGavno(dataCentersList.get(dcIndex)));
                }
            }
        }
    }

    public static void main(String[] args) {
        final FastScanner in = new FastScanner(System.in);
        try (PrintWriter out = new PrintWriter(System.out)) {
            solve(in, out);
        }
    }
}
