package com.mortmann.andja.creator.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorHelper {
	public static void ShowErrorAlert(String errortitle, String content) {
		Alert a = new Alert(AlertType.ERROR);
		a.setTitle(errortitle);
		a.setContentText(content);
		a.show();
	}
}
