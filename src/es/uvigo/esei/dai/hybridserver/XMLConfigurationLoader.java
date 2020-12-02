/**
 *  HybridServer
 *  Copyright (C) 2020 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.parser.ConfigurationContentHandler;
import es.uvigo.esei.dai.hybridserver.parser.ParsingUtils;

public class XMLConfigurationLoader {

	public Configuration load(File xmlFile) throws Exception {

		ConfigurationContentHandler contendHandler = new ConfigurationContentHandler();
		
		try {
			
			//Funciona con validacion Externa pero no con Interna
			ParsingUtils.parseAndValidateWithExternalXSD(xmlFile,"configuration.xsd", contendHandler);
			
			//parseAndValidateWithInternalXSD(xmlFile, contendHandler);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new Exception("Configuration parsing error", e);
		}
		
		return contendHandler.getConfiguration();
	}

}
