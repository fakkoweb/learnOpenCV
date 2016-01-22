package it.polito.elite.teaching.learnOpenCVBlocks;

import org.opencv.core.Mat;

/**
 * This is an empty block.. it behaves like an unactive LearnBlock.
 * Use this as starting point to create further blocks.
 * <p>
 * @author	Dario Facchini <io.dariofacchini @ gmail.com>
 * @since	2014-07-01
 */
public class Stub extends LearnBlock {

	//put here specific state or FXML variables (if needed)
	//metti qui variabili di stato o FXML (se necessario)
	
	public Stub()
	{
		//put here specific state variable initialization (if needed)
		//metti qui l'inizializzazione delle variabili se serve
		//THERE IS NO NEED TO LOAD THE FXML
		//NON CARICARE IL FILE FXML, LearnBlock fa tutto automaticamente
	}
	
	@Override
	public boolean validate(final Mat input)
	{
		
		//implement rules to check validation on state variables
		//implementa le regole che fanno ritornare true a questa funzione solo se il blocco è pronto a processare!
		//stub method has no rules...
		return true;
		
	}

    @Override	
	public Mat elaborate(Mat input)
	{
    	//FROM Mat TO Mat
    	//Put here function and elaboration and codeVersion choosing
    	//Metti qui il processamento e la scelta della versione del codice da usare
    	
    	return input;		
	};

	

	
}