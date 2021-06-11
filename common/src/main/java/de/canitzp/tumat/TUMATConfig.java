package de.canitzp.tumat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TUMATConfig {
    
    public static File configurationFile;
    
    public static void reload(){
        if(configurationFile == null){
            return;
        }
    
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(configurationFile)){
            props.load(fis);
        } catch(IOException e){
            e.printStackTrace();
        }
        
    }

}
