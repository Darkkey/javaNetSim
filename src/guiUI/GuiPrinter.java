package guiUI;

import core.Node;

public class GuiPrinter extends ApplicationLayerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6715607641533125774L;

	public GuiPrinter(String inName, MainScreen inMainScreen) {

		super(inName, inMainScreen, "images/simulation/printer.png");

	}

	@Override
	public void addInterfaces(MainScreen parent, Node node) {

		node.addNetworkInterface(core.NetworkInterface
				.getIntName(core.NetworkInterface.Ethernet10T)
				+ "0", core.NetworkInterface.Ethernet10T, true);

	}
}
