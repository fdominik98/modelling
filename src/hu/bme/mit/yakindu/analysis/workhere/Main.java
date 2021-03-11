package hu.bme.mit.yakindu.analysis.workhere;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.Region;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
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
		
		while (iterator.hasNext()) {
			
			EObject content = iterator.next();
			
			if(content instanceof State) {
				State state = (State) content;				
				System.out.print(state.getName());
				EList<Transition> t = state.getOutgoingTransitions();
				for(int i = 0; i< t.size(); i++) {
					Vertex v = t.get(i).getTarget();
					if(v instanceof State) {
						State s2 = (State)v;
						System.out.print("  "+ state.getName() + "->" + s2.getName());
					}
				}
				System.out.println();
			}
		}
		
		System.out.println();
		System.out.println();
		EList<Scope> scopes = s.getScopes();
		for(int i = 0; i<scopes.size();i++) {
			EList<EObject> content = scopes.get(i).getMembers();
			for(int j = 0; j< content.size();j++) {
				if(content.get(j) instanceof EventDefinition) {
					EventDefinition ed = (EventDefinition) content.get(j);
					System.out.println(ed.getName());
				}
				if(content.get(j) instanceof VariableDefinition) {
					VariableDefinition ed = (VariableDefinition) content.get(j);
					System.out.println(ed.getName());
				}
					
			}
		}
		
		
		System.out.println();
		System.out.println();
		
		
		
		scopes = s.getScopes();
		ArrayList<String> stateNames = new ArrayList<String>();
		ArrayList<String> eventNames = new ArrayList<String>();
		scopes = s.getScopes();
		for(int i = 0; i<scopes.size();i++) {
			EList<EObject> content = scopes.get(i).getMembers();
			for(int j = 0; j< content.size();j++) {
				if(content.get(j) instanceof EventDefinition) {
					EventDefinition ed = (EventDefinition) content.get(j);					
					eventNames.add(ed.getName());
				}
				if(content.get(j) instanceof VariableDefinition) {
					VariableDefinition vd = (VariableDefinition) content.get(j);
					stateNames.add(vd.getName());
				}
					
			}
		}		
		System.out.println("public static void print(IExampleStateMachine s) {");
		for(int i = 0; i< stateNames.size() ; i++ ){
			String name = stateNames.get(i);
			String upperName = name.substring(0,1).toUpperCase()+name.substring(1,name.length());
			System.out.println("System.out.println(\""+upperName.substring(0,1)+" = \" + s.getSCInterface().get"+upperName+"());");
		}	
		System.out.println("}");
		
		
		
		
		System.out.println();
		System.out.println();
		
		
		
		
		
		
		System.out.println( "public static void main(String[] args) throws IOException {\n" + 
				"	ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"	s.setTimer(new TimerService());\r\n" + 
				"	RuntimeService.getInstance().registerStatemachine(s, 200);\n" + 
				"	s.init(); \n" + 
				"	s.enter();\n" + 
				"	s.runCycle();\n" + 				 
				"	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));\n" + 
				"	while(true) {\n" + 
				"		String line = reader.readLine();	"); 
				for(int i = 0; i< eventNames.size();i++) {
					String name = eventNames.get(i);
					String upperName = name.substring(0,1).toUpperCase()+name.substring(1,name.length());
					System.out.println(
					"		if(line.equals(\""+name+"\")) {\n" + 
					"			s.raise"+upperName+"();\n" + 
					"			s.runCycle();\n" + 
					"			print(s);\n" + 
					"		}"
					);
				}				
				System.out.println(
				"		if(line.equals(\"exit\")) {\n" + 
				"			print(s);\n" + 
				"			System.exit(0);\n" + 
				"		}\n" + 
				"	}\n" + 
				"}\n"); 
		
				System.out.println("public static void print(IExampleStatemachine s) {"); 
				for(int i = 0; i< stateNames.size() ; i++ ){
					String name = stateNames.get(i);
					String upperName = name.substring(0,1).toUpperCase()+name.substring(1,name.length());
					System.out.println("	System.out.println(\""+upperName.substring(0,1)+" = \" + s.getSCInterface().get"+upperName+"());");
				}				 
				System.out.println("}");
		
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
