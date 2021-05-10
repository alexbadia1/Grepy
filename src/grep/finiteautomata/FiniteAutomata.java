package grep.finiteautomata;

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

import grep.LabeledDefaultEdge;
import grep.Util;
import grep.finiteautomata.states.State;

public class FiniteAutomata {
	protected String type;
	protected ArrayList<State> states;
	protected ArrayList<String> sigma;
	protected ArrayList<State> startStates;
	protected ArrayList<DeltaFunction> delta;
	protected ArrayList<State> acceptedStates;
	private int id;
	protected Graph<String, DefaultEdge> graph;
	
	protected FiniteAutomata(ArrayList<State> states, ArrayList<String> sigma, ArrayList<State> startStates,
			ArrayList<DeltaFunction> delta, ArrayList<State> acceptedStates, int id, String type) {
		super();
		this.states = states;
		this.sigma = sigma;
		this.startStates = startStates;
		this.delta = delta;
		this.acceptedStates = acceptedStates;
		this.id = id;
		this.type = type;
		this.graph = new DirectedPseudograph<>(LabeledDefaultEdge.class);
	}// constructor

	protected ArrayList<State> getStartingStates() {
		return this.startStates;
	}

	protected boolean setStartingStates(ArrayList<State> startStates) {
		if(states.size() != 1) {return false;}
		this.startStates = startStates;
		return true;
	}// setStartingStates

	public ArrayList<String> getSigma() {
		return sigma;
	}

	public void setSigma(ArrayList<String> sigma) {
		this.sigma = sigma;
	}

	public ArrayList<State> getStates() {
		return states;
	}

	public void setStates(ArrayList<State> states) {
		this.states = states;
	}
	
	public void addStates(ArrayList<State> states) {
		this.states.addAll(states);
	}
	
	public void addState(State state) {
		this.states.add(state);
	}

	public ArrayList<DeltaFunction> getDelta() {
		return delta;
	}

	public void setDelta(ArrayList<DeltaFunction> delta) {
		this.delta = delta;
	}
	
	public void addDelta(DeltaFunction delta) {
		this.delta.add(delta);
	}
	
	public void addDeltas(ArrayList<DeltaFunction> deltas) {
		this.delta.addAll(deltas);
	}

	protected ArrayList<State> getF() {
		return this.acceptedStates;
	}

	protected void setF(ArrayList<State> F) {
		this.acceptedStates = F;
	}

	public int getId() {
		return id;
	}
	
	private LabeledDefaultEdge searchForDuplicateEdge(
			String startStateName, 
			String endStateName, 
			ArrayList<LabeledDefaultEdge> edgeLabels) {
		
		for (int i = 0; i < edgeLabels.size(); ++i) {
			LabeledDefaultEdge curr = edgeLabels.get(i);
			if(curr.getSource().equals(startStateName) 
					&& curr.getDestinatnion().equals(endStateName)) {
				return curr;
			}// if
		}// for
		
		return null;
	}// searchForDuplicateEdge
	
	public void toGraph() {		
		// Add start state
		this.graph.addVertex("start");
		
		// Add states
		for(State state: this.states) {
			this.graph.addVertex(String.valueOf(state.name));
		}// for
		
		// Edge for start -> startingState
		this.graph.addEdge("start", String.valueOf(this.startStates.get(0).name), new LabeledDefaultEdge("", "", ""));
		
		// Add edges using delta functions
		ArrayList<LabeledDefaultEdge> edgeLabels = new ArrayList<LabeledDefaultEdge>();
		for(DeltaFunction trans: this.delta) {
			String startStateName = String.valueOf(trans.startingState.name);
			
			for (State endState: trans.endingStates) {
				String endStateName = String.valueOf(endState.name);
				
				// Edge already exists, multiple edges that can be combined into one
				if (this.graph.getEdge(startStateName, endStateName) != null) {
					LabeledDefaultEdge foundEdgeLabel = this.searchForDuplicateEdge(startStateName, endStateName, edgeLabels);
					foundEdgeLabel.setLabel(foundEdgeLabel.toString() + ", " + trans.transitionSymbol);
					
					// Copy edge label to a new class to avoid duplicate edge errors problems
					LabeledDefaultEdge edgeCopy = new LabeledDefaultEdge(
							foundEdgeLabel.toString(),
							startStateName, 
							endStateName);
					
					// Remove existing edge before adding new edge
					this.graph.removeEdge(startStateName, endStateName);
					this.graph.addEdge(startStateName, endStateName, edgeCopy);
				}// if
				
				// New edge
				else {
					LabeledDefaultEdge edgeLabel = new LabeledDefaultEdge(trans.transitionSymbol,startStateName, endStateName);
					// System.out.println("Added Egde: <s>" + startStateName + " <d>" + endStateName + " label " + edgeLabel.toString());
					this.graph.addEdge(startStateName, endStateName, edgeLabel);
					edgeLabels.add(edgeLabel);
				}// else
			}// for
		}// for
	}// toGraph
	
