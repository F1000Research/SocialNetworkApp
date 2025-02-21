/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates,
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

package org.baderlab.csapps.socialnetwork.tasks;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.visualstyles.VisualStyles;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * For applying visual styles
 *
 * @author Victor Kofia
 */
public class ApplyVisualStyleTask extends AbstractTask {

    private VisualMappingManager vmmServiceRef;
    private VisualStyleFactory visualStyleFactoryServiceRef;
    private VisualMappingFunctionFactory passthroughMappingFactoryServiceRef;
    private VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    private VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    private TaskMonitor taskMonitor;

    /**
     * Main app manager
     */
    SocialNetworkAppManager appManager = null;

    /**
     * Create new apply visual style task
     *
     * @param visualStyleFactoryServiceRef
     * @param vmmServiceRef
     * @param passthroughMappingFactoryServiceRef
     * @param continuousMappingFactoryServiceRef
     * @param discreteMappingFactoryServiceRef
     */
    public ApplyVisualStyleTask(VisualStyleFactory visualStyleFactoryServiceRef, VisualMappingManager vmmServiceRef,
            VisualMappingFunctionFactory passthroughMappingFactoryServiceRef, VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef, SocialNetworkAppManager appManager) {
        this.vmmServiceRef = vmmServiceRef;
        this.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
        this.passthroughMappingFactoryServiceRef = passthroughMappingFactoryServiceRef;
        this.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
        this.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
        this.appManager = appManager;
    }

    /**
     * Add a label to each edge
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle addEdgeLabels(VisualStyle visualStyle) {
        // Get column name
        String colName = (String) this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.EDGE_LABEL)[0];
        // Assign edge label filter to column
        PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>) this.passthroughMappingFactoryServiceRef
                .createVisualMappingFunction(colName, Integer.class, BasicVisualLexicon.EDGE_LABEL);
        visualStyle.addVisualMappingFunction(mapping);
        return visualStyle;
    }

    /**
     * Add a label to each node
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle addNodeLabels(VisualStyle visualStyle) {
        // Get column name
        String colName = (String) this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.NODE_LABEL)[0];
        // Assign node label filter to column
        PassthroughMapping<Integer, ?> mapping = (PassthroughMapping<Integer, ?>) this.passthroughMappingFactoryServiceRef
                .createVisualMappingFunction(colName, Integer.class, BasicVisualLexicon.NODE_LABEL);
        visualStyle.addVisualMappingFunction(mapping);
        return visualStyle;
    }

    /**
     * Create InCites visual style
     *
     * @return VisualStyle incitesVisualStyle
     */
    private VisualStyle createIncitesVisualStyle() {
        VisualStyle incitesVisualStyle = this.visualStyleFactoryServiceRef.createVisualStyle("InCites");
        addNodeLabels(incitesVisualStyle);
        modifyEdgeWidth(incitesVisualStyle);
        modifyNodeSize(incitesVisualStyle);
        modifyEdgeOpacity(incitesVisualStyle);
        modifyNodeColor(incitesVisualStyle);
        modifyNodeBorder(incitesVisualStyle);
        modifyNodeShape(incitesVisualStyle);
        return incitesVisualStyle;
    }

    /**
     * Create PubMed visual style
     *
     * @return VisualStyle pubmedVisualStyle
     */
    private VisualStyle createPubmedVisualStyle() {
        VisualStyle pubmedVisualStyle = this.visualStyleFactoryServiceRef.createVisualStyle("PubMed");
        addNodeLabels(pubmedVisualStyle);
        modifyNodeSize(pubmedVisualStyle);
        modifyEdgeWidth(pubmedVisualStyle);
        modifyEdgeOpacity(pubmedVisualStyle);
        return pubmedVisualStyle;
    }

    /**
     * Create Scopus visual style
     *
     * @return VisualStyle scopusVisualStyle
     */
    private VisualStyle createScopusVisualStyle() {
        VisualStyle scopusVisualStyle = this.visualStyleFactoryServiceRef.createVisualStyle("Scopus");
        addNodeLabels(scopusVisualStyle);
        modifyNodeSize(scopusVisualStyle);
        modifyEdgeWidth(scopusVisualStyle);
        modifyEdgeOpacity(scopusVisualStyle);
        return scopusVisualStyle;
    }

    /**
     * Get Default visual style
     *
     * @return VisualStyle defaultVisualStyle
     */
    private VisualStyle getDefaultVisualStyle() {
        VisualStyle defaultVisualStyle = this.getVisualStyle("Default");
        if (defaultVisualStyle == null) {
            defaultVisualStyle = this.visualStyleFactoryServiceRef.createVisualStyle("Default");
            this.vmmServiceRef.addVisualStyle(defaultVisualStyle);
        }
        return defaultVisualStyle;
    }

    /**
     * Get InCites visual style
     *
     * @return VisualStyle incitesVisualStyle
     */
    private VisualStyle getIncitesStyle() {
        VisualStyle incitesVisualStyle = this.getVisualStyle("InCites");
        if (incitesVisualStyle == null) {
            incitesVisualStyle = this.createIncitesVisualStyle();
            this.vmmServiceRef.addVisualStyle(incitesVisualStyle);
        }
        return incitesVisualStyle;
    }

