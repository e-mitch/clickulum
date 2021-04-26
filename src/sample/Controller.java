package sample;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

import javafx.scene.control.ScrollPane;
import java.io.*;
import java.lang.*;
import javafx.geometry.Insets;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.Arrays;
//import java.util.List;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.Initializable;
//import javafx.scene.Scene;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
//import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Font;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
//import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import jdk.jfr.EventType;
import jdk.management.jfr.EventTypeInfo;


public class Controller implements Initializable{
    public String stateSelection = "";
    ObservableList<String> states = FXCollections.observableArrayList("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida",
            "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
            "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
            "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
            "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia","Washington", "West Virginia",
            "Wisconsin", "Wyoming");

    @FXML
    private ComboBox<String> stateSelectCombo;
    @FXML
    private Button newButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button openButton;
    @FXML
    private GridPane grid;
    @FXML
    private VBox englishBox;
    @FXML
    private VBox mathBox;
    @FXML
    private VBox scienceBox;
    @FXML
    private VBox socStudBox;
    @FXML
    private void selectState(ActionEvent event) {

    }
    @FXML
    private GridPane resourcesGrid;
    String[][] selectedClasses = new String[7][4];
    List<String> electiveSelections = new ArrayList<String>();
    Boolean openedData = false;
    int shownGrade = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EventHandler<ActionEvent> newHandler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                clearGrid(grid);
            }
        };
        newButton.setOnAction(newHandler);
        assert stateSelectCombo != null : "fx:id=\"stateSelectCombo\" was not injected: check your FXML file 'sample.fxml'.";
        assert grid != null : "fx:id\"grid\" was not injected: check your FXML file 'sample.fxml'.";
        stateSelectCombo.getItems().setAll(states);
        stateSelectCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String previousValue, String currentValue) {
                clearGrid(grid);
                if(openedData == false) {
                    //If event is not triggered by user opening data, build grid from scratch using information from database
                    if (previousValue != currentValue) {
                        stateSelection = currentValue;
                        String[][] classes = getClasses(stateSelection);
                        List<List<String>> selected = new ArrayList<>();
                        buildGrid(grid, classes,selected);

                    }
                }
            }
        });
    }
    static String[] english = new String[4];
    static String mathematics[] = new String[4];
    static String science[] = new String[4];
    static String socialStudies[] = new String[4];
    static String electives1[] = new String[4];
    static String electives2[] = new String[4];

    public static String[][] getClasses(String state) {
        String classArrays[][] = new String[][]{};
        String selectedState = state.toLowerCase();
        String query = "SELECT * FROM " + selectedState;
        //String url = "jdbc:sqlite:/Users/EllieMitchell/clickulumTest/" + selectedState + ".sl3";
        String url = "jdbc:sqlite:/Users/EllieMitchell/Desktop/clickulumJavaFX/reqs.sqlite";
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            //Conect with database and load selected state's data into arrays
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            resultSet = statement
                    .executeQuery(query);

            int counter = 0;
            while (resultSet.next()) {
                english[counter] = resultSet.getString("englishCol");
                mathematics[counter] = resultSet.getString("mathCol");
                science[counter] = resultSet.getString("scienceCol");
                socialStudies[counter] = resultSet.getString("socialStudCol");
                electives1[counter] = resultSet.getString("electives1Col");
                //electives2[counter] = resultSet.getString("electives2Col");
                counter++;
            }
            classArrays = new String[][]{english, mathematics, science, socialStudies, electives1, electives2};
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classArrays;
    }

    static String previousValue = "";
    private static void setPreviousValue(ComboBox cb){
        previousValue = String.valueOf(cb.getSelectionModel().getSelectedItem());
    }

    private static String getPreviousValue(){
        return previousValue;
    }

    private void buildGrid(GridPane grid, String[][] classArray, List<List<String>> selectedValues) {
        int column = 1;
        int row = 1;
        //iterates through arrays of class options by class category
        for (int i = 0; i < 4; i++) {
            row = 1;
            //iterates through classes within in each class category
            for (int x = 0; x < classArray[i].length; x++) {
                //splits class option into array to see if there is more than one option for a class slot.
                String[] values = classArray[i][x].split("&");
                if (values.length > 1) {
                    List<String> tempValue = Arrays.asList(values);
                    List<String> valueSet = new ArrayList<String>(tempValue);
                    ComboBox classCombo = new ComboBox();
                    for(int a = 0; a < valueSet.size(); a++){
                        classCombo.getItems().add(valueSet.get(a));
                    }
                    grid.add(classCombo, column, row);
                    classCombo.setOnAction((event) -> {
                        int thisColumn = grid.getColumnIndex(classCombo);
                        int thisRow = grid.getRowIndex(classCombo);
                        selectedClasses[thisColumn-1][thisRow-1] = String.valueOf(classCombo.getSelectionModel().getSelectedItem());
                    });
                } else {
                    //Creates a label with the class's name if there is only one option and adds it to the grid
                    Label subject = new Label(classArray[i][x]);
                    subject.setTextAlignment(TextAlignment.CENTER);
                    Tooltip tip = new Tooltip(classArray[i][x]);
                    if(classArray[i][x].length() > 48){
                        tip.setWrapText(true);
                        tip.setMaxWidth(200);
                        tip.setShowDelay(Duration.millis(0));
                        subject.setTooltip(tip);
                        subject.setTextAlignment(TextAlignment.CENTER);
                    }
                    if(column!= 0 && row!=0) {
                        selectedClasses[column-1][row - 1] = classArray[i][x];
                    }
                    grid.add(subject, column, row);
                    GridPane.setHalignment(subject, HPos.CENTER);
                    //Button classButton = new Button(classArray[i][x]);
                }
                row++;
            }
            column++;
        }

        //populate electives area
        String[] values = classArray[4][0].split("&");
        for(int i = 0; i < values.length; i++){
            values[i] = values[i].strip();
        }
        List<List<String>> electiveInformation = new ArrayList<List<String>>();

        List<String> nullEntry = new ArrayList<String>();
        nullEntry.add("Null");
        nullEntry.add("500");
        nullEntry.add("0");
        nullEntry.add("0");
        electiveInformation.add(nullEntry);
        for (int i = 0; i < values.length; i++) {
            //finds out if a value has already been added to electiveInformation
            boolean alreadyExists = false;
            int thisCount = 0;
            int classIndex = -1;
            for (int a = 0; a < electiveInformation.size(); a++) {
                if (electiveInformation.get(a).contains(values[i])) {
                    alreadyExists = true;
                    thisCount = Integer.parseInt(electiveInformation.get(a).get(1));
                    classIndex = a;
                    break;
                }
            }
            //if it already has been added, increments the maximum number of times it can be selected
            if(alreadyExists){
                electiveInformation.get(classIndex).set(1, String.valueOf(thisCount + 1));
            } else {
                //otherwise, adds the class to electiveInformation
                List<String> tempInformation = new ArrayList<String>(4);
                tempInformation.add(values[i]);
                //shows current maximum number of times it can be selected
                tempInformation.add("1");
                //shows current number of times the user has selected it
                tempInformation.add("0");
                //Shows whether the class should still be listed based on whether it has been selected the maximum number of times.
                tempInformation.add("false");
                electiveInformation.add(tempInformation);
            }
        }

        //create all the comboboxes for electives
        List<ComboBox> electivesCombos = new ArrayList<ComboBox>();
        ComboBox el51combo = new ComboBox();
        electivesCombos.add(el51combo);
        grid.add(el51combo, 5, 1);
        ComboBox el52combo = new ComboBox();
        electivesCombos.add(el52combo);
        grid.add(el52combo, 5, 2);
        ComboBox el53combo = new ComboBox();
        electivesCombos.add(el53combo);
        grid.add(el53combo, 5, 3);
        ComboBox el54combo = new ComboBox();
        electivesCombos.add(el54combo);
        grid.add(el54combo, 5, 4);
        ComboBox el61combo = new ComboBox();
        electivesCombos.add(el61combo);
        grid.add(el61combo, 6, 1);
        ComboBox el62combo = new ComboBox();
        electivesCombos.add(el62combo);
        grid.add(el62combo, 6, 2);
        ComboBox el63combo = new ComboBox();
        electivesCombos.add(el63combo);
        grid.add(el63combo, 6, 3);
        ComboBox el64combo = new ComboBox();
        electivesCombos.add(el64combo);
        grid.add(el64combo, 6, 4);
        ComboBox el71combo = new ComboBox();
        electivesCombos.add(el71combo);
        grid.add(el71combo, 7, 1);
        ComboBox el72combo = new ComboBox();
        electivesCombos.add(el72combo);
        grid.add(el72combo, 7, 2);
        ComboBox el73combo = new ComboBox();
        electivesCombos.add(el73combo);
        grid.add(el73combo, 7, 3);
        ComboBox el74combo = new ComboBox();
        electivesCombos.add(el74combo);
        grid.add(el74combo, 7, 4);
        View thisView = new View();
        for(int a=0; a < electivesCombos.size(); a++) {
            //initial population of comboboxes
            ComboBox thisCombo = electivesCombos.get(a);
            for(int b=0; b<electiveInformation.size(); b++){
                thisCombo.getItems().add(electiveInformation.get(b).get(0));
            }
            //populates combobox and if a value has been selected the maximum allowed number of times, removes it from other comboboxes
            String previousValue = "";
            thisCombo.setOnMouseClicked((event) -> {
                setPreviousValue(thisCombo);
            });
            List<String> chosenElectives = electiveSelections;
            thisCombo.valueProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    int comboColumn = grid.getColumnIndex(thisCombo);
                    int comboRow = grid.getRowIndex(thisCombo);
                    selectedClasses[comboColumn-1][comboRow-1] = String.valueOf(thisCombo.getSelectionModel().getSelectedItem());
                    String pv = getPreviousValue();
                    int comboIndex = electivesCombos.indexOf(thisCombo);
                    String selectedValue = "";
                    if(thisCombo.getSelectionModel().getSelectedItem() != "Null"){
                        selectedValue = String.valueOf(thisCombo.getSelectionModel().getSelectedItem());
                    } else {

                    }

                    for(int i = 0; i < electiveInformation.size(); i++){
                        if(electiveInformation.get(i).get(0).equals(selectedValue)){
                            int currentUsage = Integer.parseInt(electiveInformation.get(i).get(2));
                            electiveInformation.get(i).set(2, String.valueOf(currentUsage+1));
                            if(Integer.parseInt(electiveInformation.get(i).get(1)) == Integer.parseInt(electiveInformation.get(i).get(2))){
                                electiveInformation.get(i).set(3, "true");
                                for(int g = 0; g < electivesCombos.size(); g++){
                                    if(electivesCombos.get(g).getItems().contains(selectedValue)){
                                        if(g != comboIndex){
                                            if(electivesCombos.get(g).getSelectionModel().getSelectedItem() != String.valueOf(selectedValue)) {
                                                electivesCombos.get(g).getItems().remove(selectedValue);
                                            }
                                        }
                                    }
                                }
                            }
                        } if(pv != "null"){
                            if(electiveInformation.get(i).get(0) == pv){
                                int currentUsage = Integer.parseInt(electiveInformation.get(i).get(2));
                                electiveInformation.get(i).set(2,String.valueOf(currentUsage-1));
                                if(Integer.parseInt(electiveInformation.get(i).get(1)) > Integer.parseInt(electiveInformation.get(i).get(2))){
                                    electiveInformation.get(i).set(3, "false");
                                    for(int f = 0; f < electivesCombos.size(); f++) {
                                        if (!electivesCombos.get(f).getItems().contains(pv)){
                                            if(electivesCombos.get(f).getSelectionModel().getSelectedItem() != String.valueOf(pv)){
                                                electivesCombos.get(f).getItems().add(pv);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            });
        }
    }

    public void exportData(ActionEvent actionEvent) {
        Stage currentStage = (Stage) saveButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(currentStage);
        if(file != null){
            int grade = 9;
            String newContent = "";
            newContent += stateSelectCombo.getSelectionModel().getSelectedItem() + "\n";
            for(int i = 0; i < 4; i++){
                String thisLine = "Grade " + grade + ": ";
                for(int a = 0; a < 7; a++) {
                    if(selectedClasses[a][i] != "null") {
                        thisLine += " " + selectedClasses[a][i] + ",";
                    }
                }
                thisLine += "\n";
                grade += 1;
                newContent += thisLine;
            }
            SaveFile(newContent, file);
        }
    }


    private void SaveFile(String content, File file){
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearGrid(GridPane grid){
        List<Node> nodes = new ArrayList<Node>();
        nodes = grid.getChildren();
        grid.getChildren().remove(12, nodes.size());
    }

    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        // Set title for DirectoryChooser
        directoryChooser.setTitle("Select Save Location");

        // Set Initial Directory
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    List<String> classSelections = new ArrayList<String>();
    public void openData(ActionEvent actionEvent) {
        clearGrid(grid);
        Stage currentStage = (Stage) saveButton.getScene().getWindow();
        final FileChooser chooser = new FileChooser();
        final Button openButton = new Button("Open curriculum");
        File file = chooser.showOpenDialog(currentStage);
        List<List<String>> lines = new ArrayList<List<String>>();
        List<String> allLines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(file.getPath())))) {

            String line;
            while ((line = reader.readLine()) != null) {
                allLines.add(line);
            }
            for(int a = 1; a < allLines.size(); a++) {
                String[] currentLine = new String[]{};
                List<String> thisLine = new ArrayList<String>();
                currentLine = allLines.get(a).split(",");
                String[] withoutGrade = currentLine[0].split(":");
                currentLine[0] = withoutGrade[1];
                thisLine = Arrays.asList(currentLine);
                lines.add(thisLine);
            }
            openedData = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        stateSelectCombo.getSelectionModel().select(allLines.get(0));
        String[][] classes = getClasses(allLines.get(0));
        List<List<String>> test = new ArrayList<>();
        buildGrid(grid, classes, test);
        List<String> electivePicks = new ArrayList<String>();
        List<Node> nodes = new ArrayList<Node>();
        nodes = grid.getChildren();
        List<String> selections = new ArrayList<String>();
        try {
            for (int i = 12; i < nodes.size(); i++) {
                if(nodes.get(i) instanceof ComboBox){
                    int column = (i - 13) / 4;
                    int row = (i - 13) % 4;
                    String value = lines.get(row).get(column).strip();
                    if(column < 4){
                        classSelections.add(value);
                    } else {
                        electivePicks.add(value);
                    }
                    ComboBox thisDropdown = (ComboBox) nodes.get(i);
                    thisDropdown.getSelectionModel().select(value);
                }
            }

        }catch(Exception e){

        }
    }

    private void openFile(File file) {
        //modify this method to load in curriculum
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    Controller.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }

    public void getResources(String[] urls, String[] names, String[] otherOptions, VBox box){
        if(box.getChildren() != null){
            box.getChildren().clear();
        }
        for(int i = 0; i < names.length; i++) {
            String url = urls[i];
            Hyperlink link = new Hyperlink(urls[i]);
            link.setText("\n\u2022 " + names[i]);
            link.setOnAction((event) -> {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            });
            box.getChildren().add(link);
        }
        for(int a = 0; a < otherOptions.length; a++){
            Label subject = new Label("\n\u2022 " + otherOptions[a]);
            box.getChildren().add(subject);
        }
    }
    public void get9Resources(ActionEvent actionEvent) {
        String[] englishUrls = {"https://www.milestonebooks.com/item/1-129-1/?list=Building_Christian_English_Series", "https://www.time4learning.com/homeschool-curriculum/ninth-grade-language-arts.html", "https://www.christianbook.com/page/homeschool/a-beka/a-beka-literature/a-beka-grade-9-lit?event=Homeschool%7C1000794"};
        String[] englishNames = {"Rod & Staff Grade 9", "Time4Learning 9th Grade English", "Abeka Literature Grade 9"};
        String[] englishOtherOptions = {"AP English Literature and Composition", "AP English Language and Composition"};
        getResources(englishUrls, englishNames, englishOtherOptions, englishBox);
        String[] socStudUrls = {"https://www.time4learning.com/homeschool-curriculum/high-school-us-history-1.html", "https://www.christianbook.com/page/homeschool/a-beka/a-beka-history/a-beka-grade-9-history?event=Homeschool%7C1000789"};
        String[] socStudNames = {"Time4Learning U.S. History I", "Abeka History Grade 9"};
        String[] socStudOtherOptions = {"AP Comparative Government and Politics", "AP European History", "AP Human Geography", "AP Macroeconomics", "AP Microeconomics", "AP Psychology", "AP United States Government and Politics", "AP United States History", "AP World History: Modern"};
        getResources(socStudUrls, socStudNames, socStudOtherOptions, socStudBox);
        String[] mathUrls = {"https://www.teachingtextbooks.com/pre-algebra-version4.html", "https://www.teachingtextbooks.com/algebra1-version4.html", "https://www.ctcmath.com/how-it-works/home-school", "https://www.lifeoffred.uniquemath.com/lof-balgebra.php#.YF4A80hKjOR"};
        String[] mathNames = {"Teaching Texbooks Pre-Algebra", "Teaching Textbooks Algebra", "CTC Math", "Life of Fred: Beginning Algebra"};
        String[] mathOtherOptions = {"AP Calculus AB", "AP Calculus BC", "AP Computer Science A", "AP Computer Science Principles", "AP Statistics"};
        getResources(mathUrls, mathNames, mathOtherOptions, mathBox);
        String[] scienceUrls = {"https://www.christianbook.com/page/homeschool/apologia/apologia-secondary/exploring-creation-with-biology?event=Homeschool%7C1002236", "https://www.time4learning.com/homeschool-curriculum/high-school-biology.html"};
        String[] scienceNames = {"Apologia Exploring Creation with Biology", "Time4Learning High School Biology Curriculum"};
        String[] scienceOtherOptions = {"AP Biology", "AP Chemistry", "AP Environmental Science", "AP Physics 1: Algebra-Based", "AP Physics 2: Algebra-Based", "AP Physics C: Electricity and Magnetism", "AP Physics C: Mechanics"};
        getResources(scienceUrls, scienceNames, scienceOtherOptions, scienceBox);
    }

    public void get10Resources(ActionEvent actionEvent){
        String[] englishUrls = {"https://www.milestonebooks.com/item/1-129-2/?list=Rod_and_Staff_Grade_10", "https://www.time4learning.com/homeschool-curriculum/tenth-grade-language-arts.html", "https://www.christianbook.com/page/homeschool/a-beka/a-beka-literature/a-beka-grade-10-lit?event=Homeschool%7C1000794"};
        String[] englishNames = {"Rod & Staff Grade 10", "Time4Learning 10th Grade English",  "Abeka Literature Grade 10"};
        String[] englishOtherOptions = {"AP English Literature and Composition", "AP English Language and Composition"};
        getResources(englishUrls, englishNames, englishOtherOptions, englishBox);
        String[] socStudUrls = {"https://www.christianbook.com/page/homeschool/a-beka/a-beka-history/a-beka-grade-10-history?event=Homeschool%7C1000789", "https://www.time4learning.com/homeschool-curriculum/high-school-world-history.html"};
        String[] socStudNames = {"Abeka History Grade 10","Time4Learning High School Survey of World History"};
        String[] socStudOtherOptions = {"AP Comparative Government and Politics", "AP European History", "AP Human Geography", "AP Macroeconomics", "AP Microeconomics", "AP Psychology", "AP United States Government and Politics", "AP United States History", "AP World History: Modern"};
        getResources(socStudUrls, socStudNames, socStudOtherOptions, socStudBox);
        String[] mathUrls = {"https://www.teachingtextbooks.com/geometry-version4.html", "https://www.christianbook.com/page/homeschool/master-books/master-books-jacobs-geometry?event=Homeschool%7C1000116", "https://www.ctcmath.com/how-it-works/home-school", "https://www.lifeoffred.uniquemath.com/lof-geometry.php#.YG8BVkhKjOQ", "https://www.christianbook.com/page/homeschool/math/saxon-math/saxon-geometry?event=Homeschool%7C1002939"};
        String[] mathNames = {"Teaching Texbooks Geometry", "Jacobs Geometry", "CTC Math", "Life of Fred: Geometry", "Saxon Math Geometry"};
        String[] mathOtherOptions = {"AP Calculus AB", "AP Calculus BC", "AP Computer Science A", "AP Computer Science Principles", "AP Statistics"};
        getResources(mathUrls, mathNames, mathOtherOptions, mathBox);
        String[] scienceUrls = {"https://www.christianbook.com/page/homeschool/apologia/apologia-secondary/exploring-creation-with-chemistry?event=Homeschool%7C1002236", "https://www.time4learning.com/homeschool-curriculum/high-school-chemistry.html"};
        String[] scienceNames = {"Apologia Exploring Creation with Chemistry", "Time4Learning High School Chemistry"};
        String[] scienceOtherOptions = {"AP Biology", "AP Chemistry", "AP Environmental Science", "AP Physics 1: Algebra-Based", "AP Physics 2: Algebra-Based", "AP Physics C: Electricity and Magnetism", "AP Physics C: Mechanics"};
        getResources(scienceUrls, scienceNames, scienceOtherOptions, scienceBox);
    }

    public void get11Resources(ActionEvent actionEvent){
        String[] englishUrls = {"https://www.christianbook.com/page/homeschool/a-beka/a-beka-literature/a-beka-grade-11-lit?event=Homeschool%7C1000794", "https://www.time4learning.com/homeschool-curriculum/eleventh-grade-language-arts.html"};
        String[] englishNames = {"Abeka Literature Grade 11", "Time4Learning 11th Grade English Language Arts Curriculum"};
        String[] englishOtherOptions = {"AP English Literature and Composition", "AP English Language and Composition"};
        getResources(englishUrls, englishNames, englishOtherOptions, englishBox);
        String[] socStudUrls = {"https://www.christianbook.com/page/homeschool/a-beka/a-beka-history/a-beka-grade-11-history?event=Homeschool%7C1000789", "https://www.time4learning.com/homeschool-curriculum/high-school-us-history-2.html"};
        String[] socStudNames = {"Abeka History Grade 11", "Time4Learning High School U.S. History II Curriculum"};
        String[] socStudOtherOptions = {"AP Comparative Government and Politics", "AP European History", "AP Human Geography", "AP Macroeconomics", "AP Microeconomics", "AP Psychology", "AP United States Government and Politics", "AP United States History", "AP World History: Modern"};
        getResources(socStudUrls, socStudNames, socStudOtherOptions, socStudBox);
        String[] mathUrls = {"https://www.teachingtextbooks.com/algebra2-version4.html", "https://www.ctcmath.com/how-it-works/home-school", "https://www.lifeoffred.uniquemath.com/lof-aalgebra.php#.YG8DJEhKjOQ"};
        String[] mathNames = {"Teaching Textbooks Algebra II", "CTC Math", "Life of Fred: Algebra II"};
        String[] mathOtherOptions = {"AP Calculus AB", "AP Calculus BC", "AP Computer Science A", "AP Computer Science Principles", "AP Statistics"};
        getResources(mathUrls, mathNames, mathOtherOptions, mathBox);
        String[] scienceUrls = {"https://www.christianbook.com/page/homeschool/apologia/apologia-secondary/exploring-creation-with-physics?event=Homeschool%7C1002236", "https://www.time4learning.com/homeschool-curriculum/high-school-physics.html"};
        String[] scienceNames = {"Apologia Exploring Creation with Physics", "Time4Learning High School Physics"};
        String[] scienceOtherOptions = {"AP Biology", "AP Chemistry", "AP Environmental Science", "AP Physics 1: Algebra-Based", "AP Physics 2: Algebra-Based", "AP Physics C: Electricity and Magnetism", "AP Physics C: Mechanics"};
        getResources(scienceUrls, scienceNames, scienceOtherOptions, scienceBox);
    }

    public void get12Resources(ActionEvent actionEvent){
        String[] englishUrls = {"https://www.christianbook.com/page/homeschool/a-beka/a-beka-literature/a-beka-grade-12-lit?event=Homeschool%7C1000794", "https://www.time4learning.com/homeschool-curriculum/twelfth-grade-language-arts.html"};
        String[] englishNames = {"Abeka Literature Grade 12", "Time4Learning 12th Grade English Language Arts"};
        String[] englishOtherOptions = {"AP English Literature and Composition", "AP English Language and Composition"};
        getResources(englishUrls, englishNames, englishOtherOptions, englishBox);
        String[] socStudUrls = {"https://www.christianbook.com/page/homeschool/a-beka/a-beka-history/a-beka-grade-12-history?event=Homeschool%7C1000789", "https://www.time4learning.com/homeschool-curriculum/high-school-us-government.html"};
        String[] socStudNames = {"Abeka History Grade 12", "Time4Learning High School US Government"};
        String[] socStudOtherOptions = {"AP Comparative Government and Politics", "AP European History", "AP Human Geography", "AP Macroeconomics", "AP Microeconomics", "AP Psychology", "AP United States Government and Politics", "AP United States History", "AP World History: Modern"};
        getResources(socStudUrls, socStudNames, socStudOtherOptions, socStudBox);
        String[] mathUrls = {"https://www.teachingtextbooks.com/pre-calculus-version4.html", "https://www.ctcmath.com/how-it-works/home-school"};
        String[] mathNames = {"Teaching Textbooks Precalculus", "CTC Math"};
        String[] mathOtherOptions = {"AP Calculus AB", "AP Calculus BC", "AP Computer Science A", "AP Computer Science Principles", "AP Statistics"};
        getResources(mathUrls, mathNames, mathOtherOptions, mathBox);
        String[] scienceUrls = {"https://www.christianbook.com/page/homeschool/apologia/apologia-secondary/exploring-creation-with-advanced-biology?event=Homeschool%7C1002236", "https://www.christianbook.com/page/homeschool/apologia/apologia-secondary/exploring-creation-with-marine-biology?event=Homeschool%7C1002236", "https://www.christianbook.com/page/homeschool/apologia/apologia-secondary/advanced-chemistry-in-creation?event=Homeschool%7C1002236", "https://www.christianbook.com/page/homeschool/apologia/apologia-secondary/advanced-physics-in-creation?event=Homeschool%7C1002236", "https://www.time4learning.com/homeschool-curriculum/high-school-environmental-science.html"};
        String[] scienceNames = {"Apologia Exploring Creation with Advanced Biology", "Apologia Exploring Creation with Marine Biology", "Apologia Advanced Chemistry in Creation", "Apologia Advanced Physics in Creation", "High School Environmental Science Curriculum"};
        String[] scienceOtherOptions = {"AP Biology", "AP Chemistry", "AP Environmental Science", "AP Physics 1: Algebra-Based", "AP Physics 2: Algebra-Based", "AP Physics C: Electricity and Magnetism", "AP Physics C: Mechanics"};
        getResources(scienceUrls, scienceNames, scienceOtherOptions, scienceBox);
    }
}
