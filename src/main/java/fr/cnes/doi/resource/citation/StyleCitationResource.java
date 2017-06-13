/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.resource.citation;

import java.util.List;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * The supported styles for citation.
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class StyleCitationResource extends BaseCitationResource {

    /**
     * Init.
     * @throws ResourceException 
     */
    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        final StringBuilder description = new StringBuilder();
        description.append("Selects a style");
        description.append("A \"style\" can be chosen from the list of style "
                + "names found in the CSL style repository.");
        description.append("Many styles are supported, including common styles "
                + "such as apa and harvard3.");
        setDescription(description.toString());        
    }
                           
    /**
     * Returns the styles for a citation.
     * @return the possibles styles for a citation
     */
    @Get
    public List<String> getStyles() {
        return this.app.getClient().getStyles();
    }  
          
    
    /**
     * Describes the Get Method.
     * @param info Wadl description
     */
    @Override
    protected final void describeGet(final MethodInfo info) {
        info.setName(Method.GET);
        info.setDocumentation("Retrieves the list of supported styles"); 
        addResponseDocToMethod(info, createResponseDoc(Status.SUCCESS_OK, "Operation successful", listRepresentation("Style representation", "A list of style, one per row")));        
        addResponseDocToMethod(info, createResponseDoc(Status.SERVER_ERROR_INTERNAL, "server internal error, try later and if problem persists please contact us"));
    }      
}
