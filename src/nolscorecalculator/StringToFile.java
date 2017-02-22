/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nolscorecalculator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author shep
 */
public class StringToFile {
    
    public static void write(String filename, String fileContents) {	
		try {
			File file = new File(filename);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(fileContents);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
}
