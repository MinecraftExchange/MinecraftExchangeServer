package org.mcexchange.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mcexchange.api.Connection;
import org.mcexchange.api.MessagePacket;
import org.mcexchange.api.plugin.ConnectionPlugin;
import org.mcexchange.api.plugin.ExchangePlugin;
import org.mcexchange.api.plugin.ExchangePluginLoader;
import org.mcexchange.api.plugin.PluginPlugin;

public class ExchangeServer implements Runnable, ConnectionPlugin {
	//------------------------------//
	//    STATIC/SINGLETON STUFF    //
	//------------------------------//
	
	//the one instance of ExchangeServer
	private static final ExchangeServer es = new ExchangeServer();
	
	public static final int DEFAULT_PORT = 62924;

	/**
	 * Gets the singleton instance of ExchangeServer.
	 */
	public static ExchangeServer getInstance() {
		return es;
	}

	//------------------------------//
	//       NON-STATIC STUFF       //
	//------------------------------//
	
	private ServerProperties sp;
	private boolean listening = false;
	private int port = DEFAULT_PORT;
	private ServerSocketChannel socket = null;
	private final Map<Connection,Thread> connections = new HashMap<Connection,Thread>();
	
	//private constructor ensuring that this is the only instance.
	private ExchangeServer() {
	}
	
	/**
	 * Loads the properties file/creates it. The given command-line arguments
	 * will be parsed for options. All properties can be specified in the c-l,
	 * they won't persist beyond that execution.
	 * @param override The properties to override.
	 */
	public void loadProperties(String[] override) {
		sp = new ServerProperties(override);
	}
	
	public void loadPlugins() {
		ExchangePluginLoader epl = new ExchangePluginLoader();
		List<ExchangePlugin> plugins =  epl.loadAllPlugins();
		plugins.add(this);//we need to recieve notifications of when a connection closes :)
		for(ExchangePlugin ep : plugins) {
			Class<?>[] interfaces = ep.getClass().getInterfaces();
			for(Class<?> i : interfaces) {
				if(!ExchangePlugin.class.isAssignableFrom(i)) continue;
				if(ExchangePlugin.plugins.containsKey(i)) {
					ExchangePlugin.plugins.get(i).add(ep);
				} else {
					List<ExchangePlugin> lep = new ArrayList<ExchangePlugin>();
					lep.add(ep);
					Class<? extends ExchangePlugin> clazz = i.asSubclass(ExchangePlugin.class);
					ExchangePlugin.plugins.put(clazz, lep);
				}
			}
		}
		//register PluginPlugins
		List<ExchangePlugin> pluginplugins = ExchangePlugin.plugins.get(PluginPlugin.class);
		if(pluginplugins==null) return;
		for(ExchangePlugin ep : pluginplugins) {
			PluginPlugin pp = ((PluginPlugin) ep);
			Class<? extends ExchangePlugin>[] clazz  = pp.getPlugins();
			if(clazz == null) continue;
			for(Class<? extends ExchangePlugin> cp : clazz) {
				List<ExchangePlugin> cps = ExchangePlugin.plugins.get(cp);
				if(cps == null) continue;
				for(ExchangePlugin exp : cps) pp.registerPlugin(exp);
			}
		}
	}
	
	/**
	 * Get the server properties object.
	 */
	public ServerProperties getProperties() {
		return sp;
	}
	
	/**
	 * Obtains a reference to the threads the client connections are running
	 * in. It is important to note that this list is NOT copied so any changes
	 * here will affect the whole server.
	 */
	public Collection<Thread> getThreads() {
		return connections.values();
	}
	
	/**
	 * Obtains a reference to the client connections. It is important to note
	 * that this list is NOT copied so any changes here will affect the whole
	 * Server.
	 */
	public Set<Connection> getConnections() {
		return connections.keySet();
	}
	
	/**
	 * Binds the server to the specified port.
	 * @param bindPort
	 */
	public void bind(int bindPort) {
		try {
		    socket = ServerSocketChannel.open();
		    socket.socket().bind(new InetSocketAddress(bindPort));
			System.out.println("Successfully bound to port: "+bindPort+".");
		} catch (IOException e) {
			System.err.println("Could not bind to port: "+bindPort+".");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Makes server listen for incoming connections from clients.
	 */
	public void listen() {
		listening = true;
		System.out.println("Listening for client connections.");
		while(listening) {
			try {
				Connection cc = new Connection(socket.accept());
				Thread t = new Thread(cc);
				t.start();
				connections.put(cc,t);
				System.out.println("Succesfully connected to client " + cc);
				for(ExchangePlugin p : ExchangePlugin.plugins.get(ConnectionPlugin.class)) ((ConnectionPlugin) p).onConnectionEstablished(cc);
			} catch (IOException e) {
				System.err.println("Could not connect to client.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Where all of the not-so-exciting action occurs!
	 */
	public void run() {
		try {
			port = Integer.parseInt(sp.getProperty("port"));
		} catch(NumberFormatException e) {
			//ignore...
		}
		bind(port);
		listen();
	}
	
	/**
	 * Send a message to every client.
	 */
	public void broadcastMessage(String message) {
		for(Connection c : getConnections()) {
			MessagePacket mp = c.packets.getMessage();
			mp.setMessage(message);
			c.sendPacket(mp);
		}
	}

	/**
	 * This class has to implement ConnectionPlugin so that it knows when
	 * a connection terminates. This specific method is unused seeing as
	 * this class establishes the connections.
	 */
	public void onConnectionEstablished(Connection c) { }
	
	/**
	 * Removes a closed connection from the connection list.
	 */
	public void onConnectionEnd(Connection c) {
		connections.remove(c);
	}
}
