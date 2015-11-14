package core;
/*
Java Firewall Simulator (jFirewallSim)

Copyright (c) 2004, jFirewallSim development team All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

	- Redistributions of source code must retain the above copyright notice, this list
	  of conditions and the following disclaimer.
	- Redistributions in binary form must reproduce the above copyright notice, this list
	  of conditions and the following disclaimer in the documentation and/or other
	  materials provided with the distribution.
	- Neither the name of the Canberra Institute of Technology nor the names of its
	  contributors may be used to endorse or promote products derived from this software
	  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/



/**
 * A set of attributes encapsulated in an Object that is sent to the LayerInfoHandler
 * by Simulation Objects such as Protocols, NetworkInterfaces, Links.
 *
 * <P>Each LayerInfo object can be considered to be a <B>snapshot</B> of a piece of data
 * such as a Packet or ApplicationData message at a particular Layer of the OSI or other
 * networking model.</P>
 *
 * <P>For example, say if the NetworkInterface sends or receives some data.
 * In the NetworkInterface send and receive methods, we instantiate a LayerInfo object, set as many attributes
 * as we can (we want to provide the GUI with all the information it needs). Then we send that
 * LayerInfo object to the LayerInfoHandler. The NetworkInterface has an attribute OSI_LAYER which
 * we use to specify which layer of the OSI model that the LayerInfo relates to. </P>
 *
 * <P>Aside from the Simulation this class is the only other class that should be used by the GUI. You will
 * need to use this class when calling the method getNextLayer() on the Simulation class.</P>
 *
 * <P>This class should be modified by adding Attributes when more Layer specific information is required.</P>
 *
 * <P>See the LayerInfo.java class file for information on each of the fields contained.</P>
 *
 * @author tristan_veness
 * @version 13 June 2004
 * @version v0.10
 */

public class LayerInfo {
     /** A string containing the getClass().getName() of the Object sending the LayerInfo */
     private String objectType;
     /** A string containing the object's name property if it has a name */
     private String objectName;
     /** The type of the data, eg. UDP Packet, Ethernet Frame, ApplicationData Message, etc. */
     private String dataType;
     /** A String containing the data in the Packet or ApplicationData */
     private String data;
     /** The layer of the OSI model that the Object is considered part of */
     private String layer;
     /** The desciption of what is happening at this phase of the Process */
     private String description;
         
     /**
	* Instantiate a LayerInfo object and set the objectType attribute
	* @author tristan_veness
 	* @param objectType The getClass().getName() of the object sending the LayerInfo.
	* @version v0.10
	**/
     public LayerInfo(String objectType) {
          this.objectType = objectType;

     }
     /**
	* Instantiate a LayerInfo object and set the objectType and objectName attributes
	* @author tristan_veness
 	* @param objectType - The getClass().getName() of the object sending the LayerInfo.
	* @param objectName - The getName() of the object sending the LayerInfo.
	* @version v0.10
	**/
     public LayerInfo(String objectType, String objectName) {
          this.objectType = objectType;
          this.objectName = objectName;
     }
     
     public LayerInfo(String objectType, String objectName, String dataType, String layer, String description) {
          this.objectType = objectType;
          this.objectName = objectName;
          this.layer = layer;
          this.description = description;
          this.dataType = dataType;
     }
     
     /**
     * Sets the objectType attribute of the LayerInfo
     * @author tristan_veness
 	 * @param s - The getClass().getName() of the object sending the LayerInfo.
     * @version v0.10
     **/
     public void setObjectType(String s) {
          objectType = s;
     }

     /**
     * Gets the objectType attribute of the LayerInfo
     * @author tristan_veness
     * @version v0.10
     **/
     public String getObjectType() {
          return objectType;
     }

     /**
     * Sets the objectName attribute of the LayerInfo
     * @author tristan_veness
	 * @param s  - The getName() of the object sending the LayerInfo.
     * @version v0.10
     **/
     public void setObjectName(String s) {
          objectName = s;
     }

     /**
     * Gets the objectName attribute of the LayerInfo
     * @author tristan_veness
     * @return objectName
     * @version v0.10
     */
     public String getObjectName() {
          return objectName;
     }
     
     /**
     * Sets the dataType attribute of the LayerInfo
     * @author tristan_veness
	 * @param s  - The type of data the object sent or manipulated, eg. "UDP Datagram", "Ethernet Frame", etc.
     * @version v0.10
     **/
     public void setDataType(String s) {
          dataType = s;
     }

     /**
     * Gets the dataType attribute of the LayerInfo
     * @author tristan_veness
     * @version v0.10
     **/
     public String getDataType() {
          return dataType;
     }   

     /**
     * Sets the data attribute of the LayerInfo
     * @author tristan_veness
	 * @param s - The data of the Packet sent or manipulated by the object sending the LayerInfo.
     * @version v0.10
     **/
     public void setData(String s) {
          data = s;
     }

     /**
     * Gets the data attribute of the LayerInfo
     * @author tristan_veness
     * @version 0.10
     **/
     public String getData() {
          return data;
     }

     /**
     * Sets the osiLayer attribute of the LayerInfo
     * @author tristan_veness
	 * @param osiLayer - The OSI Layer that the object sending the LayerInfo is considered a part of.
     * @version v0.10
     **/
     public void setLayer(String osiLayer) {
          this.layer = osiLayer;
     }

     /**
     * Gets the osiLayer attribute of the LayerInfo
     * @author tristan_veness
     * @version v0.10
     */
     public String getLayer() {
          return layer;
     }
     
     /**
      * Sets the Description attribute of the LayerInfo
      * @author tristan_veness
  	  * @param Description  -The OSI Layer that the object sending the LayerInfo is considered a part of.
      * @version v0.10
      **/
      public void setDescription(String inDescription) {
           this.description = inDescription;
      }

      /**
      * Gets the Description attribute of the LayerInfo
      * @author tristan_veness
      * @version v0.10
      **/
      public String getDescription() {
           return description;
      }
      
     /**
     * Displays all of the attributes of this LayerInfo Object via System.out.println calls
     * This method is useful for a Command Line Interface
     * @author tristan_veness
     * @return output
     * @version v0.10
     */
     public String[] getRecordedInfo() {        
     	String output[] = {objectType,objectName,dataType,data,layer,description};
  		return output;  		
     }
}
