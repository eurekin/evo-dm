package EvolutionaryAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;

import data.DataLoader;
import java.io.Serializable;

/*
 * Singleton class
 */
public class Configuration implements Serializable {

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Configuration other = (Configuration) obj;
        if ((this.Comment == null) ? (other.Comment != null) : !this.Comment.equals(other.Comment)) {
            return false;
        }
        if ((this.FileComment == null) ? (other.FileComment != null) : !this.FileComment.equals(other.FileComment)) {
            return false;
        }
        if (this.Fsc != other.Fsc) {
            return false;
        }
        if (this.ECHO != other.ECHO) {
            return false;
        }
        if (this.PopSize != other.PopSize) {
            return false;
        }
        if (this.Selection != other.Selection) {
            return false;
        }
        if (this.Pcrossover != other.Pcrossover) {
            return false;
        }
        if (this.Pmutation != other.Pmutation) {
            return false;
        }
        if (this.StopGeneration != other.StopGeneration) {
            return false;
        }
        if (this.StopEval != other.StopEval) {
            return false;
        }
        if (this.MUTATION != other.MUTATION && (this.MUTATION == null || !this.MUTATION.equals(other.MUTATION))) {
            return false;
        }
        if (this.CROSSOVER != other.CROSSOVER && (this.CROSSOVER == null || !this.CROSSOVER.equals(other.CROSSOVER))) {
            return false;
        }
        if (this.CrossValidation != other.CrossValidation) {
            return false;
        }
        if (this.E_WEIGHT != other.E_WEIGHT) {
            return false;
        }
        if ((this.TestDataFileName == null) ? (other.TestDataFileName != null) : !this.TestDataFileName.equals(other.TestDataFileName)) {
            return false;
        }
        if ((this.TrainDataFileName == null) ? (other.TrainDataFileName != null) : !this.TrainDataFileName.equals(other.TrainDataFileName)) {
            return false;
        }
        if ((this.Image_Blob_Counts_Filename == null) ? (other.Image_Blob_Counts_Filename != null) : !this.Image_Blob_Counts_Filename.equals(other.Image_Blob_Counts_Filename)) {
            return false;
        }
        if ((this.Image_Blobs_Filename == null) ? (other.Image_Blobs_Filename != null) : !this.Image_Blobs_Filename.equals(other.Image_Blobs_Filename)) {
            return false;
        }
        if ((this.Image_Doc_Words_Filename == null) ? (other.Image_Doc_Words_Filename != null) : !this.Image_Doc_Words_Filename.equals(other.Image_Doc_Words_Filename)) {
            return false;
        }
        if ((this.Image_Words_Filename == null) ? (other.Image_Words_Filename != null) : !this.Image_Words_Filename.equals(other.Image_Words_Filename)) {
            return false;
        }
        if ((this.Image_TEST_Doc_Words == null) ? (other.Image_TEST_Doc_Words != null) : !this.Image_TEST_Doc_Words.equals(other.Image_TEST_Doc_Words)) {
            return false;
        }
        if ((this.Image_TEST_Blob_Counts == null) ? (other.Image_TEST_Blob_Counts != null) : !this.Image_TEST_Blob_Counts.equals(other.Image_TEST_Blob_Counts)) {
            return false;
        }
        if ((this.Image_TEST_Blobs == null) ? (other.Image_TEST_Blobs != null) : !this.Image_TEST_Blobs.equals(other.Image_TEST_Blobs)) {
            return false;
        }
        if (this.DATASET_RULES != other.DATASET_RULES) {
            return false;
        }
        if (this.ACTIVE_CLASS != other.ACTIVE_CLASS) {
            return false;
        }
        if (this.RULE_ATTRIBUTES != other.RULE_ATTRIBUTES) {
            return false;
        }
        if (this.RULE_CLASS_BITS != other.RULE_CLASS_BITS) {
            return false;
        }
        if (this.RULE_GENE_NO_BITS != other.RULE_GENE_NO_BITS) {
            return false;
        }
        if (this.RULE_GENE_OPERATOR_BITS != other.RULE_GENE_OPERATOR_BITS) {
            return false;
        }
        if (this.RULE_GENE_VALUE_BITS != other.RULE_GENE_VALUE_BITS) {
            return false;
        }
        if (this.RULE_GENE_VALUES != other.RULE_GENE_VALUES) {
            return false;
        }
        if (this.RULE_GENE_MAX_VALUE != other.RULE_GENE_MAX_VALUE) {
            return false;
        }
        if (this.MUTATION_OF_CLASS != other.MUTATION_OF_CLASS) {
            return false;
        }
        if (this.FITNESS_DEFAULT != other.FITNESS_DEFAULT) {
            return false;
        }
        if (this.TestNumber != other.TestNumber) {
            return false;
        }
        if ((this.TestFileReport == null) ? (other.TestFileReport != null) : !this.TestFileReport.equals(other.TestFileReport)) {
            return false;
        }
        if ((this.TestExFileReport == null) ? (other.TestExFileReport != null) : !this.TestExFileReport.equals(other.TestExFileReport)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.ReportIT != null ? this.ReportIT.hashCode() : 0);
        hash = 61 * hash + (this.Comment != null ? this.Comment.hashCode() : 0);
        hash = 61 * hash + (this.FileComment != null ? this.FileComment.hashCode() : 0);
        hash = 61 * hash + (this.Fsc ? 1 : 0);
        hash = 61 * hash + (this.ECHO ? 1 : 0);
        hash = 61 * hash + this.PopSize;
        hash = 61 * hash + this.Selection;
        hash = 61 * hash + Float.floatToIntBits(this.Pcrossover);
        hash = 61 * hash + Float.floatToIntBits(this.Pmutation);
        hash = 61 * hash + (int) (this.StopGeneration ^ (this.StopGeneration >>> 32));
        hash = 61 * hash + Float.floatToIntBits(this.StopEval);
        hash = 61 * hash + (this.MUTATION != null ? this.MUTATION.hashCode() : 0);
        hash = 61 * hash + (this.CROSSOVER != null ? this.CROSSOVER.hashCode() : 0);
        hash = 61 * hash + this.CrossValidation;
        hash = 61 * hash + Float.floatToIntBits(this.E_WEIGHT);
        hash = 61 * hash + (this.TestDataFileName != null ? this.TestDataFileName.hashCode() : 0);
        hash = 61 * hash + (this.TrainDataFileName != null ? this.TrainDataFileName.hashCode() : 0);
        hash = 61 * hash + (this.Image_Blob_Counts_Filename != null ? this.Image_Blob_Counts_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_Blobs_Filename != null ? this.Image_Blobs_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_Doc_Words_Filename != null ? this.Image_Doc_Words_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_Words_Filename != null ? this.Image_Words_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_TEST_Doc_Words != null ? this.Image_TEST_Doc_Words.hashCode() : 0);
        hash = 61 * hash + (this.Image_TEST_Blob_Counts != null ? this.Image_TEST_Blob_Counts.hashCode() : 0);
        hash = 61 * hash + (this.Image_TEST_Blobs != null ? this.Image_TEST_Blobs.hashCode() : 0);
        hash = 61 * hash + this.DATASET_RULES;
        hash = 61 * hash + this.ACTIVE_CLASS;
        hash = 61 * hash + this.RULE_ATTRIBUTES;
        hash = 61 * hash + this.RULE_CLASS_BITS;
        hash = 61 * hash + this.RULE_GENE_NO_BITS;
        hash = 61 * hash + this.RULE_GENE_OPERATOR_BITS;
        hash = 61 * hash + this.RULE_GENE_VALUE_BITS;
        hash = 61 * hash + this.RULE_GENE_VALUES;
        hash = 61 * hash + this.RULE_GENE_MAX_VALUE;
        hash = 61 * hash + (this.MUTATION_OF_CLASS ? 1 : 0);
        hash = 61 * hash + Float.floatToIntBits(this.FITNESS_DEFAULT);
        hash = 61 * hash + this.TestNumber;
        hash = 61 * hash + (this.TestFileReport != null ? this.TestFileReport.hashCode() : 0);
        hash = 61 * hash + (this.TestExFileReport != null ? this.TestExFileReport.hashCode() : 0);
        return hash;
    }
    static private Configuration Config = null;
    //////////////////////////////////////////////
    static int classes = 0;

    static public int getClassesNo() {
        return classes;
    }

    static public void setClassesNo(int i) {
        classes = i;
    }

    public static void setConfiguration(Configuration configuration) {
        Config = configuration;
    }
    //////////////////////////////////////////////
    private Report ReportIT;
    private String Comment; /*comment on research -> see on reports*/

    private String FileComment; /*comment on research -> see on reports*/


    public Report getReport() {
        return ReportIT;
    }
