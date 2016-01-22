package it.polito.elite.teaching.learnOpenCV;
	
import it.polito.elite.teaching.learnOpenCVBlocks.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
//import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

/**
 * Main interface for using a pipeline of blocks, render code, display errors and results.
 * See documentation for details.
 * <p>
 * @author	Dario Facchini <io.dariofacchini @ gmail.com>
 * @since	2014-07-01
 */
public class MainWindowGUI {
	
	//Program name (to be decided)
	private String programName = "Learn OpenCV";
	
	//List of internal variables
	private Stage thisWindow;
	private ObservableList<LearnBlock> blocksPipe;
    private Mat matSrc;
    private Mat matDst;
    //Last calculated "Images" (for JavaFX performance purposes only)
    private Image imgSrc;
    private Image imgDst;
    private Image blockImgIn;
    private Image blockImgOut;
    
	//Keep state of Tab content (to optimize loadings and management)
	//private ObservableList<Node> blockControlLabels;
	//private ObservableList<Node> blockControls;

	//Windows controllers (each is opened when needed, only one instance!)
	public EditBlock blockStore = null;
	protected Stage compareViewWindow = null;
	protected Stage graphViewWindow = null;
	
	//Code generator variables
	private final String codeTemplatePath = "cod/template.txt";
	private String codeSymbolicTemplate;
	private String codeRenderedTemplate;
	//Code generation personalizable constants
	private final String codeInputVarName = "nextInput";
	private final String codeOutputVarName = "nextOutput";
	
    
	//Linked FXML panels and controls in window
    @FXML private SplitPane horizontalSplit;
    	//bottompane
		@FXML private TextArea logArea;
		//toppane
    	@FXML private SplitPane verticalSplit;
    		//leftpane
    		@FXML protected GridPane imgViewGrid;
				@FXML protected ImageView imgViewSrc;
					@FXML private Button imgLoadButton;
					@FXML private Button globalCompareButton;
					@FXML private Button imgSaveButton;
				@FXML protected ImageView imgViewDst;
			//centerpane
			@FXML private ListView<LearnBlock> blocksPipeView;
				@FXML private Button executePipe;
				@FXML private Button addBlock;
				@FXML private Button deleteBlock;
				@FXML private Button moveUpBlock;
				@FXML private Button moveDownBlock;
			//rightpane
			@FXML private TabPane blockOptions;
				//infotab
					@FXML private Label blockDescription;
				//controlstab
				@FXML private Tab controlsTab;
					@FXML private ScrollPane blockControlsView;
						@FXML private VBox blockControlsCollection;
							@FXML private GridPane blockControlsGrid;
				//detailstab
				@FXML private Tab detailsTab;
					@FXML private GridPane blockDetailsView;
						@FXML private ImageView blockImgViewIn;
						@FXML private Button localCompareButton;
						@FXML private ImageView blockImgViewOut;
				//codetab
					@FXML private TextArea blockCodeView;


	
	
	
	////////////////////////////////////
	// STARTUP
	////////////////////////////////////
	/**
	 * Main Contructor: opens up a window with main application in it
	 * <p>
	 * Constructor does these things:
	 * -	WINDOW SETTINGS: if EditBlock window is opened, it will be closed toghether with MainWindowGUI
	 * -	LOAD STYLE: load fxml and show graphics
	 * -	IMAGE VIEWS: set up some bindings to resize image previews accordingly with window structure
	 * -	BLOCKS PIPE VIEW: set up empty LearnBlock list and view, on which LearnBlockGUI factory is applied
	 * -	BLOCK DETAILS VIEW: set up handlers for load Tabs content dynamically with block selection in the list
	 * 
	 * @param primaryStage Stage in which show main application window
	 */
	public MainWindowGUI(Stage primaryStage)
	{
		try
		{
			//////////////////
			//WINDOW SETTINGS
			thisWindow = primaryStage;		    
		    //Set onClose() handler for blockStore window
		    thisWindow.setOnCloseRequest(new EventHandler<WindowEvent>(){
	            @Override
				public void handle(WindowEvent we) {
	            	if(blockStore != null)
	            	{
	            		blockStore.stage.close();
	            	}
	            	if(compareViewWindow != null)
	            	{
	            		compareViewWindow.close();
	            	}	
	            }
	        });
			Blur block = new Blur();
			
			/////////////
			//LOAD STYLE
	    	//Load FXML structure for this window and set this class as controller
	    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/" + this.getClass().getSimpleName() + ".fxml"));
	    	fxmlLoader.setController(this);
	    	try {
	 	        Parent parent = (Parent) fxmlLoader.load();
	 	        Scene scn = new Scene(parent,1024.0,768.0);
	 	        scn.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		    	thisWindow.setScene( scn );
		    	thisWindow.setTitle("~ "+programName+" ~");	
	 	    } catch (IOException e) {
	 	        throw new RuntimeException(e);
	 	    }
	    	

	    	
			//SHOW WINDOW
		    thisWindow.show();
		    

		    
	    	/////////////
	    	//IMAGE VIEWS
	    	//Do some bindings for image previews dynamic resizing
	    	imgViewSrc.fitWidthProperty().bind( ((Pane)imgViewSrc.getParent()).widthProperty() );
	    	imgViewSrc.fitHeightProperty().bind( ((Pane)imgViewSrc.getParent()).heightProperty() );
	    	imgViewDst.fitWidthProperty().bind( ((Pane)imgViewDst.getParent()).widthProperty() );
	    	imgViewDst.fitHeightProperty().bind( ((Pane)imgViewDst.getParent()).heightProperty() );
	    	
	    	
	    	
	    	/////////////
	    	//BLOCKS PIPE VIEW
	    	//Allocate an empty list of LearnBlocks
	    	blocksPipe = FXCollections.observableArrayList();
	    	//Link blocksView to blocks (to show in window changes dynamically)
	    	blocksPipeView.setItems( blocksPipe );
	    	//..from this moment, any add/delete on blocksPipe will be displayed automatically within blocksPipeView!
	    	
	    	//Set ListView to use a dedicated GUI designer (aka "factory" controller) for elements of type ListCell<LearnBlock>, named LearnBlockGUI
	    	final MainWindowGUI mainController = this;
	    	blocksPipeView.setCellFactory(
	    		new Callback< ListView<LearnBlock>, ListCell<LearnBlock> >()
	    		{
	    			@Override 
	    			public ListCell<LearnBlock> call(ListView<LearnBlock> list) {
	    				return new LearnBlockGUI(mainController, blocksPipe);
	    			}
	    		}
	    	);
	    	
	    	
	    	
	    	
	    	/////////////////////
	    	//BLOCK OPTIONS VIEW 
	    	//Initialize list of controls (nodes for the selected Block) to be loaded in blockControlsView upon selection

	    	//Do some bindings for image previews (inside details tab!)
	    	blockImgViewIn.fitWidthProperty().bind( ((Pane)blockImgViewIn.getParent()).widthProperty() );
	    	blockImgViewIn.fitHeightProperty().bind( ((Pane)blockImgViewIn.getParent()).heightProperty() );
	    	blockImgViewOut.fitWidthProperty().bind( ((Pane)blockImgViewOut.getParent()).widthProperty() );
	    	blockImgViewOut.fitHeightProperty().bind( ((Pane)blockImgViewOut.getParent()).heightProperty() );
	    	//blockDetailsView.getColumnConstraints().get(0).prefWidthProperty().bind(verticalSplit.getDividers().get(1).positionProperty());
	    	//SplitPane.setResizableWithParent(imgGrid, Boolean.FALSE);

	    	
	    	//Perform actions when selection on the Blocks list changes (it updates the single Tab selected)
	    	// THE ONLY HANDLER RESPONSIBLE FOR REFRESHING blockOptions, 
	    	blocksPipeView.getSelectionModel().selectedItemProperty()
	    	.addListener(new ChangeListener<LearnBlock>()
	    	{
	    		boolean parsingControlsWasOK = false;

	    		@Override
	    		public void changed(ObservableValue<? extends LearnBlock> observable, LearnBlock oldValue, final LearnBlock newValue)
	    		{
	    			if( oldValue != null )		//there was a selected block before
	    			{
	    				/////////////
	    				// CONTROLS
	    				//If components were fetched correctly before
	    				if( parsingControlsWasOK )
	    				{
		    				//Move old selected Block's controls back to Block's root
	    					unloadControls(oldValue);
	    				}
	    				
	    			}

	    			
	    			//blockControlsCollection.getChildren().clear();		//this command REMOVES all the nodes from blockControls list!!

	    			
	    			if( newValue != null )
	    			{
	    				//TABS ENABLE
	    				controlsTab.disableProperty().set(false);
	    				detailsTab.disableProperty().set(false);
	    				
	    				////////////
	    				// CONTROLS
		    			parsingControlsWasOK = loadControls(newValue);
		    			if(!parsingControlsWasOK) logError("Controls or description could not be parsed correctly for selected block");

						////////////
	    				// DETAILS
		    			//ask for Images only when that tab is selected?? (less lag)
		    			//if(	blockOptions.getSelectionModel().getSelectedItem().getId().equalsIgnoreCase("details") )
		    			loadDetails(newValue);
		    			
	    			}
	    			else					//nope, new selection is empty!
	    			{
	    				//TABS DISABLE
	    				controlsTab.disableProperty().set(true);
	    				detailsTab.disableProperty().set(true);
	    				
	    				//Restore the "no block selected" message to Controls Tab
	    				//blockControlsCollection.getChildren().add( blockDescription );
	    				blockDescription.setText("No block selected.");
	    				//Reset details view
	    				unloadDetails();
	    				
	    			}

	    		}
	        });
	        		    
		    //DEBUG: Manually populate LearnBlockPipe
			//blocksPipe.add(new Stub());
			//blocksPipe.add(new Stub());
			//blocksPipe.add(new Overlay());

	    	
	    	
		
			
			//OTHER OPERATIONS HERE ON MAIN...
	    	//Welcome message
	    	logArea.setText("Welcome to "+programName+"! Credits to Bartolotta Marco and Facchini Dario from PoliTo\n");
			


		    

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	
	
	

	//////////////////////////
	// METHODS LINKED TO FXML WINDOW CONTROLS
	//////////////////////////
    /**
     * Fired by Load Image button
     * 
     * @param event Event Not really used
     */
    @FXML
    private void loadImage(ActionEvent event)
    {
    	try
    	{
			FileChooser chooser = new FileChooser();
			chooser.setTitle("View Pictures");
			chooser.getExtensionFilters().addAll(
		                new FileChooser.ExtensionFilter("All Images", "*.*"),
		                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
		                new FileChooser.ExtensionFilter("PNG", "*.png"),
		                new FileChooser.ExtensionFilter("BMP", "*.bmp")
		                //new FileChooser.ExtensionFilter("gif", "*.gif") 
		            );
			File file=chooser.showOpenDialog(thisWindow);
			String path=file.getAbsolutePath().toString();
			matSrc = Highgui.imread(path);
			imgSrc = LearnBlock.Mat2Image(matSrc);
			imgViewSrc.setImage( imgSrc );
			//System.out.println(path);
			if(matSrc!=null)
			{
				logInfo("image loaded! Ready for execution.");
				executePipe.disableProperty().set(false);
			}
			else
			{
				logError("image could not be loaded!");
				imgSaveButton.disableProperty().set(true);
			}
    	}
    	catch (Exception e)
    	{
    		
    	}
    	
    }
	
    /**
     * Fired by Save Image button
     * 
     * @param event Event Not really used
     */
    @FXML
    private void saveImage(ActionEvent event)
    {
		try
		{
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Save Picture");
			chooser.getExtensionFilters().add
			(
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
            );
			File file=chooser.showSaveDialog(thisWindow);
			
			Highgui.imwrite(file.getAbsolutePath()+".jpg", matDst);
		}
		catch(Exception e)
		{
			System.out.println("Can't save image");
			logError("Can't save image");
		}
    }
    
    /**
     * Fired by Compare button on main view
     * 
     * @param event Event Not really used
     */
    @FXML
    private void compareFinalResult(ActionEvent event)
    {
    	openCompareView(imgSrc, imgDst);
    }
    
    /**
     * Fired by Compare button on details tab
     * 
     * @param event Event Not really used
     */
    @FXML
    private void compareLocalResult(ActionEvent event)
    {
    	openCompareView(blockImgIn, blockImgOut);
    }
    
    
    
    /////////////////////////////////
    // LEARNBLOCKPIPE METHODS
    /////////////////////////////////

	
    //Single block operations
    
    /**
     * Simply instanciates an EditBlock which opens a new window. Called by Add Block button
     */
    @FXML
    private void addBlock()
    {
    	if(blockStore==null) blockStore = new EditBlock(this);
    }
    
    /**
     * Call this (it is called by EditBlock) to add a new LearnBlock from outside MainWindowGUI
     * 
     * @param newBlock The LearnBlock instance to be inserted at the bottom of the list
     */
    public void pushBlock(LearnBlock newBlock)
    {
    	if(newBlock!=null)
    	{
    		blocksPipe.add(newBlock);
    		newBlock.reset();
    		if(!newBlock.checkLoading()) logWarning("Block "+newBlock.getName()+ " reports: "+newBlock.getError());
    		else logInfo("Block "+newBlock.getName()+" successfully loaded and fully operational");
    	}
    }

    /**
     * Moves UP in the list the currently selected block
     */
    @FXML
    public void moveUpBlock()
    {
    	int selected_index = blocksPipeView.getSelectionModel().getSelectedIndex();
    	if(selected_index>0)
    	{
	    	moveBlock(selected_index, selected_index-1);
	    	//Keeps the selection up with the moved element
	    	blocksPipeView.getSelectionModel().clearAndSelect(selected_index-1);
    	}
    }
    
    /**
     * Moves DOWN in the list the currently selected block
     */
    @FXML
    public void moveDownBlock()
    {
    	int selected_index = blocksPipeView.getSelectionModel().getSelectedIndex();
    	moveBlock(selected_index, selected_index+1);
    	//Keeps the selection up with the moved element
    	blocksPipeView.getSelectionModel().clearAndSelect(selected_index+1);
    }
       
    /**
     * Move blocks in the list. When indexes are out of bound calling this function does nothing
     * @param from_position 0-n
     * @param to_position 0-n
     */
    public void moveBlock(int from_position, int to_position)
    {
    	if(from_position != -1 && to_position >= 0 && to_position < blocksPipe.size())
	    {
    		//Takes at index-1 the selected element, while setting at index the element previously at index-1
    		blocksPipe.set(from_position, blocksPipe.set(to_position, blocksPipe.get(from_position)) );
    	}    	
    }
    
    /**
     * Destroys block currently at index
     * 
     * @param index 0-n
     */
    public void removeBlock(int index)
    {
    	blocksPipe.remove(index);
    }
    
    /**
     * Destroys all blocks
     */
    @FXML
    public void clearPipe()
    {
    	blocksPipe.clear();
    	logInfo("Pipeline cleared!");
    }

    
    //Pipe operations
    
    /**
     * Run a validation of currently setup pipe, returns boolean.
     * False means execution will surely raise error.
     * True does not necessary mean execution will be successful.
     * Read documentation for further details.
     * 
     * @return Result of validation
     */
    @FXML
    private boolean validatePipe()
    {
    	boolean validation = true;
    	if(matSrc!=null)
    	{
    		Mat currentBlockInput = null;
	    	int blockNum = 0;		//this keeps track of the current block under validation
	    	for (LearnBlock block : blocksPipe)
	    	{
	    		//IF it is the first block, perform the check with matSrc
	    		//ELSE IF previous block is not active, use last currentBlockInput
	    		//ELSE try to perform a check with the last output of previous block
	    		if(blockNum==0) currentBlockInput = matSrc;
	    		else if(!blocksPipe.get(blockNum-1).getActiveProperty().get())
	    		{
	    			//DO NOTHING
	    		}
	    		else currentBlockInput = blocksPipe.get(blockNum-1).getOutputMat();
	    		
	    		//Increment for error report (from 1 to N)
	    		++blockNum;
	    		
	    		//Perform check for this block
	    		boolean currentBlockStatus = block.check(currentBlockInput);	//local result is considered for output
	    		validation = validation && currentBlockStatus;					//global result will be returned
	    		
	    		//consider errors only on active blocks
	    		if(!block.getActiveProperty().get()) logArea.appendText("Block " + blockNum + " is not active\n");
	    		else
	    		{
		    		if(currentBlockStatus == false)	//some error occurred, so print it
		    		{
		    			logArea.appendText("Block " + blockNum + " detected an error: " + block.getError() + "\n");
		    		}
		    		else							//no error, print OK status
		    		{
		    			logArea.appendText("Block " + blockNum + " is ready to process\n");
		    		}
	    		}
	    	}
	
	    	if(validation) logInfo("Pipeline seems ready to Execute");
	    	else logInfo("Pipeline will generate errors on next Execute (or maybe some blocks received no input yet)");
    	}
    	else
    	{
    		logInfo("Cannot check pipeline without a source image");
    		validation=false;
    	}
    	return validation;
    	
    }
    
    /**
     * Executes current setup pipe. Outputs final image in destination view.
     * See documentation for behaviour details.
     */
    @FXML
    private void executePipe()
    {
    	//Begin execution
    	logArea.appendText("Executing pipeline...\n");
    	
    	//Clear Destination Matrix
    	matDst = null;
    	
    	if(matSrc!=null)
    	{
    		//compute result of pipeline
	    	Mat result = matSrc;
	    	int blockNum = 0;		//this keeps track of the current block under operation
	    	Iterator<LearnBlock> i = blocksPipe.iterator();
	    	while (i.hasNext())
	    	{
	    		LearnBlock block = i.next();
	    		++blockNum;
	    		
	    		//PROCESS THIS BLOCK
	    		result = block.process(result);
	    		
	    		//consider errors only on active blocks
	    		if(!block.getActiveProperty().get()) logArea.appendText("Block " + blockNum + " bypassed\n");
	    		else
	    		{
		    		if(result==null)	//some error occurred, so print it
		    		{
		    			logArea.appendText("Block " + blockNum + " encountered error: " + block.getError() + "\n");
		    		}
		    		else				//no error, print OK status
		    		{
		    			logArea.appendText("Block " + blockNum + " process complete\n");
		    		}
	    		}
	    		
	    	}
	    	
	    	//if result is not empty, all pipe has executed with no errors: display it and render new code
	    	if(result!=null)
	    	{
		    	matDst = result;
	    		imgDst = LearnBlock.Mat2Image(matDst);
	    		imgViewDst.setImage(imgDst);
		    	logArea.appendText("Execution successfully complete! All " + blockNum + " blocks executed.\n");
		    	imgSaveButton.disableProperty().set(false);
		    	globalCompareButton.disableProperty().set(false);
		    	loadCode();
	    	}
	    	//else ERASE code (it is available only if elaboration went all fine!) and imageview
	    	else
	    	{
	    		logArea.appendText("Execution not successful.\n");
				imgSaveButton.disableProperty().set(true);
				globalCompareButton.disableProperty().set(true);
				imgViewDst.setImage(null);
	    		unloadCode();
	    	}

	    	
	    	//refresh blockDetails (only if a block is currently selected)
			if(blocksPipeView.getSelectionModel().getSelectedItem() != null)
				loadDetails(blocksPipeView.getSelectionModel().getSelectedItem());
				
	    	
    	}
    	else
    	{
    		logError("Source image not loaded!");
    	}
    	
    	
    }

    /**
     * Clears log area
     */
    @FXML
    private void clearLog()
    {
    	logArea.clear();
    }
    
    
    
    
    /////////////////////////////////
    // PROGRAM HIDDEN MANAGEMENT
    /////////////////////////////////
    
    /**
     * Log a custom error. Formats it automatically.
     * 
     * @param error
     */
    private void logError(String error)
    {
    	logArea.appendText("Error: " + error + "\n");
    }
    
    /**
     * Log a custom warning. Formats it automatically.
     * 
     * @param error
     */
    private void logWarning(String error)
    {
    	logArea.appendText("Warning: " + error + "\n");
    }
    
    /**
     * Log a custom info or tip. Formats it automatically.
     * 
     * @param error
     */
    private void logInfo(String error)
    {
    	logArea.appendText("Info: " + error + "\n");
    }


	/**
	 * Call this to load Controls for the newly selected Block in the "Controls" tab
	 * Returns true only if block has all elements and we could correctly load them
	 * 
	 * @param newBlock
	 * @return result
	 */
	private boolean loadControls(final LearnBlock newBlock)
	{
		/* OLD CODE - CONTROLS WERE MANAGED BY MAINWINDOWGUI INITIALLY
		Label description = new Label();
		description.setAlignment(Pos.TOP_LEFT);
		description.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		description.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		GridPane controlsGrid = new GridPane();
		description.setAlignment(Pos.TOP_RIGHT);
		description.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		description.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		
		//boolean descriptionTaken = false;
		
		//Fetching components
		for (Node control : newBlock.getElements().getChildrenUnmodifiable())
		{
			//some restyling and then add in a gridpane
			if ( !descriptionTaken && control instanceof Label && control.getId() !=null && control.getId().equalsIgnoreCase("description"))
			{
				description.setText( ((Label)control).getText() );
				descriptionTaken = true;
			}
			else if( control instanceof HBox )
    		{
    			//((HBox)control).setAlignment(Pos.CENTER);
    			//((HBox)control).setPrefWidth(Double.MAX_VALUE);
    			//((HBox)control).setPrefHeight(Control.USE_COMPUTED_SIZE);
    			//((HBox)control).setPadding(new Insets(2));
    			//((HBox)control).setSpacing(4);
    			boolean labelTaken = false;
    			boolean controlTaken = false;
    			for ( Node element : ((HBox)control).getChildrenUnmodifiable())
    			{
    				((Control)element).setPrefSize(Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE);
    				if( element instanceof Label && !labelTaken)
    				{
    					blockControlLabels.add(element);
    					labelTaken=true;
    				}
    				else if ( element instanceof Control && !controlTaken)
    				{
    					element.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			            @Override
    			            public void handle(MouseEvent event) {
    			            	newBlock.notifyModification();
    			            }
    					});
    					element.setOnInputMethodTextChanged(new EventHandler<InputEvent>() {
    			            @Override
    			            public void handle(InputEvent event) {
    			            	System.out.println("check performed");
    			            	newBlock.check();
    			            }
    					});
    					blockControls.add(element);
    					controlTaken=true;
    				}
    			}
    			
    		}

    	}
    	*/

		
		if( !newBlock.getDescription().isEmpty() && !newBlock.getGui().getControls().isEmpty() )
		{
			//Add Description Label to GUI
			blockDescription.setText(newBlock.getDescription());
			//Add Grid to GUI
			int r=0;
			for( Parent component : newBlock.getGui().getControls() )
			{
				blockControlsGrid.add(new Label(""+ (r+1) ), 0, r);
				blockControlsGrid.add(component, 1, r);
				for( Node control : component.getChildrenUnmodifiable() )
				{
					if(control instanceof Control)
					{
						control.setOnKeyPressed(new EventHandler<KeyEvent>() {
				            @Override
				            public void handle(KeyEvent event) {
				            	if(event.getCode() != KeyCode.TAB)
				            		newBlock.notifyModification();
				            }
						});
						control.setOnMouseClicked(new EventHandler<MouseEvent>() {
				            @Override
				            public void handle(MouseEvent event) {
				            	newBlock.notifyModification();
				            }
						});
					}
				}
				
				r++;
			}
			
			return true;
		}
		else
		{
			return false;
		}

	}
	

	/**
	 * Method to restore controls to its original container
	 * Since controls are generic physical GUI nodes, it is necessary to store them back (it's not possible to clone them!)
	 * WARNING: ALWAYS CALL THIS BEFORE LOADING CONTROLS AGAIN: otherwise controls gui nodes of the previous Block will be lost!
	 * 
	 * @param oldBlock
	 */
	private void unloadControls(final LearnBlock oldBlock)
	{
		
		/* OLD CODE - store them back into the Block they were taken from - NOW DONE BY LEARNBLOCK ITSELF WITH restoreControls()
		int i=0;
		oldBlock.getGui().getRoot().getChildrenUnmodifiable().add(0, blockControlsCollection.getChildren().get(0));
		for (Node control : oldBlock.getGui().getRoot().getChildrenUnmodifiable())
		{
			if( control instanceof HBox )
			{
				((HBox)control).getChildren().add(oldBlock.getGui().getControlLabels().get(i));
				((HBox)control).getChildren().add(oldBlock.getGui().getControls().get(i));
				i++;
			}
			//(oldValue.getElements()).getChildren().addAll(blockNodes);			//this command DOES NOT REMOVE the nodes from blockControls
																					//list and ADDS them BACK to the Block (they belong to)
		}
		*/


		oldBlock.getGui().restoreControls();
		blockControlsGrid.getChildren().clear();
		blockDescription.setText("");
	}
	

	/**
	 * Call this to load Details for the newly selected Block in the "Details" tab
	 * @param newBlock
	 */
	private void loadDetails(final LearnBlock newBlock)
	{
		if(newBlock.getOutputMat()!=null) localCompareButton.disableProperty().set(false);
		else localCompareButton.disableProperty().set(true);
		blockImgIn = newBlock.getInputImage();
		blockImgOut = newBlock.getOutputImage();
		blockImgViewIn.setImage(blockImgIn);
		blockImgViewOut.setImage(blockImgOut);	
	}

	/**
	 * Safe method to erase details when a block is deselected
	 */
	private void unloadDetails()
	{
		//Set empty images in Details Tab
		blockImgViewIn.setImage(null);
		blockImgViewOut.setImage(null);
	}
	

	/**
	 * Call this to load Code: this will refresh Code View generating new code. Please minimize calls to only when sure it's needed
	 */
	private void loadCode()
	{
		
		//Load code template in string
		Scanner scanner = null;
		codeSymbolicTemplate="";
		try {
			//System.out.println(getClass().getResource(codeTemplatePath).getPath());
			File file=new File( getClass().getResource(codeTemplatePath).getPath() );
			scanner = new Scanner(file);
			while (scanner.hasNextLine())
			{
				codeSymbolicTemplate += scanner.nextLine()+"\n";
			}
		} catch (Exception e) {
			// Auto-generated catch block
			logError("Template.txt not found or loading error!");
			e.printStackTrace();
		}
		finally
		{
			if (scanner != null) scanner.close();
		}
		

		
		//Generate functions code and calls code
		List<String> functionRenderedNames = new ArrayList<String>();
		String callsCode = new String("");
		String functionsCode = new String("");
		
    	for (LearnBlock block : blocksPipe)
    	{
    		if( block.getActiveProperty().get() )
    		{
    			//choosing a name for current function (can be more blocks of same type but different values!)
        		int suffix = 1;
        		String chosenFunctionName = block.getName() + suffix;
        		while( functionRenderedNames.contains(chosenFunctionName) )
        			chosenFunctionName = block.getName() + (++suffix);
        		functionRenderedNames.add(chosenFunctionName);
        		//populating code for all calls..
        		callsCode = callsCode.concat(""
        				+ "\n\t" + chosenFunctionName + "();"
        				+ "\n\t<!INPUT> = <!OUTPUT>;");
        		//populating code for all function implementations..
        		functionsCode = functionsCode.concat
        		(
        			"private void " + chosenFunctionName + "()\n"
        				+ "{\n"
        				+ block.getCode()
        				+ "\n}\n\n"
        		);
        		
        		//System.out.println(block.checkLoading() + block.getError());
        		
    		}

    		
    	}
    	
    	
    	//Render final code
    	//System.out.println(callsCode);
    	//System.out.println(functionsCode);
    	
		codeRenderedTemplate = new String(codeSymbolicTemplate); 	
		codeRenderedTemplate = codeRenderedTemplate.replaceAll( "<!\\s*(CALLS)\\s*>" , callsCode );
		codeRenderedTemplate = codeRenderedTemplate.replaceAll( "<!\\s*(FUNCTIONS)\\s*>" , functionsCode );
		codeRenderedTemplate = codeRenderedTemplate.replaceAll( "<!\\s*(INPUT)\\s*>" , codeInputVarName );
		codeRenderedTemplate = codeRenderedTemplate.replaceAll( "<!\\s*(OUTPUT)\\s*>" , codeOutputVarName );
		
    	
    	//Load it to Code View
    	blockCodeView.setText(codeRenderedTemplate);
    	
	}

	/**
	 * Call this to erase Code View: a message "Code unavailable" will be displayed (should be called when unsuccessful elaboration happens)
	 */
	private void unloadCode()
	{
		blockCodeView.setText("Code unavailable.\nPlease perform a successful execution first and a valid code will be rendered.");
	}

	//COMPARE VIEW FUNCTIONS

    /**
     * Opens up a CompareView window with the two images in input.
     * CompareView window is associated with MainWindow (infact it is not a static method) so opening a new one
     * will close the previous CompareView opened (if any)
     * 
     * @param leftImage
     * @param rightImage
     */
    public void openCompareView(Image leftImage, Image rightImage)
    {
    	if(compareViewWindow!=null)
    	{
    		compareViewWindow.close();
    	}
    	
    	BorderPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("fxml/CompareWindowGUI.fxml"));
            compareViewWindow = new Stage();
            compareViewWindow.setTitle("Compare View");
            compareViewWindow.setScene(new Scene(root, 1024, 768));
            compareViewWindow.show();
        	compareViewWindow.setOnCloseRequest(new EventHandler<WindowEvent>(){
                @Override
				public void handle(WindowEvent we) {
                	compareViewWindow = null;
                }
            });
            
        	//and hide this current window (if this is whant you want)
            //((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        GridPane compareViewGrid = (GridPane) root.getCenter();
        
        Group leftZoomContent = new Group( new ImageView(leftImage) );
        Group rightZoomContent = new Group( new ImageView(rightImage) );
        Parent leftZoomPane = createZoomPane( leftZoomContent );
        Parent rightZoomPane = createZoomPane( rightZoomContent );
 
        compareViewGrid.add( leftZoomPane , 0, 0);
        compareViewGrid.add( rightZoomPane , 1, 0);
        
        bindBidirectionalZoomPane(leftZoomPane,rightZoomPane, leftZoomContent, rightZoomContent);

    }
	private Parent createZoomPane(final Group group)
	{

	    final double SCALE_DELTA = 1.1;
	    final StackPane zoomPane = new StackPane();
	    zoomPane.getChildren().add(group);
	    final Group scrollContent = new Group(zoomPane);
	    final ScrollPane scroller = new ScrollPane();
	    scroller.setContent(scrollContent);
	    
		scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	    
		//SCROLLING via mousewheel
	    scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>()
	    {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue)
			{
				zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
			}
	    });

