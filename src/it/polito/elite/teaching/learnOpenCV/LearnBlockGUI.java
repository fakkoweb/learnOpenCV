package it.polito.elite.teaching.learnOpenCV;

import it.polito.elite.teaching.learnOpenCVBlocks.LearnBlock;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * A specialization for the behavior of ListCell for the LearnBlock type
 * Standard method of visualizing a Cell of a List is overridden when items are of type LearnBlock
 * <p>
 * @author	Dario Facchini <io.dariofacchini @ gmail.com>
 * @since	2014-07-01
 */
public class LearnBlockGUI extends ListCell<LearnBlock> {

		private static MainWindowGUI mainController;
		private static boolean firstRun = true;
		
		//Timeline for animations and periodical events control
		private final static Timeline timeline = new Timeline();
		
		//FXML
		@FXML private CheckBox blockActiveFlag;
		@FXML private GridPane blockGUI;
			@FXML private Label blockNumber;
			@FXML private Circle checkLed;
			@FXML private Label blockName;
			@FXML private Button buttonF;
			@FXML private Button buttonH;
			//Specific GUI elements for this LearnBlock are then dynamically loaded here, as children of this HBox
			//@FXML private HBox blockSpecificGUI = new HBox();

		//ANIMATED LED COLOURS
		private static final RadialGradient ledOffColor = new RadialGradient
			(		
				0, 
                0, 
                -1, 
                -1, 
                10, 
                false, 
                CycleMethod.NO_CYCLE, 
                new Stop(0, Color.web("0x808080ff")), 
                new Stop(1, Color.BLACK)
			);
		private static final RadialGradient ledOnRedColor = new RadialGradient
				(		
					0, 
	                0, 
	                0, 
	                0, 
	                10, 
	                false, 
	                CycleMethod.NO_CYCLE, 
	                new Stop(0, Color.WHITESMOKE), 
	                new Stop(0.4, Color.RED),
	                new Stop(1, Color.BLACK)
				);
		private static final RadialGradient ledOnYellowColor = new RadialGradient
				(		
					0, 
	                0, 
	                0, 
	                0, 
	                10, 
	                false, 
	                CycleMethod.NO_CYCLE, 
	                new Stop(0, Color.WHITESMOKE), 
	                new Stop(0.25, Color.YELLOW),
	                new Stop(1, Color.BLACK)
				); 
		private static final RadialGradient ledOnGreenColor = new RadialGradient
				(		
					0, 
	                0, 
	                0, 
	                0, 
	                10, 
	                false, 
	                CycleMethod.NO_CYCLE, 
	                new Stop(0, Color.WHITESMOKE), 
	                new Stop(0.25, Color.LIGHTGREEN),
	                new Stop(1, Color.BLACK)
				); 

