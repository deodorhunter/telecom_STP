package stp;

public class Message {
    protected int root;
    protected int distanceToRoot;
    protected int srcId;
    protected int destId;
    protected boolean pathThrough;

    public Message(int root, int distanceToRoot, int srcId, int destId, boolean pathThrough) {
        this.root = root;
        this.distanceToRoot = distanceToRoot;
        this.srcId = srcId;
        this.destId = destId;
        //boolean che indica se il path va verso l'origine, per determinare se la porta
        //di uno switch è root port o designated port
        this.pathThrough = pathThrough;
    }
}
