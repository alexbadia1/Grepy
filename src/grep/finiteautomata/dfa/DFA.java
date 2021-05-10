package grep.finiteautomata.dfa;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
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

import grep.LabeledDefaultEdge;
import grep.finiteautomata.State;

public class DFA {
	public Hashtable<HashSet<State>, State> states;
	public ArrayList<DFADeltaFunction> transitions;
	private int stateId;
	private int dfaID;
	private Graph<String, DefaultEdge> graph;
	private HashSet<State> startingStateKey;
	private ArrayList<HashSet<State>> acceptingStateKeys;
	
	
	public DFA(int dfaId) {
		this.stateId = 0;
		this.states = new Hashtable<HashSet<State>, State>();
		this.transitions = new ArrayList<DFADeltaFunction>();
		this.startingStateKey = new HashSet<State>();
		this.acceptingStateKeys = new ArrayList<HashSet<State>>();
		this.graph = new DirectedPseudograph<>(LabeledDefaultEdge.class);
		this.dfaID = dfaId;
	}// constructor
	
	public State getStartingState() {
		return this.states.get(this.startingStateKey);
	}// setStartingState
	
	public void setStartingState(HashSet<State> nfaStates) {;
		this.startingStateKey = nfaStates;
	}// setStartingState
	
	public ArrayList<State> getAcceptingStates() {
		ArrayList<State> acceptingStates = new ArrayList<State>();
		
		for (HashSet<State> key: this.acceptingStateKeys) {
			acceptingStates.add(this.states.get(key));
		}// for
		
		return acceptingStates;
	}// getAcceptingStates
	
	public void setAcceptingStates(ArrayList<HashSet<State>> nfaStates) {;
		this.acceptingStateKeys = nfaStates;
	}// setStartingState
	
	public void addAcceptingState(HashSet<State> nfaStates) {
		this.acceptingStateKeys.add(nfaStates);
	}// addAcceptingState
	
	public int useStateId () {
		return this.stateId++;
	}// useStateId
	
//	public boolean test(String input) {
//		
//		
//		
//	}// test
	
	/*
	 * Converts the DFA to a graph
	 */
	public void toGraph() {
		// New graph for NFA
		this.graph = new  DirectedPseudograph<>(LabeledDefaultEdge.class);
		
		// Add start state
		this.graph.addVertex("start");
		
		// Add states
		for(State state: this.states.values()) {
			this.graph.addVertex(String.valueOf(state.name));
		}// for
		
		// Edge for start -> startingState
		this.graph.addEdge(
				"start", 
				String.valueOf(this.states.get(this.startingStateKey).name), 
				new LabeledDefaultEdge("")
		);// this.graph.addEdge
		
		// Add edges using delta functions
		// Search by state, to find states that have 
		// multiple edges that can be combined into one.
		ArrayList<Integer> skip = new ArrayList<Integer>();
		for(int i = 0; i < this.transitions.size(); ++i) {
			if (!skip.contains(Integer.valueOf(i))) {
				DFADeltaFunction currentDelta = this.transitions.get(i); 
				ArrayList<String> duplicateEdgesSymbols = new ArrayList<String>();
				
				// Search for duplicate edges in starting from (i + 1)
				for (int h = i + 1; h < this.transitions.size(); ++h) {
					if (this.states.get(this.transitions.get(h).getInputState()).name 
							== this.states.get(currentDelta.getInputState()).name) {
						if (this.states.get(this.transitions.get(h).getOutputState()).name 
								== this.states.get(currentDelta.getOutputState()).name) {
							duplicateEdgesSymbols.add(this.transitions.get(h).getInputSymbol());
							skip.add(Integer.valueOf(h));
						}// if
					}// if
				}// for
				
				String edgeName = currentDelta.getInputSymbol() + ", ";
				for (String symbol: duplicateEdgesSymbols) {
					edgeName += symbol + ", ";
				}// for
				
				this.graph.addEdge(
						String.valueOf(this.states.get(currentDelta.getInputState()).name), // Input state
						String.valueOf(this.states.get(currentDelta.getOutputState()).name), // output state
						new LabeledDefaultEdge(edgeName) // transition
				);// this.graph.addEdge
			}// if
		}// for
	}// toGraph
		
