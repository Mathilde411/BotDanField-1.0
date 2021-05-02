package fr.bastoup.BotDanField.features.commands.custom;

public class CustomCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustomCommandException( String message ) {
        super( message );
    }

    public CustomCommandException( String message, Throwable cause ) {
        super( message, cause );
    }

    public CustomCommandException( Throwable cause ) {
        super( cause );
    }

}
