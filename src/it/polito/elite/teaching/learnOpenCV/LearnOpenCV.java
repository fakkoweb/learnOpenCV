package it.polito.elite.teaching.learnOpenCV;

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class LearnOpenCV extends Application{

	////////////////////////////////////
	// JAVA FX STARTUP
	////////////////////////////////////
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		MainWindowGUI mainWindow = new MainWindowGUI(primaryStage);
	}
	
	
}
