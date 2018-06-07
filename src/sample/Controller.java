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
import javafx.scene.control.TreeItem;
import warehouse.Warehouse;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

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
    @FXML
    public Label orgOrder;
    @FXML
    public Label status;
    @FXML
    public Label opOrder;
    @FXML
    public Label opPath;
    @FXML
    public Label opDis;

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
                    cells[i][j].setFill(Color.LIGHTGRAY);
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

        wh.orderShortestPath(distances, items, location, weightInfo, this.cells, null, false, this);

        String orgOrder = "Original Order: ";
        for (int item : items) {
            orgOrder += Integer.toString(item) + ",";
        }
        this.orgOrder.setText(orgOrder);
        this.status.setText("Singal order");
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

        ArrayList<ArrayList<Integer>> filteredOrders = new ArrayList<>(orders.size());

        for (int i = 0; i < orders.size(); i++) {
            ArrayList<Integer> filteredOrder = new ArrayList<>(orders.get(i));
            filteredOrders.add(filteredOrder);
        }

        for (int i = 0; i < filteredOrders.size(); i++) {
            ArrayList<Integer> forder = filteredOrders.get(i);
            for (int j = 0; j < forder.size(); j++) {
                if (weightInfo.containsKey(forder.get(j)) && weightInfo.get(forder.get(j)) > weightBound) {
                    forder.remove(j);
                    j--;
                }
            }
        }

        ArrayList<ArrayList<Integer>> orderStatus = wh.ordersReorganize(filteredOrders, this.weightInfo, weightBound);

        File f = new File("batch.txt");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path file = Paths.get("batch.txt");

        for (int i = 0; i < filteredOrders.size(); i++) {

            ArrayList<String> output = new ArrayList<>();
            String orgOrder = "Original order: ";
            for (int item : orders.get(i)) {
                orgOrder += Integer.toString(item) + ",";
            }
            output.add(orgOrder);

            if (orderStatus.get(i) == null) { // singal order
                output.add("Singal order");

                try {
                    Files.write(file, output, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<Integer> order = filteredOrders.get(i);

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

                wh.orderShortestPath(distances, order, location, this.weightInfo, this.cells, file, true, null);

            } else if (orderStatus.get(i).get(0) == -1) { // split

                output.add("Split into " + orderStatus.get(i).size() + " subOrders");

                try {
                    Files.write(file, output, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<Integer> order = filteredOrders.get(i);
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

                ArrayList<Integer> lastSubOrder = new ArrayList<>();
                for(int j = orderStatus.get(i).get(orderStatus.get(i).size() - 1); j < order.size(); j++){
                    lastSubOrder.add(order.get(j));
                }
                subOrders.add(lastSubOrder);

                for (ArrayList<Integer> subOrder : subOrders) {

                    ArrayList<int[]> location = new ArrayList<>();
                    location.add(start);
                    location.add(end);

                    for (int ii = 0; ii < subOrder.size(); ii++) {
                        for (int j = 0; j < 30000; j++) {
                            if ((int) allInfo[j][0] == subOrder.get(ii)) {
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

                    wh.orderShortestPath(distances, subOrder, location, this.weightInfo, this.cells, file, true, null);
                }
            } else if (orderStatus.get(i).get(0) == 1) {

                String cmbOrders = "Combined with order ";

                for (int j = 1; j < orderStatus.get(i).size(); j++) {
                    cmbOrders += (orderStatus.get(i).get(j) + 1) + ",";
                }
                output.add(cmbOrders);

                try {
                    Files.write(file, output, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<Integer> combinedOrder = new ArrayList<>();
                for (int j = 1; j < orderStatus.get(i).size(); j++) {
                    combinedOrder.addAll(filteredOrders.get(orderStatus.get(i).get(j)));
                }

                ArrayList<int[]> location = new ArrayList<>();
                location.add(start);
                location.add(end);


                for (int ii = 0; ii < combinedOrder.size(); ii++) {
                    for (int j = 0; j < 30000; j++) {
                        if ((int) allInfo[j][0] == combinedOrder.get(ii)) {
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

                wh.orderShortestPath(distances, combinedOrder, location, this.weightInfo, this.cells, file, true, null);
            }

            output.clear();
            output.add("*****");
            try {
                Files.write(file, output, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.readBatchFile();
    }

    private void refreshColor(){
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells[0].length; j++) {
                if ((j - 1) % 2 == 0 && (20 - i) % 2 == 0) {
                    cells[i][j].setFill(Color.RED);
                } else {
                    cells[i][j].setFill(Color.LIGHTGRAY);
                }
            }
        }
    }

    public void readBatchFile(){
        Path file = Paths.get("batch.txt");

        Stream<String> stream = null;
        try {
            stream = Files.lines(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Iterator<String> iterator = stream.iterator();

        int[] splitNum = new int[3000];
        int orderNum = 0;
        int lineNum = 1;

        while (iterator.hasNext()) {
            String line = iterator.next();
            if (lineNum == 2) {
                if (line.contains("Split")) {
                    splitNum[orderNum] = Integer.parseInt(line.split(" ")[2]);
                }
            }
            if (line.equals("*****")) {
                lineNum = 0;
                orderNum++;
            }
            lineNum++;
        }

        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        for (int i = 0; i < orderNum; i++) {
            if (splitNum[i] == 0) {
                TreeItem<String> r = makeBranch("order " + Integer.toString(i + 1), root);
            } else {
                TreeItem<String> r = makeBranch("order " + Integer.toString(i + 1), root);
                r.setExpanded(true);

                for (int j = 0; j < splitNum[i]; j++) {
                    makeBranch("subOrder " + Integer.toString(j + 1), r);
                }
            }
        }

        this.tree.getSelectionModel().selectedItemProperty()
                .addListener((v, oldValue, newValue) -> {
                    this.refreshColor();

                    TreeItem<String> item = (TreeItem<String>) newValue;

                    Stream<String> stream0 = null;
                    try {
                        stream0 = Files.lines(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    Iterator<String> iterator0 = stream0.iterator();

                    if (item.getParent().getValue() == null) {
                        int odNum = Integer.parseInt(item.getValue().split(" ")[1]) - 1;

                        int cOdNum = 0;
                        String line = null;

                        while (cOdNum != odNum) {
                            line = iterator0.next();
                            if (line.equals("*****")) {
                                cOdNum++;
                            }
                        }
                        this.orgOrder.setText(iterator0.next());
                        this.status.setText(iterator0.next());
                        this.opOrder.setText(iterator0.next());
                        this.opPath.setText(iterator0.next());
                        this.opDis.setText(iterator0.next());

                        String[] s1 = iterator0.next().split(",");
                        String[] s2 = iterator0.next().split(",");

                        while (s1.length == 2) {
                            int[] var1 = new int[2];
                            int[] var2 = new int[2];
                            var1[0] = Integer.parseInt(s1[0]);
                            var1[1] = Integer.parseInt(s1[1]);
                            var2[0] = Integer.parseInt(s2[0]);
                            var2[1] = Integer.parseInt(s2[1]);

                            this.wh.drawPath(this.cells, var1, var2);

                            s1 = iterator0.next().split(",");
                            s2 = iterator0.next().split(",");
                        }
                    } else {
                        int odNum = Integer.parseInt(item.getParent().getValue().split(" ")[1]) - 1;
                        int subodNum = Integer.parseInt(item.getValue().split(" ")[1]) - 1;

                        int cOdNum = 0;
                        String line = null;

                        while (cOdNum != odNum) {
                            line = iterator0.next();
                            if (line.equals("*****")) {
                                cOdNum++;
                            }
                        }
                        this.orgOrder.setText(iterator0.next());
                        this.status.setText(iterator0.next());

                        int cSubodNum = 0;

                        while (cSubodNum != subodNum) {
                            line = iterator0.next();
                            if (line.equals("---")) {
                                cSubodNum++;
                            }
                        }
                        this.opOrder.setText(iterator0.next());
                        this.opPath.setText(iterator0.next());
                        this.opDis.setText(iterator0.next());

                        String[] s1 = iterator0.next().split(",");
                        String[] s2 = iterator0.next().split(",");

                        while (s1.length == 2) {
                            int[] var1 = new int[2];
                            int[] var2 = new int[2];
                            var1[0] = Integer.parseInt(s1[0]);
                            var1[1] = Integer.parseInt(s1[1]);
                            var2[0] = Integer.parseInt(s2[0]);
                            var2[1] = Integer.parseInt(s2[1]);

                            this.wh.drawPath(this.cells, var1, var2);

                            s1 = iterator0.next().split(",");
                            s2 = iterator0.next().split(",");
                        }
                    }
                });

        this.tree.setRoot(root);
        this.tree.setShowRoot(false);
    }

    private TreeItem<String> makeBranch(String title, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<>(title);
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }

    private void orderSelected(){

    }

}


















