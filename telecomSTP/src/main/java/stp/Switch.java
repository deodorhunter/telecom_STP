package stp;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


public class Switch extends SwitchSpanningTree {
    protected HashMap<Integer, Boolean> stLinks;
    protected int rootSupposto;
    protected int distanzaRootSupposto;
    protected int vicinoSulRootPath;
    public Switch(int id, Topologia tlink, ArrayList<Integer> vicini) {
        super(id, tlink, vicini);
        this.rootSupposto = this.id;
        this.distanzaRootSupposto = 0;
        this.vicinoSulRootPath = this.id;

        //Per ogni switch, assegna un boolean ad ogni vicino, col significato di:
        //true: vicino sulla shortest path verso la root per questo switch, in pratica porta root
        //false: vicino non sulla shortest path verso la root per questo switch, in pratica porta designata
        this.stLinks = new HashMap<Integer, Boolean>();
        for(Integer i : this.vicini) {
            this.stLinks.put(i, false);
        }
        System.out.println(stLinks);
    }

    //broadcast dei messaggi in inizializzazione
    public void sendInitialMessages() {
        for(Integer vicino: vicini){
            System.out.println("Sono lo switch "+this.id+" che manda un messaggio allo switch "+vicino);
            sendMessage(new Message(this.rootSupposto, 0, this.id, vicino, false ));
        }
    }

    //processa in base alla minima distanza supposta tra switch e messaggio ricevuto
    public void processMessage(Message msg) {
        //il root supposto risulta maggiore di quello contenuto nel messaggio
        if(msg.root<this.rootSupposto){
            //si aggiorna la vista del root dello switch che ha ricevuto il messaggio
            this.rootSupposto=msg.root;
            this.distanzaRootSupposto=msg.distanceToRoot+1;
//            this.stLinks.remove(this.vicinoSulRootPath);
            this.vicinoSulRootPath=msg.srcId;
            //questo switch mette come percorso verso il root l'id dello switch da cui ha ricevuto il messaggio
            this.stLinks.put(msg.srcId, true);

            //applicate le modifiche, broadcast a tutti
            for(Integer vicino: this.vicini){
                boolean sullaRootPath = this.stLinks.containsKey(vicino) ? this.stLinks.get(vicino) : false;
                this.sendMessage(new Message(this.rootSupposto, this.distanzaRootSupposto, this.id, vicino, sullaRootPath));
            }
            System.out.println("Switch "+this.id+" processing message from switch "+msg.srcId+"has stLinks: "+this.stLinks);
        }
        //root supposto uguale a quello contenuto nel messaggio
        else if(msg.root==this.rootSupposto){
            //lo switch corrente risulta sulla shortest path verso lo switch msg.destinationId verso la root
            //crea un link dallo switch corrente verso lo switch msg.destinationId se non esiste
            if(msg.pathThrough) {
                this.stLinks.put(msg.srcId, false);
                System.out.println("Switch "+this.id+" processing message from switch "+msg.srcId+"has stLinks: "+this.stLinks);
            }

            //lo switch corrente non risulta sulla shortest path verso lo switch msg.destinationId verso la root
            //la shortest path per msg.destinationId verso la sua root supposta non passa dallo switch corrente
            else {
                if(this.distanzaRootSupposto>msg.distanceToRoot + 1){
                    this.distanzaRootSupposto=msg.distanceToRoot + 1;

                    //aggiorna il vicino attualmente sulla shortest path
                    this.stLinks.remove(vicinoSulRootPath);
                    this.stLinks.put(msg.srcId, true);
                    int oldVicinoSulRootPath = this.vicinoSulRootPath;
                    this.vicinoSulRootPath = msg.srcId;

                    //se cambia il vicinoSulRoot path, questo switch deve notificare i nuovi e vecchi vicini sul path
                    Message msgToOld = new Message(this.rootSupposto, this.distanzaRootSupposto, this.id, oldVicinoSulRootPath, false);
                    this.sendMessage(msgToOld);

                    Message msgToNew = new Message(this.rootSupposto, this.distanzaRootSupposto, this.id, this.vicinoSulRootPath, true);
                    this.sendMessage(msgToNew);

                    System.out.println("Switch "+this.id+" processing message from switch "+msg.srcId+"has stLinks: "+this.stLinks);
                }
                //pareggio, scegli il vicino con l'id minore
                else if(this.distanzaRootSupposto == msg.distanceToRoot + 1) {
                    //la shortest path corrente passa  per uno switch con id maggiore dello switch corrente
                    if(this.vicinoSulRootPath > msg.srcId) {
                        //fondamentale per evitare configurazioni in cui sono ancora presenti dei loop
                        //vedi ad esempio input4.txt e input5.txt
                        this.stLinks.remove(vicinoSulRootPath);

                        this.stLinks.put(msg.srcId, true);
                        int oldVicinoSulRootPath = this.vicinoSulRootPath;
                        this.vicinoSulRootPath = msg.srcId;

                        //se cambia il vicinoSulRoot path, questo switch deve notificare i nuovi e vecchi vicini sul path
                        Message msgToOld = new Message(this.rootSupposto, this.distanzaRootSupposto, this.id, oldVicinoSulRootPath, false);
                        this.sendMessage(msgToOld);

                        Message msgToNew = new Message(this.rootSupposto, this.distanzaRootSupposto, this.id, this.vicinoSulRootPath, true);
                        this.sendMessage(msgToNew);

                        System.out.println("Switch "+this.id+" processing message from switch "+msg.srcId+"has stLinks: "+this.stLinks);
                    }
                    //la shortest path corrente passa  per uno switch con id minore dello switch corrente
                    else {
                        this.stLinks.remove(msg.srcId);
                        System.out.println("Switch "+this.id+" processing message from switch "+msg.srcId+"has stLinks: "+this.stLinks);
                    }
                }
                //cancella se non funziona il removeNode--> OVVIAMENTE NON FUNZIONA
//                else if(this.distanzaRootSupposto==0);
                //this.distanceToClaimedRoot < msg.distanceToRoot + 1
                else {
                    this.stLinks.remove(msg.srcId);
                    System.out.println("Switch "+this.id+" processing message from switch "+msg.srcId+"has stLinks: "+this.stLinks);
                }
            }
        }
    }

