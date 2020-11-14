package gui;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.Arrays;


public class AgentWindow {
    Stage stage;

    public AgentWindow(String playerName, String role, String info, boolean isAlive) {
        String[] result = info.split("#");
        String[] susRates = Arrays.copyOfRange(info.split("#"), 1, info.split("#").length);
        String status = isAlive ? "Alive" : "Dead";

        // TOP COMPONENT
        HBox[] topHBox = new HBox[4];
        topHBox[0] = new HBox(new Label("Name:    \t"), new Label(playerName));
        topHBox[1] = new HBox(new Label("Role:    \t"), new Label(role));
        topHBox[2] = new HBox(new Label("Trait:   \t"), new Label(result[0]));
        topHBox[3] = new HBox(new Label("Status:  \t"), new Label(status));
        VBox vbox1 = new VBox(topHBox);
        vbox1.getStyleClass().add("top");
        vbox1.setPadding(new Insets(0, 0, 50, 0));


        // TABLE TITLE
        Label label1 = new Label("Name");
        label1.getStyleClass().clear();
        label1.getStyleClass().add("headers");
        Label label2 = new Label("Sus Rate (%)");
        label2.getStyleClass().clear();
        label2.getStyleClass().add("headers");

        HBox hbox = new HBox(label1, label2);
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("table-title");

        VBox vbox2 = new VBox();
        vbox2.setAlignment(Pos.CENTER);
        vbox2.getStyleClass().add("table-title");
        vbox2.getChildren().add(hbox);


        // TABLE
        VBox col1 = new VBox();
        VBox col2 = new VBox();
        for(String susRate : susRates) {
            String[] pair = susRate.split(":");
            int percentage = (int) (Math.floor(Float.parseFloat(pair[1])*100));

            Label peer = new Label(pair[0]);
            Label peerSusRate = new Label(percentage + "%");
            peer.getStyleClass().add("table-body1");
            peerSusRate.getStyleClass().add("table-body2");
            col1.getChildren().add(peer);
            col2.getChildren().add(peerSusRate);
        }
        col1.getStyleClass().add("no-bg");
        col2.getStyleClass().add("no-bg");

        // TABLE BODY
        VBox vbox3 = new VBox(new HBox(col1, col2));
        vbox3.setAlignment(Pos.CENTER);



        // CLOSE BUTTON
        Button closeButton = new Button();
        closeButton.setText("Close");
        closeButton.setOnAction(e -> stage.close());
        VBox vbox4 = new VBox(closeButton);
        vbox4.setPadding(new Insets(10, 0, 10, 0));
        vbox4.setAlignment(Pos.CENTER);




        // Final layout
        stage = new Stage();
        stage.setTitle(playerName.toUpperCase() + " | " + role.toUpperCase());
        VBox layout = new VBox(vbox1);
        layout.getChildren().add(vbox2);
        layout.getChildren().add(vbox3);
        layout.getChildren().add(vbox4);
        layout.getStyleClass().add("ola");
        layout.getStylesheets().add("/gui/style.css");
        layout.getStylesheets().add("https://fonts.googleapis.com/css?family=Open+Sans");
        layout.applyCss();
        Scene scene = new Scene(layout, 600, 225+24*susRates.length);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/resources/icon.png"));
    }
    public void display() { stage.show(); }
}