import java.io.Serializable;

public class PlayerInfo implements Serializable,Comparable<PlayerInfo>{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 2183473835610092014L;
	private String playerName;
	    private String host;
	    private int port;
	    private int podiumPosition; // Nueva propiedad para la posición en el podio

	    public PlayerInfo(String playerName, String host, int port) {
	        this.playerName = playerName;
	        this.host = host;
	        this.port = port;
	        // La posición en el podio se inicializará en 0 o algún otro valor predeterminado
	        this.podiumPosition = 0;
	    }

	    public PlayerInfo(String playerName2, String host2, int port2, int i) {
	    }

	    // Getter y setter para la nueva propiedad
	    public int getPodiumPosition() {
	        return podiumPosition;
	    }

	    public void setPodiumPosition(int podiumPosition) {
	        this.podiumPosition = podiumPosition;
	    }

	     public String getPlayerName() {
	        return playerName;
	    }

	    public void setPlayerName(String playerName) {
	        this.playerName = playerName;
	    }

	    public String getHost() {
	        return host;
	    }

	    public void setHost(String host) {
	        this.host = host;
	    }

	    public int getPort() {
	        return port;
	    }

	    public void setPort(int port) {
	        this.port = port;
	    }

	    // Implementación del método compareTo para la interfaz Comparable
	    @Override
	    public int compareTo(PlayerInfo other) {
	        return Integer.compare(this.podiumPosition, other.podiumPosition);
	    }
	    
}
