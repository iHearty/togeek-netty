package cn.togeek.netty;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.restlet.engine.util.StringUtils;

public final class Settings {
   public static final Settings EMPTY = new Builder().build();

   private SortedMap<String, String> settings;

   private Settings(Map<String, String> settings) {
      // we use a sorted map for consistent serialization when using getAsMap()
      this.settings = Collections.unmodifiableSortedMap(
         new TreeMap<String, String>(settings));
   }

   public static Builder builder() {
      return new Builder();
   }

   /**
    * Returns the setting value associated with the setting key.
    *
    * @param setting
    *           The setting key
    * @return The setting value, <tt>null</tt> if it does not exists.
    */
   public String get(String setting) {
      return settings.get(setting);
   }

   /**
    * Returns the setting value associated with the setting key. If it does not
    * exists, returns the default value provided.
    */
   public String get(String setting, String defaultValue) {
      String value = get(setting);
      return StringUtils.isNullOrEmpty(value) ? defaultValue : value;
   }

   /**
    * Returns the setting value (as float) associated with the setting key. If
    * it does not exists, returns the default value provided.
    * 
    * @throws SettingsException
    */
   public Float getAsFloat(String setting, Float defaultValue)
      throws SettingsException {
      String value = get(setting);

      if(StringUtils.isNullOrEmpty(value)) {
         return defaultValue;
      }

      try {
         return Float.parseFloat(value);
      }
      catch(NumberFormatException e) {
         throw new SettingsException("Failed to parse float setting [" + setting
            + "] with value [" + value + "]", e);
      }
   }

   /**
    * Returns the setting value (as double) associated with the setting key. If
    * it does not exists, returns the default value provided.
    * 
    * @throws SettingsException
    */
   public Double getAsDouble(String setting, Double defaultValue)
      throws SettingsException {
      String value = get(setting);

      if(StringUtils.isNullOrEmpty(value)) {
         return defaultValue;
      }

      try {
         return Double.parseDouble(value);
      }
      catch(NumberFormatException e) {
         throw new SettingsException("Failed to parse double setting ["
            + setting + "] with value [" + value + "]", e);
      }
   }

   /**
    * Returns the setting value (as int) associated with the setting key. If it
    * does not exists, returns the default value provided.
    * 
    * @throws SettingsException
    */
   public Integer getAsInt(String setting, Integer defaultValue)
      throws SettingsException {
      String value = get(setting);

      if(StringUtils.isNullOrEmpty(value)) {
         return defaultValue;
      }

      try {
         return Integer.parseInt(value);
      }
      catch(NumberFormatException e) {
         throw new SettingsException("Failed to parse int setting [" + setting
            + "] with value [" + value + "]", e);
      }
   }

   /**
    * Returns the setting value (as long) associated with the setting key. If it
    * does not exists, returns the default value provided.
    * 
    * @throws SettingsException
    */
   public Long getAsLong(String setting, Long defaultValue)
      throws SettingsException {
      String value = get(setting);

      if(StringUtils.isNullOrEmpty(value)) {
         return defaultValue;
      }

      try {
         return Long.parseLong(value);
      }
      catch(NumberFormatException e) {
         throw new SettingsException("Failed to parse long setting [" + setting
            + "] with value [" + value + "]", e);
      }
   }

   /**
    * Returns the setting value (as boolean) associated with the setting key. If
    * it does not exists, returns the default value provided.
    */
   public Boolean getAsBoolean(String setting, Boolean defaultValue) {
      String value = get(setting);

      if(StringUtils.isNullOrEmpty(value)) {
         return defaultValue;
      }

      return Boolean.parseBoolean(value);
   }

   /**
    * The settings as a flat {@link java.util.Map}.
    * 
    * @return an unmodifiable map of settings
    */
   public Map<String, String> getAsMap() {
      return Collections.unmodifiableMap(this.settings);
   }

   /**
    * A builder allowing to put different settings and then {@link #build()} an
    * immutable settings implementation. Use {@link Settings#settingsBuilder()}
    * in order to construct it.
    */
   public static class Builder {
      private final Map<String, String> map = new LinkedHashMap<>();

      private Builder() {
      }

      /**
       * Removes the provided setting from the internal map holding the current
       * list of settings.
       */
      public String remove(String key) {
         return map.remove(key);
      }

      /**
       * Returns a setting value based on the setting key.
       */
      public String get(String key) {
         return map.get(key);
      }

      /**
       * Sets a setting with the provided setting key and value.
       *
       * @param key
       *           The setting key
       * @param value
       *           The setting value
       * @return The builder
       */
      public Builder put(String key, String value) {
         map.put(key, value);
         return this;
      }

      /**
       * Sets the setting with the provided setting key and the boolean value.
       *
       * @param setting
       *           The setting key
       * @param value
       *           The boolean value
       * @return The builder
       */
      public Builder put(String setting, boolean value) {
         put(setting, String.valueOf(value));
         return this;
      }

      /**
       * Sets the setting with the provided setting key and the int value.
       *
       * @param setting
       *           The setting key
       * @param value
       *           The int value
       * @return The builder
       */
      public Builder put(String setting, int value) {
         put(setting, String.valueOf(value));
         return this;
      }

      /**
       * Sets the setting with the provided setting key and the long value.
       *
       * @param setting
       *           The setting key
       * @param value
       *           The long value
       * @return The builder
       */
      public Builder put(String setting, long value) {
         put(setting, String.valueOf(value));
         return this;
      }

      /**
       * Sets the setting with the provided setting key and the float value.
       *
       * @param setting
       *           The setting key
       * @param value
       *           The float value
       * @return The builder
       */
      public Builder put(String setting, float value) {
         put(setting, String.valueOf(value));
         return this;
      }

      /**
       * Sets the setting with the provided setting key and the double value.
       *
       * @param setting
       *           The setting key
       * @param value
       *           The double value
       * @return The builder
       */
      public Builder put(String setting, double value) {
         put(setting, String.valueOf(value));
         return this;
      }

      /**
       * Sets all the provided settings.
       */
      public Builder put(Settings settings) {
         map.putAll(settings.getAsMap());
         return this;
      }

      /**
       * Sets all the provided settings.
       */
      public Builder put(Map<String, String> settings) {
         map.putAll(settings);
         return this;
      }

      /**
       * Sets all the provided settings.
       */
      @SuppressWarnings("rawtypes")
      public Builder put(Properties properties) {
         for(Map.Entry entry : properties.entrySet()) {
            map.put((String) entry.getKey(), (String) entry.getValue());
         }

         return this;
      }

      /**
       * Builds a {@link Settings} (underlying uses {@link Settings}) based on
       * everything set on this builder.
       */
      public Settings build() {
         return new Settings(Collections.unmodifiableMap(map));
      }
   }
}