		//DRAG'n'DROP VARIABLES
		public static DataFormat dataFormat =  new DataFormat("mycell");
	    private static IntegerProperty ind = new SimpleIntegerProperty(-1);	//I keep track of which item is now currently being dragged
	    private static LearnBlock temp = null; 		//I can hold the dragged item Object for some time.
	    private static int toBeDeleted = -1; 	//When any cell is dragged then I'm being named which index to be deleted
	    
		
	    
	    
		LearnBlockGUI(final MainWindowGUI mainController, final ObservableList<LearnBlock> blocksList)
		{
			
			//////////////////////////////////
			// STATIC FIELDS INITIALIZATION
			if(firstRun)
			{
				timeline.setCycleCount(Animation.INDEFINITE);
				timeline.setAutoReverse(false);
				timeline.play();
				LearnBlockGUI.mainController = mainController;
			}
			firstRun=false;
			/////////////////////////////////
			
			
			/////////////////////////////////
			// DRAG'N'DROP EFFECT
			this.indexProperty().addListener( new ChangeListener<Number>()
	    	{
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{
					if(getIndex() < blocksList.size())
					{
						//If some kind of mice will click on your then do this
					    setOnMouseClicked(new EventHandler<MouseEvent>(){
					
					        @Override
					        public void handle(MouseEvent arg0) {
					            getListView().scrollTo(getIndex()); 
					        }   
					        
					    });
					    setOnMouseReleased(new EventHandler<MouseEvent>(){
					
					        @Override
					        public void handle(MouseEvent arg0) {
					        	
					        }   
					        
					    });
					    //Some body is dragging and they are on me.
					    setOnDragEntered(new EventHandler<DragEvent>(){
					        @Override
					        public void handle(DragEvent observable) {
					        	timeline.pause();	//for performance...
					            //System.out.println("Entered");
					            if(observable.getTransferMode() == TransferMode.MOVE){
					                Object o = observable.getDragboard().getContent(dataFormat);
					                if(toBeDeleted == getIndex()){
					                    return;
					                }
					                if(toBeDeleted != -1){
					                	mainController.removeBlock(toBeDeleted);
					                    toBeDeleted = -1;
					                }
					                if(o != null && temp != null ){
					                    if(getIndex() < blocksList.size())                                    
					                        blocksList.add(getIndex(),temp);
					                    else if(getIndex() == blocksList.size())
					                        blocksList.add(temp);
					                    
					                }
					                
					                ind.set(getIndex());
					            }
					        }
					
					    });
					    ind.addListener(new InvalidationListener(){
					
					        @Override
					        public void invalidated(Observable observable) {
					             if(getIndex() == ind.get()){
					                InnerShadow is = new InnerShadow();
					                is.setOffsetX(1.0);
					                is.setColor(Color.web("#666666"));
					                is.setOffsetY(1.0);                               
					                setEffect(is);
					            }else{
					                setEffect(null);
					            }   
					        }
					        
					    });
					    //Some body just went off dragging from my cell.
					    setOnDragExited(new EventHandler<DragEvent>(){
					
					        @Override
					        public void handle(DragEvent arg0) {
					            //System.out.println("Exited");
					            if(arg0.getTransferMode() == TransferMode.MOVE){
					            	timeline.play();	//resume any paused animation for performance
					                Object o = arg0.getDragboard().getContent(dataFormat);
					                if(o != null){
					                    setEffect(null);                          
					                    if(getIndex()<blocksList.size())
					                        toBeDeleted = getIndex();
					                      
					                }
					            }
					        }
					
					    });
					   
					    //OMG! That mice pressed me. I need to take some action
					    pressedProperty().addListener(new ChangeListener<Boolean>(){
					
					        @Override
					        public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					            InnerShadow is = new InnerShadow();
					                    is.setOffsetX(1.0);
					                    is.setColor(Color.web("#666666"));
					                    is.setOffsetY(1.0);
						                setEffect(is);					                    
					            if(arg2){
					               // //System.out.println("Pressed " + getIndex() + " "+items.size());
					             
					                
					            }
					            else
					                setEffect(null);    
					        }
					
					    });
					
					    //Ok I'm off I'm Over stop dragging me man!
					    setOnDragOver(new EventHandler<DragEvent>(){
					        @Override
					        public void handle(DragEvent event) {           
					            //System.out.println("Over");
					            event.acceptTransferModes(TransferMode.MOVE);                
					        }
					
					    });
					    
					    //Hey hey hey You are dragging me! Wait I need to call somebody
					    setOnDragDetected(new EventHandler<MouseEvent>(){
					            @Override
					            public void handle(MouseEvent event) {
					                //System.out.println("Detected");
					                Dragboard db = getListView().startDragAndDrop(TransferMode.MOVE);
					                temp = blocksList.get(getIndex());
					                toBeDeleted = getIndex();
					                Object item = blocksList.get(getIndex());
					                /* Put a string on a dragboard */
					                ClipboardContent content = new ClipboardContent();
					                if(item != null)
					                    content.put(dataFormat,item.toString());             
					                else
					                    content.put(dataFormat,"XData");             
					                db.setContent(content); 
					                event.consume();
					
					            }
					
					    });
					}
					
					
				}
	    	});
			/////////////////////////////////
			

		}
		
		private void loadFXML()
		{
			//Initiate a new fxml loader (it will convert fxml in objects)
			//UNFORTUNATELY this file must be loaded once for each Cell, EVEN IF it is the same file!!
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource( "fxml/" + this.getClass().getSimpleName() + ".fxml" ));
			fxmlLoader.setController(this);	
			//Load() structure and populate HBox from the loader
			try {
				fxmlLoader.load();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
		}
		
	    //Override the method of listCell for content update
		//item: the new item that will take the place of the current item in this cell
		//empty: if the new item is null or not
	    @Override
	    protected void updateItem(final LearnBlock item, boolean empty)
	    {
	    	// DEBINDING: before updating item delete old item's bidirectional binding with activeFlag
	    	if(getItem() != null) blockActiveFlag.selectedProperty().unbindBidirectional(getItem().getActiveProperty());
	    	
	    	
	        // calling super here is very important - don't skip this!
	        super.updateItem(item, empty);
	        

	        if (!empty)
	        {
				//Load this block's standard graphics (yes, every time..)
				loadFXML();
				
				//BINDING: bind again with activeFlag
	        	blockActiveFlag.selectedProperty().bindBidirectional(item.getActiveProperty());
	        	blockName.setText(item.getClass().getSimpleName());
	        	blockNumber.setText( ""+(this.getIndex()+1) );
				item.getExecutingProperty().addListener(new ChangeListener<Boolean>()
				{
					@Override
					public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue)
					{
						//when block has ended execution AND output is null
						if(newValue == false && item.getOutputMat()==null)
						{
			        		//System.out.println("disabilitare!");
			        		buttonF.disableProperty().set(true);
			        		buttonH.disableProperty().set(true);
						}
			        	else
			        	{
			        		//System.out.println("abilitare!");
			        		buttonF.disableProperty().set(false);
			        		buttonH.disableProperty().set(false);	        		
			        	}
					}
				});

	        	
	        	////////////////////////////////////
	        	// ANIMATION & PERIODICAL EVENTS
	        	//timeline.stop();
				//Load animations
				//add keyframe for led on
				timeline.getKeyFrames().add(
					new KeyFrame(Duration.millis(400), new EventHandler<ActionEvent>() {
					    @Override
					    public void handle(ActionEvent event)
					    {
					    	if(getItem()!=null)
					    	{
					    		if(!getItem().getActiveProperty().get())
					    		{
					    			checkLed.fillProperty().setValue(ledOffColor);
					    			blockName.setTextFill(Color.BLACK);
					    		}
						    	else if(!getItem().getValidProperty().get())
						    	{
						    		checkLed.fillProperty().setValue(ledOnRedColor);
						    		blockName.setTextFill(Color.DARKRED);
						    	}
						    	else if(!getItem().getReadyProperty().get())
						    	{
						    		checkLed.fillProperty().setValue(ledOnYellowColor);
						    		blockName.setTextFill(Color.DARKORANGE);
						    	}					    		
					    		else
					    		{
					    			checkLed.fillProperty().setValue(ledOnGreenColor);
					    			blockName.setTextFill(Color.DARKGREEN);
					    		}

					    	}
					    }
					})
				);
				//add keyframe for led off
				timeline.getKeyFrames().add(
					new KeyFrame(Duration.millis(800), new EventHandler<ActionEvent>() {
					    @Override
					    public void handle(ActionEvent event)
					    {
					    	if(getItem()!=null && getItem().getModifiedProperty().get())
					    	{
						    	checkLed.fillProperty().setValue(ledOffColor);
					    	}
					    }
					})
				);
				timeline.play();
				//////////////////////////
				
	        	
	            //setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	            this.setGraphic( blockGUI );
	            //N.B.: THIS command is equivalent to:
	            //"this.setGraphic( this.item.getItem().getElements() );"
	            //since, after calling super, item is the new item in this cell!
	            
	        }
	        else
	        {
	        	//if(getItem() != null) blockActiveFlag.selectedProperty().unbindBidirectional(old_item.getActiveProperty());
	        }
	         
	        
	    }

	    
	    
	    //HIDDEN METHODS
	    
	    private void showInPopup(final Image toshow, final String title)
	    {
	    	ImageView view = new ImageView();;

			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);

			Pane dialogVbox = new Pane();

			view.setImage(toshow);
			dialogVbox.getChildren().add(view);

			Scene dialogScene = new Scene(dialogVbox, toshow.getWidth(), toshow.getHeight());
			view.fitWidthProperty().bind(
					((Pane) view.getParent()).widthProperty());
			view.fitHeightProperty().bind(
					((Pane) view.getParent()).heightProperty());
			dialog.setScene(dialogScene);
			dialog.setTitle(title);

			dialog.showAndWait();
	    }
	    
	    
	    //METHODS FOR GRAPHIC BUTTONS IN THE CELL	    
	    @FXML
	    protected void deleteBlock()
	    {
	    	mainController.removeBlock( this.getIndex() );
	    	//blocksPipe.remove( getItem() );
	    }
        
	    @FXML
	    protected void moveUpBlock()
	    {
	    	mainController.moveBlock(this.getIndex(), this.getIndex()-1);
	    }
	    
	    @FXML
	    protected void moveDownBlock()
	    {
	    	mainController.moveBlock(this.getIndex(),this.getIndex()+1);
	    }
	    
		@FXML
		protected void showFourier()
		{
			showInPopup(getItem().getOutputFourier(), ""+getItem().getName()+" Output - Fourier Transform");
		}
		
		@FXML
		protected void showHistogram()
		{
			showInPopup(getItem().getOutputHistogram(),  ""+getItem().getName()+" Output - Histogram");
		}

	}
	