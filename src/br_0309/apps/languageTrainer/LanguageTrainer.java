package br_0309.apps.languageTrainer;

import br_0309.apps.languageTrainer.data.ExerciseData;
import br_0309.apps.languageTrainer.data.UniversalData;
import br_0309.apps.languageTrainer.data.UserData;
import br_0309.apps.languageTrainer.scenes.controllers.ControllerProfileSelect;
import br_0309.apps.languageTrainer.scenes.controllers.ControllerTranslate;
import br_0309.apps.languageTrainer.scenes.controllers.IController;
import br_0309.apps.languageTrainer.util.FXUtil;
import br_0309.apps.languageTrainer.util.SystemUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

// TODO: Add icons at different resolutions
// TODO: Make themes less terrible
// TODO: I18nize Copyrights.txt
// FIXME FIXME: GET DEPLOYMENT WORKING!!
public class LanguageTrainer extends Application {

    public static UserData userData = new UserData();
    public static UniversalData universalData = new UniversalData();

    public static Stage window;
    public static IController currentController;

    public static Random random;

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            FXUtil.showExceptionDialog("", throwable.toString(), throwable);
        });
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(Reference.LOGO)));
            window = primaryStage;
            showLogin();
            LanguageHandler.setDisplayLanguage(userData.getLanguage());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Reference.FXML_MENU), ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()));
            BorderPane root = loader.load();
            Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
            scene.getStylesheets().add(getClass().getResource(userData.getTheme()).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage
                    .setTitle(ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()).getString("generic.windowTitle") + " " + Reference.VERSION);
            currentController = loader.getController();
            primaryStage.setOnCloseRequest(event -> {
                // OK returns false for some reason despite FXUtil.showConfirm... returning true when OK
                if (!askForExit()) {
                    currentController.onExit();
                    event.consume();
                }
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            FXUtil.showExceptionDialog("", "", e);
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        // System-specific settings
        if (SystemUtil.isWindows()) {
            File file = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "LanguageTrainer"
                    + File.separator);
            file.mkdirs();
            Reference.DEFAULT_SAVE_DIR = file.getAbsolutePath();

        } else if (SystemUtil.isMac()) {
            // FIXME: Find out proper mac data directory
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            File file = new File(System.getProperty("user.home") + File.separator + "Libraries" + File.separator + "ApplicationSupport" + File.separator);
            file.mkdirs();
            Reference.DEFAULT_SAVE_DIR = file.getAbsolutePath();
        } else {
            File file = new File(System.getProperty("user.home") + File.separator + "LanguageTrainer" + File.separator);
            file.mkdirs();
            Reference.DEFAULT_SAVE_DIR = file.getAbsolutePath();
        }
        Reference.DEFAULT_EXERCISE_DIR = Reference.DEFAULT_SAVE_DIR + File.separator + "exercises" + File.separator;
        // If the application is run from anything but loose files, redirect
        // console to log_<<time>>
        if (!SystemUtil.isDirectory() || SystemUtil.isMacApp()) {
            File log = new File(Reference.DEFAULT_SAVE_DIR + "logs" + File.separator + "log_" + SystemUtil.getTimeAndDate());
            try {
                log.getParentFile().mkdirs();
                log.createNewFile();
                PrintStream writer = new PrintStream(log);
                System.setErr(writer);
                System.setOut(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*
         * TODO: Add code to copy files over automatically on a different thread
		 */

        // Set language to best suited language before user settings are loaded
        LanguageHandler.setDisplayLanguage(LanguageHandler.getBestLocale());
        printSystemInfo();
        universalData.load();
        File file = new File(Reference.DEFAULT_EXERCISE_DIR);
        file.mkdirs();
        universalData.addExerciseLocation(file);
        random = new Random(SystemUtil.getTimeAndDate().hashCode());
        launch(args);
    }

    /**
     * Print all relevant information about the system, user and languages
     */
    private static void printSystemInfo() {
        System.out.println("Operating System:\t\t" + System.getProperty("os.name") + "\n" + "Operating System Version:\t" + System.getProperty("os.version")
                + "\n" + "Architecture:\t\t\t" + System.getProperty("os.arch") + "\n" + "Java Version:\t\t\t" + System.getProperty("java.version") + "\n"
                + "Java Vendor:\t\t\t" + System.getProperty("java.vendor") + "\n" + "JavaFX Version:\t\t\t" + System.getProperty("javafx.version") + "\n"
                + "Java Home Dir:\t\t\t" + System.getProperty("java.home") + "\n" + "Temporary Dir:\t\t\t" + System.getProperty("java.io.tmpdir") + "\n"
                + "Execution Dir:\t\t\t" + System.getProperty("user.dir") + "\n" + "User Home Dir:\t\t\t" + System.getProperty("user.home") + "\n"
                + "System language:\t\t" + System.getProperty("user.language") + "\n" + "JVM Default Locale:\t\t" + Locale.getDefault() + "\n"
                + "Best suited locale:\t\t" + LanguageHandler.getBestLocale() + "\nLanguage Trainer version:\t" + Reference.VERSION + "\n");
    }

    /**
     * Shows the profile select screen
     */
    private void showLogin() {
        Stage stage = new Stage();
        // TODO: Add more images for better resolutions
        stage.getIcons().add(new Image(Reference.LOGO));
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Reference.FXML_PROFILE_SELECT),
                ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Reference.THEMES[0]);
            stage.setMinWidth(350);
            stage.setMinHeight(400);
            stage.setScene(scene);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        stage.showAndWait();
        ControllerProfileSelect controller = loader.getController();
        if (!controller.isProfileSelected) {
            System.exit(0);
        }
    }

    public static void setScene(String sceneLoc) {
        currentController.onExit();
        FXMLLoader loader = new FXMLLoader(LanguageTrainer.class.getResource(sceneLoc), ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(LanguageTrainer.class.getResource(userData.getTheme()).toExternalForm());
            currentController = loader.getController();
            window.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Add title/header
            FXUtil.showExceptionDialog("", "", e);
        }
    }

    public static void showMenu() {
        currentController.onExit();
        FXMLLoader loader = new FXMLLoader(LanguageTrainer.class.getResource(Reference.FXML_MENU),
                ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(LanguageTrainer.class.getResource(userData.getTheme()).toExternalForm());
            currentController = loader.getController();
            window.setScene(scene);
            window.setMinHeight(400);
            window.setMinWidth(425);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Add title/header
            FXUtil.showExceptionDialog("", "", e);
        }
    }

    public static boolean askForExit() {
        ResourceBundle BUNDLE = ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault());
        return FXUtil.showConfirmationDialog(BUNDLE.getString("generic.confirm"), BUNDLE.getString("generic.confirmQuit"),
                BUNDLE.getString("generic.confirmQuit"), BUNDLE.getString("generic.ok"), BUNDLE.getString("generic.cancel"));
    }

    public static void playSoundCorrect() {
        if (userData.getPlaySounds()) {
            AudioInputStream audioIn = null;
            try {
                BufferedInputStream in = new BufferedInputStream(LanguageTrainer.class.getResourceAsStream(userData.getSoundCorrect()));
                audioIn = AudioSystem.getAudioInputStream(in);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (audioIn != null) {
                        audioIn.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void playSoundIncorrect() {
        if (userData.getPlaySounds()) {
            AudioInputStream audioIn = null;
            try {
                BufferedInputStream in = new BufferedInputStream(LanguageTrainer.class.getResourceAsStream(userData.getSoundIncorrect()));
                audioIn = AudioSystem.getAudioInputStream(in);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (audioIn != null) {
                        audioIn.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void playSoundFinished() {
        if (userData.getPlaySounds()) {
            AudioInputStream audioIn = null;
            try {
                BufferedInputStream in = new BufferedInputStream(LanguageTrainer.class.getResourceAsStream(userData.getSoundFinished()));
                audioIn = AudioSystem.getAudioInputStream(in);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (audioIn != null) {
                        audioIn.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void showTranslation(ArrayList<ExerciseData> selected) {
        currentController.onExit();
        FXMLLoader loader = new FXMLLoader(LanguageTrainer.class.getResource(Reference.FXML_TRANSLATION),
                ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(LanguageTrainer.class.getResource(userData.getTheme()).toExternalForm());
            ControllerTranslate controller = loader.getController();
            controller.init(selected);
            currentController = controller;
            window.setScene(scene);
        } catch (IOException e) {
            // TODO: Add title/header
            FXUtil.showExceptionDialog("", "", e);
        }
    }

}
