import java.io.*;
import java.time.*;
import java.util.*;
 


public class Tp2 {
		
	private static void putToMap(TreeMap<String,TreeMap<LocalDate, Integer>> map, String med_label, LocalDate date, Integer q) {
		// add a new delivery to the pharmacy catalogue;
		// keeps also into account the possibility that a newly delivered stock of a given medicine could have 
		// the same expiration date of an already available batch
		
		TreeMap<LocalDate, Integer> stocks = map.get(med_label);
	    if (stocks == null) {
	        stocks = new TreeMap<LocalDate,Integer> ();
	        map.put(med_label, stocks);
	        stocks.put(date, q);
	    }
	    else {
	    	if (stocks.containsKey(date)) {
	    		Integer old_stock = stocks.get(date);
	    		stocks.replace(date, old_stock+q);
	    	}
	    	else {
	    		stocks.put(date, q);
	    		}
	    	}
	    }

	private static void updateStock(TreeMap<String,TreeMap<LocalDate, Integer>> map,LocalDate date) {
		// checks all the medicines in stock and removes the ones that are expired
		for (String label: map.keySet()) {
			TreeMap<LocalDate,Integer> stock_tocheck = map.get(label);
			stock_tocheck.keySet().removeIf((LocalDate exp_date) -> exp_date.isBefore(date.plusDays(1)));
			}
		}


	private static void addToOrders(TreeMap<String,Integer> list, String med_label, int quantity){
		// add a new order to the list of orders
		// keeps into account that several clients might request the same medicine, 
		// therefore it bundles all the order of the same medicine into one single order
		if (list.isEmpty()) {
			list.put(med_label,quantity);
			}
		else {
			if (list.containsKey(med_label)) {
				Integer old_quantity = list.get(med_label);
				list.replace(med_label, old_quantity+quantity);
			}
			else {
				list.put(med_label,quantity);
				}
			}
		}
		
	private static boolean isAvailable(TreeMap<String,TreeMap<LocalDate, Integer>> map, LocalDate now, String med_label,int therapy) {
		// checks if there are enough packages of a medicine 
		// to sell to a customer
		if (map.containsKey(med_label)) {
			TreeMap<LocalDate,Integer> stock_toinspect = map.get(med_label);	    					    
			
			for (LocalDate date : stock_toinspect.keySet()) {
			     Integer quant_in_stock = stock_toinspect.get(date);
			     if (therapy <= quant_in_stock && date.isAfter(now.plusDays(therapy-1))) {
			    	 stock_toinspect.replace(date, quant_in_stock-therapy);
			    	 if (stock_toinspect.get(date)==0) {
			    		 stock_toinspect.remove(date);
			    		 }
			    	 return true;
			    	 }
			     }
			return false;
			}
		else {
			return false;
			}
		}