	public void export() {
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
            if(v.compareTo(String.valueOf(this.states.get(this.startingStateKey))) == 0) {
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
            System.out.println("Writing NFA in DOT language to \"src/graphs/dfa" + String.valueOf(dfaID) + ".txt\"");
            File file = new File("src/graphs/dfa" + String.valueOf(dfaID) + ".txt");
            exporter.exportGraph(this.graph, file);
        }// try
        catch(Exception e){
        	System.out.println("Failed to write NFA in DOT language to \"src/graphs/dfa" + String.valueOf(dfaID) + ".txt\"");
        }// catch
       
        // Use DOT to create NFA image file
        try {
        	// Visualize graph in image file
        	System.out.println("Visualizing NFA at \"src/graphs/dfa" + String.valueOf(dfaID) + ".png\"");
        	
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

        	ArrayList<String> acceptedStates = new ArrayList<String>();
        	for (HashSet<State> k: this.acceptingStateKeys) {
        		acceptedStates.add(String.valueOf(this.states.get(k).name));
        	}// for
        	
            // Set new geometry of cells
        	cells.forEach((s, c) ->{
            	c.setGeometry(new mxGeometry(0.0, 0.0, 55.0, 55.0));
            	
            	// Remove outline for starting node
            	if (s.equals("start")) {
            		c.setStyle("strokeColor=#FFFFFF");
            	}// if
            	
            	// Highlight (well, actually grey out), accepted nodes
            	else if (acceptedStates.contains(s)) {
            		c.setStyle("fillColor=#808080");
            	}// if
            });
        	
            layout.execute(graphAdapter.getDefaultParent());
            BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        	File imgFile = new File("src/graphs/dfa" + String.valueOf(dfaID) + ".png");
        	ImageIO.write(image, "PNG", imgFile);
        	
        	System.out.println("Successfully output NFA to \"src/graphs/dfa" + String.valueOf(dfaID) + ".png\"");
        }// try
        catch (Exception e) {
        	System.out.println("Failed to output NFA to \"src/graphs/dfa" + String.valueOf(dfaID) + ".png\"");
        }// catch
	}// export

	public String toString() {
		// Print out the all states
		String states = "{";
		int count = 0;
		for (State state: this.states.values()) {
			if (count < this.states.values().size() - 1) {
				states += state.name  + ", ";
			}// if
			
			else {
				states += state.name;
			}// else
			count++;
		}// for
		states += "}";
		
		// Print out deltas
		String deltas = "{\n\t";
		for (DFADeltaFunction delta: this.transitions) {
			String endState = "{ " + String.valueOf(this.states.get(delta.getOutputState()).name)  + " }";
			
			deltas += "Delta(" + String.valueOf(this.states.get(delta.getInputState()).name) + ", " 
					+ delta.getInputSymbol() + ") = " 
					+ endState + "\n\t";
		}// for
		deltas += "}\n";
		
		// Print out the accepted states
		String acceptingStates = "{";
		int acceptCount = 0;
		for (HashSet<State> acceptStateKey: this.acceptingStateKeys) {
			if (acceptCount < this.acceptingStateKeys.size() - 1) {
				acceptingStates += String.valueOf(this.states.get(acceptStateKey).name)  + ", ";
			}// if
			else {
				acceptingStates += String.valueOf(this.states.get(acceptStateKey).name);
			}// else
			acceptCount++;
		}// for
		acceptingStates += "}";
		
		String ans = "\n\n";
		ans += "DFA: \n"; 
		ans += "Start State = " + String.valueOf(this.states.get(this.startingStateKey).name) + "\n";
		ans += "States = " + states + "\n";
		ans += "Transitions = " + deltas;
		ans += "Accepting State = " + acceptingStates;
		
		return ans;
	}// toString
}// NFA