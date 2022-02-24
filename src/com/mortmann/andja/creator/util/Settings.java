package com.mortmann.andja.creator.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.mortmann.andja.creator.GUI;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

@Root
public class Settings {
	public Settings() {
	}

	public Settings(String exportPath) {
		Settings.exportPath = exportPath;
	}

	@Element(required = false)
	public static String exportPath = "./";
	@Element(required = false)
	public static String CurrentLanguage = "English";

	public static void ShowSettingsDialog() {
		Dialog<Settings> dialog = new Dialog<>();
		dialog.setTitle("Settings");
		dialog.setResizable(false);

		Label locationLabel = new Label("Export Path: ");
		Button locationButton = new Button(Settings.exportPath);
		DirectoryChooser location = new DirectoryChooser();
		if (Files.exists(Paths.get(exportPath)))
			location.setInitialDirectory(Paths.get(exportPath).toFile());
		location.setTitle("Please select Folder to export Data");

		locationButton.setOnAction(x -> {
			File output = location.showDialog(GUI.Instance.mainWindow);
			if (output == null)
				return;
			if (output.exists() == false) {
				return;
			}
			if (output.isFile()) {
				output = output.getParentFile();
			}
			try {
				locationButton.setText(output.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		GridPane grid = new GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
		float percentage = 25f;
		col1.setPercentWidth(percentage);
		col1.setHgrow(Priority.ALWAYS);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(100f - percentage);
		col2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(col1, col2);
		locationButton.setMinWidth(((100f - percentage) / 100f) * 500f);
		locationButton.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
		grid.add(locationLabel, 0, 0);
		grid.add(locationButton, 1, 0);
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().setMinSize(500, 250);
		ButtonType buttonTypeOk = new ButtonType("Save", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

		Label languageLabel = new Label("Language");
		grid.add(languageLabel, 0, 1);

		ComboBox<String> languageBox = new ComboBox<String>(FXCollections.observableArrayList(GUI.Languages));
		languageBox.getSelectionModel().select(CurrentLanguage);
		languageBox.setMinWidth(((100f - percentage) / 100f) * 500f);
		grid.add(languageBox, 1, 1);

		dialog.setResultConverter(new Callback<ButtonType, Settings>() {
			@Override
			public Settings call(ButtonType b) {
				if (b == buttonTypeOk) {
					Settings.exportPath = locationButton.getText();
					Settings.CurrentLanguage = languageBox.getValue();
				}
				return null;
			}
		});
		dialog.showAndWait();
	}

}