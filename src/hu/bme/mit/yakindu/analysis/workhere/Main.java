package hu.bme.mit.yakindu.analysis.workhere;

import java.awt.List;
import java.util.ArrayList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.Region;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Vertex;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		ArrayList<String> names = new ArrayList<String>();
		
		// Reading model
		Statechart s = (Statechart) root;
		for(Region r : s.getRegions()) {
			EList<Vertex> verts = r.getVertices();
			System.out.println("csapda állapotok:");
			for(Vertex v : verts) {
				if(v instanceof State) {
					State state = (State) v;
					if(state.getName() != null || state.getName() != "") {
						if(state.getOutgoingTransitions().size() == 0) {
							System.out.println(state.getName());
						}
						names.add(state.getName());
					}
					
				}
			}			
		}
		System.out.println();
		int n = 0;
		for(Region r : s.getRegions()) {
			EList<Vertex> verts = r.getVertices();
			System.out.println("Ajánlott nevek:");
			for(Vertex v : verts) {
				if(v instanceof State) {
					State state = (State) v;						
					if(state.getName() == null || state.getName() == "") {
						while(names.contains("State "+n)) {
							n++;
						}
						System.out.println("State "+n);
						names.add("State " + n);
					}
				}
			}			
		}
		System.out.println();

		
		TreeIterator<EObject> iterator = s.eAllContents();	
		State prevState = null;		
		while (iterator.hasNext()) {
			
			EObject content = iterator.next();
			
			if(content instanceof State) {
				State state = (State) content;
				if(prevState != null) {
					System.out.print(" "+prevState.getName()+" -> "+state.getName()+"\n");					
				}
				prevState = state;
				System.out.print(state.getName());
			}
		}
		
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