//	-----------------------------------------------------------------------------
    private boolean Fsc = true;

    public void setFsc(boolean F) {
        Config.Fsc = F;
    }

    public boolean isFsc() {
        return Config.Fsc;
    }
//	-----------------------------------------------------------------------------

    static public void newConfiguration(String S, String ResearchComment) {
        Config = new Configuration(S);
        Config.setResearchComment(ResearchComment);
    }

    static public Configuration getConfiguration() {// throws Exception{
        if (Config != null) {
            return Config;
        } else {
            return null;//throw new Exception("Configuration not Initialised!");
        }
    }
//	-----------------------------------------------------------------------------
    private boolean ECHO = false;

    boolean isEcho() {
        return ECHO;
    }
//  -----------------------------------------------------------------------------
    static private String PROMPT = "\n [CAREX2.4] ";

    public String getPrompt() {
        return PROMPT;
    }
    //  -----------------------------------------------------------------------------
    private int PopSize;

    public int getPopSize() {
        return PopSize;
    }
    private int Selection; //0-roullete wheel, 1-random, 2+tournament

    public int getSelection() {
        return Selection;
    }
    private float Pcrossover;

    public float getCrossoverValue() {
        return Pcrossover;
    }
    private float Pmutation;

    public float getMutationValue() {
        return Pmutation;
    }
    private long StopGeneration;
    private float StopEval;

    public enum MutationType {

        SM, FAM;
    };
    private MutationType MUTATION;

    public MutationType getMutationType() {
        return MUTATION;
    }

    public enum CrossoverType {

        SCX, BCX;
    };
    private CrossoverType CROSSOVER;

    public CrossoverType getCrossoverType() {
        return CROSSOVER;
    }