    /**
     * Get PubMed visual style
     *
     * @return VisualStyle pubmedVisualStyle
     */
    private VisualStyle getPubmedVisualStyle() {
        VisualStyle pubmedVisualStyle = this.getVisualStyle("PubMed");
        if (pubmedVisualStyle == null) {
            pubmedVisualStyle = this.createPubmedVisualStyle();
            this.vmmServiceRef.addVisualStyle(pubmedVisualStyle);
        }
        return pubmedVisualStyle;
    }

    /**
     * Get Scopus visual style
     *
     * @return VisualStyle scopusVisualStyle
     */
    private VisualStyle getScopusVisualStyle() {
        VisualStyle scopusVisualStyle = this.getVisualStyle("Scopus");
        if (scopusVisualStyle == null) {
            scopusVisualStyle = this.createScopusVisualStyle();
            this.vmmServiceRef.addVisualStyle(scopusVisualStyle);
        }
        return scopusVisualStyle;
    }

    /**
     * Get task monitor
     *
     * @return TaskMonitor taskMonitor
     */
    private TaskMonitor getTaskMonitor() {
        return this.taskMonitor;
    }

    /**
     * Return the visual style with the specified name in the set of all visual
     * styles. null is returned if no visual style is found.
     * 
     * @param String name
     * 
     * @return VisualStyle visualStyle
     */
    private VisualStyle getVisualStyle(String name) {
        Iterator<VisualStyle> it = this.vmmServiceRef.getAllVisualStyles().iterator();
        VisualStyle visualStyle = null;
        while (it.hasNext()) {
            visualStyle = it.next();
            if (visualStyle.getTitle().equalsIgnoreCase(name)) {
                break;
            }
            visualStyle = null;
        }
        return visualStyle;
    }

