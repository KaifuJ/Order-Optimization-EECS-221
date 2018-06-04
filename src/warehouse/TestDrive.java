package warehouse;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TestDrive {
    public static void main(String[] args) {

        Warehouse wh = new Warehouse(1, 1, 1, 1, 39, 21);

        if (Integer.parseInt(str) == 2) {
            System.out.print("Please input the file name containing orders: ");
            String filename = in.nextLine();

            File inputfile = new File(filename);
            BufferedReader br1;
            try{
                br1 = new BufferedReader(new FileReader(inputfile));
            }catch(FileNotFoundException e){
                System.out.println("Can't find the file.");
                return;
            }

            ArrayList<ArrayList<Integer>> orders = new ArrayList<>();
            String line = null;
            try {
                while ((line = br1.readLine()) != null) {
                    String[] or = line.split("\t");
                    ArrayList<Integer> order = new ArrayList<>();
                    for (int i = 0; i < or.length; i++) {
                        order.add(Integer.parseInt(or[i]));
                    }
                    orders.add(order);
                }
            } catch (IOException e) {
                System.out.println("IOException occurs!");
            }

            for (int i = 0; i < orders.size(); i++) {
                ArrayList<Integer> order = orders.get(i);

                ArrayList<int[]> location = new ArrayList<>();
                location.add(start);
                location.add(end);

                for (int ii = 0; ii < order.size(); ii++) {
                    for (int j = 0; j < 30000; j++) {
                        if ((int) allInfo[j][0] == order.get(ii)) {
                            double decimal = allInfo[j][1] - (int) allInfo[j][1];
                            int x = 2 * (int) allInfo[j][1];
                            int y = 2 * (int) allInfo[j][2];
                            if (decimal <= 0.5) {
                                x--;
                            } else {
                                x++;
                            }
                            int[] loca = {x, y};
                            location.add(loca);
                        }
                    }
                }

                double[][] distances = new double[location.size()][location.size()];
                for (int ii = 0; ii < location.size(); ii++) {
                    for (int j = 0; j < location.size(); j++) {
                        if (ii == j) {
                            distances[ii][j] = 0;
                        } else {
                            distances[ii][j] = wh.shortestPath(location.get(ii), location.get(j));
                        }
                    }
                }

//                wh.orderShortestPath(distances, order, location, weightInfo);
                System.out.println("-------------------------------------");
            }
        } else {
            System.out.println("Please follow the instruction!");
        }
    }
}
















