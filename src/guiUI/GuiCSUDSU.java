package guiUI;

import core.Node;

public class GuiCSUDSU extends DataLinkLayerDevice {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5724485576670507704L;

	/**

	 * @param inName  The name of the switch

	 * @param inMainscreen	The JFrame that the Switch will be created on

	 */

	public GuiCSUDSU(String inName, MainScreen inMainScreen) {

		super(inName, inMainScreen,"images/simulation/csudsu2.png");	

	}
	
	@Override
	public void addInterfaces(MainScreen parent, Node node){
        ((core.CSUDSU)node).addSerialInterface();
        ((core.CSUDSU)node).addWANInterface();
	}

}

