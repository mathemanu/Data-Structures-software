import java.io.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner; 
import java.lang.Math;
import java.util.Arrays;


public class Tp1 {
	
	private static class Building{
		
		public double longitude;
		public double latitude;
		public int Nboxes;
		public double dist;
		
		public Building(double y_coord, double x_coord, int N, double d){
            latitude = y_coord;
            longitude = x_coord;
            Nboxes = N;
            dist = d;
        }
	}
	
	
	// *** sorting algorithm ***
	
	
	// merge sort by latitude
	private static Building[] merge_latitude(Building[] building_left, Building[] building_right) {
		
		Building[] tot_building = new Building[building_left.length + building_right.length];
        int i = 0, j = 0;
        
        for (int k = 0; k < tot_building.length; k++) {
            if (i >= building_left.length) { 
            	tot_building[k] = building_right[j++];
            	}
            else if (j >= building_right.length) {
            	tot_building[k] = building_left[i++];
            }
            else if (building_left[i].latitude <= building_right[j].latitude) {
            	tot_building[k] = building_left[i++];
            }
            else {
            	tot_building[k] = building_right[j++];
            }
        }
        return tot_building;
    }

    private static Building[] mergesort_latitude(Building[] input) {
        int L = input.length;
        if (L <= 1) return input;
        
        Building[] building_left = new Building[L/2];
        Building[] building_right = new Building[L - L/2];
        
        for (int i = 0; i < building_left.length; i++)
            building_left[i] = input[i];
        for (int i = 0; i < building_right.length; i++)
            building_right[i] = input[i + L/2];
        
        return merge_latitude(mergesort_latitude(building_left), mergesort_latitude(building_right));
    }

	
	// merge sort by longitude
	private static Building[] merge_longitude(Building[] building_left, Building[] building_right) {
		
		Building[] tot_building = new Building[building_left.length + building_right.length];
        int i = 0, j = 0;
        
        for (int k = 0; k < tot_building.length; k++) {
            if (i >= building_left.length) { 
            	tot_building[k] = building_right[j++];
            	}
            else if (j >= building_right.length) {
            	tot_building[k] = building_left[i++];
            }
            else if (building_left[i].longitude <= building_right[j].longitude) {
            	tot_building[k] = building_left[i++];
            }
            else {
            	tot_building[k] = building_right[j++];
            }
        }
        return tot_building;
    }

    private static Building[] mergesort_longitude(Building[] input) {
        int L = input.length;
        if (L <= 1) return input;
        
        Building[] building_left = new Building[L/2];
        Building[] building_right = new Building[L - L/2];
        
        for (int i = 0; i < building_left.length; i++)
            building_left[i] = input[i];
        for (int i = 0; i < building_right.length; i++)
            building_right[i] = input[i + L/2];
        
        return merge_longitude(mergesort_longitude(building_left), mergesort_longitude(building_right));
    }


	// merge sort by distance
	private static Building[] merge_dist(Building[] building_left, Building[] building_right) {
		
		Building[] tot_building = new Building[building_left.length + building_right.length];
        int i = 0, j = 0;
        
        for (int k = 0; k < tot_building.length; k++) {
            if (i >= building_left.length) { 
            	tot_building[k] = building_right[j++];
            	}
            else if (j >= building_right.length) {
            	tot_building[k] = building_left[i++];
            }
            else if (building_left[i].dist <= building_right[j].dist) {
            	tot_building[k] = building_left[i++];
            }
            else {
            	tot_building[k] = building_right[j++];
            }
        }
        return tot_building;
    }

    private static Building[] mergesort_dist(Building[] input) {
        int L = input.length;
        if (L <= 1) return input;
        
        Building[] building_left = new Building[L/2];
        Building[] building_right = new Building[L - L/2];
        
        for (int i = 0; i < building_left.length; i++)
            building_left[i] = input[i];
        for (int i = 0; i < building_right.length; i++)
            building_right[i] = input[i + L/2];
        
        return merge_dist(mergesort_dist(building_left), mergesort_dist(building_right));
    }

