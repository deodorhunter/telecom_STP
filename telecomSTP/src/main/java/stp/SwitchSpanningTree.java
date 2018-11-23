package stp;

import java.util.ArrayList;

public class SwitchSpanningTree{

    protected int id;
    protected Topologia tlink;
    protected ArrayList<Integer> vicini;

    public SwitchSpanningTree(int id, Topologia tlink, ArrayList<Integer> vicini){
        this.id=id;
        this.tlink=tlink;
        this.vicini=vicini;
    }

    //Verifica che tutti i vicini abbiano un backlink verso te
    public void verificaVicini() throws Exception{
        for(Integer vicino : vicini) {
            Switch s = this.tlink.getSwitch(vicino);
            if(s==null){
                throw new Exception("Questo switch non esiste nella topologia indicata, switchId: "+ this.id);
            }
            if(!s.haVicino(this.id)){
                throw new Exception("Questo switch non ha come vicino switchId: "+ this.id);
            }
        }
    }
    public boolean haVicino(int id) {
        return this.vicini.contains(id);
    }

    public void sendMessage(Message msg) {
        this.tlink.sendMessage(msg);
    }
}
