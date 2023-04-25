package org.example;

import java.util.*;

class DataCentre{
    private int numberOfReloads;
    public final boolean[] servers;
    private int running;

    DataCentre(int numberOfServers) {
        this.servers = new boolean[numberOfServers];
        Arrays.fill(servers, true);
        running = numberOfServers;
    }

    public void reset(){
        numberOfReloads++;
        running = servers.length;
        Arrays.fill(servers, true);
    }

    public void disable(int index){
        if(servers[index]){
            servers[index] = false;
            running--;
        }
    }

    public int RxA(){
        if(numberOfReloads == 0) return running;
        return numberOfReloads * running;
    }
}


public class First {
    private static final String DISABLE = "DISABLE";
    private static final String GETMAX = "GETMAX";
    private static final String GETMIN = "GETMIN";
    private static final String RESET  = "RESET";

    private static int getPos(ArrayList<DataCentre> dataCentres, Comparator<DataCentre> centreComparator){
        int at = 0;

        for (int j = 0; j < dataCentres.size(); j++) {
            at = centreComparator.compare(dataCentres.get(j), dataCentres.get(at)) > 0 ? j : at;
        }

        return at + 1;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int numOfDC = in.nextInt();
        int numOfServers = in.nextInt();
        int numOfCommands = in.nextInt();

        ArrayList<DataCentre> dataCentres = new ArrayList<>();
        for (int i = 0; i < numOfDC; i++) {
            dataCentres.add(new DataCentre(numOfServers));
        }

        for (int i = 0; i < numOfCommands; i++) {
            String command = in.next();
            switch (command){
                case DISABLE -> dataCentres.get(in.nextInt()-1).disable(in.nextInt()-1);
                case GETMAX -> System.out.println(getPos(dataCentres, Comparator.comparingInt(DataCentre::RxA)));
                case GETMIN -> System.out.println(getPos(dataCentres, (o1, o2) -> o2.RxA() - o1.RxA()));
                case RESET -> dataCentres.get(in.nextInt()-1).reset();
            }

        }
    }
}