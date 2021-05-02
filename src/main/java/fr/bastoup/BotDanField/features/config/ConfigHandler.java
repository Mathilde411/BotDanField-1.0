package fr.bastoup.BotDanField.features.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import fr.bastoup.BotDanField.beans.Config;

public class ConfigHandler {

	private static Config config = null;
	private static File configFile = null;

	public static void configBuilder() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			setConfig(mapper.readValue(configFile, Config.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Config getConfig() {
		return config;
	}

	private static void setConfig(Config cfg) {
		ConfigHandler.config = cfg;
	}

	public static File getConfigFile() {
		return configFile;
	}

	public static void setConfigFile(File configFile) {
		ConfigHandler.configFile = configFile;
	}
}
