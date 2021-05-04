import org.jgrapht.graph.DefaultEdge;

public class LabeledDefaultEdge extends DefaultEdge {
	private static final long serialVersionUID = 1975235766892156562L;
	private String label;
    
    public LabeledDefaultEdge(String label) {
    	super();
        this.label = label;
    }// constructor

    /**
     * Override the toString() Method to return the label of the edge
     */
    @Override
    public String toString() {
        return label;
    }// toString
}// class
