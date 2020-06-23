package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Adiacenza;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Race;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Season> boxAnno;

    @FXML
    private Button btnSelezionaStagione;

    @FXML
    private ComboBox<Race> boxGara;

    @FXML
    private Button btnSimulaGara;

    @FXML
    private TextField textInputK;

    @FXML
    private TextField textInputK1;

    @FXML
    private TextArea txtResult;

    @FXML
    void doSelezionaStagione(ActionEvent event) {
    	this.txtResult.clear();
    	Season s=this.boxAnno.getValue();
    	if(s==null) {
    		this.txtResult.appendText(" ATTENZIONE!Nessun valore selezionato per il campo stagione.\n");
    	}
    	List<Race> races=model.creaGrafo(s);
    	Integer vertici=model.getNumVertici(), archi=model.getNumArchi();
    	if(vertici==0 || archi.equals(0)) {
    		this.txtResult.appendText("ATTENZIONE! Qualcosa e' andato storto nella creazione del grafo.\n");
    		return;
    	}
    	this.txtResult.appendText("GRAFO CREATO!\n #VERTICI: "+vertici+" e #ARCHI: "+archi+"\n");
    	List<Adiacenza> massimi=model.getPesoMassimo();
    	if(massimi.size()==1) {
    		this.txtResult.appendText("L'arco di peso massimo nel grafo e':\n");
    		this.txtResult.appendText(massimi.get(0).getR1()+" , "+massimi.get(0).getR2()+"-->"+massimi.get(0).getPeso()+"\n");
    	}else if(massimi.size()>1) {
    		this.txtResult.appendText("Gli archi di peso massimo nel grafo sono:\n");
    		for(Adiacenza a: massimi) {
    			this.txtResult.appendText(a.getR1()+" , "+a.getR2()+"-->"+a.getPeso()+"\n");
    		}
    	}
    	this.boxGara.getItems().addAll(races);
    }

    @FXML
    void doSimulaGara(ActionEvent event) {
    	this.txtResult.clear();
    	String Pstring=this.textInputK.getText();
    	String Tstring=this.textInputK1.getText();
    	Float P=null;
    	Integer T=null;
    	try {
    		P=Float.parseFloat(Pstring);
    	}catch (NumberFormatException e) {
    		e.printStackTrace();
    		this.txtResult.appendText("ATTENZIONE! Il valore 'P' inserito non è un numero corretto.\n");
    	}
    	try {
    		T=Integer.parseInt(Tstring);
    	}catch (NumberFormatException e) {
    		e.printStackTrace();
    		this.txtResult.appendText("ATTENZIONE! Il valore 'T' inserito non è un numero corretto.\n");
    	}
    	Race r=this.boxGara.getValue();
    	if(r==null) {
    		this.txtResult.appendText("ATTENZIONE! Nessuna gara selezionata!\n");
    		return;
    	}
    	Map<Driver,Integer> pilotiPunti=model.simula(P,T,r);
    	if(pilotiPunti.size()==0) {
    		this.txtResult.appendText("ATTENZIONE! Qualcosa e' andato storto durante la simulazione.\n");
    		return;
    	}
    	this.txtResult.appendText("La simulazione ha generato i seguenti punti:\n");
    	for(Driver d:pilotiPunti.keySet()) {
    		this.txtResult.appendText(d+" : "+pilotiPunti.get(d)+"\n");
    	}
    }

    void loadData() {
    	this.boxAnno.getItems().addAll(model.getSeasons());
    }
    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert btnSelezionaStagione != null : "fx:id=\"btnSelezionaStagione\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert boxGara != null : "fx:id=\"boxGara\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert btnSimulaGara != null : "fx:id=\"btnSimulaGara\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK1 != null : "fx:id=\"textInputK1\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		loadData();
	}
}
