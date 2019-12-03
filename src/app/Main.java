package app;

import java.io.IOException;

import app.bd.BD;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage ventanaPrincipal) throws IOException {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		ventanaPrincipal.setScene(scene);
		ventanaPrincipal.setTitle("Simulador FX");
		ventanaPrincipal.setResizable(false);
		ventanaPrincipal.setOnCloseRequest(e -> System.exit(0));
		ventanaPrincipal.show();
		BD.getInstance();
	}

}
