import dao.CityDao;
import dao.CityDaoFile;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.City;
import service.CityService;
import service.CityServiceFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphCitiesApp extends Application {
    static CityDao dao;
    static CityService service;
    public static Logger logger = Logger.getLogger(GraphCitiesApp.class.getName());
    public static void main(String[] args) throws IOException {
        logger.addHandler(new FileHandler("cities"));
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 600,400);

        primaryStage.setScene(scene);
        primaryStage.show();

        Button loadBtn = new Button("Wczytaj");
        loadBtn.setAlignment(Pos.CENTER);
        TableView<City> table = new TableView<>();

        ListView<City> citiesList = new ListView<>();

        HBox row = new HBox();
        row.getChildren().add(loadBtn);
        Label label = new Label("Plik: ");
        row.getChildren().add(label);
        row.setAlignment(Pos.CENTER);
        row.setSpacing(10);
        root.add(row,0,0);

        HBox filterBox = new HBox();
        ComboBox<String> countryCodeBox = new ComboBox<>();
        filterBox.getChildren().add(countryCodeBox);
        root.add(filterBox, 0, 1);
        root.add(citiesList,0, 2);

        root.setVgap(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        countryCodeBox.setOnAction(event -> {
            String code = countryCodeBox.getSelectionModel().getSelectedItem();
            if ("ALL".equals(code)){
                citiesList.getItems().clear();
                citiesList.getItems().addAll(dao.findAll());
                return;
            }
            citiesList.getItems().clear();
            citiesList.getItems().addAll(service.findByCountryCode(code));
        });

        loadBtn.setOnAction(event -> {
            try {
                //okno dialogowe o wyboru pliku
                FileChooser fileChooser = new FileChooser();
                File file;
                //wyświetlenie okna w trybie open
                file = fileChooser.showOpenDialog(primaryStage);
                //jeśli użytkownik nie wybrał pliku to file jest równe null
                if (file == null) {
                    logger.log(Level.WARNING, "Nie wybrano żadnego pliku");
                    //logger.warning("Nie wybrano żadnego pliku");
                    return;
                }
                dao = new CityDaoFile(file.getPath());
                service = new CityServiceFile(dao);
                citiesList.getItems().addAll(dao.findAll());
                label.setText(file.getPath());
                countryCodeBox.getItems().add("ALL");
                countryCodeBox.getItems().addAll(service.findCountryCodes());
                logger.log(Level.INFO,"Wczytano plik "+ file.getPath());
            } catch (IOException e) {
                System.out.println("Blad odczytu pliku!!!");
            }
        });
    }
}
