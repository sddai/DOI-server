/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.logging.business;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * File Handler to log business information.
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class FileHandlerApplication extends FileHandler {
    
    /**
     * File handler for applications.
     * @throws IOException
     * @throws SecurityException
     */
    public FileHandlerApplication() throws IOException, SecurityException {
    super();
  }    
    
}