package com.rokue.game.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rokue.game.entities.Hall;
import com.rokue.game.entities.enchantments.Enchantment;
import com.rokue.game.entities.monsters.Monster;
import com.rokue.game.entities.monsters.WizardMonster;
import com.rokue.game.events.EventManager;
import com.rokue.game.states.PlayMode;
import com.rokue.game.util.Cell;

public class GameSaveManager {
    private static final String SAVE_DIRECTORY = "saves";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private final EventManager eventManager;
    private PlayMode currentPlayMode;

    public GameSaveManager(EventManager eventManager) {
        this.eventManager = eventManager;
        // Create saves directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(SAVE_DIRECTORY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentPlayMode(PlayMode playMode) {
        this.currentPlayMode = playMode;
    }

    public void saveGame(PlayMode playMode) {
        String timestamp = DATE_FORMAT.format(new Date());
        String fileName = "game_save_" + timestamp + ".sav";
        Path savePath = Paths.get(SAVE_DIRECTORY, fileName);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savePath.toFile()))) {
            GameSaveData saveData = new GameSaveData(
                playMode.getCurrentHall(),
                playMode.getHero(),
                playMode.getRemainingTime(),
                playMode.getCurrentHall().getMonsters(),
                playMode.getCurrentHall().getEnchantments()
            );
            oos.writeObject(saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<SaveFileInfo> getSaveFiles() {
        List<SaveFileInfo> saveFiles = new ArrayList<>();
        File saveDir = new File(SAVE_DIRECTORY);
        
        if (saveDir.exists() && saveDir.isDirectory()) {
            File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".sav"));
            if (files != null) {
                for (File file : files) {
                    try {
                        Date saveDate = DATE_FORMAT.parse(file.getName().substring(10, 29));
                        saveFiles.add(new SaveFileInfo(file.getName(), saveDate));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        // Sort by date, newest first
        saveFiles.sort((a, b) -> b.getSaveDate().compareTo(a.getSaveDate()));
        return saveFiles;
    }

    public GameSaveData loadGame(String fileName) {
        Path savePath = Paths.get(SAVE_DIRECTORY, fileName);
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savePath.toFile()))) {
            GameSaveData saveData = (GameSaveData) ois.readObject();
            
            // Restore transient fields
            saveData.getHero().setEventManager(eventManager);
            
            // First clear existing monsters and enchantments from the hall
            Hall hall = saveData.getCurrentHall();
            hall.clearMonsters();
            hall.clearEnchantments();
            
            // Restore the grid state first
            Cell[][] gridState = saveData.getGridState();
            if (gridState != null) {
                hall.setGrid(gridState);
            }
            
            // Restore monsters with their positions and behaviors
            List<Monster> monsters = saveData.getMonsters();
            for (Monster monster : monsters) {
                // The readObject method in each monster class will handle basic behavior restoration
                if (monster instanceof WizardMonster) {
                    WizardMonster wizard = (WizardMonster) monster;
                    wizard.setEventManager(eventManager);
                    if (currentPlayMode != null) {
                        wizard.setPlayMode(currentPlayMode);
                    }
                }
                hall.addMonster(monster);
            }
            
            // Restore enchantments with their positions
            List<Enchantment> enchantments = saveData.getEnchantments();
            for (Enchantment enchantment : enchantments) {
                if (!enchantment.isCollected()) {
                    hall.addEnchantment(enchantment);
                }
            }
            
            return saveData;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class SaveFileInfo {
        private final String fileName;
        private final Date saveDate;

        public SaveFileInfo(String fileName, Date saveDate) {
            this.fileName = fileName;
            this.saveDate = saveDate;
        }

        public String getFileName() {
            return fileName;
        }

        public Date getSaveDate() {
            return saveDate;
        }

        @Override
        public String toString() {
            return new SimpleDateFormat("MMM dd, yyyy HH:mm:ss").format(saveDate);
        }
    }
} 