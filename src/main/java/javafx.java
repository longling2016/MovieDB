import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import javafx.scene.layout.VBox;

public class hw3 extends Application {

    private static VBox countryInner;
    private static ArrayList<CheckBox> selGenres;
    private static Connection conn;
    private static ComboBox<String> comboBox4;
    private static ArrayList<CheckBox> selCountries;

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:oracle:thin:@localhost:49161:xe";
        String user = "longling";
        String password = "longling";
        conn = DriverManager.getConnection(url, user, password);
        launch(args);
        conn.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Stage window = primaryStage;
        window.setTitle("COEN 280 Movie selector");

        String[] genresList = {"Action", "Adventure", "Animation", "Children", "Comedy", "Crime", "Documentary",
                "Drama", "Fantasy", "Film-Noir", "Horror", "IMAX", "Musical", "Mystery", "Romance", "Sci-Fi",
                "Thriller", "War", "Western"};

        String[] countryList = {"Denmark", "Ireland", "Brazil", "Burkina", "Poland", "Yugoslavia", "Aruba", "Croatia",
                "Algeria", "South", "China", "Botswana", "Jamaica", "Peru", "East", "Chile", "Federal", "Norway",
                "Austria", "Iceland", "USA", "Spain", "France", "Germany", "Finland", "India", "Czech", "Senegal",
                "Venezuela", "Occupied", "Kazakhstan", "Australia", "Hungary", "Vietnam", "Taiwan", "Cuba", "Greece",
                "Soviet", "Romania", "Philippines", "Afghanistan", "Bhutan", "Canada", "Israel", "Belgium", "Hong",
                "New", "UK", "Russia", "Sweden", "Argentina", "Republic", "Switzerland", "Libya", "Bulgaria", "Singapore",
                "Mexico", "Netherlands", "Italy", "Tunisia", "Japan", "West", "Portugal", "Iran", "Thailand",
                "Czechoslovakia", "Bosnia", "Ivory", "Turkey", "Colombia"};

        // Genres Block ---------------------------------------------------------------
        Label genresT = new Label("Genres");
        genresT.setFont(new Font("Arial", 20));

        VBox genresInner = new VBox();
        genresInner.setPadding(new Insets(10, 10, 10, 10));
        genresInner.setSpacing(10);
        genresInner.getChildren().add(genresT);

