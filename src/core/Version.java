/*
Java Network Simulator (jNetSim)

Copyright (c) 2005 - 2006, Ice Team;  All rights reserved.
Copyright (c) 2004, jFirewallSim development team;  All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

	- Redistributions of source code must retain the above copyright notice, this list
	  of conditions and the following disclaimer.
	- Redistributions in binary form must reproduce the above copyright notice, this list
	  of conditions and the following disclaimer in the documentation and/or other
	  materials provided with the distribution.
	- Neither the name of the Ice Team nor the names of its
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


package core;

/**
 * This Class hold a array of Strings listing the stakeholders of the 
 * project, their role and semester they worked on the project
 * @author  luke_hamilton
 * @author  Key
 * @since	Sep 23, 2004
 * @version v0.31
 */
public class Version {
	public final static String CORE_VERSION = "v0.42";		//version of the simulation core
	public final static String YEARS = "2005 - 2009";
        
	public static final String TEAM_MEMBERS[] = { 
                        "http://sf.net/projects/javanetsim","release date: 26 Sep 2009", "",
                        "fork of jFirewallSim project (http://sf.net/projects/jfirewallsim/)",                        
                        "from 03 Nov 2005","",
                        "Maintainer",
                        "Alexander Bolshev [Key]",  
                        "","Contributors & Other Developers:",
                        "Konstantin Karpov [QweR]", "\t Developer / Tester",                        
                        "Anatoly Chekh [achekh]", "\t OSPF, Testing & Bugfixing",
                        "Ilgar Alekperov [Gift]", "\t First version of TCP, Echo TCP, Export to HTML, Testing & Bugfixing",                        
                        "Igor Goroshkov", "\t RIP protocol core & GUI",  
                        "Oleg Listov [listov]", "\t DNS",
                        "", "If you found a bug, please post it to: http://sf.net/tracker/?atid=784685&group_id=152576",                        
                        
                        //"", "Get information about latest releases at rss:",  "http://sf.net/export/rss2_projfiles.php?group_id=152576", "and about latest news at rss:", "http://sf.net/export/rss2_projnews.php?group_id=152576&rss_fulltext=1"
                         /*,                                                                         "Semester 2, 2004", "",
												  "Angela Brown    " , 	"Project Manager / Documentation Manager / Developer        	    ",
												  "Bevan Calliess  " , 	"Disaster Recovery Manager / Assistant Project Manager / Developer  ", 
												  "Luke Hamilton   " ,	"Documentation Manager / Project Manager / Developer            	",	
												  "Michael Reith   " , 	"Testing Manager / Assistant Release Manager / Developer            ",
												  "Rob Hulford     " ,	"Release Manager / Assistant Disaster Recovery Manager / Developer  \n",
												  "Semester 1, 2004", "",
												  "Tristan Veness  " , 	"Project Manager / Developer          				",
												  "James Nikolaidis" ,	"Developer                              			",*/
			};
}//EOF
