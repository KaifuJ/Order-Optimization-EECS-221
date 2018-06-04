package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import warehouse.Warehouse;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField text;
    @FXML
    private RadioButton manInput;
    @FXML
    private RadioButton byFile;
    @FXML
    private Button submit;
    @FXML
    private TextField startX;
    @FXML
    private TextField startY;
    @FXML
    private TextField endX;
    @FXML
    private TextField endY;
    @FXML
    private VBox yCod;
    @FXML
    private HBox xCod1;
    @FXML
    private HBox xCod2;
    @FXML
    private VBox centerVBox;
    @FXML
    private HBox centerHBox;
    @FXML
    private TextField weightBound;
    @FXML
    private TreeView tree;

    private double[][] allInfo;
    private Map<Integer, Double> weightInfo;
    private Warehouse wh;
    private int[] start;
    private int[] end;

    Rectangle[][] cells;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 10, 10, 10));
        grid.setAlignment(Pos.TOP_LEFT);
        this.cells = new Rectangle[21][39];

        for (int i = 0; i < 21; i++) {
            String c = null;
            if ( 20 - i < 10) {
                c = " " + Integer.toString( 20 - i);
            } else {
                c = Integer.toString(20 - i);
            }
            Label y = new Label(c);
            this.yCod.getChildren().add(y);
        }

        for (int i = 0; i < 39; i++) {
            String c = null;
            if (i - 1 < 10) {
                c = Integer.toString(i - 1);
                Label x = new Label(c);
                this.xCod1.getChildren().add(x);
            } else {
                c = Integer.toString(i - 1);
                Label x = new Label(c);
                this.xCod2.getChildren().add(x);
            }
        }

        for (int i = 0; i < cells.length; i++) { // 21
            for (int j = 0; j < cells[0].length; j++) { // 39
                cells[i][j] = new Rectangle();
                cells[i][j].setWidth(20);
                cells[i][j].setHeight(20);
                GridPane.setConstraints(cells[i][j], j, i);
                if ((j - 1) % 2 == 0 && (20 - i) % 2 == 0) {
                    cells[i][j].setFill(Color.RED);
                } else {
                    cells[i][j].setFill(Color.BLUE);
                }
                grid.getChildren().add(cells[i][j]);
            }
        }
        grid.setGridLinesVisible(true);

        centerHBox.getChildren().addAll(grid);

        borderPane.setCenter(centerVBox);

        /******************************************************************/

        File file = new File("/home/kaifuj/Classes/eecs221/project/src/warehouse-grid.csv");
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException e){
            System.out.println("Can't find the file.");
            return;
        }

        String s;
        this.allInfo = new double[30000][3];
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

        this.weightInfo = new HashMap<>(20000);
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

        this.wh = new Warehouse(1, 1, 1, 1, 39, 21);
        this.start = new int[2];
        this.end = new int[2];
    }

    public void submitClicked(){
        this.refreshColor();

        String input;
        if (manInput.isSelected()) {
            input = text.getText();
            this.handleManInput(input);
        } else {
            input = text.getText();
            double weightBound = Double.parseDouble(this.weightBound.getText());
            this.handleByFile(input, weightBound);
        }
    }

    private void handleManInput(String order){

        this.start[1] = Integer.parseInt(startX.getText());
        this.start[0] = Integer.parseInt(startY.getText());
        this.end[1] = Integer.parseInt(endX.getText());
        this.end[0] = Integer.parseInt(endY.getText());

        String str = this.text.getText();
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

        File f = new File("test.txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path file = Paths.get("test.txt");
        wh.orderShortestPath(distances, items, location, weightInfo, this.cells, file);
    }

    private void handleByFile(String filename, double weightBound) {
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

        this.start[1] = Integer.parseInt(startX.getText());
        this.start[0] = Integer.parseInt(startY.getText());
        this.end[1] = Integer.parseInt(endX.getText());
        this.end[0] = Integer.parseInt(endY.getText());

        ArrayList<ArrayList<Integer>> orderStatus = wh.ordersReorganize(orders, this.weightInfo, weightBound);

        File f = new File("batch.txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path file = Paths.get("batch.txt");

        for (int i = 0; i < orders.size(); i++) {
            if (orderStatus.get(i) == null) { // singal order
                ArrayList<Integer> order = orders.get(i);

                ArrayList<int[]> location = new ArrayList<>();
                location.add(start);
                location.add(end);

                for (int ii = 0; ii < order.size(); ii++) {
                    for (int j = 0; j < 30000; j++) {
                        if ((int) allInfo[j][0] == order.get(ii)) {
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
                for (int ii = 0; ii < location.size(); ii++) {
                    for (int j = 0; j < location.size(); j++) {
                        if (ii == j) {
                            distances[ii][j] = 0;
                        } else {
                            distances[ii][j] = wh.shortestPath(location.get(ii), location.get(j));
                        }
                    }
                }

                wh.orderShortestPath(distances, order, location, this.weightInfo, this.cells, file);
            } else if (orderStatus.get(i).get(0) == -1) { // split
                ArrayList<Integer> order = orders.get(i);
                ArrayList<ArrayList<Integer>> subOrders = new ArrayList<>(orderStatus.get(i).size() - 1 + 1);
                // (size - 1) splitPoints, (size) subOrders

                for (int j = 1; j < orderStatus.get(i).size(); j++) {
                    int splitPoint = orderStatus.get(i).get(j);
                    ArrayList<Integer> subOrder = new ArrayList<>();
                    for (int x = 0; x < splitPoint; x++) {
                        subOrder.add(order.get(x));
                    }
                    subOrders.add(subOrder);
                } // still lack one subOrder: the subOrder after the last splitPoint
            }

        }

    }

    private void refreshColor(){
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells[0].length; j++) {
                if ((j - 1) % 2 == 0 && (20 - i) % 2 == 0) {
                    cells[i][j].setFill(Color.RED);
                } else {
                    cells[i][j].setFill(Color.BLUE);
                }
            }
        }
    }

}