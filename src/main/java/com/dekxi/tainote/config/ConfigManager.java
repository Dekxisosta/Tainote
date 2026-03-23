package com.dekxi.tainote.config;

import javafx.scene.paint.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class ConfigManager {
    private Properties forceDefaults;
    private Properties props;
    private Properties defaults;
    private Map<String, String> textStyles;
    private Map<String, String> nodeStyles;
    private Path configFile;

    public ConfigManager(Path configFile){
        this.configFile = configFile;
        textStyles = new HashMap<>();
        nodeStyles = new HashMap<>();
        ensureDir();
        loadForceDefaults();
        loadDefaults();
        loadProps();
        loadPropsToTextStyles(forceDefaults);
        loadPropsToNodeStyles(forceDefaults);
        loadPropsToTextStyles(props);
        loadPropsToNodeStyles(props);
        loadPropsToTextStyles(defaults);
        loadPropsToNodeStyles(defaults);
    }
    public String getEditorStyleSheet(){
        StringBuilder styleSheet = new StringBuilder();
        textStyles.forEach((k,v) -> styleSheet.append(String.format("%s: %s;%n", k, v)));
        nodeStyles.forEach((k,v) -> styleSheet.append(String.format("%s: %s;%n", k, v)));
        return styleSheet.toString();
    }
    public Color getFontColor(){return Color.web(textStyles.getOrDefault("-fx-text-fill", defaults.getProperty("text.-fx-text-fill")));}
    public String getFontStyle(){return textStyles.getOrDefault("-fx-font-family", defaults.getProperty("text.-fx-font-family"));}
    public int getFontSizeInPx(){
        String fontSizeStr = textStyles.getOrDefault("-fx-font-size", defaults.getProperty("text.-fx-font-size")).replaceAll("[^0-9]", "");
        return Integer.parseInt(fontSizeStr);
    }
    public void setFontStyle(String style){textStyles.put("-fx-font-family", String.format("'%s'", style));}
    public void setFontSizeInPx(int size){textStyles.put("-fx-font-size", size + "px");}
    public void setFontColor(Color color){
        textStyles.put(
            "-fx-text-fill", String.format("rgb(%d, %d, %d)",
                    (int)(color.getRed() * 255),
                    (int)(color.getGreen() * 255),
                    (int)(color.getBlue() * 255)));
    }
    public void saveUserProperties(){
        try (OutputStream out =Files.newOutputStream(configFile)) {
            props = convertMapsToProps(new Properties());
            props.store(new OutputStreamWriter(out, StandardCharsets.UTF_8), "Editor Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Properties convertMapsToProps(Properties propsObj){
        textStyles.forEach((k,v) -> propsObj.setProperty("text."+k, v));
        nodeStyles.forEach((k,v) -> propsObj.setProperty("node."+k, v));

        textStyles.forEach((k,v)->{
            System.out.println(k+":"+v);
        });
        nodeStyles.forEach((k,v)->{
            System.out.println(k+":"+v);
        });
        return propsObj;
    }
    private void ensureDir(){
        try {
            Files.createDirectories(configFile.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadForceDefaults(){
        try(InputStream in = getClass().getResourceAsStream("/config/forcedefaults.properties")){
            forceDefaults = new Properties();
            forceDefaults.load(in);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void loadDefaults(){
        try(InputStream in = getClass().getResourceAsStream("/config/default.properties")){
            defaults = new Properties();
            defaults.load(in);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void loadProps(){
        try(InputStream in = Files.newInputStream(configFile)){
            props = new Properties(defaults);
            props.load(new InputStreamReader(in,StandardCharsets.UTF_8));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void loadPropsToTextStyles(Properties propLoad){
        propLoad.entrySet().stream()
                .filter(e -> e.getKey().toString().startsWith("text."))
                .forEach(e -> {
                    String key = e.getKey().toString().substring(5);
                    textStyles.putIfAbsent(key, e.getValue().toString());
                });
    }
    private void loadPropsToNodeStyles(Properties propLoad){
        propLoad.entrySet().stream()
                .filter(e -> e.getKey().toString().startsWith("node."))
                .forEach(e -> {
                    String key = e.getKey().toString().substring(5);
                    textStyles.putIfAbsent(key, e.getValue().toString());
                });
    }

}
