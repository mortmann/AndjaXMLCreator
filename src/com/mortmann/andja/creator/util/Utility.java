package com.mortmann.andja.creator.util;

import com.mortmann.andja.creator.other.Item;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class Utility {
	public static Callback<ListView<Item>, ListCell<Item>> GetItemListView() {
		return new Callback<ListView<Item>, ListCell<Item>>() {
			@Override
			public ListCell<Item> call(ListView<Item> view) {
				return new ListCell<Item>(){

                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                    }

                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty == false && isHover() == false && isFocused() == false){
                            setText(item.toString());
                            setStyle("-fx-text-fill: black; -fx-background-color: " +item.getColor()+";");
                            hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                            	if(isNowHovered) {
                            		setStyle("-fx-text-fill: black; -fx-background-color: #0096C9;");
                            	}
                            	else 
                                    setStyle("-fx-text-fill: black; -fx-background-color: " +item.getColor()+";");
                            });
                        }

                    }

                };					}

		};
	}
}