    public static Building[] triple_mergesort(Building[] input) {

        Building[] sorted_bldg = new Building[input.length]; 
        
        sorted_bldg = mergesort_latitude(input);
        sorted_bldg = mergesort_longitude(sorted_bldg);
        sorted_bldg = mergesort_dist(sorted_bldg);
        
        return sorted_bldg;
    }

    // checking if array is indeed sorted (useful for debugging)
//    private static boolean isSorted(Building[] building_array) {
//        for (int i = 1; i < building_array.length; i++)
//            if (building_array[i].dist < building_array[i-1].dist 
//            		|| ( (building_array[i].dist == building_array[i-1].dist) 
//            				&& (building_array[i].longitude < building_array[i-1].longitude)) 
//            		|| ( (building_array[i].dist == building_array[i-1].dist) 
//            				&& (building_array[i].longitude == building_array[i-1].longitude) 
//            				&& (building_array[i].latitude < building_array[i-1].latitude)) ) {
//            	return false;
//            }
//        return true;
//    }

    
    // *** end sorting algorithm ***

	public static void main(String[] args) {
		// args[0] = file to read, input
		// args[1] = file to write, output
		
//		long start = System.currentTimeMillis();
		
		File fileName = new File(args[0]); 
		

        try {
        	// read the given file args[0]
        	
		    Scanner scan = new Scanner(fileName); 
			
		    // use of the built-in structure of LinkedList
		    LinkedList<Building> building_list = new LinkedList<Building>();
		    
		    // process the first line of the file
		    // tot_boxes = total number of boxes to be shipped
		    // max_capacity = maximum capacity of the truck
		    
		    int tot_boxes = scan.nextInt();
		    int max_capacity = scan.nextInt();
		    
		    if (tot_boxes > max_capacity) {
		    	System.out.println("Attention: the number of requested boxes exceeds the maximum capacity of the truck.");
		    	System.out.println("Only " + String.format("%d",max_capacity)+  "boxes will be loaded in the truck.");
		    }
		    
		    
		    // process the remaining lines of the file
		    // construction of a linked list of Buildings 
		    
		    while (scan.hasNext()) {		    	
		    	
		    	int Nboxes = scan.nextInt();	// number of boxes in the building
		    	
		    	// process the location (longitude,latitude) of the building
		    	String coords = scan.next();		    	
		    	String[] coords_list = coords.substring(1).split(",|\\)");
		    	double y_coord = Double.parseDouble(coords_list[0]); 
		    	double x_coord = Double.parseDouble(coords_list[1]);
		    	
		    	Building bldg = new Building(y_coord,x_coord,Nboxes,0.0);
		    	building_list.add(bldg);
		    }
		    scan.close();
		    
		    
		    ListIterator<Building> iterator = building_list.listIterator();
		    
		    // identify the main point of service (POS), 
		    // i.e. the building with the biggest number of boxes
		    
		    double x_POS = 0;
		    double y_POS = 0;
		    
		    int max_Nboxes = 0;
		    
		    while (iterator.hasNext()) {
		    	Building bldg_toinspect = iterator.next();
		    	if (max_Nboxes < bldg_toinspect.Nboxes) {
		    		max_Nboxes = bldg_toinspect.Nboxes;
		    		x_POS = bldg_toinspect.longitude;
		    		y_POS = bldg_toinspect.latitude;
		    	}
		    }
		    

		    // calculation of the distance between the main point of service (POS) 
		    // and the other buildings; use of the haversine formula
		    // (the formula with "atan2" is equivalent to the formula on Wikipedia with arcsin)
		    
		    iterator = building_list.listIterator(0);
		    double R = 6371e3; // radius of the Earth in meters
		    double phi1 = Math.toRadians(y_POS);
		    
		    while (iterator.hasNext()) {
		    	Building bldg = iterator.next();
		    	
		    	double phi2 = Math.toRadians(bldg.latitude);
		    	double Dphi = Math.toRadians(bldg.latitude-y_POS);
		    	double Dlambda = Math.toRadians(bldg.longitude-x_POS);

		    	double a = Math.sin(Dphi/2) * Math.sin(Dphi/2) +
		    	        Math.cos(phi1) * Math.cos(phi2) *
		    	        Math.sin(Dlambda/2) * Math.sin(Dlambda/2);
		    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));  

		    	bldg.dist = R * c;
		    }

		    
		    // use of merge sort algorithm for sorting the buildings (3 times)
		    // 1) order by increasing latitude, 
		    // 2) order by increasing longitude
		    // 3) order by increasing distance
		    // application of the sorting algorithm to an Array of Buildings 
		    
