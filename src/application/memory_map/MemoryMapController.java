package application.memory_map;

import java.util.ArrayList;

import application.model.Particion;
import application.model.ParticionVariable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MemoryMapController {

	/*
	 * PARTICIONES FIJAS
	 * 
	 */
	public static void generarMapaMemoriaPartFijas(ArrayList<ArrayList<Particion>> particiones, int ram, String titulo) {

		HBox hbox = new HBox();

		for (int i = 0; i < particiones.size(); i++) {

			Canvas canvas = new Canvas(200, 1200);
			GraphicsContext gc = canvas.getGraphicsContext2D();

			int yRectangulo = 100; // Corner izquierdo eje y del rectangulo
			int yTexto = 120; // Posicion eje y texto

			gc.setFill(Color.BLUE);
			gc.fillRect(50, 45, 150, 50);

			gc.setStroke(Color.BLACK);
			gc.strokeText("T = " + i, 110, 30);

			gc.setStroke(Color.WHITE);
			gc.strokeText("SO : " + ram + " u.m.", 60, 75);

			for (int j = 0; j < particiones.get(i).size(); j++) {
				
				Particion p = particiones.get(i).get(j);

				if (p.isLibre()) {
					gc.setFill(Color.GREEN);
					gc.fillRect(50, yRectangulo, 150, 100);

					gc.strokeText("Partición: " + p.getId(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Tamaño: " + p.getTamanio() + " u.m.", 60, yTexto);
					yTexto += 20;
					gc.strokeText("Proceso: - ", 60, yTexto);
					yTexto += 20;
					gc.strokeText("Estado: Libre", 60, yTexto);

				} else {
					gc.setFill(Color.RED);
					gc.fillRect(50, yRectangulo, 150, 100);
					
					gc.strokeText("Partición: " + p.getId(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Tamaño: " + p.getTamanio() + " u.m.", 60, yTexto);
					yTexto += 20;
					gc.strokeText("Proceso: " + p.getProceso(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Estado: Ocupado", 60, yTexto);
				}

				yRectangulo += 105;
				yTexto += 45;

			}

			hbox.getChildren().add(canvas);

		}

		ScrollPane sp = new ScrollPane(hbox);

		Scene scene = new Scene(sp, 600, 400, new Color(0.8, 0.8, 0.8, 0.2));

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(titulo);
		stage.show();

	}

	/*
	 * PARTICIONES VARIABLES
	 * 
	 */
	public static void generarMapaMemoriaPartVariables(ArrayList<ArrayList<ParticionVariable>> particiones, int ram, String titulo) {

		HBox hbox = new HBox();

		for (int i = 0; i < particiones.size(); i++) {

			Canvas canvas = new Canvas(200, 1200);
			GraphicsContext gc = canvas.getGraphicsContext2D();

			int yRectangulo = 100; // Corner izquierdo eje y del rectangulo
			int yTexto = 120; // Posicion eje y texto

			gc.setFill(Color.BLUE);
			gc.fillRect(50, 45, 150, 50);

			gc.setStroke(Color.BLACK);
			gc.strokeText("T = " + i, 110, 30);

			gc.setStroke(Color.WHITE);
			gc.strokeText("SO : " + ram + " u.m.", 60, 75);

			for (int j = 0; j < particiones.get(i).size(); j++) {
				
				ParticionVariable p = particiones.get(i).get(j);

				if (p.isLibre()) {
					gc.setFill(Color.GREEN);
					gc.fillRect(50, yRectangulo, 150, 100);

					gc.strokeText("Dir. Inicio: " + p.getDirInicio(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Dir. Fin: " + p.getDirFin(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Proceso: - ", 60, yTexto);
					yTexto += 20;
					gc.strokeText("Estado: Libre", 60, yTexto);

				} else {
					gc.setFill(Color.RED);
					gc.fillRect(50, yRectangulo, 150, 100);
					
					gc.strokeText("Dir. Inicio: " + p.getDirInicio(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Dir. Fin: " + p.getDirFin(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Proceso: " + p.getProceso(), 60, yTexto);
					yTexto += 20;
					gc.strokeText("Estado: Ocupado", 60, yTexto);
				}

				yRectangulo += 105;
				yTexto += 45;

			}

			hbox.getChildren().add(canvas);

		}

		ScrollPane sp = new ScrollPane(hbox);

		Scene scene = new Scene(sp, 600, 400, new Color(0.8, 0.8, 0.8, 0.2));

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(titulo);
		stage.show();

	}

}
