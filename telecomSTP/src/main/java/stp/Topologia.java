package stp;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Topologia {
    private HashMap<Integer, Switch> switches;
    private Queue<Message> messages;

    public Topologia(String tInputFile) {
        this.switches = new HashMap<Integer, Switch>();
        this.messages = new LinkedList<Message>();
        parseInputFile(tInputFile);
        try {
            //verifica la correttezza della topologia fornita dal file.
            for(Integer switchId : this.switches.keySet()) {
                this.switches.get(switchId).verificaVicini();
            }
        }
        catch (Exception e) {
            System.err.println(tInputFile + " ha fornito una topologia incorretta");
        }
    }

    //verifica del file di configurazione di rete
    public void parseInputFile(String tInputFile){
        boolean isValid=true;
        try{
            FileInputStream fstream=new FileInputStream(tInputFile);
            DataInputStream dis=new DataInputStream(fstream);
            BufferedReader br=new BufferedReader(new InputStreamReader(dis));
            int switchId;
            String line; String[] splits;
            while((line=br.readLine())!=null){
                System.out.println(line);
                splits=line.split("\t", 0);
                if(splits.length!=2){
                    isValid=false;
                    break;
                }
                switchId=Integer.parseInt(splits[0]);
                splits=splits[1].split(",", 0);
                ArrayList<Integer> vicini=new ArrayList<Integer>();
                for(String s : splits){
                    vicini.add(Integer.parseInt(s));
                }
                Switch nSwitch= new Switch(switchId, this, vicini);
                switches.put(switchId, nSwitch);
            }
            if(!isValid){
                System.err.println("Formato file invalido");
                switches.clear();
            }
            fstream.close();
            dis.close();
            br.close();
        }catch(Exception e){
            System.err.println("Errore nella lettura del file di topologia "+ tInputFile+ ", causato da: " +e.getMessage());
        }
    }

    // prende lo switch corrispondente all'id
    public Switch getSwitch(int id){
        return this.switches.get(id);
    }

    // invio dei messaggi per l'autoapprendimento
    public void sendMessage(Message msg){
        Switch origine=this.switches.get(msg.srcId);
        if(origine == null){
           System.err.println("SrcId era nullo");
           throw new NullPointerException();
        }
        // cerca se tra i vicini ha il destinatario del messaggio, in tal caso aggiunge il messaggio
        // alla coda di messaggi rappresentata da messages
        if(origine.vicini.contains(msg.destId)){
            this.messages.add(msg);
        }
        else{
            System.err.println("Il messaggio puo essere inviato solo ai vicini immediati");
        }
    }

    //logging su file esterno della configurazione ottenuta
    public void log(String outputFile){
        Graph<Integer, String> g=new SparseMultigraph<>();
        try {
            PrintStream out = new PrintStream(new FileOutputStream(outputFile));
            Iterator<Integer> switchIds = this.switches.keySet().iterator();
            ArrayList<Integer> sortedIds= new ArrayList<Integer>();
            while(switchIds.hasNext()) {
                sortedIds.add(switchIds.next());
            }
            Collections.sort(sortedIds);
            System.out.println(sortedIds);

            for(Integer id : sortedIds) {
//              System.out.println("Switch :" + id+ "stLink "+this.switches.get(id).stLinks);
                String entry = this.switches.get(id).generateLogString();
                System.out.println(entry.length());
                if(entry.length()!=1){
                String splits[]= entry.split(", ");
                g.addVertex(id);
                for(String subsplit : splits){
                    String adj_entry[]=subsplit.split(" - ");
                    StringBuilder sb=new StringBuilder();
                    sb.append("Edge "+ adj_entry[1]+ " - " + adj_entry[0]);
                    System.out.println(sb);
                    if(!g.getEdges().contains(sb.toString()))
                        g.addEdge("Edge "+subsplit, id, Integer.parseInt(adj_entry[1]));
                }
                StringBuilder sb=new StringBuilder();
                if(this.getSwitch(id).distanzaRootSupposto==0){
                    sb.append("NODE: " +id+ "\n\t\twith links\t"+ entry + "\n\t\t(RP=true, DP=false)\t\t" + this.switches.get(id).stLinks+ "\n\t\tI AM THE ROOT\n");
                }else
                    sb.append("NODE: " +id+ "\n\t\twith links\t"+entry + "\n\t\t(RP=true, DP=false)\t\t" + this.switches.get(id).stLinks+ "\n\t\tmy cost to root is: "+ this.getSwitch(id).distanzaRootSupposto+"\n");
                out.print(sb);
                out.flush();}
            }
            out.close();
            System.out.println("Grafo: "+ g.toString());
            printGraph(g, outputFile);
        }
        catch (Exception e) {
            System.err.println("Log spanning tree error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //esecuzione algoritmo
    public void runStp(){
        for(Integer switchId : this.switches.keySet()) {
            this.switches.get(switchId).sendInitialMessages();
        }
        while(!this.messages.isEmpty()) {
            Message msg = this.messages.poll();
            this.switches.get(msg.destId).processMessage(msg);
        }
    }

    //stampa il grafo
    public void printGraph (Graph<Integer, String> graph, String outfile){
        Layout<Integer, String> layout = new CircleLayout(graph);
        layout.setSize(new Dimension(800,800)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(850,850)); //Sets the viewing area size

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexFillPaintTransformer(i -> Color.WHITE);

        JFrame frame = new JFrame(outfile);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

    }
}
