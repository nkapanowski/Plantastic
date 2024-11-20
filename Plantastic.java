import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Plantastic extends Application {

    private int ecoFriendlyPoints = 0;
    private int ecoFriendlyLevel = 1;
    private Label ecoFriendlyMeterLabel;
    private BorderPane mainLayout;

    private VBox treePatch, flowerPatch, housePlantPatch;
    private String[][] plantOptions = {
            {"Birch Tree", "Maple Tree", "Palm Tree", "Bonsai Tree"},
            {"Daffodil", "Chrysanthemum", "Poppy", "Rose", "Sunflower"},
            {"Succulent", "Bamboo", "Fern", "Monstera", "Aloe Vera"}
    };

    private enum GrowthStage {
        SEED, SPROUT, BLOSSOM;

        public GrowthStage next() {
            return this.ordinal() < GrowthStage.values().length - 1
                    ? GrowthStage.values()[this.ordinal() + 1]
                    : this; // Stay at BLOSSOM
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Plantastic Clicker Game");

        // Main layout
        mainLayout = new BorderPane();

        // Initial start screen
        VBox startScreen = new VBox(20);
        startScreen.setStyle("-fx-alignment: center; -fx-padding: 50;");

        Label welcomeLabel = new Label("Welcome to Plantastic!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Button startButton = new Button("Start");
        startButton.setFont(Font.font("Arial", 16));

        // Remove default focus styling for the start button
        startButton.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        startButton.setOnAction(event -> initializeGame());

        startScreen.getChildren().addAll(welcomeLabel, startButton);
        mainLayout.setCenter(startScreen);

        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGame() {
        // Create the game layout
        VBox gameLayout = new VBox(20);
        gameLayout.setPadding(new Insets(20));
        gameLayout.setStyle("-fx-alignment: center;");

        // Add the Eco-Friendly Meter at the top
        ecoFriendlyMeterLabel = new Label("Eco-Friendly Meter: Level 1 (0 points)");
        ecoFriendlyMeterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ecoFriendlyMeterLabel.setStyle(
                "-fx-border-color: green; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f0fff0;");
        gameLayout.getChildren().add(ecoFriendlyMeterLabel);

        // Add plant patches
        HBox patchesLayout = new HBox(20);
        patchesLayout.setPadding(new Insets(10));
        patchesLayout.setStyle("-fx-alignment: center;");

        treePatch = createPatch("Tree Patch");
        flowerPatch = createPatch("Flower Patch");
        housePlantPatch = createPatch("House Plant Patch");

        patchesLayout.getChildren().addAll(treePatch, flowerPatch, housePlantPatch);

        gameLayout.getChildren().add(patchesLayout);
        mainLayout.setCenter(gameLayout);

        updatePatches(); // Initialize with no plants visible
    }

    private VBox createPatch(String title) {
        VBox patchBox = new VBox(10);
        patchBox.setPadding(new Insets(10));
        patchBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-color: #e8f5e9;");

        Label patchTitle = new Label(title);
        patchTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox plantsBox = new VBox(10); // Empty initially; plants are added dynamically
        patchBox.getChildren().addAll(patchTitle, plantsBox);
        return patchBox;
    }

    private void updatePatches() {
        updatePatch(treePatch, plantOptions[0], 2);
        updatePatch(flowerPatch, plantOptions[1], 3);
        updatePatch(housePlantPatch, plantOptions[2], 4);
    }

    private void updatePatch(VBox patch, String[] plants, int pointsPerClick) {
        VBox plantsBox = (VBox) patch.getChildren().get(1); // Plants container
        plantsBox.getChildren().clear(); // Clear previous plants
        for (int i = 0; i < ecoFriendlyLevel && i < plants.length; i++) {
            plantsBox.getChildren().add(createPlantBox(plants[i], pointsPerClick));
        }
    }

    private VBox createPlantBox(String plantName, int pointsPerClick) {
        VBox plantBox = new VBox(5);
        plantBox.setPadding(new Insets(5));
        plantBox.setStyle("-fx-background-color: #f9fbe7; -fx-background-radius: 5;");

        Label plantLabel = new Label(plantName + " (Seed)");
        Button plantButton = new Button("Grow " + plantName);

        // Remove default focus styling for the button
        plantButton.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        GrowthStage[] stage = {GrowthStage.SEED}; // Initial stage

        plantButton.setOnAction(event -> {
            if (stage[0] != GrowthStage.BLOSSOM) {
                stage[0] = stage[0].next();
                plantLabel.setText(plantName + " (" + stage[0] + ")");

                // Add points to the Eco-Friendly Meter
                ecoFriendlyPoints += pointsPerClick;
                updateEcoFriendlyMeter();
            }
        });

        plantBox.getChildren().addAll(plantLabel, plantButton);
        return plantBox;
    }

    private void updateEcoFriendlyMeter() {
        if (ecoFriendlyPoints >= ecoFriendlyLevel * 5 && ecoFriendlyLevel < 5) {
            ecoFriendlyLevel++;
        }
        ecoFriendlyMeterLabel.setText("Eco-Friendly Meter: Level " + ecoFriendlyLevel + " (" + ecoFriendlyPoints + " points)");
        updatePatches();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
