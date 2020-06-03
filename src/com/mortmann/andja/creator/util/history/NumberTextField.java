package com.mortmann.andja.creator.util.history;

public class NumberTextField extends TextFieldHistory {
	int maxLength = 0;
	float minNumber = Float.MIN_VALUE;
	float maxNumber = Float.MAX_VALUE;
	boolean isFloat = false;
	
	public NumberTextField(int maxLength) {
		super();
		this.maxLength = maxLength; 
	}
	public NumberTextField(int maxLength,boolean isFloat) {
		super();
		this.maxLength = maxLength; 
		this.isFloat = isFloat;
	}
	public NumberTextField(boolean isFloat, float Min, float Max) {
		super();
		this.isFloat = isFloat;
		minNumber = Min;
		maxNumber = Max;
	}
	public NumberTextField(int maxLength, float maxNumber) {
		super();
		this.maxLength = maxLength; 
		this.maxNumber = maxNumber;
	}
	public NumberTextField(String s,int maxLength, float maxNumber) {
		super(s);
		this.maxLength = maxLength; 
		this.maxNumber = maxNumber;
	}
	public NumberTextField() {
		super();
	}
	public NumberTextField(String s) {
		super(s);
	}
	public NumberTextField(String s,int maxLength) {
		super(s);
		this.maxLength = maxLength; 
	}
	public NumberTextField(float minimum, float maximum) {
		minNumber = minimum;
		maxNumber = maximum;
	}
	public NumberTextField(int maxLength, float minimum, float maximum) {
		this.maxLength = maxLength; 
		minNumber = minimum;
		maxNumber = maximum;
	}
	@Override
	public void replaceText(int start, int end, String text) {
		if (validate(text)) {
		 if (this.getMaxLength() <= 0) {
	            // Default behavior, in case of no max length
	            super.replaceText(start, end, text);
	        }
	        else {
	            // Get the text in the textfield, before the user enters something
	            String currentText = this.getText() == null ? "" : this.getText();
	            
	            // Compute the text that should normally be in the textfield now
	            String finalText = currentText.substring(0, start) + text + currentText.substring(end);
	            
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
	            // Limit the Textfield Number-Value when maximum is given
	            if(GetFloatValue()>maxNumber){
	            	setText(maxNumber+"");
	            }
	            if(GetFloatValue()<minNumber){
	            	setText(minNumber+"");
	            }
	        }

		}

	}
	
	@Override
	public void replaceSelection(String text) {
		if (validate(text)) {
			super.replaceSelection(text);
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
	private boolean validate(String text) {
		if(isFloat){
			if(text.matches("[.]")){
				if(this.getText().contains(".")){
					return false;
				}
			}
			return text.matches("[0-9]*[.]?[0-9]*");
		}
		return text.matches("[0-9]*");
	}
	public boolean isFloat() {
		return isFloat;
	}
}