import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.SwingConstants;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

public class NFA {
	public State startState = null;
	public ArrayList<State> states = new ArrayList<State>();
	public ArrayList<NFADeltaFunction> transitions = new ArrayList<NFADeltaFunction>();
	private int id = -1;
	
	/**
	 * Alphabet read in from the input file
	 */
	private String[] alphabet = {"a", "b", "c", "d"};
	
	
	private Graph<String, DefaultEdge> graph = new DirectedPseudograph<>(LabeledDefaultEdge.class);
	
	/**
	 * Accepting states in the NFA
	 * 
	 * Not implemented as a list since Thompson Construction
	 * results in an NFA with only one accepting state.
	 */
	public State acceptingState = null;
	
	public NFA(State startState, 
			ArrayList<State> states, 
			ArrayList<NFADeltaFunction> transitions, 
			State acceptingState, 
			int id,
			String[] alphabet) {
		super();
		this.startState = startState;
		this.states = states;
		this.transitions = transitions;
		this.acceptingState = acceptingState;
		this.id = id;
		this.alphabet = alphabet;
	}// constructor
	
	public String[] getAlphabet() {
		return this.alphabet;
	}// getAlphabet
	
	public void toGraph() {
		// New graph for NFA
		this.graph = new  DirectedPseudograph<>(LabeledDefaultEdge.class);
		
		// Add start state
		this.graph.addVertex("start");
		
		// Add states
		for(State state: this.states) {
			this.graph.addVertex(String.valueOf(state.name));
		}// for
		
		// Edge for start -> startingState
		this.graph.addEdge("start", String.valueOf(this.startState.name), new LabeledDefaultEdge(""));
		
		// Add edges using delta functions
		for(NFADeltaFunction deltaFunction: this.transitions) {
			for (State endState: deltaFunction.getEndingStates()) {
				this.graph.addEdge(
						String.valueOf(deltaFunction.getStartingState().name), // Input state
						String.valueOf(endState.name), // output state
						new LabeledDefaultEdge(deltaFunction.getTransitionSymbol()) // transition
				);// this.graph.addEdge
			}// for
		}// for
		
		DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> v);