//  -----------------------------------------------------------------------------
    private int CrossValidation = 10;

    public int getCrossvalidationValue() {
        return CrossValidation;
    }
    float E_WEIGHT = 0.0f;

    public float getEWeight() {
        return E_WEIGHT;
    }
    private String TestDataFileName = null;
    private String TrainDataFileName = null;
    private String Image_Blob_Counts_Filename = null;
    private String Image_Blobs_Filename = null;
    private String Image_Doc_Words_Filename = null;
    private String Image_Words_Filename = null;
    private String Image_TEST_Doc_Words = null;
    private String Image_TEST_Blob_Counts = null;
    private String Image_TEST_Blobs = null;

    public boolean isImageDataConfiguration() {
        if (Image_Blob_Counts_Filename != null
                && Image_Blobs_Filename != null
                && Image_Doc_Words_Filename != null
                && Image_Words_Filename != null) {
            return true;
        } else {
            return false;
        }

    }

    public String getImageBlobCountsFilename() {
        return Image_Blob_Counts_Filename;
    }

    public String getImageBlobsFilename() {
        return Image_Blobs_Filename;
    }

    public String getImageDocWordFilename() {
        return Image_Blob_Counts_Filename;
    }

    public String getImageWordsFilename() {
        return Image_Words_Filename;
    }

    public String getImageTESTDocWordsFilename() {
        return Image_TEST_Doc_Words;
    }

    public String getImageTESTBlobCountsFilename() {
        return Image_TEST_Blob_Counts;
    }

    public String getImageTESTBlobsFilename() {
        return Image_TEST_Blobs;
    }
    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////// RuleSet -> Rule -> RuleGene ///////////////////
    private int DATASET_RULES = 10;   //number of rules in individual
    private int ACTIVE_CLASS = -1;    //if there is only one active class (>=0) gives its ID, if all classes are active -1

//------------------------------------------------------------------------------
    /**
     * number of rules in individual RuleSet
     * @return number of rules in individual
     */
    public int getNumberOfRules() {
        return DATASET_RULES;
    }

//------------------------------------------------------------------------------
    public boolean isOneClassActive() {
        if (ACTIVE_CLASS == -1) {
            return false;
        } else {
            return true;
        }
    }

