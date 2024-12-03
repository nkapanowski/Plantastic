import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.util.HashMap;


public class Plantastic extends Application {


    private int ecoFriendlyPoints = 0;
    private int ecoFriendlyLevel = 1;
    private Label ecoFriendlyMeterLabel;
    private BorderPane mainLayout;


    private HashMap<String, Plant> plants = new HashMap<>(); // Stores the plant information (name, growth stage, clicks)


    private int[] unlockedPlants = {1, 1, 1}; // Number of unlocked plants per category


    private String[][] plantOptions = {
            {"Birch Tree", "Maple Tree", "Spruce Tree", "Palm Tree", "Acadia Tree"}, // Trees
            {"Daffodil", "Orchaid", "Poppy", "Rose", "Sunflower"},            // Flowers
            {"Succulent", "Bamboo", "Fern", "Monstera", "Aloe Vera"}                // Houseplants
    };


    private Button[] categoryButtons = new Button[3]; // Store category buttons for color update


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Plantastic Clicker Game");


        // Main layout
        mainLayout = new BorderPane();


        // Initial start screen
        VBox startScreen = new VBox(20);
        startScreen.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: lightgreen;");


        Label welcomeLabel = new Label("Welcome to Plantastic!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button startButton = new Button("Start");
        startButton.setStyle("-fx-font-size: 16px;");
        startButton.setOnAction(event -> showCategorySelection());


        startScreen.getChildren().addAll(welcomeLabel, startButton);
        mainLayout.setCenter(startScreen);


        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void showCategorySelection() {
        VBox categorySelection = new VBox(20);
        categorySelection.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: lightgreen;");


        Label selectLabel = new Label("Select a plant category to start:");
        selectLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");


        // Buttons for each category
        Button treeButton = new Button("Trees");
        treeButton.setStyle("-fx-font-size: 16px;");
        treeButton.setOnAction(event -> initializeGame(0));


        Button flowerButton = new Button("Flowers");
        flowerButton.setStyle("-fx-font-size: 16px;");
        flowerButton.setOnAction(event -> initializeGame(1));


        Button housePlantButton = new Button("Houseplants");
        housePlantButton.setStyle("-fx-font-size: 16px;");
        housePlantButton.setOnAction(event -> initializeGame(2));


        // Save the category buttons for later style updates
        categoryButtons[0] = treeButton;
        categoryButtons[1] = flowerButton;
        categoryButtons[2] = housePlantButton;


        categorySelection.getChildren().addAll(selectLabel, treeButton, flowerButton, housePlantButton);
        mainLayout.setCenter(categorySelection);
    }


    private void initializeGame(int categoryIndex) {
        VBox gameLayout = new VBox(20);
        gameLayout.setPadding(new Insets(20));
        gameLayout.setStyle("-fx-alignment: center; -fx-background-color: lightgreen;");


        // Back Button
        Button backButton = new Button("Back to Main Menu");
        backButton.setStyle("-fx-font-size: 14px;");
        backButton.setOnAction(event -> showCategorySelection());
        gameLayout.getChildren().add(backButton);


        // Eco-Friendly Meter
        ecoFriendlyMeterLabel = new Label("Eco-Friendly Meter: Level 1 (0 points)");
        ecoFriendlyMeterLabel.setStyle(
                "-fx-font-size: 18px; -fx-border-color: green; -fx-border-width: 2; -fx-padding: 10;");
        gameLayout.getChildren().add(ecoFriendlyMeterLabel);


        // Add plant buttons for the selected category
        VBox plantButtons = new VBox(10);
        plantButtons.setStyle("-fx-padding: 10;");


        for (int i = 0; i < plantOptions[categoryIndex].length; i++) {
            int plantIndex = i;
            String plantName = plantOptions[categoryIndex][plantIndex];


            // Initialize all plants with SEED stage and 0 clicks
            plants.put(plantName, new Plant(plantName, GrowthStage.SEED, 0));


            Button plantButton = new Button(plantName + " (Seed)");
            plantButton.setStyle("-fx-font-size: 14px;");
            plantButton.setVisible(i < unlockedPlants[categoryIndex]); // Hide locked plants


            plantButton.setOnAction(event -> {
                ecoFriendlyPoints += 2; // Increment points per click
                updateEcoFriendlyMeter();


                // Update plant growth stage
                Plant plant = plants.get(plantName);
                plant.incrementClicks();


                // Update button text
                plantButton.setText(plant.getName() + " (" + plant.getGrowthStage() + ")");


                // Unlock next plant when this plant reaches FULLY_GROWN
                if (plant.getGrowthStage() == GrowthStage.FULLY_GROWN && plantIndex + 1 < plantButtons.getChildren().size()) {
                    unlockNextPlant(plantButtons, plantIndex + 1, categoryIndex);
                }


                // Check if all plants in the category are fully grown
                checkCategoryCompletion(categoryIndex);
            });


            plantButtons.getChildren().add(plantButton);
        }


        gameLayout.getChildren().add(plantButtons);
        mainLayout.setCenter(gameLayout);
    }


    private void unlockNextPlant(VBox plantButtons, int nextPlantIndex, int categoryIndex) {
        if (nextPlantIndex < plantButtons.getChildren().size()) {
            Button nextPlantButton = (Button) plantButtons.getChildren().get(nextPlantIndex);
            nextPlantButton.setVisible(true);
            unlockedPlants[categoryIndex] = nextPlantIndex + 1;
        }
    }


    private void updateEcoFriendlyMeter() {
        int pointsNeededForNextLevel = ecoFriendlyLevel * 10;
        if (ecoFriendlyPoints >= pointsNeededForNextLevel) {
            ecoFriendlyLevel++;
        }
        ecoFriendlyMeterLabel.setText(
                "Eco-Friendly Meter: Level " + ecoFriendlyLevel + " (" + ecoFriendlyPoints + " points)");
    }


    // Check if all plants in the category are fully grown
    private void checkCategoryCompletion(int categoryIndex) {
        boolean allFullyGrown = true;
        for (int i = 0; i < plantOptions[categoryIndex].length; i++) {
            String plantName = plantOptions[categoryIndex][i];
            Plant plant = plants.get(plantName);
            System.out.println("Checking plant: " + plantName + " | Growth Stage: " + plant.getGrowthStage()); // Debugging line
            if (plant.getGrowthStage() != GrowthStage.FULLY_GROWN) {
                allFullyGrown = false;
                break;
            }
        }


        if (allFullyGrown) {
            System.out.println("All plants in category " + getCategoryName(categoryIndex) + " are fully grown!"); // Debugging line
            displayCategoryCompletionMessage(categoryIndex);


            // Make sure the button's background color changes to dark green
            categoryButtons[categoryIndex].setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;"); // Change button color to dark green
        }
    }


    // Display completion message when all plants in a category are fully grown
    private void displayCategoryCompletionMessage(int categoryIndex) {
        VBox completionMessageLayout = new VBox(20);
        completionMessageLayout.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: lightgreen;");


        String categoryName = getCategoryName(categoryIndex);
        Label completionMessage = new Label(categoryName + " category complete!");
        completionMessage.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");


        Button backButton = new Button("Back to Category Selection");
        backButton.setStyle("-fx-font-size: 16px;");
        backButton.setOnAction(event -> showCategorySelection());


        completionMessageLayout.getChildren().addAll(completionMessage, backButton);
        mainLayout.setCenter(completionMessageLayout);
    }


    // Get the category name based on the index
    private String getCategoryName(int categoryIndex) {
        switch (categoryIndex) {
            case 0: return "Tree";
            case 1: return "Flower";
            case 2: return "Houseplant";
            default: return "Unknown";
        }
    }


    // GrowthStage enum
    enum GrowthStage {
        SEED,
        SPROUT,
        BLOSSOM,
        FULLY_GROWN;


        public GrowthStage next() {
            return this.ordinal() < values().length - 1 ? values()[this.ordinal() + 1] : this;
        }
    }


    // Plant class with growth stage and click counter
    class Plant {
        private String name;
        private GrowthStage growthStage;
        private int clicks;


        public Plant(String name, GrowthStage growthStage, int clicks) {
            this.name = name;
            this.growthStage = growthStage;
            this.clicks = clicks;
        }


        public String getName() {
            return name;
        }


        public GrowthStage getGrowthStage() {
            return growthStage;
        }


        public void incrementClicks() {
            clicks++;
            if (clicks >= 5 && growthStage == GrowthStage.SEED) {
                growthStage = GrowthStage.SPROUT;
            } else if (clicks >= 10 && growthStage == GrowthStage.SPROUT) {
                growthStage = GrowthStage.BLOSSOM;
            } else if (clicks >= 15 && growthStage == GrowthStage.BLOSSOM) {
                growthStage = GrowthStage.FULLY_GROWN;
            }
        }
    }
}
