package grep;
import org.jgrapht.graph.DefaultEdge;

public class LabeledDefaultEdge extends DefaultEdge {
	private static final long serialVersionUID = 1975235766892156562L;
	private String label;
	private String source;
	private String destinatnion;
    
    public LabeledDefaultEdge(String label, String source, String destination) {
    	super();
        this.label = label;
        this.source = source;
        this.destinatnion = destination;
    }// constructor

    /**
     * Override the toString() Method to return the label of the edge
     */
    @Override
    public String toString() {
        return label;
    }// toString

	public String getSource() {
		return source;
	}

	public String getDestinatnion() {
		return destinatnion;
	}
	
	public void setLabel(String newLabel) {
		this.label = newLabel;
	}// setLabel
}// class
