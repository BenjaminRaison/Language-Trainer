package br_0309.apps.languageTrainer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.ResourceBundle;

import com.aquafx_project.AquaFx;

import br_0309.apps.languageTrainer.data.UniversalData;
import br_0309.apps.languageTrainer.data.UserData;
import br_0309.apps.languageTrainer.util.SystemUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// FIXME: No custom icons for installer
public class LanguageTrainer extends Application {

	public static UserData userData = new UserData();
	public static UniversalData universalData = new UniversalData();
	public static Stage window;

	@Override
	public void start(Stage primaryStage) {
		window = primaryStage;
		Stage one = new Stage();
		try {
			BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource(Reference.FXML_PROFILE_SELECT),
					ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()));
			System.out.println(pane.getPrefWidth() + "  " + pane.getPrefHeight());
			Scene scene = new Scene(pane);
			one.setScene(scene);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		one.showAndWait();
		try {
			BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource(Reference.FXML_MENU),
					ResourceBundle.getBundle(Reference.BUNDLE_LOC, Locale.getDefault()));
			Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			scene.getStylesheets().add(getClass().getResource(Reference.CSS_APPLICATION).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (SystemUtil.isWindows()) {
			File file = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "LanguageTrainer"
					+ File.separator);
			file.mkdirs();
			Reference.DEFAULT_SAVE_DIR = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator
					+ "LanguageTrainer" + File.separator;
		} else if (SystemUtil.isMac()) {
			// TODO: Check
			File file = new File(System.getProperty("user.home") + File.separator + "Libraries" + File.separator + "ApplicationSupport" + File.separator);
			file.mkdirs();
			Reference.DEFAULT_SAVE_DIR = System.getProperty("user.home") + File.separator + "Libraries" + File.separator + "ApplicationSupport" + File.separator;
		} else {
			File file = new File(System.getProperty("user.home") + File.separator + "LanguageTrainer" + File.separator);
			file.mkdirs();
			Reference.DEFAULT_SAVE_DIR = System.getProperty("user.home") + File.separator + "LanguageTrainer" + File.separator;
		}
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
		LanguageHandler.setDisplayLanguage(LanguageHandler.getBestLocale());
		if (SystemUtil.isMac()) {
			AquaFx.style();
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		} else if (System.getProperty("os.name").startsWith("Windows")) {

		} else {

		}
		printSystemInfo();
		universalData.load();
		launch(args);
		System.exit(0);
	}

	private static void printSystemInfo() {
		System.out.println("Operating System:\t\t\t" + System.getProperty("os.name"));
		System.out.println("Operating System Version:\t" + System.getProperty("os.version"));
		System.out.println("Architecture:\t\t\t\t" + System.getProperty("os.arch"));
		System.out.println("Java Version:\t\t\t\t" + System.getProperty("java.version"));
		System.out.println("Java Vendor:\t\t\t\t" + System.getProperty("java.vendor"));
		System.out.println("JavaFX Version:\t\t\t\t" + System.getProperty("javafx.version"));
		System.out.println("Java Home Dir:\t\t\t\t" + System.getProperty("java.home"));
		System.out.println("Temporary Dir:\t\t\t\t" + System.getProperty("java.io.tmpdir"));
		System.out.println("System language:\t\t\t" + System.getProperty("user.language"));
		System.out.println("Execution Dir:\t\t\t\t" + System.getProperty("user.dir"));
		System.out.println("User Home Dir:\t\t\t\t" + System.getProperty("user.home") + "\n");
	}

}
