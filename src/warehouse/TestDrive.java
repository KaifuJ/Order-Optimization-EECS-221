package warehouse;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TestDrive {
    public static void main(String[] args) {
        File file = new File("/home/kaifuj/Classes/eecs221/project/src/warehouse-grid.csv");
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException e){
            System.out.println("Can't find the file.");
            return;
        }

        String s;
        double[][] allInfo = new double[30000][3];
        int index = 0;
        try {
            while ((s = br.readLine()) != null) {
                String[] row = s.split(", ");
                for (int i = 0; i < 3; i++) {
                    allInfo[index][i] = Double.parseDouble(row[i]);
                }
                index++;
            }
        } catch (IOException e) {
            System.out.println("IOException occurs!");
        }

        File weightFile = new File("/home/kaifuj/Classes/eecs221/project/src/item-dimensions-tabbed.txt");
        try {
            br = new BufferedReader(new FileReader(weightFile));
        } catch (FileNotFoundException e) {
            System.out.println("Can't find the file");
            return;
        }

        Map<Integer, Double> weightInfo = new HashMap<>(20000);
        try {
            int count = 0;
            while ((s = br.readLine()) != null) {
                if (count == 0) {
                    count++;
                    continue;
                }
                String[] row = s.split("\t");
                int id = Integer.parseInt(row[0]);
                double weight = Double.parseDouble(row[row.length - 1]);
                weightInfo.put(id, weight);
            }
        } catch (IOException e) {
            System.out.println("IOException occurs!");
        }

        /******************/

        Scanner in = new Scanner(System.in);
        System.out.println("Please select the method you want to input order(s):\n" +
                "1: Manually input    2: File input");
        String str = in.nextLine();

        int[] start = new int[2];
        System.out.println("Please input your start location:");
        System.out.print("x = ");
        start[0] = Integer.parseInt(in.nextLine());
        System.out.print("y = ");
        start[1] = Integer.parseInt(in.nextLine());

        int[] end = new int[2];
        System.out.println("Please input your end location:");
        System.out.print("x = ");
        end[0] = Integer.parseInt(in.nextLine());
        System.out.print("y = ");
        end[1] = Integer.parseInt(in.nextLine());

        Warehouse wh = new Warehouse(1, 1, 1, 1, 39, 21);


        if (Integer.parseInt(str) == 1) {

            System.out.println("Please input all items. Use ',' to sepreate them:");
            str = in.nextLine();
            String[] temp = str.split(",");

            ArrayList<Integer> items = new ArrayList<>();
            for (String i : temp) {
                items.add(Integer.parseInt(i));
            }

            ArrayList<int[]> location = new ArrayList<>();
            location.add(start);
            location.add(end);

            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < 30000; j++) {
                    if ((int) allInfo[j][0] == items.get(i)) {
                        int x = 2 * (int) allInfo[j][1];
                        int y = 2 * (int) allInfo[j][2];

                        int[] left = {x - 1, y};
                        int[] right = {x + 1, y};
                        location.add(left);
                        location.add(right);
                    }
                }
            }

            double[][] distances = new double[location.size()][location.size()];
            for (int i = 0; i < location.size(); i++) {
                for (int j = 0; j < location.size(); j++) {
                    if (i == j) {
                        distances[i][j] = 0;
                    } else {
                        distances[i][j] = wh.shortestPath(location.get(i), location.get(j));
                    }
                }
            }

//            wh.orderShortestPath(distances, items, location, weightInfo);

        }


        else if (Integer.parseInt(str) == 2) {
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
















