package it.polito.elite.teaching.learnOpenCV;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;




import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import it.polito.elite.teaching.learnOpenCVBlocks.*;

/**
 * Main interface for single blocks execution and loading into MainWindowGUI
 * See documentation for details.
 * <p>
 * @author Marco Bartolotta <bartolotta.marco @ gmail.com>
 * @since	2014-07-01
 */
public class EditBlock {
	/////////////////////
	/// FXML VARIABLES///
	////////////////////
	@FXML
	protected GridPane edit;

	@FXML
	protected AnchorPane control;

	@FXML
	protected ListView<String> libraries;

	@FXML
	protected ListView<String> algorithms;

	@FXML
	protected ListView<String> function;

	@FXML
	protected Button add;

	@FXML
	protected Button save;

	@FXML
	protected Button elaborate;

	@FXML
	protected ImageView source;

	@FXML
	protected ImageView processed;

	@FXML
	protected TextArea blockCode;

	@FXML
	private Label showError;

	@FXML
	protected TextArea description;
    ////////////////////////////////////////////////////////////////////////
	// VARIABLES LINKED TO THE LIST OF LIBRARIES FUNCTIONS AND ALGORITHMS//
	//////////////////////////////////////////////////////////////////////
	ObservableList<ObservableList<String>> lib;
	ObservableList<String> core;
	ObservableList<String> ImgProc;
	ObservableList<String> libViewed;
	ObservableList<String> algViewed;
     /////////////////////
	// LINK TO THE BLOCK//
	//////////////////////
	private LearnBlock block;
     /////////////////////////////////////
	// THE MAT THAT SHOULD BE ELABORATED//
	/////////////////////////////////////
	Mat src;
     /////////////////////
	// WINDOW VARIABLES//
	////////////////////
	private MainWindowGUI mainWindow;
	protected Parent root;
	public Stage stage;
	//////////////////////////////////////
	//FUNCTION RELATED TO LISTS SETTINGS//
	/////////////////////////////////////
	private void setLists(){
		libViewed = FXCollections.observableArrayList("ImgProc", "Core");
		libraries.setItems(libViewed);
		algViewed = FXCollections.observableArrayList("Show Histogram",
				"Fourier Transform");
		algorithms.setItems(algViewed);

		ImgProc = FXCollections.observableArrayList("GaussianBlur",
				"MedianBlur", "Laplacian", "Erode", "Dilate", "Blur", "Scharr",
				"cvtColor", "Sobel", "Canny", "Adaptive Threshold");
		core = FXCollections.observableArrayList("AddWeighted", "AbsDiff");
		lib = FXCollections.observableArrayList(ImgProc, core);
		
	}
	/////////////////////////////
	//LISTNER RELATED TO LISTS//
	///////////////////////////
	@FXML
	private void setListner()
	{
		libraries.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				function.setItems(lib.get(libraries.getSelectionModel()
						.getSelectedIndex()));
				add.setDisable(false);

			}

		});

		algorithms.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				add.setDisable(true);
				function.setItems(null);
				blocksAlgorithm();

			}

		});

		function.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {

				switch (libraries.getSelectionModel()
						.getSelectedIndex()) {
				case 0:
					blocksImgproc();
					break;
				case 1:
					blocksCore();
					break;
				}

			}

		});
		
	}

	/////////////////////
	//CLASS CONSTRUCTOR//
	////////////////////
	public EditBlock(MainWindowGUI mainWin) {
         /////////////////////////////////
		//SET THE SCENE AND FXML LOADER//
	   /////////////////////////////////
		mainWindow = mainWin;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/" + this.getClass().getSimpleName() + ".fxml"));

		loader.setController(this);
		try {
			
			root = (Parent) loader.load();
			stage = new Stage();
			stage.setTitle("Edit Block");
			Scene scene = new Scene(root, 1024, 768);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();

			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent we) {
					mainWindow.blockStore = null;
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
         ////////////////////////////////////////////////////
		 //INITIALIZATION AND SETTINGS OF GRAPHIC ELEMENTS//
		///////////////////////////////////////////////////
		description.setEditable(false);
		blockCode.setEditable(false);
		save.disableProperty().set(true);
		elaborate.getStyleClass().add("greenButton");
		add.getStyleClass().add("blackButton");
		
		//loading default test image
		File file = new File( getClass().getResource("img/lenna.png").getPath() );
		String path=file.getAbsolutePath().toString();
		src = Highgui.imread( path );
		source.setImage(LearnBlock.Mat2Image(src));
		
		source.fitWidthProperty().bind(
				((Pane) source.getParent()).widthProperty());
		source.fitHeightProperty().bind(
				((Pane) source.getParent()).heightProperty());

		processed.fitWidthProperty().bind(
				((Pane) processed.getParent()).widthProperty());
		processed.fitHeightProperty().bind(
				((Pane) processed.getParent()).heightProperty());

		setLists();

		////////////////////////////
		//SSETTING LISTNER//
		////////////////////////////
		setListner();
         ////////////////////////////////////////////////////////////////////
		// POPULATE THE WINDOW BY SELECTING THE FIRST FUNCTION OF THE FIRST//
		// LIBRARY///////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////
		libraries.getSelectionModel().select(0);
		function.getSelectionModel().select(0);

	}
     ////////////////////////////////////////////////
	// LOADS THE IMAGEPROC LIBRARY FUNCTION BLOCKS//
	///////////////////////////////////////////////
	@FXML
	private void blocksImgproc() {

		switch (function.getSelectionModel().getSelectedIndex()) {
		case 0:
			block = new GaussianBlur();
			break;
		case 1:
			block = new MedianBlur();
			break;
		case 2:
			block = new Laplacian();
			break;
		case 3:
			block = new Erode();
			break;
		case 4:
			block = new Dilate();
			break;
		case 5:
			block = new Blur();
			break;
		case 6:
			block = new Scharr();
			break;
		case 7:
			block = new CvtColor();
			break;
		case 8:
			block = new Sobel();
			break;
		case 9:
			block = new Canny();
			break;
		case 10:
			block = new AdaptiveThreshold();
			break;

		}

		setGUI();
		
	}
     ///////////////////////////////////////////
	// LOADS THE CORE LIBRARY FUNCTION BLOCKS//
	//////////////////////////////////////////
	@FXML
	private void blocksCore() {

		switch (function.getSelectionModel().getSelectedIndex()) {
		case 0:
			block = new AddWeighted();
			break;
		case 1:
			block = new AbsDiff();
			break;
		}

		setGUI();
	}
     //////////////////////////////
	// LOADS THE ALGORITHM BLOCKS//
	//////////////////////////////
	private void blocksAlgorithm() {
		switch (algorithms.getSelectionModel().getSelectedIndex()) {
		case 0:
			block = new ShowHistogram();
			break;
		case 1:
			block = new FourierTransform();
			break;

		}
		
		setGUI();

	}
     //////////////////////////////////////////
	// SHOW THE EFFECT OF THE SELECTED BLOCKS//
	//////////////////////////////////////////
	@FXML
	private void preview() {
		// HOW TO USE A LEARNBLOCK
		// You can verify a block before processing it
		// 1 - setInputMat() + check() --> returns FALSE if some error occurred,
		// TRUE otherwise
		// 2 - check(input) --> returns FALSE if some error occurred, TRUE
		// otherwise
		// or you can process it directly (check() is always called internally!)
		// 1 - setInputMat() + process() --> returns NULL if some error occurred
		// 2 - process(input) --> returns NULL if some error occurred
		// In either cases, you can call getError() to get a description of last
		// error occurred!!
		// Use it the way you want!!!!

		Mat mattoShow = block.process(src);
		if (mattoShow != null && block.check(src) == true) {
			final ImageView frameView = (ImageView) root.lookup("#processed");
			Image imagetoShow = block.getOutputImage();
			frameView.setImage(imagetoShow);
			showError.setText("OK!");
		} else {
			showError.setText(block.getError());
		}
	}
     ////////////////////////////
	// ADD THE REQUESTED BLOCK//
	///////////////////////////
	@FXML
	private void addBlock() {
		if (block != null && block.check(src) == true) {
			mainWindow.pushBlock(block);
			int i;
			i = function.getSelectionModel().getSelectedIndex();
			function.getSelectionModel().clearSelection();
			function.getSelectionModel().select(i);
		} else {
			showError.setText("Cannot add block: " + block.getError());
		}
	}
     /////////////////////////////
	// SAVE THE PROCESSED IMAGE//
	////////////////////////////
	@FXML
	private void saveImg() {
		try {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Save Picture");
			chooser.getExtensionFilters().add(
					new FileChooser.ExtensionFilter("JPG", "*.jpg"));
			File file = chooser.showSaveDialog(stage);

			Highgui.imwrite(file.getAbsolutePath() + ".jpg", block.process(src));
		} catch (Exception e) {
			System.out.println("Can't save image");
		}
	}
     /////////////////////////////
	// LOAD THE IMAGE TO PROCESS//
	/////////////////////////////
	@FXML
	private void openImg() {
		try {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Open Picture");
			chooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("All Images", "*.*"),
					new FileChooser.ExtensionFilter("JPG", "*.jpg"),
					new FileChooser.ExtensionFilter("PNG", "*.png"),
					new FileChooser.ExtensionFilter("BMP", "*.bmp"),
					new FileChooser.ExtensionFilter("gif", "*.gif"),
					new FileChooser.ExtensionFilter("TIFF", "*.tiff"));
			File file = chooser.showOpenDialog(stage);
			src = Highgui.imread(file.getAbsolutePath());
			ImageView frameView = (ImageView) root.lookup("#source");
			Image imagetoShow = LearnBlock.Mat2Image(src);
			frameView.setImage(imagetoShow);
		} catch (Exception e) {
		}
	}

     //////////////////////
	//SHOW THE BLOCK GUI//
	/////////////////////
	@FXML
	private void setGUI(){
		control.getChildren().clear();
		control.getChildren().add(block.getGui().getRoot());
		blockCode.clear();
		description.setText(block.getDescription());
		blockCode.setText("Perform an execution to visualize code.");
		block.getGui().getRoot().getStyleClass().add("back");

		block.getExecutingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if (newValue == false)
					blockCode.setText(block.getCode());
					save.disableProperty().set(false);
			}

		});

	}
	/////////////////////////////////////////
	// Set the text of the description Area//
	////////////////////////////////////////
	protected void setDescription() {
		description.setText(block.getDescription());

	}

}
