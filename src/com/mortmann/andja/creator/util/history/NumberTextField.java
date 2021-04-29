package com.mortmann.andja.creator.util.history;

import java.util.function.UnaryOperator;

import javafx.application.Platform;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.converter.IntegerStringConverter;

public class NumberTextField extends TextFieldHistory {
	int maxLength = 0;
	float minNumber = Float.MIN_VALUE;
	float maxNumber = Float.MAX_VALUE;
	boolean isFloat = false;
	
	public NumberTextField(int maxLength) {
		super();
		this.maxLength = maxLength;  
		AddValidator();
	}
	public NumberTextField(int maxLength,boolean isFloat) {
		super();
		this.maxLength = maxLength; 
		this.isFloat = isFloat; 
		AddValidator();
	}
	public NumberTextField(boolean isFloat, float minimum, float maximum) {
		super();
		this.isFloat = isFloat;
		if(minimum>maximum) {
			minNumber = maximum;
			maxNumber = minimum; 
		} else {
			minNumber = minimum;
			maxNumber = maximum; 
		}
		AddValidator();
	}
	public NumberTextField(int maxLength, float maxNumber) {
		super();
		this.maxLength = maxLength; 
		this.maxNumber = maxNumber; 
		AddValidator();
	}
	public NumberTextField(String s, int maxLength, float maxNumber) {
		super(s);
		this.maxLength = maxLength; 
		this.maxNumber = maxNumber; 
		AddValidator();
	}
	public NumberTextField() {
		super(); 
		AddValidator();
	}
	public NumberTextField(String s) {
		super(s); 
		AddValidator();
	}
	public NumberTextField(String s,int maxLength) {
		super(s);
		this.maxLength = maxLength;  
		AddValidator();
	}
	public NumberTextField(float minimum, float maximum) {
		if(minimum > maximum) {
			minNumber = maximum;
			maxNumber = minimum; 
		} else {
			minNumber = minimum;
			maxNumber = maximum; 
		}
		AddValidator();
	}
	public NumberTextField(int maxLength, float minimum, float maximum) {
		this(minimum,maximum);
		this.maxLength = maxLength; 
		AddValidator();
	}
	
	void AddValidator() {
		UnaryOperator<Change> integerFilter = change -> {
	    	String newText = change.getControlNewText();
	    	if(newText.isBlank()) {
	    		change.setText("0");
	    	}
	    	if(newText.matches("0[0-9]")) {
                change.setRange(0, 1);
                change.setCaretPosition(change.getCaretPosition()-1);
                change.setAnchor(change.getAnchor()-1);
	    	}
            if (newText.matches("-?[0-9]*") || isFloat && newText.matches("[-]?[0-9]*(\\.[0-9]*)?")) { 
                return change;
            } else if ("-".equals(change.getText()) ) {
                if (change.getControlText().startsWith("-")) {
                    change.setText("");
                    change.setRange(0, 1);
                    change.setCaretPosition(change.getCaretPosition()-2);
                    change.setAnchor(change.getAnchor()-2);
                    return change;
                } else {
                    change.setRange(0, 0);
                    return change;
                }
            }
		    return null;
		};
		setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		AddChangeListener(new ChangeListenerHistory() {
			@Override
			public void changed(Object old, Object changed, boolean newChange) {
				if(GetFloatValue()>maxNumber){
					Platform.runLater(() -> { 
						textProperty().setValue((int)maxNumber+"");
			        }); 
	            }
	            if(GetFloatValue()<minNumber){
	            	Platform.runLater(() -> { 
						textProperty().setValue((int)minNumber+"");
			        }); 
	            }
			}
		}, true);
	}
	
	@Override
	public void replaceText(int start, int end, String text) {
		 if (this.getMaxLength() <= 0) {
	            // Default behavior, in case of no max length
	            super.replaceText(start, end, text);
        }
        else {
            // Get the text in the textfield, before the user enters something
            String currentText = this.getText() == null ? "" : this.getText();
            
            // Compute the text that should normally be in the textfield now
            String finalText = currentText.substring(0, start) + text + currentText.substring(end);
            if(finalText.startsWith("-")) {
            	finalText = finalText.substring(1, finalText.length());
            }
            // If the max length is not excedeed
            int numberOfexceedingCharacters = finalText.length() - this.getMaxLength();
            if (numberOfexceedingCharacters <= 0) {
                // Normal behavior
                super.replaceText(start, end, text);
            }
            else {
                // Otherwise, cut the the text that was going to be inserted
                String cutInsertedText = text.substring(
                        0, 
                        text.length() - numberOfexceedingCharacters
                );
                // And replace this text
                super.replaceText(start, end, cutInsertedText);
            }
        }
	}
	
	public int GetIntValue(){
		if(isFloat){
			System.out.println("WARNING! YOUR TRYING TO GET A INT VALUE FROM AN FLOAT FIELD! ");
			try {
				throw new Exception("INT VALUE FROM AN FLOAT FIELD");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(this.getText()==null||this.getText()==""||this.getText().trim().isEmpty()){
			return 0;
		}
		try {
			return Integer.parseInt(this.getText().trim());
		} catch (Exception e) {
			return 0;
		}
	}
	
	public float GetFloatValue(){
		if(this.getText()==null||this.getText()==""||this.getText().trim().isEmpty()){
			return 0;
		}
		return Float.parseFloat(this.getText().trim());
	}
	private int getMaxLength() {
		return maxLength;
	}
	
	public boolean isFloat() {
		return isFloat;
	}
	public boolean IsValidEntry() {
		if(isFloat) {
			return GetFloatValue()!=0;
		}
		return GetIntValue()>0;
	}
}