	public static void main(String[] args) {
		
		File IN_fileName = new File(args[0]); 
		String OUT_fileName = args[1];
		
		LocalDate current_date = LocalDate.MIN;
		
		int client_idx = 0;
		
		
	    try {
	    	// read the given file args[0]
	    	Scanner scan = new Scanner(IN_fileName);
	    	scan.useDelimiter(";");
		    
		    // use the built-in structure of TreeMap where each key is the label of the medicine 
		    // and each value is a TreeMap where key = expiration date and value = stock of medicine
	    	// this way the sorting is already there and possibly the complexity is reduced to O(log k) instead of linear O(k)
		    TreeMap<String, TreeMap<LocalDate,Integer>> Apotheek_Catalogue  = new TreeMap<String, TreeMap<LocalDate,Integer>>();
		    
		    
		    // again, might wanna use another TREEMAP (key = label, value = amount to order) 
		    // to have the sorting for free and reduce complexity 
		    // (especially when updating the amount to order for a given medicine if a new customer requests it again 
		    // -- the search would then be O(log n), instead of O(n))
		    TreeMap<String,Integer> orders = new TreeMap<String, Integer>();
		    
	    	try {
	    		FileWriter filewriter = new FileWriter(OUT_fileName);
	    		BufferedWriter bufferedWriter = new BufferedWriter(filewriter);

		    
	    		while(scan.hasNext()) {
	    			
	    			// removes extra spaces and split the scanned block of text
	    			String line = scan.next().trim();
	    			String lineWithoutSpaces = line.replaceAll("\\s+", " ");
	    			String[] split = lineWithoutSpaces.split("\\s"); 
	    			
	    			// uses the switch method for a neater reading
	    			switch(split[0]) {
	    			case "APPROV":
	    				for (int i=2;i<split.length;i= i+ 3) {
	    					String label = split[i]; // medicine name
							int quant = Integer.parseInt(split[i+1]); // quantity of medicine
	    					LocalDate expiration_date = LocalDate.parse(split[i+2]); // expiration date of medicine
				    		
				    		putToMap(Apotheek_Catalogue,label, expiration_date, quant); 
				    		}
	    				bufferedWriter.write("APPROV OK");
	    				bufferedWriter.newLine();
	    				bufferedWriter.newLine();
	    				break;
	    				
	    			case "DATE":
	    				current_date = LocalDate.parse(split[1]);
	    				if (orders.isEmpty()) {
	    					bufferedWriter.write(current_date.toString() + " OK");
	    					bufferedWriter.newLine();
	    					}
	    				else {
	    					bufferedWriter.write(current_date.toString() + " COMMANDES :");
	    					bufferedWriter.newLine();
	    					for(Map.Entry<String,Integer> entry : orders.entrySet()) {
	    						  String label = entry.getKey();
	    						  Integer order = entry.getValue();
	    						  
	    						  bufferedWriter.write(label + String.format(" %-5d", order));
	    						  bufferedWriter.newLine();
	    						  }
	    					orders.clear();
	    					}
	    				bufferedWriter.newLine();
	    					    				
	    				break;
	    				
	    			case "STOCK":
	    				// checks all the medicines in stock and removes the ones that are expired
	    				updateStock(Apotheek_Catalogue, current_date);

	    				bufferedWriter.write("STOCK " + current_date.toString());
	    				bufferedWriter.newLine();
	    				
	    				for(Map.Entry<String,TreeMap<LocalDate,Integer>> entry : Apotheek_Catalogue.entrySet()) {
	    					 String label = entry.getKey();
	    					 TreeMap<LocalDate,Integer> list_of_stocks = entry.getValue();
	    					 
	    					 if (!list_of_stocks.isEmpty()) {
	    						 for (LocalDate date : list_of_stocks.keySet()) {
	    							 Integer quant = list_of_stocks.get(date);
	    							 bufferedWriter.write(String.format("%-15s", label) + String.format("%-5d", quant) + " " + date.toString());
		    						 bufferedWriter.newLine();
		    						 }
	    						 }
	    					 }
	    				bufferedWriter.newLine();
	    				break;
	    				
	    			case "PRESCRIPTION":
	    				client_idx++;
	    				bufferedWriter.write("PRESCRIPTION " + client_idx);
	    				bufferedWriter.newLine();
	    			
	    				for (int i=2;i<split.length;i= i+ 3) {
	    					String medicijn = split[i]; // medicine name
							int dose = Integer.parseInt(split[i+1]); // quantity of medicine
	    					int cycle = Integer.parseInt(split[i+2]); // cycles of treatment
	    					int total_therapy = dose*cycle;

	    					
	    					if (isAvailable(Apotheek_Catalogue,current_date,medicijn,total_therapy)) {
		    					bufferedWriter.write(String.format("%-15s", medicijn) + String.format("%-5d", dose) + String.format("%-5d", cycle) + " OK");
		    					bufferedWriter.newLine(); 
		    					}
	    					else {
		    					bufferedWriter.write(String.format("%-15s", medicijn) + String.format("%-5d", dose) + String.format("%-5d", cycle) + " COMMANDE");
		    					bufferedWriter.newLine(); 
		    					
		    					addToOrders(orders, medicijn, total_therapy);
	    						}
	    				}
	    						    				
	    				bufferedWriter.newLine();
	    				break;
	    			}
	    			
	    			
//	    			if (split[0].equals("APPROV")) {
//	    				for (int i=2;i<split.length;i= i+ 3) {
//	    					String label = split[i]; // medicine name
//							int quant = Integer.parseInt(split[i+1]); // quantity of medicine
//	    					LocalDate expiration_date = LocalDate.parse(split[i+2]); // expiration date of medicine
//				    		
//				    		putToMap(Apotheek_Catalogue,label, expiration_date, quant); 
//    	    			}
//	    				bufferedWriter.write("APPROV OK");
//	    				bufferedWriter.newLine();
//	    				bufferedWriter.newLine();
//	    			}
//	    			
//	    			if (split[0].equals("DATE")) {
//	    				current_date = LocalDate.parse(split[1]);
//	    				if (orders.isEmpty()) {
//	    					bufferedWriter.write(current_date.toString() + " OK");
//	    					bufferedWriter.newLine();
//	    				}
//	    				else {
//	    					bufferedWriter.write(current_date.toString() + " COMMANDES :");
//	    					bufferedWriter.newLine();
//	    					for(Map.Entry<String,Integer> entry : orders.entrySet()) {
//	    						  String label = entry.getKey();
//	    						  Integer order = entry.getValue();
//	    						  
//	    						  bufferedWriter.write(label + String.format(" %-5d", order));
//	    						  bufferedWriter.newLine();
//	    						}
//	    					orders.clear();
//	    				}
//	    				bufferedWriter.newLine();
//	    				
//	    				// checks all the medicines in stock and removes the ones that are expired
//	    				for (String label: Apotheek_Catalogue.keySet()) {
//	    					TreeMap<LocalDate,Integer> stock_tocheck = Apotheek_Catalogue.get(label);
//	    					Collection<LocalDate> set_dates = stock_tocheck.keySet();
//	    					Iterator<LocalDate> iterator = set_dates.iterator();
//	    					while(iterator.hasNext()) {
//	    				         LocalDate exp_date_tocheck = iterator.next();
//	    				         if (exp_date_tocheck.isBefore(current_date)) {
//		    							iterator.remove();
//		    						}
//	    				      }
//	    				}
//	    				
//	    			}
//	    			
//	    			if (split[0].equals("STOCK")) {
//	    				bufferedWriter.write("STOCK " + current_date.toString());
//	    				bufferedWriter.newLine();
//	    				
//	    				for(Map.Entry<String,TreeMap<LocalDate,Integer>> entry : Apotheek_Catalogue.entrySet()) {
//	    					 String label = entry.getKey();
//	    					 TreeMap<LocalDate,Integer> list_of_stocks = entry.getValue();
//	    					 
//	    					 if (!list_of_stocks.isEmpty()) {
//	    						 for (LocalDate date : list_of_stocks.keySet()) {
//	    							 Integer quant = list_of_stocks.get(date);
//	    							 bufferedWriter.write(String.format("%-15s", label) + String.format("%-5d", quant) + " " + date.toString());
//		    						 bufferedWriter.newLine();
//	    						 }
//	    					 }
//	    				}
////	    				for (String key : Apotheek_Catalogue.keySet()) {
////	    					LinkedList<Medicine_stock> expdates = Apotheek_Catalogue.get(key);
////	    					if (!expdates.isEmpty()) {
////	    						bufferedWriter.write(key);
////		    					for(int num=0; num<expdates.size(); num++) {
////		    						int n = expdates.get(num).quantity;
////		    						LocalDate d = expdates.get(num).expiration_date;
////		    						bufferedWriter.write(String.format(" %-5d", n) + " " + d.toString());
////		    						bufferedWriter.newLine();
////		    					}
////	    					}
////	    				}
//
//	    				bufferedWriter.newLine();
//	    			}
//	    			else if (split[0].equals("PRESCRIPTION")) {
//	    				client_idx++;
//	    				bufferedWriter.write("PRESCRIPTION " + client_idx);
//	    				bufferedWriter.newLine();
//	    			
//	    				for (int i=2;i<split.length;i= i+ 3) {
//	    					String medicijn = split[i]; // medicine name
//							int dose = Integer.parseInt(split[i+1]); // quantity of medicine
//	    					int cycle = Integer.parseInt(split[i+2]); // cycles of treatment
//	    					int total_therapy = dose*cycle;
//	    					
//	    					if (Apotheek_Catalogue.containsKey(medicijn)) {
//	    						TreeMap<LocalDate,Integer> stock_toinspect = Apotheek_Catalogue.get(medicijn);	    					    
//		    					Boolean found = false;
//		    					
//		    					for (LocalDate date : stock_toinspect.keySet()) {
//		    					     Integer quant_in_stock = stock_toinspect.get(date);
//		    					     if (total_therapy <= quant_in_stock && date.isAfter(current_date.plusDays(total_therapy-1))) {
//		    					    	 stock_toinspect.replace(date, quant_in_stock-total_therapy);
//		    					    	 if (quant_in_stock==0) {
//		    					    		 stock_toinspect.remove(date);
//		    					    		 }
//		    					    	 found = true;
//		    					    	 break;
//		    					    	 }
//		    					   }
//
//		    							    					
//		    					if (found) {
//			    					bufferedWriter.write(String.format("%-15s", medicijn) + String.format("%-5d", dose) + String.format("%-5d", cycle) + " OK");
//			    					bufferedWriter.newLine(); 
//		    					}
//	    						else {
//			    					bufferedWriter.write(String.format("%-15s", medicijn) + String.format("%-5d", dose) + String.format("%-5d", cycle) + " COMMANDE");
//			    					bufferedWriter.newLine(); 
//			    					
//			    					addToOrders(orders, medicijn, total_therapy);
//	    						}
//
//	    					}
//	    					else {
//		    					bufferedWriter.write(String.format("%-15s", medicijn) + String.format("%-5d", dose) + String.format("%-5d", cycle) + " COMMANDE");
//		    					bufferedWriter.newLine(); 
//		    					
//		    					addToOrders(orders, medicijn, total_therapy);
//		    					}
//	    					
//	    					
//
//    					
//	    				}
//	    				bufferedWriter.newLine();
//	    			}
	    			
	    			
	    			}
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
 
//        catch(FileNotFoundException ex) {
//            System.out.println("Unable to open file '" + IN_fileName + "'");                
//        }
	    
	    
		    
        }
        


}