	public void export(String filename) {
		DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> v);

		// Set Graph properties
		exporter.setGraphAttributeProvider(()-> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			 map.put("rankdir", DefaultAttribute.createAttribute("LR"));
			 return map;
		});
		
		ArrayList<String> acceptedStates = new ArrayList<String>();
    	for (State acceptingState: this.acceptedStates) {
    		acceptedStates.add(String.valueOf(acceptingState.name));
    	}// for
    	
    	
		// Set vertex properties
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            
            // Make double circle for accepting states
            if(acceptedStates.contains(v)) {
            	map.put("shape", DefaultAttribute.createAttribute("doublecircle"));
            	map.put("label", DefaultAttribute.createAttribute("q" + v));
            }// if
            
            // No circle for start state
            else if (v.equals(String.valueOf("start"))) {
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
        
        this.exportToFile(exporter, filename);
        this.exportToPng(exporter, filename);
	}// export
	
	private void exportToFile(DOTExporter<String, DefaultEdge> exporter, String filename) {
		// Write NFA's DOT to simple text file
        try {
        	// Show NFA in DOT language via standard output
        	Writer writer = new StringWriter();
        	exporter.exportGraph(this.graph, writer);
        	System.out.println("\n\n\n" + Util.divider);
        	System.out.println("FA in DOT language:");
        	System.out.println(Util.divider);
        	System.out.print(writer.toString());
        	System.out.println(Util.divider);
            
            // Write DOT language to a note pad file
            System.out.println("\n\nWriting NFA in DOT language to \"src/grep/graphs/" + filename + ".txt\"");
            File file = new File("src/grep/graphs/" + filename + ".txt");
            exporter.exportGraph(this.graph, file);
            System.out.println("Successfully wrote FA in DOT language to \"src/grep/graphs/" + filename + ".txt\"\n");
        }// try
        catch(Exception e){
        	System.out.println("Failed to write FA in DOT language to \"src/grep/graphs/" + filename + ".txt\"\n" + e.toString());
        }// catch
	}// exportToFile
	
	private void exportToPng(DOTExporter<String, DefaultEdge> exporter, String filename) {
		// Use DOT to create NFA image file
        try {
        	// Visualize graph in image file
        	System.out.println("\nVisualizing FA at \"src/grep/graphs/" + filename + ".png\"");
        	
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
        	for (State acceptingState: this.acceptedStates) {
        		acceptedStates.add(String.valueOf(acceptingState.name));
        	}// for
        	
            // Set new geometry of cells
        	cells.forEach((s, c) ->{
            	c.setGeometry(new mxGeometry(0.0, 0.0, 55.0, 55.0));
            	
            	// Remove outline for starting node
            	if (s.equals("start")) {
            		c.setStyle("strokeColor=#FFFFFF");
            	}// if
            	
            	// Highlight (well, actually grey out), accepted nodes
            	else if (acceptedStates.contains(s.replaceAll("q", ""))) {
            		c.setStyle("fillColor=#808080");
            	}// if
            });
        	
            layout.execute(graphAdapter.getDefaultParent());
            BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        	File imgFile = new File("src/grep/graphs/" + filename + ".png");
        	ImageIO.write(image, "PNG", imgFile);
        	
        	System.out.println("Successfully output FA to \"src/grep/graphs/" + filename + ".png\"");
        }// try
        catch (Exception e) {
        	System.out.println("Failed to output FA to \"src/grep/graphs/" + filename + ".png\"");
        }// catch
	}// exportToPng
	
	private String statesToString(ArrayList<State> states) {
		String statesToString = "{";
		for (int i = 0; i < states.size(); ++i) {
			if (i < states.size() - 1) {
				statesToString += states.get(i).name  + ", ";
			}// if
			
			else {
				statesToString += states.get(i).name;
			}// else
		}// for
		statesToString += "}";
		
		return statesToString;
	}// statesToString
	
	private String deltaToString(ArrayList<DeltaFunction> transtions) {
		String delta = "{\n";
		for (DeltaFunction transition: transtions) {
			delta += "   " + transition.toString() + "\n";
		}// for
		delta += "}\n";
		
		return delta;
	}// statesToString
	
	public String toString() {
		String ans = "";
		ans += this.type + ": \n"; 
		ans += "States = " + this.statesToString(this.states) + "\n";
		ans += "Alphabet = " + Arrays.toString(this.sigma.toArray()) + "\n";
		ans += "Start State(s) = " + this.statesToString(this.startStates) + "\n";
		ans += "Transition(s) = " + this.deltaToString(this.delta);
		ans += "Accepting State = " + this.statesToString(this.acceptedStates);
		
		System.out.println(ans);
		
		return ans;
	}// toString
}// FiniteAutomata