    //logging su file esterno
    public String generateLogString() {
//        abilita debug creazione stringhe di log
//        try {
//            PrintStream out = new PrintStream(new FileOutputStream("switchesLog.txt", true));
//            out.println("I'm switch " + this.id + " and this is my stLinks map, I have connection to this switches. \n" +
//                    "If true, this is the path to root and this is root port, otherwise " +
//                    "it's designated port because I'm closer to root/I'm the root\n" + this.stLinks +"\n"+
//            "My distance to root equals: "+ this.distanzaRootSupposto+ "\n");
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        ArrayList<Integer> sorted = new ArrayList<Integer>();
        Iterator<Integer> iter = this.stLinks.keySet().iterator();
        while(iter.hasNext()) {
            sorted.add(iter.next());
        }
        StringBuilder sb = new StringBuilder();
        if(sorted.size()!=0){
            Collections.sort(sorted);

            int idx = 0;
            while(idx < sorted.size() - 1) {
                sb.append(this.id + " - " + sorted.get(idx) + ", ");
                idx++;
            }
            sb.append(this.id + " - " + sorted.get(idx));
            return sb.toString();
        }

        return sb.append(" ").toString();
    }

    public HashMap<Integer, Boolean> getStLinks(){
        return this.stLinks;
    }

    public void setVicinoSulRootPath(int vicinoSulRootPath) {
        this.vicinoSulRootPath = vicinoSulRootPath;
    }

    public void resetDistanzaRootSupposto() {
        this.distanzaRootSupposto = 0;
    }
    public void resetVicinoSulRootPath() {
        this.vicinoSulRootPath = this.id;
    }
}
