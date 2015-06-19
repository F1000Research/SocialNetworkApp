package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.cytoscape.work.TaskMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ??
 * 
 * @author Victor Kofia
 */
// TODO: Write class description
public class PubMedXmlParser extends DefaultHandler {

    /**
     * XML Parsing variables. Used to temporarily store data.
     */
    boolean isAuthor = false;
    boolean isFirstName = false;
    boolean isInstitution = false;
    boolean isJournal = false;
    boolean isLastName = false;
    boolean isMiddleInitial = false;
    boolean isPMID = false;
    boolean isPubDate = false;
    boolean isTimesCited = false;
    boolean isTitle = false;

    /**
     * The author of a specific publication. This variable is globally
     * referenced to allow for multiple additions in to a publication
     */
    private Author author = null;
    private StringBuilder lastName = null;
    private StringBuilder firstName = null;
    private StringBuilder middleInitials = null;
    private StringBuilder institution = null;
    /**
     * A publication's journal
     */
    private StringBuilder journal = null;
    /**
     * A list containing all authors found in a particular publication
     */
    private ArrayList<Author> pubAuthorList = new ArrayList<Author>();
    /**
     * A publication's date
     */
    private StringBuilder pubDate = null;
    /**
     * A list containing all the results that search session has yielded
     */
    private ArrayList<Publication> pubList = new ArrayList<Publication>();
    /**
     * A publication's unique identifier
     */
    private StringBuilder pmid = null;

    /**
     * A publication's total number of citations
     */
    private StringBuilder timesCited = null;
    /**
     * A publication's title
     */
    private StringBuilder title = null;
    
    /**
     * Progress bar variables
     */
    private int currentSteps = 0;
    private int totalSteps = 0;
    private double progress = 0.0;
    
    /**
     * Create a new PubMed xml parser
     * 
     * @param File xml
     */
    public PubMedXmlParser(File xml, TaskMonitor taskMonitor) {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(xml, this);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
            // TODO: add log message
        } catch (SAXException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Encountered temporary server issues. Please " + "try again some other time.");
            // TODO: add log message
        } catch (IOException e) {
            e.printStackTrace();
            CytoscapeUtilities.notifyUser("Unable to connect to PubMed. Please check your " + "internet connection.");
            // TODO: add log message
        }
    }
    
    // Collect tag contents (if applicable)
    @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.isPubDate) {
            this.pubDate.append(ch, start, length);
            this.isPubDate = false;
        }
        if (this.isAuthor) {
            this.author = new Author(new String(ch, start, length), Category.PUBMED);
            this.isAuthor = false;
        }
        if (this.isInstitution) {
            this.institution.append(ch, start, length);
            this.isInstitution = false;
        }
        if (this.isFirstName) {
            this.firstName.append(ch, start, length);
            this.isFirstName = false;
        }
        if (this.isLastName) {
            this.lastName.append(ch, start, length);
            this.isLastName = false;
        }
        if (this.isMiddleInitial) {
            this.middleInitials.append(ch, start, length);
            this.isMiddleInitial = false;
        }
        if (this.isJournal) {
            this.journal.append(ch, start, length);
            this.isJournal = false;
        }
        if (this.isTitle) {
            this.title.append(ch, start, length);
            this.isTitle = false;
        }
        if (this.isTimesCited) {
            this.timesCited.append(ch, start, length);
            this.isTimesCited = false;
        }
        if (this.isPMID) {
            this.pmid.append(ch, start, length);
            this.isPMID = false;
        }
    }

    /**
     * Returns true iff attributes contains the specified text
     *
     * @param Attribute attributes
     * @param String text
     * 
     * @return Boolean bool
     */
    public boolean contains(Attributes attributes, String text) {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getValue(i).equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    // Create new publication and add it to overall publist
    @Override
    /* (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("PubmedArticle")) {
            Publication pub = new Publication(this.title != null ? this.title.toString() : null, 
            		                          this.pubDate != null ? this.pubDate.toString() : null, 
            		                          this.journal != null ? this.journal.toString() : null, 
            		                          this.timesCited != null ? this.timesCited.toString() : null, 
            		                          null, 
            		                          this.pubAuthorList);
            pub.setPMID(this.pmid != null ? this.pmid.toString() : null);
            this.pubList.add(pub);
            this.pubAuthorList.clear();
        }
        if (qName.equals("Author")) {
            // add the firstname,lastname, initial to the author
            this.author.setFirstName(this.firstName != null ? this.firstName.toString() : null);
            this.author.setLastName(this.lastName != null ? this.lastName.toString() : null);
            this.author.setMiddleInitial(this.middleInitials != null ? this.middleInitials.toString() : null);
            this.author.setFirstInitial(this.firstName.substring(0, 1));
            this.author.setLabel(this.author.getFirstInitial() + " " + this.author.getLastName());
            this.author.setInstitution(this.institution != null ? this.institution.toString() : null);

            // Add author to publication author list
            if (!this.pubAuthorList.contains(this.author)) {
                this.pubAuthorList.add(this.author);
            }

        }
    }

    /**
     * Get publication list
     * 
     * @return ArrayList pubList
     */
    public ArrayList<Publication> getPubList() {
        return this.pubList;
    }

    // Reset variable contents
    @Override
    /* (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("Author")) {
            this.isAuthor = true;
        }
        if (qName.equals("Affiliation")) {
            this.isInstitution = true;
            this.institution = new StringBuilder();
        }
        if (qName.equals("LastName")) {
            this.isLastName = true;
            this.lastName = new StringBuilder();
        }
        if (qName.equals("ForeName")) {
            this.isFirstName = true;
            this.firstName = new StringBuilder();
        }
        if (qName.equals("Initials")) {
            this.isMiddleInitial = true;
            this.middleInitials = new StringBuilder();
        }
        if (qName.equals("Title")) {
            this.isJournal = true;
            this.journal = new StringBuilder();
        }
        if (qName.equals("PubDate")) {
            this.isPubDate = true;
            this.pubDate = new StringBuilder();
        }
        if (qName.equals("ArticleTitle")) {
            this.isTitle = true;
            this.title = new StringBuilder();
        }
        if (qName.equals("PmcRefCount")) {
            this.isTimesCited = true;
            this.timesCited = new StringBuilder();
        }
        if (qName.equals("ArticleId") && contains(attributes, "pubmed")) {
            this.isPMID = true;
            this.pmid = new StringBuilder();
        }
    }
    
    /**
     * Set progress monitor
     *
     * @param TaskMonitor taskMonitor
     * @param String taskName
     * @param int totalSteps
     */
    private void setProgressMonitor(TaskMonitor taskMonitor, String taskName, int totalSteps) {
        taskMonitor.setTitle(taskName);
        taskMonitor.setProgress(0.0);
        this.currentSteps = 0;
        this.totalSteps = totalSteps;
    }

    /**
     * Update progress monitor
     *
     * @param int currentSteps
     */
    private void updateProgress(TaskMonitor taskMonitor) {
        this.currentSteps += 1;
        this.progress = (double) this.currentSteps / this.totalSteps;
        taskMonitor.setStatusMessage("Complete: " + toPercent(this.progress));
        taskMonitor.setProgress(this.progress);
    }
    
    /**
     * Return progress as a percentage
     *
     * @param Double progress
     * @return String percentage
     */
    private String toPercent(double progress) {
        progress = progress * 100;
        DecimalFormat df = new DecimalFormat("00");
        return df.format(progress) + "%";
    }

}