//------------------------------------------------------------------------------
    public int getActiveClass() {
        return ACTIVE_CLASS;
    }
    //////////// RULE ///////////////////////////////////////
    private int RULE_ATTRIBUTES = 4;      //number of attributes
    private int RULE_CLASS_BITS = 2;      //number of bits containing class id

    public int getNumberOfAttributes() {
        return RULE_ATTRIBUTES;
    }
    ////////////////// RuleGene //////////////////////////////
                    /*   0       1         <- 2-9 ->   <- 10-18 ->  <19-20>
     *  on/off  operator    value1      value2      class_id
     */
    private int RULE_GENE_NO_BITS = 21;
    private int RULE_GENE_OPERATOR_BITS = 1;     // operator bits in/not in
    private int RULE_GENE_VALUE_BITS = 8;        // 8 bits for value
    private int RULE_GENE_VALUES = 2;            // number of values
    private int RULE_GENE_MAX_VALUE = -1;        //max value of value (8 bits -> 255)

    public int getMaxValue() {
        if (RULE_GENE_MAX_VALUE == -1) {
            RULE_GENE_MAX_VALUE = (int) Math.pow(2.0f, RULE_GENE_VALUE_BITS) - 1;
        }
        return RULE_GENE_MAX_VALUE;
    }

    public int getNumberOfValues() {
        return RULE_GENE_VALUES;
    }

    public int getRuleGeneNoBits() {
        return RULE_GENE_NO_BITS;
    }

    public int getBitsForOperator() {
        return RULE_GENE_OPERATOR_BITS;
    }

    public int getRuleGeneValueBits() {
        return RULE_GENE_VALUE_BITS;
    }
    /////////////////////////////////////////////////////////////////////
    private boolean MUTATION_OF_CLASS = true;

    public boolean isMutationOfClass() {
        return MUTATION_OF_CLASS;
    }

    public int getClassBits() {
        return RULE_CLASS_BITS;
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    private float FITNESS_DEFAULT = -99f;

    public float getFINTESSDEFAULT() {
        return FITNESS_DEFAULT;
    }

//------------------------------------------------------------------------------
    public String getTestFileName() {
        return this.TestDataFileName;
    }

//------------------------------------------------------------------------------
    public String getTrainFileName() {
        return this.TrainDataFileName;
    }
    int TestNumber;
    String TestFileReport;
    String TestExFileReport;
//------------------------------------------------------------------------------

    public void setReportFileName(String filename) {
        TestFileReport = filename;
        ReportIT = new Report(TestNumber, CrossValidation, TestFileReport, TestExFileReport);
    }

    public void setCv(int testsNo, int crossvalidaionNo) {
        this.CrossValidation = crossvalidaionNo;
        this.TestNumber = testsNo;
        ReportIT = new Report(TestNumber, CrossValidation, TestFileReport, TestExFileReport);
    }

    private Configuration(String FileName) {
        StringBuilder S = new StringBuilder();
        S.append("configs/");
        S.append(FileName);
        S.append(".properties");
        System.setProperty("file.encoding", "UTF-8");
        Properties properties = new Properties();
        File f = new File(S.toString());
        InputStream is;
        try {
            is = new FileInputStream(f);
            properties.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////

        this.TestDataFileName = properties.getProperty("TestDataFileName");
        this.TrainDataFileName = properties.getProperty("TrainDataFileName");

        RULE_ATTRIBUTES = Integer.parseInt(properties.getProperty("RULE_ATTRIBUTES"));      //number of attributes
        RULE_CLASS_BITS = Integer.parseInt(properties.getProperty("RULE_CLASS_BITS"));      //number of bits containing class id

        RULE_GENE_NO_BITS = Integer.parseInt(properties.getProperty("RULE_GENE_NO_BITS"));
        RULE_GENE_OPERATOR_BITS = Integer.parseInt(properties.getProperty("RULE_GENE_OPERATOR_BITS"));     // operator bits in/not in
        RULE_GENE_VALUE_BITS = Integer.parseInt(properties.getProperty("RULE_GENE_VALUE_BITS"));        // 8 bits for value
        RULE_GENE_VALUES = Integer.parseInt(properties.getProperty("RULE_GENE_VALUES"));            // number of values
        RULE_GENE_MAX_VALUE = Integer.parseInt(properties.getProperty("RULE_GENE_MAX_VALUE"));        //max value of value (8 bits -> 255)

        DATASET_RULES = Integer.parseInt(properties.getProperty("DATASET_RULES"));   //number of rules in individual
        ACTIVE_CLASS = Integer.parseInt(properties.getProperty("ACTIVE_CLASS"));    //if there is only one active class (>=0) gives its ID, if all classes are active -1

        ECHO = Boolean.parseBoolean(properties.getProperty("ECHO"));

        Fsc = Boolean.parseBoolean(properties.getProperty("FSC"));

        Pcrossover = Float.parseFloat(properties.getProperty("Pcrossover"));
        Pmutation = Float.parseFloat(properties.getProperty("Pmutation"));

        Selection = Integer.parseInt(properties.getProperty("Selection")); //0-roullete wheel, 1-random, 2+tournament

        StopEval = Float.parseFloat(properties.getProperty("StopEval"));
        StopGeneration = Long.parseLong(properties.getProperty("StopGeneration"));
        PopSize = Integer.parseInt(properties.getProperty("PopSize"));

        CrossValidation = Integer.parseInt(properties.getProperty("CROSSVALIDATION"));

        // XXX modyfikacje potrzebne do API, patrz metoda setCv
        TestNumber = Integer.parseInt(properties.getProperty("TEST_NUMBER"));
        TestFileReport = properties.getProperty("TEST_REPORT_FILE");
        TestExFileReport = properties.getProperty("TEST_FULL_REPORT_FILE");
        ReportIT = new Report(TestNumber, CrossValidation, TestFileReport, TestExFileReport);

        ////////// CROSSOVER & MUTATION TYPE //////////////////////////////////////////////////////////

        String Cross = properties.getProperty("CROSSOVER_TYPE");
        if (Cross.equals("BCX")) {
            CROSSOVER = CrossoverType.BCX;
        } else {
            CROSSOVER = CrossoverType.SCX; //standard crossover
        }
        String Mut = properties.getProperty("MUTATION_TYPE");
        if (Mut.equals("FAM")) {
            MUTATION = MutationType.FAM;
        } else {
            MUTATION = MutationType.SM; //standard random mutation -> simple mutation
        }

        //IMAGE DATA ////////////////////////////////////////////////////////////////////
        Image_Blob_Counts_Filename = properties.getProperty("IMAGE_Blob_Counts");
        Image_Blobs_Filename = properties.getProperty("IMAGE_Blobs");
        Image_Doc_Words_Filename = properties.getProperty("IMAGE_Doc_Words");
        Image_Words_Filename = properties.getProperty("IMAGE_Words");

        Image_TEST_Doc_Words = properties.getProperty("IMAGE_TEST_Doc_Words");
        Image_TEST_Blob_Counts = properties.getProperty("IMAGE_TEST_Blob_Counts");
        Image_TEST_Blobs = properties.getProperty("IMAGE_TEST_Blobs");

        FileComment = properties.getProperty("FILE_COMMENT");
    }

//------------------------------------------------------------------------------
    public long getStopGeneration() {
        return this.StopGeneration;
    }

//------------------------------------------------------------------------------
    public float getStopEval() {
        return StopEval;
    }

//------------------------------------------------------------------------------
    public ArrayList<String> getFileNames() {
        /*
         * TODO insert code here
         */
        return null;
    }

//  ------------------------------------------------------------------------------
    private void setResearchComment(String S) {
        if (S.length() > 0) {
            Comment = S;
        } else {
            Comment = new String();
        }
    }

//------------------------------------------------------------------------------
    public String toString() {
        StringBuilder SB = new StringBuilder();
        Configuration Config = Configuration.getConfiguration();
        if (this.Comment.length() > 0 || this.FileComment.length() > 0) {
            SB.append("\n----------------------------------------------------------------------");
            if (this.Comment.length() > 0) {
                SB.append("\n  # " + this.Comment);
            }
            if (this.FileComment.length() > 0) {
                SB.append("\n  # " + this.FileComment);
            }
        }
        SB.append("\n----------------------------------------------------------------------\n" + Configuration.getConfiguration().getPrompt() + DataLoader.FileSummary() + "  ");
        SB.append("\n C(pop=" + Config.getPopSize() + ", gen=" + Config.getStopGeneration() + ", rules=" + Config.getNumberOfRules() + ", crossvalidation=" + Config.getCrossvalidationValue());
        if (this.isFsc() == true) {
            SB.append(" Fit FSC ");
        } else {
            SB.append(" Fit ACC ");
        }
        SB.append(" Pm=" + Configuration.getConfiguration().getMutationValue() + " " + Configuration.getConfiguration().getMutationType()
                + " Px=" + Configuration.getConfiguration().getCrossoverValue() + " " + Configuration.getConfiguration().getCrossoverType()
                + " sel=" + Configuration.getConfiguration().getSelection() + " )");
        return SB.toString();
    }

    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        Configuration cfg = Configuration.getConfiguration();
        // SB.append("file_comment;system;pop_size;stop_gen;rule_no;cross_value
        // ;measure;mutation_val;mutation_type;crossover_value;crossover_type;
        // selection\n");
        sb.append(this.Comment);
        sb.append(";");
        sb.append(this.FileComment);
        sb.append(";");
        sb.append("[CAREX2.4]");
        sb.append(";");
        sb.append(cfg.getPopSize());
        sb.append(";");
        sb.append(cfg.getStopGeneration());
        sb.append(";");
        sb.append(cfg.getNumberOfRules());
        sb.append(";");
        sb.append(cfg.getCrossvalidationValue());
        sb.append(";");
        sb.append((this.isFsc() ? "Fit FSC" : "Fit ACC"));
        sb.append(";");
        sb.append(Configuration.getConfiguration().getMutationValue());
        sb.append(";");
        sb.append(Configuration.getConfiguration().getMutationType());
        sb.append(";");
        sb.append(Configuration.getConfiguration().getCrossoverValue());
        sb.append(";");
        sb.append(Configuration.getConfiguration().getCrossoverType());
        sb.append(";");
        sb.append(Configuration.getConfiguration().getSelection());
        sb.append(";");
        return sb.toString();
    }

    public void setACTIVE_CLASS(int ACTIVE_CLASS) {
        this.ACTIVE_CLASS = ACTIVE_CLASS;
    }

    public void setCROSSOVER(CrossoverType CROSSOVER) {
        this.CROSSOVER = CROSSOVER;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public void setCrossValidation(int CrossValidation) {
        this.CrossValidation = CrossValidation;
    }

    public void setDATASET_RULES(int DATASET_RULES) {
        this.DATASET_RULES = DATASET_RULES;
    }

    public void setECHO(boolean ECHO) {
        this.ECHO = ECHO;
    }

    public void setE_WEIGHT(float E_WEIGHT) {
        this.E_WEIGHT = E_WEIGHT;
    }

    public void setFITNESS_DEFAULT(float FITNESS_DEFAULT) {
        this.FITNESS_DEFAULT = FITNESS_DEFAULT;
    }

    public void setFileComment(String FileComment) {
        this.FileComment = FileComment;
    }

    public void setImage_Blobs_Filename(String Image_Blobs_Filename) {
        this.Image_Blobs_Filename = Image_Blobs_Filename;
    }

    public void setMUTATION(MutationType MUTATION) {
        this.MUTATION = MUTATION;
    }

    public void setMUTATION_OF_CLASS(boolean MUTATION_OF_CLASS) {
        this.MUTATION_OF_CLASS = MUTATION_OF_CLASS;
    }

    public static void setPROMPT(String PROMPT) {
        Configuration.PROMPT = PROMPT;
    }

    public void setPcrossover(float Pcrossover) {
        this.Pcrossover = Pcrossover;
    }

    public void setPmutation(float Pmutation) {
        this.Pmutation = Pmutation;
    }

    public void setPopSize(int PopSize) {
        this.PopSize = PopSize;
    }

    public void setRULE_ATTRIBUTES(int RULE_ATTRIBUTES) {
        this.RULE_ATTRIBUTES = RULE_ATTRIBUTES;
    }

    public void setRULE_CLASS_BITS(int RULE_CLASS_BITS) {
        this.RULE_CLASS_BITS = RULE_CLASS_BITS;
    }

    public void setRULE_GENE_MAX_VALUE(int RULE_GENE_MAX_VALUE) {
        this.RULE_GENE_MAX_VALUE = RULE_GENE_MAX_VALUE;
    }

    public void setRULE_GENE_NO_BITS(int RULE_GENE_NO_BITS) {
        this.RULE_GENE_NO_BITS = RULE_GENE_NO_BITS;
    }

    public void setRULE_GENE_OPERATOR_BITS(int RULE_GENE_OPERATOR_BITS) {
        this.RULE_GENE_OPERATOR_BITS = RULE_GENE_OPERATOR_BITS;
    }

    public void setRULE_GENE_VALUES(int RULE_GENE_VALUES) {
        this.RULE_GENE_VALUES = RULE_GENE_VALUES;
    }

    public void setRULE_GENE_VALUE_BITS(int RULE_GENE_VALUE_BITS) {
        this.RULE_GENE_VALUE_BITS = RULE_GENE_VALUE_BITS;
    }

    public void setSelection(int Selection) {
        this.Selection = Selection;
    }

    public void setStopEval(float StopEval) {
        this.StopEval = StopEval;
    }

    public void setStopGeneration(long StopGeneration) {
        this.StopGeneration = StopGeneration;
    }

    public static void setClasses(int classes) {
        Configuration.classes = classes;
    }
//  ------------------------------------------------------------------------------
    // setters
}