        Button selQ = new Button("Select Genres");
        selQ.setOnAction(e -> {
            if (comboBox4.getValue() == null) {
                AlertBox.display("Please select AND/OR relation between attributes!");
                return;
            } else if (comboBox4.getValue().equals("AND")) {
                countryInner.getChildren().clear();
                Label countryT = new Label("Country");
                countryT.setFont(new Font("Arial", 20));
                countryInner.getChildren().add(countryT);
                ArrayList<String> selected = new ArrayList<>();
                for (CheckBox each : selGenres) {
                    if (each.isSelected()) {
                        selected.add(each.getText());
                    }
                }
                if (selected.size() > 0) {
                    try {
                        Statement getCountry = conn.createStatement();
                        StringBuilder s = new StringBuilder("SELECT DISTINCT C.country FROM countries C, (");
                        for (int i = 0; i < selected.size() - 1; i++) {
                            s.append("SELECT M.mid FROM movies_helper M, genres G WHERE M.mid = G.movieID AND " +
                                    "G.genre = '" + selected.get(i) + "' INTERSECT ");
                        }
                        s.append("SELECT M.mid FROM movies_helper M, genres G WHERE M.mid = G.movieID AND " +
                                "G.genre = '" + selected.get(selected.size()-1) + "') temp WHERE C.movieID = temp.mid");
                        ResultSet rs = getCountry.executeQuery(s.toString());
                        selCountries.clear();
                        while (rs.next()) {
                            if (rs.getString("country") != null) {
                                CheckBox cur = new CheckBox(rs.getString("country"));
                                countryInner.getChildren().add(cur);
                                selCountries.add(cur);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                selCountries.clear();
                countryInner.getChildren().clear();
                Label countryT = new Label("Country");
                countryT.setFont(new Font("Arial", 20));
                countryInner.getChildren().add(countryT);
                for (String each : countryList) {
                    CheckBox cur = new CheckBox(each);
                    countryInner.getChildren().add(cur);
                    selCountries.add(cur);
                }
            }
        });

        genresInner.getChildren().add(selQ);

        selGenres = new ArrayList<>();
        for (String each : genresList) {
            CheckBox cur = new CheckBox(each);
            genresInner.getChildren().add(cur);
            selGenres.add(cur);
        }

        selGenres.get(0).setSelected(true);
        Group genresGroup = new Group();
        ScrollPane s1 = new ScrollPane();
        s1.setPannable(true);
        s1.setPrefSize(150, 300);
        s1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s1.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        s1.setContent(genresInner);

        genresGroup.getChildren().add(s1);
        VBox genres = new VBox();
        genres.setStyle(
                "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: black;");
        genres.setPadding(new Insets(10, 10, 10, 10));
        genres.setSpacing(10);
        genres.getChildren().add(genresGroup);

        // Country Block -------------------------------------------------------
        countryInner = new VBox();
        countryInner.setPadding(new Insets(10, 10, 10, 10));
        countryInner.setSpacing(10);
        Label countryT = new Label("Country");
        countryT.setFont(new Font("Arial", 20));
        countryInner.getChildren().add(countryT);

        selCountries = new ArrayList<>();
        for (String each : countryList) {
            CheckBox cur = new CheckBox(each);
            countryInner.getChildren().add(cur);
            selCountries.add(cur);
        }
        Group countryGroup = new Group();
        ScrollPane s2 = new ScrollPane();
        s2.setPannable(true);
        s2.setPrefSize(150, 300);
        s2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        s2.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        s2.setContent(countryInner);

        countryGroup.getChildren().add(s2);
        VBox country = new VBox();
        country.setStyle(
                "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: black;");
        country.setPadding(new Insets(10, 10, 10, 10));
        country.setSpacing(10);
        country.getChildren().add(countryGroup);

        // Cast Block -------------------------------------------------------
        Label castT = new Label("Cast");
        castT.setFont(new Font("Arial", 20));

        Label actor = new Label("Search Actor/Actress:");

        TextField nameInput1 = new TextField();
        nameInput1.setPromptText("Actor/Actress Name");
        nameInput1.setMinWidth(100);

        TextField nameInput2 = new TextField();
        nameInput2.setPromptText("Actor/Actress Name");
        nameInput2.setMinWidth(100);

        TextField nameInput3 = new TextField();
        nameInput3.setPromptText("Actor/Actress Name");
        nameInput3.setMinWidth(100);

        TextField nameInput4 = new TextField();
        nameInput4.setPromptText("Actor/Actress Name");
        nameInput4.setMinWidth(100);

        VBox castup = new VBox();
        castup.setPadding(new Insets(10, 10, 10, 10));
        castup.setSpacing(10);
        castup.getChildren().addAll(castT, actor, nameInput1, nameInput2, nameInput3, nameInput4);

        Label director = new Label("Search Director:");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Director Name");
        nameInput.setMinWidth(100);

        VBox castd = new VBox();
        castd.setPadding(new Insets(10, 10, 10, 10));
        castd.setSpacing(10);
        castd.getChildren().addAll(director, nameInput);


        VBox cast = new VBox();
        cast.setStyle(
                "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: black;");
        cast.setPadding(new Insets(10, 10, 10, 10));
        cast.setSpacing(10);
        cast.getChildren().addAll(castT, castup, castd);


        // Rating Block ----------------------------------------------------------------
        Label ratingT = new Label("Rating");
        ratingT.setFont(new Font("Arial", 20));
        Label empty2 = new Label("");
        Label searchR = new Label("Set Criteria for Rating Value:");
        ComboBox<String> comboBox1 = new ComboBox<>();
        comboBox1.getItems().addAll(
                " = ",
                "<",
                ">",
                "<=",
                ">="
        );
        comboBox1.setPromptText("=, <, >, <=, >=");

        TextField ratingInput = new TextField();
        ratingInput.setPromptText("Rating Value");
        ratingInput.setMinWidth(100);

        Label searchN = new Label("Set Criteria for Rating Number:");
        ComboBox<String> comboBox2 = new ComboBox<>();
        comboBox2.getItems().addAll(
                " = ",
                "<",
                ">",
                "<=",
                ">="
        );
        comboBox2.setPromptText("=, <, >, <=, >=");

        TextField numberInput = new TextField();
        numberInput.setPromptText("Rating Number");
        numberInput.setMinWidth(100);

        VBox rating = new VBox();
        rating.setStyle(
                "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: black;");
        rating.setPadding(new Insets(10, 10, 10, 10));
        rating.setSpacing(10);
        rating.getChildren().addAll(ratingT, empty2, searchR, comboBox1, ratingInput, searchN, comboBox2, numberInput);

        // Movie Year Block -----------------------------------------------------
        Label yearT = new Label("Movie Year");
        yearT.setFont(new Font("Arial", 20));

        Label empty1 = new Label("");

        DatePicker fromDatePicker = new DatePicker();
        DatePicker toDatePicker = new DatePicker();
        Label fromlabel = new Label("From Date:");
        Label tolabel = new Label("To Date:");
        VBox year = new VBox();
        year.setStyle(
                "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: black;");
        year.setPadding(new Insets(10, 10, 10, 10));
        year.setSpacing(10);
        year.getChildren().addAll(yearT, empty1, fromlabel, fromDatePicker, tolabel, toDatePicker);


        // user id tags block ------------------------------------------------
        Label tagT = new Label("Users' Tags and Rating");
        tagT.setFont(new Font("Arial", 20));

        Label id = new Label("User ID: ");
        TextField userIDInput = new TextField();
        userIDInput.setPromptText("User ID Number");
        userIDInput.setMinWidth(100);

        DatePicker fromDateUserPicker = new DatePicker();
        DatePicker toDateUserPicker = new DatePicker();
        Label from = new Label("From: ");
        Label to = new Label("To:     ");
        HBox h2 = new HBox();
        h2.getChildren().addAll(from, fromDateUserPicker);
        HBox h3 = new HBox();
        h3.getChildren().addAll(to, toDateUserPicker);

        Label searchUserR = new Label("Set Filter for User's Rating:");
        ComboBox<String> comboBox3 = new ComboBox<>();
        comboBox3.getItems().addAll(
                " = ",
                "<",
                ">",
                "<=",
                ">="
        );
        comboBox3.setPromptText("=, <, >, <=, >=");

        TextField userRatingInput = new TextField();
        userRatingInput.setPromptText("Rating Value");
        userRatingInput.setMinWidth(100);
        VBox tags = new VBox();
        tags.setStyle(
                "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: black;");
        tags.setPadding(new Insets(10, 10, 10, 10));
        tags.setSpacing(10);
        Label empty3 = new Label("");
        tags.getChildren().addAll(tagT, empty3, id, userIDInput, h2, h3, searchUserR, comboBox3, userRatingInput);

        HBox boxUp = new HBox();
        boxUp.setPadding(new Insets(10, 5, 10, 5));
        boxUp.getChildren().addAll(genres, country, cast, rating, year, tags);

        // The lower part =================================================================

        // left part
        Label search = new Label("Search for: ");
        search.setFont(new Font("Arial", 18));

        comboBox4 = new ComboBox<>();
        comboBox4.getItems().addAll(
                "AND",
                "OR"
        );
        comboBox4.setPromptText("Select AND, OR between Attributes");

        // left query -------------------------------------------------------------
        ScrollPane query = new ScrollPane();
        query.setPannable(true);
        query.setPrefSize(700, 300);
        Text queryT = new Text("<Show Query Here:>");
        queryT.setFont(new Font("Arial", 15));
        queryT.setLineSpacing(5);
        query.setFitToWidth(true);
        query.setContent(queryT);
        query.setStyle("-fx-background: white;" +
                "-fx-border-color: black;");

        // right query result -------------------------------------------------------
        ScrollPane result = new ScrollPane();
        result.setPannable(true);
        result.setPrefSize(700, 300);

        TableView<Tuple> table = new TableView<>();
        table.setEditable(true);

        TableColumn titleCol = new TableColumn("Movie_Title");
        titleCol.setMinWidth(300);
        titleCol.setCellValueFactory(
                new PropertyValueFactory<Tuple, String>("movie_Title"));

        TableColumn genreCol = new TableColumn("Genre");
        genreCol.setMinWidth(400);
        genreCol.setCellValueFactory(
                new PropertyValueFactory<Tuple, String>("genre"));

        TableColumn tagCol = new TableColumn("Tags");
        tagCol.setMinWidth(1600);
        tagCol.setCellValueFactory(
                new PropertyValueFactory<Tuple, String>("tags"));

        TableColumn yearCol = new TableColumn("Year");
        yearCol.setMinWidth(60);
        yearCol.setCellValueFactory(
                new PropertyValueFactory<Tuple, String>("year"));

        TableColumn countryCol = new TableColumn("Country");
        countryCol.setMinWidth(100);
        countryCol.setCellValueFactory(
                new PropertyValueFactory<Tuple, String>("country"));

        TableColumn ratingCol = new TableColumn("Rating_Value");
        ratingCol.setMinWidth(100);
        ratingCol.setCellValueFactory(
                new PropertyValueFactory<Tuple, String>("rating_Value"));

        TableColumn numCol = new TableColumn("Rating_Number");
        numCol.setMinWidth(150);
        numCol.setCellValueFactory(
                new PropertyValueFactory<Tuple, String>("rating_Number"));

        ObservableList<Tuple> data = FXCollections.observableArrayList();
        table.setItems(data);
        table.getColumns().addAll(titleCol, yearCol, countryCol, ratingCol, numCol, genreCol, tagCol);
        result.setContent(table);
        result.setStyle("-fx-background: white;" +
                "-fx-border-color: black;");

        //Button
        Button button = new Button("Execute Query");
        button.setOnAction(e -> {
            if (comboBox4.getValue() == null) {
                AlertBox.display("Please select AND/OR relation between attributes!");
                return;
            }
            if (!userRatingInput.getText().equals("")) {
                try {
                    Float.parseFloat(userRatingInput.getText());
                } catch (NumberFormatException err) {
                    AlertBox.display("Please enter a number for user rating value!");
                    return;
                }
            }
            if (!userIDInput.getText().equals("")) {
                try {
                    Integer.parseInt(userIDInput.getText());
                } catch (NumberFormatException err) {
                    AlertBox.display("Please enter an integer for user ID!");
                    return;
                }
            }
            if (!numberInput.getText().equals("")) {
                try {
                    Integer.parseInt(numberInput.getText());
                } catch (NumberFormatException err) {
                    AlertBox.display("Please enter an integer for total rating number!");
                    return;
                }
            }
            if (!ratingInput.getText().equals("")) {
                try {
                    Float.parseFloat(ratingInput.getText());
                } catch (NumberFormatException err) {
                    AlertBox.display("Please enter a number for the rating value!");
                    return;
                }
            }
            if (userIDInput.getText().equals("") && (fromDateUserPicker.getValue() != null ||
                    toDateUserPicker.getValue() != null || !userIDInput.getText().equals("") ||
                    comboBox3.getValue() != null)) {
                AlertBox.display("Please enter an user ID if user's rating will be filtered!");
            }
            ArrayList<String> genreList = new ArrayList<>();
            for (CheckBox each : selGenres) {
                if (each.isSelected()) {
                    genreList.add(each.getText());
                }
            }
            if (genreList.size() == 0) {
                AlertBox.display("Please select at least one genre!");
                return;
            }
            ArrayList<String> counList = new ArrayList<>();
            for (CheckBox each : selCountries) {
                if (each.isSelected()) {
                    counList.add(each.getText());
                }
            }

            StringBuilder queryContent = new StringBuilder();

            if (comboBox4.getValue().equals("AND")) {
                queryContent.append("SELECT  DISTINCT  M.title, M.year, C.country, M.ratings, M.rating_Number, " +
                        "AL.genreList, AL.tagList\nFROM movies_helper M, countries C, Agg_List AL\nWHERE M.mid in (");
                for (int i = 0; i < genreList.size() - 1; i++) {
                    queryContent.append("SELECT M.mid\nFROM movies_helper M, genres G\nWHERE M.mid = G.movieID " +
                            "AND G.genre = '" + genreList.get(i) + "'\nINTERSECT\n");
                }
                queryContent.append("SELECT M.mid\nFROM movies_helper M, genres G\nWHERE M.mid = G.movieID " +
                        "AND G.genre = '" + genreList.get(genreList.size() - 1) + "')");
                if (counList.size() != 0) {
                    queryContent.append(" AND C.country in (");
                    for (int i = 0; i < counList.size() - 1; i++) {
                        queryContent.append("'" + counList.get(i) + "', ");
                    }
                    queryContent.append("'" + counList.get(counList.size() - 1) + "')\n");
                }
                if (!nameInput1.getText().equals("")) {
                    queryContent.append("AND M.mid in (SELECT M.mid\nFROM movies_helper M, Actors A\nWHERE M.mid = A.movieID " +
                            "AND A.actorName = '" + nameInput1.getText() + "'");

                    if (!nameInput2.getText().equals("")) {
                        queryContent.append("\nINTERSECT\nSELECT M.mid\nFROM movies_helper M, Actors A\nWHERE M.mid = A.movieID " +
                                "AND A.actorName = '" + nameInput2.getText() + "'");

                        if (!nameInput3.getText().equals("")) {
                            queryContent.append("\nINTERSECT\nSELECT M.mid\nFROM movies_helper M, Actors A\nWHERE M.mid = A.movieID " +
                                    "AND A.actorName = '" + nameInput3.getText() + "'");

                            if (!nameInput4.getText().equals("")) {
                                queryContent.append("\nINTERSECT\nSELECT M.mid\nFROM movies_helper M, Actors A\nWHERE M.mid = A.movieID " +
                                        "AND A.actorName = '" + nameInput4.getText() + "'");
                            }
                        }
                    }
                    queryContent.append(")\n");
                }
                if (!nameInput.getText().equals("")) {
                    queryContent.append(" AND M.mid in (SELECT M.mid\nFROM movies_helper M, Directors D\nWHERE D.movieID = M.mid " +
                            "AND D.directorName ='" + nameInput.getText() + "')\n");
                }
                if (comboBox1.getValue() != null && !ratingInput.getText().equals("")) {
                    queryContent.append(" AND M.ratings " + comboBox1.getValue() + ratingInput.getText() + "\n");
                }
                if (comboBox2.getValue() != null && !numberInput.getText().equals("")) {
                    queryContent.append(" AND M.rating_number " + comboBox2.getValue() + numberInput.getText() + "\n");
                }
                if (fromDatePicker.getValue() != null) {
                    queryContent.append(" AND M.year >= " + fromDatePicker.getValue().toString().substring(0, 4));
                }
                if (toDatePicker.getValue() != null) {
                    queryContent.append(" AND M.year <= " + toDatePicker.getValue().toString().substring(0, 4) + "\n");
                }
                if (!userIDInput.getText().equals("")) {
                    queryContent.append(" AND M.mid in (SELECT M.mid\nFROM movies_helper M, user_rating R\nWHERE " +
                            "R.movieID = M.mid AND R.userID = " + userIDInput.getText());
                    getUserRating(fromDateUserPicker, toDateUserPicker, queryContent, comboBox3, userRatingInput);
                    queryContent.append(")\n");
                }
                queryContent.append("AND C.movieID = M.mid AND AL.mid = M.mid");
            } else {  // OR between attributes
                queryContent.append("SELECT  DISTINCT  M.title, M.year, C.country, M.ratings, M.rating_Number, " +
                        "AL.genreList, AL.tagList\nFROM movies_helper M, countries C, Agg_List AL, genres G\n" +
                        "WHERE M.mid = G.movieID AND C.movieID = M.mid AND AL.mid = M.mid AND \n(G.genre in (");
                    for (int i = 0; i < genreList.size() - 1; i++) {
                        queryContent.append("'" + genreList.get(i) + "', ");
                    }
                    queryContent.append("'" + genreList.get(genreList.size() - 1) + "')\n");
                if (counList.size() != 0) {
                    queryContent.append("OR C.country in (");
                    for (int i = 0; i < counList.size() - 1; i++) {
                        queryContent.append("'" + counList.get(i) + "', ");
                    }
                    queryContent.append("'" + counList.get(counList.size() - 1) + "')\n");
                }
                if (!nameInput1.getText().equals("")) {
                    queryContent.append(" OR M.mid in (SELECT M1.mid\nFROM movies_helper M1, Actors A\nWHERE M1.mid = A.movieID " +
                            "AND A.actorName in ('" + nameInput1.getText());
                    if (!nameInput2.getText().equals("")) {
                        queryContent.append("', '" + nameInput1.getText());
                        if (!nameInput3.getText().equals("")) {
                            queryContent.append("', '" + nameInput3.getText());
                            if (!nameInput4.getText().equals("")) {
                                queryContent.append("', '" + nameInput1.getText());
                            }
                        }
                    }
                    queryContent.append("'))\n");
                }

                if (!nameInput.getText().equals("")) {
                    queryContent.append(" OR M.mid in (SELECT M2.mid\nFROM movies_helper M2, Directors D\nWHERE D.movieID = M2.mid " +
                            "AND D.directorName = '" + nameInput.getText() + "')\n");
                }

                if (comboBox1.getValue() != null && !ratingInput.getText().equals("")) {
                    queryContent.append(" OR M.ratings " + comboBox1.getValue() + ratingInput.getText() + "\n");
                }

                if (comboBox2.getValue() != null && !numberInput.getText().equals("")) {
                    queryContent.append(" OR M.rating_number " + comboBox2.getValue() + numberInput.getText() + "\n");
                }
                if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
                    queryContent.append(" OR (M.year >= " + fromDatePicker.getValue().toString().substring(0, 4)
                            + " AND M.year <= " + toDatePicker.getValue().toString().substring(0, 4) + ")\n");
                }
                if (fromDatePicker.getValue() != null && toDatePicker.getValue() == null) {
                    queryContent.append(" OR M.year >= " + fromDatePicker.getValue().toString().substring(0, 4) + "\n");

                }
                if (fromDatePicker.getValue() == null && toDatePicker.getValue() != null) {
                    queryContent.append(" OR M.year <= " + toDatePicker.getValue().toString().substring(0, 4) + "\n");

                }
                if (!userIDInput.getText().equals("")) {
                    queryContent.append("OR M.mid in (SELECT M3.mid\nFROM movies_helper M3, user_rating R\nWHERE " +
                                    "R.movieID = M3.mid AND R.userID = " + userIDInput.getText() + "\n");
                    getUserRating(fromDateUserPicker, toDateUserPicker, queryContent, comboBox3, userRatingInput);
                    queryContent.append(")\n");
                }
                queryContent.append(")");
            }
            queryT.setText(queryContent.toString());
            System.out.println(queryContent.toString());

            try {
                Statement execute = conn.createStatement();
                ResultSet rs = execute.executeQuery(queryContent.toString());
                data.clear();
                while (rs.next()) {
                    Tuple cur = new Tuple(rs.getString("title"), rs.getString("year"), rs.getString("country"),
                            rs.getString("ratings"), rs.getString("rating_Number"), rs.getString("genreList"),
                            rs.getString("tagList"));
                    data.add(cur);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        VBox boxDownLeft = new VBox();
        boxDownLeft.setSpacing(10);

        HBox combo = new HBox();
        combo.setSpacing(20);
        combo.getChildren().addAll(comboBox4, button);

        boxDownLeft.getChildren().addAll(search, combo, query);

        HBox boxDown = new HBox();
        boxDown.setPadding(new Insets(10, 10, 10, 10));
        boxDown.setSpacing(10);
        boxDown.setStyle(
                "-fx-border-style: solid inside;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-insets: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: black;");
        boxDown.getChildren().addAll(boxDownLeft, result);


        VBox box = new VBox();
        box.setPadding(new Insets(10, 10, 10, 10));
        box.setSpacing(5);
        box.getChildren().addAll(boxUp, boxDown);


        Scene scene = new Scene(box, 1400, 800);
        window.setScene(scene);
        window.show();
    }

    private void getUserRating(DatePicker fromDateUserPicker, DatePicker toDateUserPicker, StringBuilder queryContent,
                               ComboBox comboBox3, TextField userRatingInput) {
        if (fromDateUserPicker.getValue() != null) {
            String datefrom = fromDateUserPicker.getValue().toString();
            queryContent.append(" AND R.rating_date >= TO_DATE('" + datefrom +
                    "', 'YYYY-MM-DD')" + "\n");
        }
        if (toDateUserPicker.getValue() != null) {
            String dateTo = toDateUserPicker.getValue().toString();
            queryContent.append(" AND R.rating_date <= TO_DATE('" + dateTo +
                    "', 'YYYY-MM-DD')" + "\n");
        }
        if (comboBox3.getValue() != null && !userRatingInput.getText().equals("")) {
            queryContent.append(" AND R.rating " + comboBox3.getValue() + userRatingInput.getText());
        }
    }

    public static class Tuple {

        private final SimpleStringProperty movie_Title;
        private final SimpleStringProperty year;
        private final SimpleStringProperty country;
        private final SimpleStringProperty rating_Value;
        private final SimpleStringProperty rating_Number;
        private final SimpleStringProperty genre;
        private final SimpleStringProperty tags;

        public Tuple(String movie_Title, String year, String country,
                     String rating_Value, String rating_Number, String genre, String tags) {
            this.movie_Title = new SimpleStringProperty(movie_Title);
            this.genre = new SimpleStringProperty(genre);
            this.year = new SimpleStringProperty(year);
            this.country = new SimpleStringProperty(country);
            this.rating_Value = new SimpleStringProperty(rating_Value);
            this.rating_Number = new SimpleStringProperty(rating_Number);
            this.tags = new SimpleStringProperty(tags);
        }

        public void setMovie_Title(String s) {
            movie_Title.set(s);
        }

        public void setGenre(String s) {
            genre.set(s);
        }

        public void setTags(String s) {
            tags.set(s);
        }

        public void setYear(String s) {
            year.set(s);
        }

        public void setCountry(String s) {
            country.set(s);
        }

        public void setRating_Value(String s) {
            rating_Value.set(s);
        }

        public void setRating_Number(String s) {
            rating_Number.set(s);
        }
        public String getMovie_Title() {
            return movie_Title.get();
        }
        public String getGenre() {
            return genre.get();
        }
        public String getTags() {
            return tags.get();
        }
        public String getYear() {
            return year.get();
        }
        public String getCountry() {
            return country.get();
        }
        public String getRating_Value() {
            return rating_Value.get();
        }
        public String getRating_Number() {
            return rating_Number.get();
        }
    }

    private static class AlertBox {

        private static void display(String message) {
            Stage alertWindow = new Stage();

            alertWindow.initModality(Modality.APPLICATION_MODAL);
            alertWindow.setTitle("Invalid input value");
            alertWindow.setMinWidth(250);

            Label words = new Label();
            words.setText(message);
            Button closeButton = new Button("OK");
            closeButton.setOnAction(e -> alertWindow.close());

            VBox layout = new VBox(20);
            layout.getChildren().addAll(words, closeButton);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20, 20, 20, 20));

            Scene scene = new Scene(layout);
            alertWindow.setScene(scene);
            alertWindow.showAndWait();
        }
    }
}