    /**
     * Modify edge opacity
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle modifyEdgeOpacity(VisualStyle visualStyle) {
        // Get column name
        String colName = (String) this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.EDGE_TRANSPARENCY)[0];
        ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactoryServiceRef.createVisualMappingFunction(
                colName, Integer.class, BasicVisualLexicon.EDGE_TRANSPARENCY);
        Object[] attributes = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.EDGE_WIDTH);
        // BRVs are used to set limits on edge transparency
        // (min edge transparency = 100; max edge transparency = 300)
        BoundaryRangeValues bv0 = new BoundaryRangeValues(100.0, 100.0, 100.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(300.0, 300.0, 300.0);
        // Adjust handle position
        Integer min = (Integer) attributes[1];
        Integer max = (Integer) attributes[2];
        mapping.addPoint(1, bv0);
        mapping.addPoint(max / 2, bv1);
        visualStyle.addVisualMappingFunction(mapping);
        return visualStyle;
    }

    /**
     * Modify edge width
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle modifyEdgeWidth(VisualStyle visualStyle) {
        Object[] attributes = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.EDGE_WIDTH);
        // Get column name
        String colName = (String) attributes[0];
        ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactoryServiceRef.createVisualMappingFunction(
                colName, Integer.class, BasicVisualLexicon.EDGE_WIDTH);
        // BRVs are used to set limits on edge width (max edge width = 10; min
        // edge width = 1)
        BoundaryRangeValues bv0 = new BoundaryRangeValues(1.0, 1.0, 1.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        // Adjust handle position
        int min = (Integer) attributes[1];
        int max = (Integer) attributes[2];
        mapping.addPoint(min, bv0);
        mapping.addPoint(max, bv1);
        visualStyle.addVisualMappingFunction(mapping);
        return visualStyle;
    }

    /**
     * Modify node border width and font size for faculty members specific to
     * incites data.
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle modifyNodeBorder(VisualStyle visualStyle) {
        DiscreteMapping mapping = null;
        Object[] tempVar = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.NODE_BORDER_PAINT);
        Map<String, HashMap<String, Color>> colorMap = (Map<String, HashMap<String, Color>>) tempVar[0];
        for (Entry<String, HashMap<String, Color>> colorMapEntry : colorMap.entrySet()) {
            String colName = colorMapEntry.getKey();
            mapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colName, String.class,
                    BasicVisualLexicon.NODE_BORDER_PAINT);
            for (Entry<String, Color> attrMapEntry : colorMapEntry.getValue().entrySet()) {
                mapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(mapping);
        }

        tempVar = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.NODE_BORDER_WIDTH);
        Map<String, HashMap<String, Double>> sizeMap = (Map<String, HashMap<String, Double>>) tempVar[0];
        for (Entry<String, HashMap<String, Double>> sizeMapEntry : sizeMap.entrySet()) {
            String colName = sizeMapEntry.getKey();
            mapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colName, String.class,
                    BasicVisualLexicon.NODE_BORDER_WIDTH);
            for (Entry<String, Double> attrMapEntry : sizeMapEntry.getValue().entrySet()) {
                mapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(mapping);
        }
        // Map the Label font size
        tempVar = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.NODE_LABEL_FONT_FACE);
        Map<String, HashMap<String, Font>> fontMap = (Map<String, HashMap<String, Font>>) tempVar[0];
        for (Entry<String, HashMap<String, Font>> fontMapEntry : fontMap.entrySet()) {
            String colName = fontMapEntry.getKey();
            mapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colName, String.class,
                    BasicVisualLexicon.NODE_LABEL_FONT_FACE);
            for (Entry<String, Font> attrMapEntry : fontMapEntry.getValue().entrySet()) {
                mapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(mapping);
        }

        return visualStyle;
    }

    /**
     * Modify node color
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle modifyNodeColor(VisualStyle visualStyle) {

        // set the default value for node color
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, new Color(0, 204, 204));

        DiscreteMapping mapping = null;
        Object[] tempVar = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.NODE_FILL_COLOR);
        Map<String, HashMap<String, Color>> colorMap = (Map<String, HashMap<String, Color>>) tempVar[0];
        for (Entry<String, HashMap<String, Color>> colorMapEntry : colorMap.entrySet()) {
            String colName = colorMapEntry.getKey();
            mapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colName, String.class,
                    BasicVisualLexicon.NODE_FILL_COLOR);
            for (Entry<String, Color> attrMapEntry : colorMapEntry.getValue().entrySet()) {
                mapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(mapping);
        }
        return visualStyle;
    }

    /**
     * Modify node shape
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle modifyNodeShape(VisualStyle visualStyle) {
        DiscreteMapping mapping = null;
        Object[] tempVar = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.NODE_SHAPE);
        Map<String, HashMap<String, NodeShape>> nodeShapeMap = (Map<String, HashMap<String, NodeShape>>) tempVar[0];
        for (Entry<String, HashMap<String, NodeShape>> colorMapEntry : nodeShapeMap.entrySet()) {
            String colName = colorMapEntry.getKey();
            mapping = (DiscreteMapping) this.discreteMappingFactoryServiceRef.createVisualMappingFunction(colName, String.class,
                    BasicVisualLexicon.NODE_SHAPE);
            for (Entry<String, NodeShape> attrMapEntry : colorMapEntry.getValue().entrySet()) {
                mapping.putMapValue(attrMapEntry.getKey(), attrMapEntry.getValue());
            }
            visualStyle.addVisualMappingFunction(mapping);
        }
        return visualStyle;
    }

    /**
     * Modify node size
     *
     * @param VisualStyles visualStyle
     * @return VisualStyle visualStyle
     */
    private VisualStyle modifyNodeSize(VisualStyle visualStyle) {
        Object[] attributes = this.appManager.getCurrentlySelectedSocialNetwork().getVisualStyleMap().get(BasicVisualLexicon.NODE_SIZE);
        // Get column name
        String colName = (String) attributes[0];
        ContinuousMapping<Integer, ?> mapping = (ContinuousMapping<Integer, ?>) this.continuousMappingFactoryServiceRef.createVisualMappingFunction(
                colName, Integer.class, BasicVisualLexicon.NODE_SIZE);
        // BRVs are used to set limits on node size (max size = 50; min size =
        // 10)
        BoundaryRangeValues bv0 = new BoundaryRangeValues(10.0, 10.0, 10.0);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(50.0, 50.0, 50.0);
        // Adjust handle position
        int min = (Integer) attributes[1];
        int max = (Integer) attributes[2];
        mapping.addPoint(min, bv0);
        mapping.addPoint(max, bv1);
        visualStyle.addVisualMappingFunction(mapping);
        return visualStyle;
    }

    /**
     * Apply selected network view
     *
     * @param TaskMonitor taskMonitor
     */
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        this.setTaskMonitor(taskMonitor);
        Set<VisualStyle> visualStyles = this.vmmServiceRef.getAllVisualStyles();
        VisualStyle visualStyle = null;
        switch (this.appManager.getVisualStyleID()) {
            case Category.DEFAULT:
                visualStyle = this.getDefaultVisualStyle();
                break;
            case VisualStyles.INCITES_VISUAL_STYLE:
                this.getTaskMonitor().setTitle("Loading InCites Visual Style ... ");
                this.getTaskMonitor().setProgress(0.0);
                this.getTaskMonitor().setStatusMessage("");
                visualStyle = this.getIncitesStyle();
                break;
            case VisualStyles.PUBMED_VISUAL_STYLE:
                this.getTaskMonitor().setTitle("Loading PubMed Visual Style ... ");
                this.getTaskMonitor().setProgress(0.0);
                this.getTaskMonitor().setStatusMessage("");
                visualStyle = this.getPubmedVisualStyle();
                break;
            case VisualStyles.SCOPUS_VISUAL_STYLE:
                this.getTaskMonitor().setTitle("Loading Scopus Visual Style ... ");
                this.getTaskMonitor().setProgress(0.0);
                this.getTaskMonitor().setStatusMessage("");
                visualStyle = this.getScopusVisualStyle();
                break;
        }
        this.vmmServiceRef.setCurrentVisualStyle(visualStyle);
        return;
    }

    /**
     * Set task monitor
     *
     * @param TaskMonitor taskMonitor
     */
    private void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }

}
