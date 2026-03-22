package com.dekxi.tainote.config;

import javafx.scene.paint.*;

import java.io.*;
import java.util.*;

public class Config {
    private Properties props;
    private String propsPath = "/config/default.properties";

    public Config(){
        try(InputStream in = getClass().getResourceAsStream(propsPath)){
            props = new Properties();
            props.load(in);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getFontStyle(){return props.getProperty("font.style");}
    public int getFontSize(){return Integer.parseInt(props.getProperty("font.size", "16"));}
    public Color getFontColor(){
        return Color.web(props.getProperty("font.color"));

    }

    public void setFontStyle(String style){props.setProperty("font.style", style);}
    public void setFontSize(int size){props.setProperty("font.size", String.valueOf(size));}
    public void setFontColor(Color color){props.setProperty(
            "font.color", String.format("rgb(%d, %d, %d)",
                    (int)(color.getRed() * 255),
                    (int)(color.getGreen() * 255),
                    (int)(color.getBlue() * 255)));
    }
    public void saveNewDefaults(){
        try(FileOutputStream fos = new FileOutputStream(String.valueOf(getClass().getResource(propsPath)))){
            fos.write(String.format("""
                    font.size=%s
                    font.color=%s
                    font.style=%s
                    """,
                    props.getProperty("font.size"),
                    props.getProperty("font.color"),
                    props.getProperty("font.style")
                    ).getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