		// Set Graph properties
		exporter.setGraphAttributeProvider(()-> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			 map.put("rankdir", DefaultAttribute.createAttribute("LR"));
			 return map;
		});
		
		// Set vertex properties
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            
            // Make double circle for accepting states
            if(v.compareTo(String.valueOf(this.acceptingState.name)) == 0) {
            	map.put("shape", DefaultAttribute.createAttribute("doublecircle"));
            	map.put("label", DefaultAttribute.createAttribute("q" + v));
            }// if
            
            // No circle for start state
            else if (v.compareTo(String.valueOf("start")) == 0) {
            	map.put("shape", DefaultAttribute.createAttribute("none"));
            }// if
            
            // Normal circle for other states
            else {
            	map.put("shape", DefaultAttribute.createAttribute("circle"));
            	map.put("label", DefaultAttribute.createAttribute("q" + v));
            }// else
            return map;
        });
        
        // Set edge properties
        exporter.setEdgeAttributeProvider((e) -> {
        	// The variable 'e' has a class type of IntrusiveEdge
        	// Due to limitations with the package and what not
        	// I created a LabeledDefaultEdge class that extends the
        	// DefaultEdge class. Overriding the toString() method 
        	// allows me to return just the edges label.
        	//
        	// This should be OK as I don't need the toString()
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.toString()));
            return map;
        });
        
        // Write NFA's DOT to simple text file
        try {
        	// Show NFA in DOT language via standard output
        	Writer writer = new StringWriter();
        	exporter.exportGraph(this.graph, writer);
        	System.out.println("NFA in DOT language:\n\n" + writer.toString());
            
            // Write DOT language to a note pad file
            System.out.println("Writing NFA in DOT language to \"src/graphs/nfa" + String.valueOf(id) + ".txt\"");
            File file = new File("src/graphs/nfa" + String.valueOf(id) + ".txt");
            exporter.exportGraph(this.graph, file);
        }// try
        catch(Exception e){
        	System.out.println("Failed to write NFA in DOT language to \"src/graphs/nfa" + String.valueOf(id) + ".txt\"");
        }// catch
       
        // Use DOT to create NFA image file
        try {
        	// Visualize graph in image file
        	System.out.println("Visualizing NFA at \"src/graphs/nfa" + String.valueOf(id) + ".png\"");
        	
        	// Converts the JGraphT to mxGraph
        	JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<String, DefaultEdge>(this.graph);
        	
        	// Get vertices
        	HashMap<String, mxICell> cells = graphAdapter.getVertexToCellMap();
        	
        	// Layout
            mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter, SwingConstants.WEST);
            
            // Styling
        	mxStylesheet stylesheet = graphAdapter.getStylesheet();
        	
        	// Re-style vertex
        	Map<String, Object> vertexStyle = new HashMap<String, Object>();
        	vertexStyle = stylesheet.getDefaultVertexStyle();// Copy default vertex style
        	vertexStyle.replace(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);// Replace rectangles with circles
        	vertexStyle.replace(mxConstants.STYLE_FONTCOLOR, "#000000");// Replace with black font
        	vertexStyle.replace(mxConstants.STYLE_STROKECOLOR, "#000000");// Replace blue with black outline
        	vertexStyle.replace(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");// white fill color
        	stylesheet.setDefaultVertexStyle(vertexStyle);
        	
        	// Re-style edge
        	Map<String, Object> edgeStyle = new HashMap<String, Object>();
        	edgeStyle = stylesheet.getDefaultEdgeStyle();// Copy default edge style
        	edgeStyle.replace(mxConstants.STYLE_FONTCOLOR, "#000000");// Replace with black font
        	edgeStyle.replace(mxConstants.STYLE_STROKECOLOR, "#808080");// Replace with silver arrows
        	stylesheet.setDefaultEdgeStyle(edgeStyle);

            // Set new geometry of cells
        	cells.forEach((s, c) ->{
            	c.setGeometry(new mxGeometry(0.0, 0.0, 55.0, 55.0));
            	
            	// Remove outline for starting node
            	if (s.equals("start")) {
            		c.setStyle("strokeColor=#FFFFFF");
            	}// if
            	
            	// Highlight (well, actually grey out), accepted nodes
            	else if (String.valueOf(this.acceptingState.name).equals(s)) {
            		c.setStyle("fillColor=#808080");
            	}// if
            });
        	
            layout.execute(graphAdapter.getDefaultParent());
            BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        	File imgFile = new File("src/graphs/nfa" + String.valueOf(id) + ".png");
        	ImageIO.write(image, "PNG", imgFile);
        	
        	System.out.println("Successfully output NFA to \"src/graphs/nfa" + String.valueOf(id) + ".png\"");
        }// try
        catch (Exception e) {
        	System.out.println("Failed to output NFA to \"src/graphs/nfa" + String.valueOf(id) + ".png\"");
        }// catch
	}// toGraph
	
	
	/**
	 * Shows the NFA 5 Tuple to standard output.
	 */
	public String toString() {
		String statesToString = "{";
		for (int i = 0; i < this.states.size(); ++i) {
			if (i < this.states.size() - 1) {
				statesToString += this.states.get(i).name  + ", ";
			}// if
			
			else {
				statesToString += this.states.get(i).name;
			}// else
		}// for
		statesToString += "}";
		
		String deltas = "{\n";
		for (NFADeltaFunction delta: this.transitions) {
			deltas += "   " + delta.toString() + "\n";
		}// for
		deltas += "}\n";
		
		String ans = "\n\n";
		ans += "NFA: \n"; 
		ans += "Start State = " + String.valueOf(this.startState.name) + "\n";
		ans += "States = " + statesToString + "\n";
		ans += "Alphabet = " + Arrays.toString(this.alphabet) + "\n";
		ans += "Transitions = " + deltas;
		ans += "Accepting State = " + String.valueOf(this.acceptingState.name);
		
		System.out.println(ans);
		
		return ans;
	}// toString
}// NFA