		    Building[] building_array = building_list.toArray(new Building[building_list.size()]);
		    building_array = triple_mergesort(building_array);
		    
		    
//		    System.out.println(isSorted(building_array));
//		    LinkedList<Building> sorted_building_list = new LinkedList<Building>(Arrays.asList(building_array));
		    
		    // loading of the truck by consecutively visiting the nearest buildings 
		    // until the maximum capacity of the truck or the requested amount of boxes 
		    // is reached, whichever is smaller
		    
//		    ListIterator<Building> iterator_sorted = sorted_building_list.listIterator();
		    int loaded_boxes = 0;
		    
		    for (int i=0; i<building_array.length; i++) {
		    	loaded_boxes += building_array[i].Nboxes;
		    	
		    	if (loaded_boxes < Math.min(max_capacity,tot_boxes)) {
		    		building_array[i].Nboxes =0;
		    	}
		    	else {
		    		building_array[i].Nboxes = loaded_boxes - Math.min(tot_boxes,max_capacity);
		    		break;
		    	}
		    }
		    
//		    while (iterator_sorted.hasNext()) {
//		    	Building bldg = iterator_sorted.next();
//		    	loaded_boxes += bldg.Nboxes;
//		    	
//		    	if (loaded_boxes < Math.min(max_capacity,tot_boxes)) {
//		    		bldg.Nboxes =0;
//		    	}
//		    	else {
//		    		bldg.Nboxes = loaded_boxes - Math.min(tot_boxes,max_capacity);
//		    		break;
//		    	}
//		    	
//		    }

		    
		   		    
		    // write the file args[1]
		    
//		    iterator_sorted = sorted_building_list.listIterator(0);
		    String OUT_fileName = args[1];
		    
		    try {
		    	FileWriter fileWriter = new FileWriter(OUT_fileName);

	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);	            

	            bufferedWriter.write("Truck position: (" + y_POS + "," + x_POS + ")");
	            bufferedWriter.newLine(); 
	            
	            for (int i =0; i<building_array.length; i++) {
	            	bufferedWriter.write("Distance: " + String.format("%-14.1f",building_array[i].dist) + 
		            		"Number of boxes: " + String.format("%-10d",building_array[i].Nboxes) + 
		            		"Position: " + String.format("(%.4f,%.4f)",building_array[i].latitude,building_array[i].longitude));
		            	bufferedWriter.newLine();
	            }
	            
//	            while (iterator_sorted.hasNext()) {
//	            	Building bldg_toprint = iterator_sorted.next();
//	            	bufferedWriter.write("Distance: " + String.format("%-14.1f",bldg_toprint.dist) + 
//	            		"Number of boxes: " + String.format("%-10d",bldg_toprint.Nboxes) + 
//	            		"Position: " + String.format("(%f,%f)",bldg_toprint.latitude,bldg_toprint.longitude));
//	            	bufferedWriter.newLine(); 
//	            }                                                                            

	            bufferedWriter.close();
	            
//			    System.out.println("Length of the linked list: " + building_list.size());
//			    
//			    long end = System.currentTimeMillis();
//			    long tot_time = end-start;
//			    System.out.println("Took : " + tot_time + " ms");

		    }
		    
		    catch(IOException ex) {
	            System.out.println("Error writing to file '"+ fileName + "'");
	        }
        }
        
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        
//        catch(IOException ex) {
//            System.out.println("Error reading file '" + fileName + "'");                  
//        }
 
	}

}
