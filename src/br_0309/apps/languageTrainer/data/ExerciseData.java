package br_0309.apps.languageTrainer.data;

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class ExerciseData implements Comparable<ExerciseData> {

	private SimpleBooleanProperty selected;
	private SimpleStringProperty title;
	private SimpleStringProperty languages;
	private SimpleStringProperty type;
	public File file;
	public String[] langs;

	public ExerciseData() {
	}

	public ExerciseData(boolean selected, String title, String languages, String type, File file, String[] langs2) {
		this.selected = new SimpleBooleanProperty(selected);
		this.title = new SimpleStringProperty(title);
		this.languages = new SimpleStringProperty(languages);
		this.type = new SimpleStringProperty(type);
		this.file = file;
		langs = langs2;
	}

	public boolean isSelected() {
		return selected.get();
	}

	public String getTitle() {
		return title.get();
	}

	public String getLanguages() {
		return languages.get();
	}

	public String getType() {
		return type.get();
	}

	@Override
	public int compareTo(ExerciseData o) {
		int last = title.get().compareTo(o.getTitle());
		return last;
	}

	public void setSelected(boolean value) {
		selected.set(value);
	}

	/**
	 * @returns BooleanProperty isActive for table. <br>
	 *          On table: column.setCellValueFactory(new
	 *          PropertyValueFactory<>("active"));
	 */
	public BooleanProperty activeProperty() {
		return selected;
	}

}