package com.mortmann.andja.creator.saveclasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public abstract class BaseSave {

	public static final String saveFilePath = "Latest/"; 
	public static final String backuppath = "old/";
	public static final String tempNamePrefix ="temp_";
	public static final String oldNamePrefix ="old_";

	public static boolean Save(String fileName, Object object) {
		Serializer serializer = new Persister(new AnnotationStrategy());
        try {
    		File file = Paths.get(saveFilePath, fileName).toFile();
    		CopyFile(saveFilePath, fileName, backuppath, tempNamePrefix + fileName);
			serializer.write(object, file);
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Missing requierd Data!");
			a.setContentText("Can´t save data! Fill all required data out! " + e.getMessage());
			e.printStackTrace();
			a.show();
    		CopyFile(backuppath, tempNamePrefix + fileName, saveFilePath, fileName);
			return false;
		}
        CopyFile(backuppath, tempNamePrefix + fileName, backuppath, oldNamePrefix + fileName);
		return true;
	}
	public static void CopyFile(String firstPath, String firstName, String secondPath, String secondName) {
		File first = Paths.get(firstPath, firstName).toFile();
		File second = Paths.get(secondPath, secondName).toFile();
		if(first.exists()){
			if(Files.exists(Paths.get(saveFilePath)) == false) {
				try {
					Files.createDirectory(Paths.get(saveFilePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    		if(second.exists()){
    			second.delete();
    		}
    		first.renameTo(second);
    	}
	}
	public boolean Save(){
		return Save(GetSaveFileName(), this);
	}
	public abstract String GetSaveFileName();
}