	    scroller.setPrefViewportWidth(256);
	    scroller.setPrefViewportHeight(256);

		zoomPane.setOnScroll(new EventHandler<ScrollEvent>()
		{
			@Override
			public void handle(ScrollEvent event) {
				event.consume();

				if (event.getDeltaY() == 0) {
					return;
				}

				double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA
						: 1 / SCALE_DELTA;

				// amount of scrolling in each direction in scrollContent
				// coordinate
				// units
				Point2D scrollOffset = figureScrollOffset(scrollContent,
						scroller);

				group.setScaleX(group.getScaleX() * scaleFactor);
				group.setScaleY(group.getScaleY() * scaleFactor);

				// move viewport so that old center remains in the center after
				// the
				// scaling
				repositionScroller(scrollContent, scroller, scaleFactor,
						scrollOffset);

			}
		});

		// PANNING via drag....
		final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
		scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
			}
		});
		scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event) {
				double deltaX = event.getX()
						- lastMouseCoordinates.get().getX();
				double extraWidth = scrollContent.getLayoutBounds().getWidth()
						- scroller.getViewportBounds().getWidth();
				double deltaH = deltaX
						* (scroller.getHmax() - scroller.getHmin())
						/ extraWidth;
				double desiredH = scroller.getHvalue() - deltaH;
				scroller.setHvalue(Math.max(0,
						Math.min(scroller.getHmax(), desiredH)));

				double deltaY = event.getY()
						- lastMouseCoordinates.get().getY();
				double extraHeight = scrollContent.getLayoutBounds()
						.getHeight() - scroller.getViewportBounds().getHeight();
				double deltaV = deltaY
						* (scroller.getHmax() - scroller.getHmin())
						/ extraHeight;
				double desiredV = scroller.getVvalue() - deltaV;
				scroller.setVvalue(Math.max(0,
						Math.min(scroller.getVmax(), desiredV)));
			}
		});

		return scroller;
	}
	private void bindBidirectionalZoomPane(Parent pane1, Parent pane2, Group zoom1, Group zoom2)
	{
		ScrollPane scroller1 = (ScrollPane) pane1;
		ScrollPane scroller2 = (ScrollPane) pane2;
		scroller1.hvalueProperty().bindBidirectional(scroller2.hvalueProperty());
		scroller1.vvalueProperty().bindBidirectional(scroller2.vvalueProperty());
		
		zoom1.scaleXProperty().bindBidirectional(zoom2.scaleXProperty());
		zoom1.scaleYProperty().bindBidirectional(zoom2.scaleYProperty());
		zoom1.scaleZProperty().bindBidirectional(zoom2.scaleZProperty());
	}	
	private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller)
	{
		double extraWidth = scrollContent.getLayoutBounds().getWidth()
				- scroller.getViewportBounds().getWidth();
		double hScrollProportion = (scroller.getHvalue() - scroller.getHmin())
				/ (scroller.getHmax() - scroller.getHmin());
		double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
		double extraHeight = scrollContent.getLayoutBounds().getHeight()
				- scroller.getViewportBounds().getHeight();
		double vScrollProportion = (scroller.getVvalue() - scroller.getVmin())
				/ (scroller.getVmax() - scroller.getVmin());
		double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
		return new Point2D(scrollXOffset, scrollYOffset);
	}
	private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset)
	{
	    double scrollXOffset = scrollOffset.getX();
	    double scrollYOffset = scrollOffset.getY();
	    double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
	    if (extraWidth > 0) 
	    {
	      double halfWidth = scroller.getViewportBounds().getWidth() / 2 ;
	      double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
	      scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
	    } else {
	      scroller.setHvalue(scroller.getHmin());
	    }
	    double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
	    if (extraHeight > 0)
	    {
	      double halfHeight = scroller.getViewportBounds().getHeight() / 2 ;
	      double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
	      scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
	    } else {
	      scroller.setHvalue(scroller.getHmin());
	    }
	}
	

}
