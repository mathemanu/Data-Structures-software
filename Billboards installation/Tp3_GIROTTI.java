import java.util.*;
import java.io.*;


public class Tp3 {
	
	private static class Street{
		
		public String name;
		public String depart;
		public String arrive;
		public Double cost;
		
		public Street(String rue, String start, String end, Double c){
            name = rue;
            depart = start;
            arrive = end;
            cost = c;
        }
	}	

	
	private static void addVertexToGraph(TreeMap<String,LinkedList<Street>> citygraph,String v) {
		LinkedList<Street> edges = new LinkedList<Street>();
		citygraph.put(v,edges);
	}
	
	private static void addEdgeToGraph(TreeMap<String,LinkedList<Street>> citygraph, Street newStreet) {
		// add the new edge to both the list of edges of the starting vertex AND
		// to the list of edges of the ending vertex
		citygraph.get(newStreet.depart).add(newStreet);
		
		Street newStreet2 = new Street(newStreet.name, newStreet.arrive, newStreet.depart,newStreet.cost);
		citygraph.get(newStreet.arrive).add(newStreet2);
	}
	
	private static TreeSet<Street> PrimJarnik(TreeMap<String,LinkedList<Street>> citygraph) {
		// Prim's algorithm
		
		Comparator<Street> MST_comparator = new Comparator<Street>() {
            @Override
            public int compare(Street str1, Street str2) {
            	if (!str1.depart.equals(str2.depart)) {
            		return str2.depart.compareTo(str1.depart);
            	}
            	else {
            		return str2.arrive.compareTo(str1.arrive);
            	}
            }
        };
		
		Comparator<Street> Q_comparator = new Comparator<Street>() {
            @Override
            public int compare(Street str1, Street str2) {
    	    	if (str1.cost != str2.cost) {
    	    		return str1.cost.compareTo(str2.cost);
    	    	}
    	    	else {
    	    		if (!str1.depart.equals(str2.depart)) {
    	    			return str1.depart.compareTo(str2.depart);
    	    		}
    	    		else {
    	    			return str1.arrive.compareTo(str2.arrive);
    	    		}
    	    		
    	    	}
            }
        };

		
		HashSet<String> visited_vertices = new HashSet<String>();
		PriorityQueue<Street> PrimMST = new PriorityQueue<Street>(Q_comparator);
		TreeSet<Street> MSTree = new TreeSet<Street>(MST_comparator);
		TreeSet<Street> newMSTree = new TreeSet<Street>(MST_comparator);

		
		String startV = citygraph.firstKey();
		visited_vertices.add(startV);
		citygraph.get(startV).forEach(item->PrimMST.add(item));

		while (visited_vertices.size() < citygraph.size()) {
			Street toinspect = PrimMST.poll();
			if (!visited_vertices.contains(toinspect.arrive)){
				MSTree.add(toinspect);
				visited_vertices.add(toinspect.arrive);
				citygraph.get(toinspect.arrive).forEach(item->PrimMST.add(item));
			}
		}
		
		// extra few lines of code to comply with the request that
		// one needs to print the minimum spanning tree with first vertex and 
		// second vertex in lexicographic order
		while (!MSTree.isEmpty()) {
			Street tocheck = MSTree.pollFirst();
			if (tocheck.depart.compareTo(tocheck.arrive)>0) {
				Street updatestr = new Street(tocheck.name,tocheck.arrive,tocheck.depart,tocheck.cost);
				newMSTree.add(updatestr);
			}
			else {
				newMSTree.add(tocheck);
			}
		}
		
		return newMSTree;
	}
		

	public static void main(String[] args) {

		File IN_fileName = new File(args[0]); 
		String OUT_fileName = args[1];
		


				
		TreeMap<String,LinkedList<Street>> citygraph = new TreeMap<String,LinkedList<Street>>();

	    try {
	    	// read the given file args[0]
	    	Scanner scan = new Scanner(IN_fileName);
		    
	    	try {
	    		// write on the file named args[1]
	    		FileWriter filewriter = new FileWriter(OUT_fileName);
	    		BufferedWriter bufferedWriter = new BufferedWriter(filewriter);

		    
	    		while(scan.hasNextLine()) {
	    			String token = scan.nextLine();
	    			char delimiter = '-';
	    			if (token.charAt(0) == delimiter) {
	    				break;
	    			}
	    			else {
	    				token = token.trim();
	    				addVertexToGraph(citygraph,token);
	    			}
	    		}
	    	
	    	
	    	while(scan.hasNextLine()) {
	    		String token = scan.nextLine();
	    		char delimiter = '-';
    			if (token.charAt(0) == delimiter) {
    				break;
    			}
    			else {
    				String[] split = token.split("[: ;]+");
    				String newStreetName = split[0];
    	    		String newStartPoint = split[1];
    	    		String newEndPoint = split[2];
    	    		String newCost_str = split[3];

    	    		Double newCost = Double.parseDouble(newCost_str);
    	    		Street newStreet = new Street(newStreetName, newStartPoint, newEndPoint, newCost);
    	    		
    	    		addEdgeToGraph(citygraph,newStreet);
    			}
	    		
	    	}
	    	
	    	
	    	for (String label: citygraph.keySet()) {	
	    		bufferedWriter.write(label);
	    		bufferedWriter.newLine();				
				}
	    	// *** Prim's algorithm ***
	    	TreeSet<Street> ARMtree = PrimJarnik(citygraph);			
	    	
	    	double total_cost = 0;
	    	
	    	
	    	for (Street straat: ARMtree.descendingSet()) {
				bufferedWriter.write(String.format("%-10s",straat.name) + String.format("%-8s",straat.depart) + 
						String.format("%-8s",straat.arrive)+ String.format("%-10.0f",straat.cost)  );
				total_cost += straat.cost;
				bufferedWriter.newLine();
				}
	    	
	    	
	    	bufferedWriter.write("---");
	    	bufferedWriter.newLine();
	    	bufferedWriter.write(String.format("%-10.0f",total_cost));

	    	


	    	scan.close();
	    	bufferedWriter.close();
	    	
	    	}
		    catch(IOException ex) {
	            System.out.println("Error writing file '" + OUT_fileName + "'");
	        }

	    	
	    }
	    catch(IOException ex) {
	    	System.out.println("Error reading file '" + IN_fileName + "'");                  
	    }	    
	    
		    
        }



}
