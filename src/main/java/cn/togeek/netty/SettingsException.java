package cn.togeek.netty;

/**
 * A generic failure to handle settings.
 */
public class SettingsException extends Exception {
   private static final long serialVersionUID = 1L;

   public SettingsException(String message) {
      super(message);
   }

   public SettingsException(String message, Throwable cause) {
      super(message, cause);
   }
}