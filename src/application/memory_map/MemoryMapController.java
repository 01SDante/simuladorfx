package application.memory_map;

import java.util.ArrayList;

import application.model.Particion;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MemoryMapController {

	public static void MapaMemoriaFCFS(ArrayList<ArrayList<Particion>> particiones, int ram, String titulo) {

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

				if (p.getLibre()) {
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

	public static void MemoryMap() {

		Color color = new Color(0.8, 0.8, 0.8, 0.2);
		Color azul = Color.rgb(0, 0, 128, 0.8);
		Color rojo = Color.rgb(224, 0, 0, 0.85);
		Color verde = Color.rgb(0, 128, 0, 0.8);

		/*
		 * CANVAS 1
		 * 
		 */
		final Canvas canvas1 = new Canvas(200, 800);
		GraphicsContext gc = canvas1.getGraphicsContext2D();

		gc.setFill(azul);
		gc.fillRect(50, 45, 150, 50);
		gc.setFill(rojo);
		gc.fillRect(50, 100, 150, 100);
		gc.setFill(verde);
		gc.fillRect(50, 205, 150, 100);
		gc.setFill(verde);
		gc.fillRect(50, 310, 150, 100);

		gc.setStroke(Color.BLACK);
		gc.strokeText("T = 0", 110, 30);

		gc.setStroke(Color.WHITE);
		gc.strokeText("SO : 100 u.m.", 60, 75);

		gc.strokeText("Partición: 0", 60, 120);
		gc.strokeText("Tamaño: 300 u.m.", 60, 140);
		gc.strokeText("Proceso: P1", 60, 160);
		gc.strokeText("Estado: Ocupado", 60, 180);

		gc.strokeText("Partición: 1", 60, 225);
		gc.strokeText("Tamaño: 250 u.m.", 60, 245);
		gc.strokeText("Proceso: -", 60, 265);
		gc.strokeText("Estado: Libre", 60, 285);

		gc.strokeText("Partición: 2", 60, 330);
		gc.strokeText("Tamaño: 350 u.m.", 60, 350);
		gc.strokeText("Proceso: -", 60, 370);
		gc.strokeText("Estado: Libre", 60, 390);

		/*
		 * CANVAS 2
		 * 
		 */

		final Canvas canvas2 = new Canvas(200, 800);
		gc = canvas2.getGraphicsContext2D();

		gc.setFill(azul);
		gc.fillRect(50, 45, 150, 50);
		gc.setFill(rojo);
		gc.fillRect(50, 100, 150, 100);
		gc.setFill(rojo);
		gc.fillRect(50, 205, 150, 100);
		gc.setFill(verde);
		gc.fillRect(50, 310, 150, 100);

		gc.setStroke(Color.BLACK);
		gc.strokeText("T = 1", 110, 30);

		gc.setStroke(Color.WHITE);
		gc.strokeText("SO : 100 u.m.", 60, 75);

		gc.strokeText("Partición: 0", 60, 120);
		gc.strokeText("Tamaño: 300 u.m.", 60, 140);
		gc.strokeText("Proceso: P1", 60, 160);
		gc.strokeText("Estado: Ocupado", 60, 180);

		gc.strokeText("Partición: 1", 60, 225);
		gc.strokeText("Tamaño: 250 u.m.", 60, 245);
		gc.strokeText("Proceso: P2", 60, 265);
		gc.strokeText("Estado: Ocupado", 60, 285);

		gc.strokeText("Partición: 2", 60, 330);
		gc.strokeText("Tamaño: 350 u.m.", 60, 350);
		gc.strokeText("Proceso: -", 60, 370);
		gc.strokeText("Estado: Libre", 60, 390);

		/*
		 * CANVAS 3
		 * 
		 */
		final Canvas canvas3 = new Canvas(200, 800);
		gc = canvas3.getGraphicsContext2D();

		gc.setFill(azul);
		gc.fillRect(50, 45, 150, 50);
		gc.setFill(verde);
		gc.fillRect(50, 100, 150, 100);
		gc.setFill(rojo);
		gc.fillRect(50, 205, 150, 100);
		gc.setFill(verde);
		gc.fillRect(50, 310, 150, 100);

		gc.setStroke(Color.BLACK);
		gc.strokeText("T = 2", 110, 30);

		gc.setStroke(Color.WHITE);
		gc.strokeText("SO : 100 u.m.", 60, 75);

		gc.strokeText("Partición: 0", 60, 120);
		gc.strokeText("Tamaño: 300 u.m.", 60, 140);
		gc.strokeText("Proceso: -", 60, 160);
		gc.strokeText("Estado: Libre", 60, 180);

		gc.strokeText("Partición: 1", 60, 225);
		gc.strokeText("Tamaño: 250 u.m.", 60, 245);
		gc.strokeText("Proceso: P2", 60, 265);
		gc.strokeText("Estado: Ocupado", 60, 285);

		gc.strokeText("Partición: 2", 60, 330);
		gc.strokeText("Tamaño: 350 u.m.", 60, 350);
		gc.strokeText("Proceso: -", 60, 370);
		gc.strokeText("Estado: Libre", 60, 390);

		// FIN CANVAS

		HBox hbox = new HBox();
		hbox.getChildren().addAll(canvas1, canvas2, canvas3);

		ScrollPane sp = new ScrollPane(hbox);

		Scene scene = new Scene(sp, 600, 400, color);

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Mapa de Memoria");
		stage.show();

	}

}
